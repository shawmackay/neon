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
 * neon : org.jini.projects.neon.callbacks.messages
 * MessageProducer.java
 * Created on 24-Jul-2003
 */
package org.jini.projects.neon.callbacks.messages.server;

import java.io.IOException;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.logging.Logger;

import net.jini.core.event.EventRegistration;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.lookup.ServiceID;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.export.Exporter;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.http.HttpServerEndpoint;
import net.jini.lookup.JoinManager;

import org.jini.projects.neon.callbacks.messages.server.leasing.MesgLandlord;

/**
 *Produces a message every n seconds
 * Use to test mercury and Norm services
 * @author calum
 */
public class MessageProducer implements ProducerIntf, DiscoveryListener {
	private static long EVENT_TYPE_ID = 234L;
	private long currentEventNum = 0;
	transient MesgLandlord landlord;
	LookupDiscoveryManager ldm;
	JoinManager jm;
	Logger log = Logger.getLogger("org.jini.projects.neon.callbacks.messages.server");
	Thread t = new Thread(new Notifier());
	/**
	 * 
	 */
	public MessageProducer(String[] groups) {
		log.fine("Startup");
		landlord = new MesgLandlord();
		t.start();
		// TODO Complete constructor stub for MessageProducer
		try {
			ldm = new LookupDiscoveryManager(groups, null, this);
			log.fine("Discovery started");
		} catch (IOException e) {
			// TODO Handle IOException
			e.printStackTrace();
		}
		synchronized (this) {
			try {
				wait(0);
			} catch (InterruptedException e1) {
				// TODO Handle InterruptedException
				e1.printStackTrace();
			}
		}
		log.info("Discovery complete....exiting");
	}

	public EventRegistration register(long leaseTime, RemoteEventListener listener) throws LeaseDeniedException, RemoteException {
		log.info("Registering for events");		
		Lease l = landlord.newLease(listener, leaseTime);
		EventRegistration evReg = new EventRegistration(EVENT_TYPE_ID, null, l, currentEventNum);
		log.info("Event Registration Created");
		return evReg;
	}

	public class Notifier implements Runnable {

		/* (non-Javadoc)
		* @see java.lang.Runnable#run()
		*/
		public void run() {
			
			for (;;) {

				Iterator regs = landlord.getRegistered().values().iterator();
				currentEventNum++;
				while (regs.hasNext()) {
					RemoteEventListener rel = (RemoteEventListener) regs.next();
					try {
						log.fine("Notifying");
						rel.notify(new RemoteEvent("Producer", EVENT_TYPE_ID, currentEventNum, null));
					} catch (RemoteException e) {
						// TODO Handle RemoteException
						e.printStackTrace();
					} catch (UnknownEventException e) {
						// TODO Handle UnknownEventException
						e.printStackTrace();
					}

				}
				try {
					//Logger.getLogger("MesgProducer").info("Sleeping for 10 seconds");
					Thread.sleep(1000);
				} catch (Exception e) {
				}

			}

		}

	}

	/* (non-Javadoc)
	 * @see net.jini.discovery.DiscoveryListener#discarded(net.jini.discovery.DiscoveryEvent)
	 */
	public void discarded(DiscoveryEvent e) {
		// TODO Complete method stub for discarded

	}

	/* (non-Javadoc)
	 * @see net.jini.discovery.DiscoveryListener#discovered(net.jini.discovery.DiscoveryEvent)
	 */
	public void discovered(DiscoveryEvent e) {
		log.info("Discovered");
		Uuid id = UuidFactory.generate();
		ServiceID sid = new ServiceID(id.getMostSignificantBits(), id.getLeastSignificantBits());
		try {
			Exporter exp = new BasicJeriExporter(HttpServerEndpoint.getInstance(3407), new BasicILFactory());
			Remote proxy = exp.export(this);
			MesgProdProxy regproxy = new MesgProdProxy((ProducerIntf)proxy);
			jm = new JoinManager(regproxy, null, sid, ldm, null);
			log.info("Joined");
			Runtime.getRuntime().addShutdownHook(new Thread(new Termthread()));
		} catch (IOException e1) {
			// TODO Handle IOException
			e1.printStackTrace();
		}
	}
	
	public class Termthread implements Runnable {
		
			/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			
			jm.terminate();
			ldm.terminate();
			log.info("Terminating");
			log.info("Terminating");
			
			
		}

}
	public static void main(String[] args) {
		Logger.getAnonymousLogger().info("Main method");
		if (System.getSecurityManager()==null)
			System.setSecurityManager(new RMISecurityManager());
		new MessageProducer(args);
	}
}
