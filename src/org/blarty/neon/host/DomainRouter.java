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
* neon : org.jini.projects.neon.host
*
*
* DomainRouter.java Created on 29-Jan-2004
*
* DomainRouter
*
*/
package org.jini.projects.neon.host;

import java.rmi.RemoteException;
import java.util.logging.Logger;

import net.jini.id.Uuid;
import net.jini.id.UuidFactory;

import org.jini.projects.zenith.bus.Bus;
import org.jini.projects.zenith.exceptions.NoSuchSubscriberException;
import org.jini.projects.zenith.messaging.messages.Message;
import org.jini.projects.zenith.router.RouterJoint;

/**
 * Creates a Zenith router joint to a particular domain. Messages can then by routed to this particular
 * router joint via the Zenith RouterService
* @author calum
*/
public class DomainRouter implements RouterJoint {
	ManagedDomain d;
	Bus b;
	Uuid ID = UuidFactory.generate();
	Logger log = Logger.getLogger("org.jini.projects.neon.host");
	/**
 */
	public DomainRouter(ManagedDomain d, Bus b) {
		super();
		log.finest("Domain router created");
		this.d = d;
		this.b = b;
		// URGENT Complete constructor stub for DomainRouter
	}
	
	/*
	* @see org.jini.projects.zenith.router.RouterJoint#getNameSpace()
	*/
	public String getNameSpace() throws RemoteException {
		// TODO Complete method stub for getNameSpace
		return b.getName();
	}
	
	/*
	* @see org.jini.projects.zenith.router.RouterJoint#getID()
	*/
	public Uuid getID() throws RemoteException {
		// TODO Complete method stub for getID
		
		return ID;
	}
	
	/*
	* @see org.jini.projects.zenith.router.RouterJoint#hostsSubscriber(net.jini.id.Uuid)
	*/
	public boolean hostsSubscriber(Uuid subscriberIdentity) throws RemoteException {
		// TODO Complete method stub for hostsSubscriber
		return b.hostsSubscriber(subscriberIdentity);
	}
	
	/*
	* @see org.jini.projects.zenith.router.RouterJoint#hostsTopic(java.lang.String)
	*/
	public boolean hostsTopic(String topic) throws RemoteException {
		// TODO Complete method stub for hostsTopic
		return b.hostsTopic(topic);
	}
	
	/*
	* @see org.jini.projects.zenith.router.RouterJoint#sendMessage(org.jini.projects.neon.collaboration.Message)
	*/
	public void sendMessage(String name,Message m) throws NoSuchSubscriberException, RemoteException {
		try {
			// TODO Complete method stub for sendMessage
			//VirtualProducer p = new VirtualProducer(UuidFactory.generate());
			//           Producer p = m.getSource();
			//			if (m.getMeta("transaction") != null) {
			//				try {
			//					((TransactionalResource)p).setTransaction(d.getTxnBlackBox().attachTransaction((ServerTransaction) m.getMeta("transaction")));
			//				} catch (TransactionException e1) {
			//					// URGENT Handle TransactionException
			//					e1.printStackTrace();
			//				}
			//			}
			//m.setSource(p);
			d.sendAsyncMessage(name, m);
		} catch (NoSuchAgentException e) {
			// URGENT Handle NoSuchAgentException
			throw new NoSuchSubscriberException(e);
		}
		
	}
	
	/*
	* @see org.jini.projects.zenith.router.RouterJoint#sendDirectedMessage(net.jini.id.Uuid, org.jini.projects.neon.collaboration.Message)
	*/
	public void sendDirectedMessage(Uuid subscriber, Message m) throws NoSuchSubscriberException, RemoteException {
		
		try {
			d.sendAsyncMessage(subscriber, m);
		} catch (NoSuchAgentException e) {
			// URGENT Handle NoSuchAgentException
			throw new NoSuchSubscriberException(e);
		}
	}
	
}
