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
/*
 * neon : org.jini.projects.neon.dynproxy
 * StatelessAgentFactory.java
 * Created on 16-Jul-2004
 *StatelessAgentFactory
 */
package org.jini.projects.neon.dynproxy;

import java.lang.reflect.Proxy;
import java.rmi.Remote;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.logging.Logger;

import net.jini.id.UuidFactory;

import org.jini.glyph.chalice.DefaultExporterManager;
import org.jini.glyph.chalice.ProxyPair;
import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.collaboration.Collaborative;
import org.jini.projects.neon.host.ManagedDomain;
import org.jini.projects.neon.host.NoSuchAgentException;

/**
 * Generates a stateless agent where messages are sent to the group dispatcher,
 * rather than to a paricular instance of that agent group. As such, there is no
 * guarantee, that call 1 and call 2 will be handled by the same actual agent.
 * 
 * @author calum
 */
public class StatelessAgentFactory {
	static Logger log = Logger.getLogger("org.jini.projects.neon.dynproxy");
    private static boolean useDirect = true;
    
    
	public static Object create(ManagedDomain ctx, String name) {
	
			try {
				Agent a = ctx.getRegistry().getAgent(name);
				StatelessProxy p = new StatelessProxy(a);

				log.finest("CollaborationProxy intfs req'd (external):");
				Class[] collabInterfaces = getAgentProxyInterfaces(a);
				return Proxy.newProxyInstance(Thread.currentThread()
						.getContextClassLoader(), collabInterfaces, p);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAgentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

		return null;
	}

	private static Class[] getAgentProxyInterfaces(Agent agent) {

		Agent a = agent;
		if (a == null)
			return null;
		ArrayList arr = new ArrayList();
		getInterfaces(a.getClass(), arr);
		Class[] intf = (Class[]) arr.toArray(new Class[] {});
		// return new AgentProxyInfo(a.getIdentity(), a.getNamespace() + "."
		// + a.getName(), intf);
		return intf;

	}

	private static void getInterfaces(Class cl, ArrayList arr) {
		// System.out.println("Checking interfaces...." + cl.getName());
		Class[] classes = cl.getInterfaces();
		// Class[] classes = cl.getClasses();
		for (int i = 0; i < classes.length; i++) {
			Class subclass = classes[i];
			// System.out.println("Class: " + subclass.getName());
			if (subclass.isInterface()) {
				getInterfaces(subclass, arr);
			}
			if (subclass.equals(Collaborative.class)) {
				if (!cl.getName().startsWith("$Proxy")) {
					arr.add(cl);
				}
			}
		}
	}
}
