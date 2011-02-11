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
 * MercuryClient.java
 * Created on 24-Jul-2003
 */
package org.jini.projects.neon.callbacks.messages;

import java.io.File;
import java.io.IOException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;
import net.jini.core.event.EventRegistration;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.event.EventMailbox;
import net.jini.event.MailboxRegistration;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lease.LeaseRenewalService;
import net.jini.lease.LeaseRenewalSet;

import org.jini.projects.neon.callbacks.DisconnectedCallback;
import org.jini.projects.neon.callbacks.messages.server.ProducerIntf;

/**
 * @author calum
 */
public class MercuryClient implements DiscoveryListener {
	EventMailbox mb = null;
	MailboxRegistration mbr = null;
	ProducerIntf prod = null;
	RemoteEventListener mercListener = null;
	LeaseRenewalService lrs = null;
	Logger l = Logger.getLogger("MercuryClient");
	LookupDiscoveryManager ldm = null;
	private LeaseRenewalManager lrm;
	SimpleListener sl = new SimpleListener();
	private LeaseRenewalSet leaseSet;
	/**
		 * 
		 */
	public MercuryClient(Configuration config) {
		super();
		try {
            String[] lookupGroups = (String[]) config.getEntry("MessageLookup", "lookupGroups", String[].class , LookupDiscovery.ALL_GROUPS);
			ldm = new LookupDiscoveryManager(lookupGroups, null, this);
		} catch (IOException e) {
			// TODO Handle IOException
			e.printStackTrace();
		} catch (ConfigurationException e) {
            // URGENT Handle ConfigurationException
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
		l.info("Discovery complete....exiting");
		// TODO Complete constructor stub for MercuryClient
		System.exit(0);
	}

	/* (non-Javadoc)
	 * @see net.jini.discovery.DiscoveryListener#discovered(net.jini.discovery.DiscoveryEvent)
	 */
	public void discovered(DiscoveryEvent e) {
		// TODO Complete method stub for discovered
		ServiceRegistrar[] regs = e.getRegistrars();
		Class[] merc = new Class[] { EventMailbox.class };
		Class[] norm = new Class[] { LeaseRenewalService.class };
		Class[] producer = new Class[] { ProducerIntf.class };

		ServiceTemplate merctmp = new ServiceTemplate(null, merc, null);
		ServiceTemplate normtmp = new ServiceTemplate(null, norm, null);
		ServiceTemplate prodtmp = new ServiceTemplate(null, producer, null);
		lrm = new LeaseRenewalManager();
		File f = new File("mboxreg.ser");
		MailboxRegistration mbr = null;
		EventRegistration evReg = null;
		DisconnectedCallback cbf = new DisconnectedCallback();

		try {
			mb = (EventMailbox) regs[0].lookup(merctmp);
			if (mb != null) {
				l.info("EventMailbox found");				
			}
			lrs = (LeaseRenewalService) regs[0].lookup(normtmp);
			if (lrs != null) {
				l.info("LeaseRenewalService found");				
			}
			prod = (ProducerIntf) regs[0].lookup(prodtmp);
			if (prod != null) {
				l.info("MessageProducer Found");				
			}
		} catch (RemoteException e4) {
			// TODO Handle RemoteException
			e4.printStackTrace();
		} 

		//Create or load the listener for Mercury
		try {
			mercListener = cbf.create(sl, mb, lrs);
			//register the Mercury REListener with the service		
			if (mercListener!=null){				
				evReg = prod.register(20000, mercListener);
				//Persist and store the service regsitration
				//and connect the listeners together
				cbf.addEventRegistration(evReg);
			}
		} catch (LeaseDeniedException e1) {
			// TODO Handle LeaseDeniedException
			e1.printStackTrace();
		} catch (RemoteException e1) {
			// TODO Handle RemoteException
			e1.printStackTrace();
		}	
		
		synchronized (this) {
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e2) {
				// TODO Handle InterruptedException
				e2.printStackTrace();
			}

			notify();

		}
	}

	/* (non-Javadoc)
	 * @see net.jini.discovery.DiscoveryListener#discarded(net.jini.discovery.DiscoveryEvent)
	 */
	public void discarded(DiscoveryEvent e) {
		// TODO Complete method stub for discarded

	}

	public static void main(String[] args) {
		System.setSecurityManager(new RMISecurityManager());
        Configuration config=null;
        try {
            config = ConfigurationProvider.getInstance(new String[] { "messaging.config" });
        } catch (ConfigurationException e) {
            // URGENT Handle ConfigurationException
            e.printStackTrace();
        }
		new MercuryClient(config);
	}

}
