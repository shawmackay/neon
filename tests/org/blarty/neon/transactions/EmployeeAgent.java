/*
 * Copyright 2005 neon.jini.org project 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package org.jini.projects.neon.transactions;

/*
 * EmployeeAgent.java
 * 
 * Created Mon Mar 21 14:16:18 GMT 2005
 */
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import net.jini.core.entry.Entry;
import net.jini.lookup.entry.Name;

import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.LocalAgent;
import org.jini.projects.neon.host.transactions.InternalTransaction;
import org.jini.projects.neon.host.transactions.TransactionAgent;
import org.jini.projects.neon.render.EngineInstruction;
import org.jini.projects.neon.render.PresentableAgent;
import org.jini.projects.neon.render.RenderAgent;

/**
 * 
 * @author calum
 * 
 */

public class EmployeeAgent extends AbstractAgent implements PresentableAgent, LocalAgent, Runnable {

	public EmployeeAgent() {
		this.name = "Employee";
		this.namespace = "neon.transactions";
	}

	public boolean init() {
		return true;
	}

	public void run() {
		try {
			RenderAgent render = (RenderAgent) context.getAgent("neon.Renderer");
			java.net.URL u = new URL("file:conf/identity.xsl");
			if (u != null)
				render.registerPresentation(this, u);
			else
				getAgentLogger().severe("Can't register the presentation");
		} catch (Exception ex) {
			System.err.println("Caught Exception: " + ex.getClass().getName() + "; Msg: " + ex.getMessage());
			ex.printStackTrace();
		}

	}

	/**
	 * getPresentableFormat
	 * 
	 * @return String
	 */
	public EngineInstruction getPresentableFormat(String type, String action, Map params, boolean getTemplate) {
		StringBuffer buff = new StringBuffer();
		buff.append("<html><head><title>Employee Roster</title><link rel=\"stylesheet\" type=\"text/css\" href=\"neon.css\"/></head><body><div class=\"mainbody\">");
		try {
			TransactionAgent txn = (TransactionAgent) context.getAgent("neon.Transaction");
			String spaceName = (String) getAgentConfiguration().getEntry("neon.transactions.tests", "updateSpace", String.class);
			JavaSpaceOps ops = (JavaSpaceOps) context.getAgent("JavaSpaceOps", new Entry[] { new Name(spaceName) });
			InternalTransaction itxn = txn.createTransaction(this.getIdentity());
			EmployeeEntry template = new EmployeeEntry(null,null, null);
			EmployeeEntry taken = (EmployeeEntry) ops.takeIfExists(template, 5000L);
			buff.append("<h2>Results</h2><table>");
			int numavailable = 0;
			buff.append("<tr><th>Character Name</th><th>Catchphrase</th></tr>");
			while (taken != null) {
				buff.append("<tr><td>" + taken.name + "</td><td>" + taken.job + "</td></tr>");
				getAgentLogger().info("Taken: " + taken.name);
				taken = (EmployeeEntry) ops.takeIfExists(template, 5000L);
				numavailable++;
			}
			itxn.abort();
			buff.append("</table><br/>Number of items: " + numavailable + "</div></body></html>");
		} catch (Exception ex) {
			System.err.println("Caught Exception: " + ex.getClass().getName() + "; Msg: " + ex.getMessage());
			ex.printStackTrace();
		}
		if (getTemplate)
			return new EngineInstruction("xsl", getTemplate("xsl", action), buff.toString());
		else
			return new EngineInstruction("xsl", getTemplate("xsl", action), buff.toString());

	}

	public URL getTemplate(String type, String action) {
		// TODO Complete method stub for getTemplate

		if (type.equals("html") || type.equals("xsl"))

			try {
				return new URL("file:conf/identity.xsl");
			} catch (MalformedURLException ex) {
				ex.printStackTrace();
			}
			
		return null;
	}
}
