/*
 * osiris : osiris
 * SpaceMover.java Created on 21-Apr-2004
 *SpaceMover
 */

package org.jini.projects.neon.service;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.transaction.TransactionException;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.entry.Name;
import net.jini.space.JavaSpace;

import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.util.AgentConstraintsEntry;
import org.jini.projects.neon.agents.util.AgentEntry;

/**
 * @author calum
 */
public class SpaceMover implements DiscoveryListener {
	LookupDiscoveryManager ldm;

	public SpaceMover(String group) {
		try {
			ldm = new LookupDiscoveryManager(new String[]{group}, null, this);
			synchronized (this) {
				wait();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * @see net.jini.discovery.DiscoveryListener#discarded(net.jini.discovery.DiscoveryEvent)
	 */
	public void discarded(DiscoveryEvent arg0) {
		// TODO Complete method stub for discarded
	}

	/*
	 * @see net.jini.discovery.DiscoveryListener#discovered(net.jini.discovery.DiscoveryEvent)
	 */
	public void discovered(DiscoveryEvent event) {
		System.out.println("Discovered");
		LeaseRenewalManager lrm = new LeaseRenewalManager();
		ServiceRegistrar[] regs = event.getRegistrars();
		ServiceTemplate errorJSpaceTmp = new ServiceTemplate(null, new Class[]{JavaSpace.class}, new Entry[]{new Name("ERRORSPACE")});
		ServiceTemplate salesJSpaceTmp = new ServiceTemplate(null, new Class[]{JavaSpace.class}, new Entry[]{new Name("SALESSPACE")});
		ServiceTemplate txnMgrTmp = new ServiceTemplate(null, new Class[]{TransactionManager.class}, null);
		try {
			JavaSpace errorJSpace = (JavaSpace) regs[0].lookup(errorJSpaceTmp);
			JavaSpace salesJSpace = (JavaSpace) regs[0].lookup(salesJSpaceTmp);
			TransactionManager txnMgr = (TransactionManager) regs[0].lookup(txnMgrTmp);
			// Transaction.Created txf = TransactionFactory.create(txnMgr,
			// 20000);
			// Transaction tx = txf.transaction;
			// lrm.renewFor(txf.lease, Lease.FOREVER, null);
			AgentConstraintsEntry ace = new AgentConstraintsEntry(null, null, null, null, null,null,null,null);
			try {
				
				Object entry = null;
				do {
					entry = salesJSpace.takeIfExists(ace, null, 5000);
					if (entry != null) {
						System.out.print("Removed Constraints Entry....");		
						AgentConstraintsEntry dumpedEntry = (AgentConstraintsEntry) entry;
						AgentIdentity actualAgent = new AgentIdentity(dumpedEntry.referentAgentIdentity);

						AgentEntry agent = (AgentEntry) salesJSpace.takeIfExists(new AgentEntry(actualAgent, null, null, null), null, 1000);
						if(agent!=null)
							System.out.print("..and referenced Entry");
					}
					System.out.println();
				} while (entry != null);
			} catch (RemoteException e) {
				// TODO Handle RemoteException
				e.printStackTrace();
			} catch (UnusableEntryException e) {
				// TODO Handle UnusableEntryException
				e.printStackTrace();
			} catch (TransactionException e) {
				// TODO Handle TransactionException
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Handle InterruptedException
				e.printStackTrace();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		synchronized (this) {
			notify();
		}
	}

	public static void main(String[] args) {
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new RMISecurityManager());
		new SpaceMover(args[0]);
	}
}