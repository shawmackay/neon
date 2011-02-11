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

package org.jini.projects.neon.neontests.callbacks.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.id.Uuid;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.http.HttpServerEndpoint;

import org.jini.projects.neon.agents.AgentConstraints;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.ReferenceableConstraints;
import org.jini.projects.neon.service.AgentService;
import org.jini.projects.zenith.exceptions.NoSuchSubscriberException;
import org.jini.projects.zenith.messaging.messages.InvocationMessage;
import org.jini.projects.zenith.messaging.messages.MessageHeader;
import org.jini.projects.zenith.router.RouterService;


/**
 *Simple Test harness for getting agents deployed into neon
 */
public class TestClient implements DiscoveryListener {
	private TestCallback t;
	//private AgentCallback proxy;
	private AgentIdentity agentID;
	private AgentService ag;
	Thread locThread = new Thread(new LocationThread());
	private Uuid agentSubsID;
	private RouterService router;

	public TestClient(String[] groups) {
		try {
			if (System.getSecurityManager() == null)
				System.setSecurityManager(new RMISecurityManager());
			System.out.println("First group to look in is " + groups[0]);
			LookupDiscoveryManager ldm = new LookupDiscoveryManager(groups, null, this);
			synchronized (this) {
				wait(0);
			}
		} catch (IOException e) {

			System.out.println("Err: " + e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {

			System.out.println("Err: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void discovered(DiscoveryEvent e) {
		ServiceRegistrar[] regs = e.getRegistrars();
		System.out.println("Discovered");
		try {
			router = (RouterService) regs[0].lookup(new ServiceTemplate(null, new Class[] { RouterService.class }, null));
			ag = (AgentService) regs[0].lookup(new ServiceTemplate(null, new Class[] { AgentService.class }, null));
			try {
				AgentConstraints ac = new ReferenceableConstraints(new URL("http://e0052sts3s/jinistubs/constraints.xml"));
				ac.getConstraints();
				
					BasicJeriExporter exporter = new BasicJeriExporter(HttpServerEndpoint.getInstance(0), new BasicILFactory());
					t = new TestCallback();
					//DisconnectedCallback cf = CallbackFactory.buildCallback("MobileCallback", regs, exporter,t);

					try {
						Thread.sleep(5000);
					} catch (InterruptedException e2) {
						// TODO Handle InterruptedException
						e2.printStackTrace();
					}
					locThread.start();
					
			} catch (RemoteException e1) {

				System.out.println("Err: " + e1.getMessage());
				e1.printStackTrace();
			} catch (MalformedURLException e1) {

				System.out.println("Err: " + e1.getMessage());
				e1.printStackTrace();
			}
		} catch (RemoteException e1) {

			System.out.println("Err: " + e1.getMessage());
			e1.printStackTrace();
		}

	}

	public void discarded(DiscoveryEvent e) {
	}

	public static void main(String[] args) {
		new TestClient(args);
	}

	public class LocationThread implements Runnable {
		/* (non-Javadoc)
		* @see java.lang.Runnable#run()
		*/
		public void run() {
			// TODO Complete method stub for run
			for (;;) {
				try {
					try {
						if (router != null)
							router.sendDirectedMessage(agentSubsID, new InvocationMessage(new MessageHeader(), "TellMeLocation", new Object[] {
						},null));
					} catch (NoSuchSubscriberException e1) {
						// URGENT Handle NoSuchSubscriberException
						e1.printStackTrace();
					}
				} catch (RemoteException e) {
					// TODO Handle RemoteException
					e.printStackTrace();
				}
				try {
					Thread.sleep(20000);
				} catch (Exception ex) {
				}

			}

		}

	}
}
