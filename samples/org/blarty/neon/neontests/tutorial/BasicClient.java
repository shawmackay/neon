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

package org.jini.projects.neon.neontests.tutorial;

import java.io.IOException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;
import net.jini.core.lookup.ServiceMatches;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscoveryManager;

import org.jini.projects.neon.neontests.tutorial.mobile.TrackSvcsAgent;
import org.jini.projects.neon.service.AgentService;
import org.jini.projects.zenith.exceptions.NoSuchSubscriberException;
import org.jini.projects.zenith.messaging.messages.InvocationMessage;
import org.jini.projects.zenith.messaging.messages.MessageHeader;
import org.jini.projects.zenith.router.RouterService;

public class BasicClient implements DiscoveryListener {
	public BasicClient() {
		super();
		try {
			System.out.println("Discovering");
			String[] lookups = null;
			Configuration config=null;
			try {
				config = ConfigurationProvider.getInstance(new String[]{"client.config"});
			} catch (ConfigurationException e1) {
				// URGENT Handle ConfigurationException
				e1.printStackTrace();
			}
			lookups = (String[]) config.getEntry("client", "lookupGroups", String[].class, new String[]{"public"});
			
			for(int i=0;i<lookups.length;i++)
				System.out.println("Looking in group: " + lookups[i]);
			LookupDiscoveryManager ldm = new LookupDiscoveryManager(lookups, null, this);
			synchronized (this) {
				wait(0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

	public void discarded(DiscoveryEvent e) {
	}

	public void discovered(DiscoveryEvent e) {
		System.out.println("Discovered");
		ServiceRegistrar[] regs = e.getRegistrars();
		try {
			System.out.println("Registrar:" + e.getRegistrars()[0].getLocator().toString());
		} catch (RemoteException e3) {
			// TODO Handle RemoteException
			e3.printStackTrace();
		}
		AgentService svc1=null;
		AgentService svc2 = null;
		RouterService router = null;
		TrackSvcsAgent ag = null;
		try {
			ServiceMatches matches = regs[0].lookup(new ServiceTemplate(null, new Class[]{org.jini.projects.neon.service.AgentService.class}, null), 4);
			
			try {
				svc1 = (AgentService) matches.items[0].service; 
				svc2 = (AgentService) matches.items[0].service;
			} catch (Exception ex) {
			}
			//svc = (AgentService) regs[0].lookup(new ServiceTemplate(null,
			// new Class[] { AgentService.class }, null));
			router = (RouterService) regs[0].lookup(new ServiceTemplate(null, new Class[]{RouterService.class}, null));
			if (svc1 != null && svc1 != null) {
				//We don't have any constraints, so we don't need to check
				ag = new TrackSvcsAgent();
				//ag.setConstraints(new ReferenceableConstraints(new URL("http://e0052sts3s.countrywide-assured.co.uk/jinistubs/constraints.xml")));
				System.out.println("Agent name: " + ag.getName());
				 svc1.deployAgent(ag);
				//svc1.deployAgent(ag);
				/*System.out.println("Deploying Agent One");
				svc2.deployAgent(new TransactionalAgentOne());
				System.out.println("Deploying Agent Two");
				svc2.deployAgent(new TransactionalAgentTwo());
				System.out.println("Deploying Agent Controller");
				svc1.deployAgent(new TransactionSourceAgent());*/
			} else {
				System.out.println("Two different instances of Neon need to be running");
				//System.exit(0);
			}
		} catch (RemoteException e1) {
			System.out.println("A RemoteException has occured - " + e1.getMessage());
			e1.printStackTrace();
		} 
		if (router != null) {
			System.out.println("Sending message via router");
			try {
				Thread.sleep(10000);
				router.sendMessage("TrackSvcs", new InvocationMessage(new MessageHeader(), "GetLocation", new Object[]{},null));
				System.out.println("Message sent");
				//                if (ag != null) {
				//                    System.out.println("Killing the agent");
				//                    router.sendMessage("Killer", "KillOneOf", new Object[]
				// {"TrackSvcs" });
				//                }
			} catch (RemoteException e2) {
				// URGENT Handle RemoteException
				e2.printStackTrace();
			} catch (NoSuchSubscriberException e2) {
				// URGENT Handle NoSuchSubscriberException
				e2.printStackTrace();
			} catch (InterruptedException e2) {
				// URGENT Handle InterruptedException
				e2.printStackTrace();
			}
		}
		synchronized (this) {
			notify();
		}
	}

	public static void main(String[] args) {
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new RMISecurityManager());
		new BasicClient();
	}
}
