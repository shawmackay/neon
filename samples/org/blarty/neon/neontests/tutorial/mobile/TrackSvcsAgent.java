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
 * neon : org.jini.projects.neon.org.jini.projects.neon.neontests.tutorial.mobile
 * TrackSvcsAgent.java Created on 15-Oct-2003
 */
package org.jini.projects.neon.neontests.tutorial.mobile;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import net.jini.core.lookup.ServiceItem;

import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.callbacks.CallbacksAgent;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.neon.service.AgentService;
import org.jini.projects.neon.service.ServiceAgent;
import org.jini.projects.neon.service.TransferAgent;

public class TrackSvcsAgent extends AbstractAgent implements LocationCaller, Runnable {

	private List hostsVisited;

	public TrackSvcsAgent() {
		super();

	}
	public boolean init() {
		if (hostsVisited == null) {
			System.out.println("Initialising Host list");
			System.out.println("MY ID is: " + this.ID.getID());
			hostsVisited = new ArrayList();
		}
		return true;
	}

	public void run() {
		if (!hostsVisited.contains(context.getAgentServiceID().toString())) {
			System.out.println("Adding agService: " + context.getAgentServiceID().toString());
			hostsVisited.add(context.getAgentServiceID().toString());
		}

		boolean tryagain = true;
		do {
			ServiceItem reqTransfer = null;
			//Go into a sleep loop, waiting for other hosts to join
			while (reqTransfer == null) {
				Logger.getLogger("org.jini.projects.neon.neontests.TrackSvcs").info("Waiting....");
				try {
					ServiceAgent svc = (ServiceAgent) context.getAgent("neon.Services");
					System.out.println("Checking Transferrable hosts");
					reqTransfer = checkServices(svc.getAgentHosts());
					if (reqTransfer == null)
						agentWait();
				} catch (NoSuchAgentException e1) {
					e1.printStackTrace();
				}
			}
			tryagain = startTransfer(reqTransfer);
		}
		while (tryagain);

		
	}

	private ServiceItem checkServices(List list) {
		ServiceItem transferTo = null;
		System.out.println("Checking services in list");
		if (list != null) {
			for (Iterator iter = list.iterator(); iter.hasNext();) {
				ServiceItem item = (ServiceItem) iter.next();
				if (!hostsVisited.contains(item.serviceID.toString())) {
					//We haven't been to this server yet, so transfer
					transferTo = item;
					break;
				}
			}
		}
		return transferTo;
	}

	private void agentWait() {
		synchronized (this) {
			try {
				wait(5000);
			} catch (InterruptedException e) {
				System.out.println("Err: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private boolean startTransfer(ServiceItem reqTransfer) {
		AgentService svc = (AgentService) reqTransfer.service;
		boolean tryagain = false;
		try {
            CallbacksAgent cb = (CallbacksAgent) context.getAgent("neon.Callback");
            TransferAgent intf = (TransferAgent) context.getAgent("neon.Transfer");
            
            System.out.println("Firing callbacks!");
            cb.sendCallback(getIdentity(), "Going to Transfer wohoo!");
			//Response callbackResponse = context.sendMessage("Callback", "SendCallback", new Object[] { getIdentity(), "Going to Transfer" });
	//		Response transferResponse = context.sendMessage("Transfer", "TransferAgentTo", new Object[] { this, svc });

//			boolean val = ((Boolean) transferResponse.getResponseObject()).booleanValue();
            
			if (intf.transferAgentTo(this, svc).booleanValue())
				tryagain = false;
		} catch (NoSuchAgentException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tryagain;
	}

	

	public String getLocation() {
		System.out.println("Message received");
		return this.name + "(" + this.ID + ") located @ " + context.getCurrentHost();
	}

}
