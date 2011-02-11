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
 * neon : org.jini.projects.neon.vertigo.management
 * SliceManager.java
 * Created on 09-May-2005
 *SliceManager
 */

package org.jini.projects.neon.vertigo.management;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.transaction.CannotAbortException;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.UnknownTransactionException;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.id.Uuid;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.LookupCache;
import net.jini.lookup.ServiceDiscoveryEvent;
import net.jini.lookup.ServiceDiscoveryListener;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.lookup.ServiceItemFilter;
import net.jini.lookup.entry.Name;
import net.jini.space.JavaSpace;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.util.AgentConstraintsEntry;
import org.jini.projects.neon.dynproxy.FacadeProxy;
import org.jini.projects.neon.host.AgentRegistry;
import org.jini.projects.neon.host.DomainRegistry;
import org.jini.projects.neon.host.ManagedDomain;
import org.jini.projects.neon.service.AgentBackendService;
import org.jini.projects.neon.service.AgentService;
import org.jini.projects.neon.service.admin.AgentServiceAdmin;
import org.jini.projects.neon.vertigo.application.ApplicationRef;
import org.jini.projects.neon.vertigo.application.DefaultApplication;
import org.jini.projects.neon.vertigo.management.store.SliceStorage;
import org.jini.projects.neon.vertigo.management.store.XMLSliceStorage;
import org.jini.projects.neon.vertigo.slice.AttractorSlice;
import org.jini.projects.neon.vertigo.slice.DefaultSlice;
import org.jini.projects.neon.vertigo.slice.RedundantSlice;
import org.jini.projects.neon.vertigo.slice.RepellerSlice;
import org.jini.projects.neon.vertigo.slice.Slice;
import org.jini.projects.zenith.messaging.broker.MessageBroker;
import org.jini.projects.zenith.messaging.system.MessagingService;

/**
 * Holds the list of currently deployed slices.
 * 
 * @author calum
 */
public class SliceManagerImpl implements SliceManager, ServiceDiscoveryListener {

	private HashMap<AgentIdentity, WeakReference<Slice>> attachedAgents = new HashMap<AgentIdentity, WeakReference<Slice>>();

	private LookupCache cache;

	private AgentBackendService svc;

	private Map<Uuid, Slice> sliceList = new HashMap<Uuid, Slice>();

	private Map<String, Uuid> appIDList = new HashMap<String, Uuid>();

	private Configuration config;

	private SliceStorage sliceStore;

	LookupDiscoveryManager ldm;

	ServiceDiscoveryManager sdm;

	public SliceManagerImpl(Configuration configuration) {
		System.out.println("Creating Slice Manager");
		this.config = configuration;
		try {
			String spaceName = (String) config.getEntry("org.jini.projects.neon.vertigo", "spaceName", String.class, "");
			String[] initialLookupGroups = (String[]) config.getEntry("org.jini.projects.neon.vertigo", "initialLookupGroups", String[].class);
			sliceStore = (SliceStorage) config.getEntry("org.jini.projects.neon.vertigo", "sliceStore", SliceStorage.class, new XMLSliceStorage("storage/slices", this));
			LookupLocator[] initialLookupLocators = (LookupLocator[]) config.getEntry("org.jini.projects.neon.vertigo", "initialLookupLocators", LookupLocator[].class, new LookupLocator[] {});
			ldm = new LookupDiscoveryManager(initialLookupGroups, initialLookupLocators, null);
			sdm = new ServiceDiscoveryManager(ldm, new LeaseRenewalManager());
			cache = sdm.createLookupCache(null, null, this);
			sliceStore.loadSlices();
			System.out.println("Stored Applications");
			for (Map.Entry<String, Uuid> entr : appIDList.entrySet()) {
				System.out.println(entr.getKey() + "=>" + entr.getValue());
			}
		} catch (RemoteException e) {
			// TODO Handle RemoteException
			e.printStackTrace();
		} catch (ConfigurationException e) {
			// TODO Handle ConfigurationException
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Handle IOException
			e.printStackTrace();
		}
	}

	public void showSpaceAgents(final String spacename) {
		ServiceItem txmSvc = cache.lookup(new ServiceItemFilter() {

			public boolean check(ServiceItem item) {
				// TODO Complete method stub for check
				if (item.service instanceof TransactionManager)
					return true;
				return false;
			}

		});

		ServiceItem spaceSvc = cache.lookup(new ServiceItemFilter() {

			public boolean check(ServiceItem item) {
				// TODO Complete method stub for check
				if (item.service instanceof JavaSpace)
					for (Entry entr : item.attributeSets)
						if (entr instanceof Name)
							if (((Name) entr).name.equals(spacename)) {
								System.out.println("Found matching Javaspace");
								return true;
							}
				return false;
			}
		});

		if (spaceSvc != null && txmSvc != null) {
			TransactionManager txm = (TransactionManager) txmSvc.service;
			JavaSpace theSpace = (JavaSpace) spaceSvc.service;
			System.out.println("Found Javaspace & Txn Mgr");
			Transaction.Created txn = null;
			try {
				txn = TransactionFactory.create(txm, 20000);
				LeaseRenewalManager lrm = new LeaseRenewalManager();
				lrm.renewFor(txn.lease, Lease.FOREVER, null);
				AgentConstraintsEntry tmpl = new AgentConstraintsEntry();
				AgentConstraintsEntry ace = null;
				do {
					ace = (AgentConstraintsEntry) theSpace.takeIfExists(tmpl, txn.transaction, 500L);
					if (ace != null) {
						System.out.println("Found stored agent:");
						System.out.printf("\tID:\t%s\n", ace.referentAgentIdentity);
						System.out.printf("\tLastHost:\t%s\n", ace.lastHost);
						System.out.printf("\tDomain:\t%s\n", ace.domain);
						System.out.printf("\tState:\t%s\n", ace.state);
					}
				} while (ace != null);
			} catch (LeaseDeniedException e) {
				// TODO Handle LeaseDeniedException
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Handle RemoteException
				e.printStackTrace();
			} catch (UnknownTransactionException e) {
				// TODO Handle UnknownTransactionException
				e.printStackTrace();
			} catch (CannotAbortException e) {
				// TODO Handle CannotAbortException
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
			} finally {
				try {
					if (txn != null) {
						txn.transaction.abort();
						System.out.println("Transaction Aborted");
					}
				} catch (Exception ex) {
					System.out.println("Err: " + ex.getMessage());
				}
			}
		}
	}

	public boolean deploySlice(Slice s) {
		// try {
		// svc.deploySlice(null,new SliceHolder(s));
		// } catch (RemoteException e) {
		// // TODO Handle RemoteException
		// e.printStackTrace();
		// return false;
		// }
		sliceList.put(s.getSliceID(), s);
		if (s instanceof ApplicationRef)
			appIDList.put(s.getName(), s.getSliceID());
		return true;
	}

	public ServiceID getHostingServiceID(AgentIdentity ag) {
		// TODO Auto-generated method stub
		ServiceItem[] msgSvcs = cache.lookup(new ServiceItemFilter() {
			public boolean check(ServiceItem item) {
				// TODO Auto-generated method stub
				if (item.service instanceof MessageBroker) {
					return true;
				}
				return false;
			}
		}, 10);
		for (ServiceItem svI : msgSvcs) {
			MessageBroker svc = (MessageBroker) svI.service;
			ServiceID sid = null;
			try {
				sid = svc.getServiceForChannel(ag.getID().toString());
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (sid != null)
				return sid;
		}
		return null;
	}

	public void attachAgentToSlice(AgentIdentity a, String slicename) {
		System.out.println("Attaching Agent to Slice: " + slicename);
		Slice s = findSlice(slicename);
		Collection c = DomainRegistry.getDomainRegistry().getDomains();
		for (Iterator iter = c.iterator(); iter.hasNext();) {
			ManagedDomain dom = (ManagedDomain) iter.next();
			AgentRegistry reg = dom.getRegistry();
			if (reg.contains(a)) {
				try {
					Agent agent = reg.getAgent(a);
					if (agent instanceof java.lang.reflect.Proxy) {
						Proxy p = (java.lang.reflect.Proxy) agent;
						InvocationHandler invHnd = p.getInvocationHandler(agent);
						if (invHnd instanceof FacadeProxy) {
							agent = ((FacadeProxy) invHnd).getReceiver();
						}
					}
					// System.out.println("Adding " + agent.getIdentity() +
					// " (" + agent.getClass().getName() + ")");
					AgentListObject agob = new AgentListObject(agent.getNamespace() + "." + agent.getName(), agent.getIdentity(), agent.getClass().getName(), dom.getDomainName());
					agob.setResolved(true);
					s.attachAgent(agob);
				} catch (Exception ex) {
					System.err.println("Caught Exception: " + ex.getClass().getName() + "; Msg: " + ex.getMessage());
					ex.printStackTrace();
				}

			}
		}
	}

	public void attachAgentToSlice(AgentIdentity a, Uuid sliceID) {
		System.out.println("Attaching Agent to Slice: " + sliceID);
		Slice s = sliceList.get(sliceID);
		Collection c = DomainRegistry.getDomainRegistry().getDomains();
		for (Iterator iter = c.iterator(); iter.hasNext();) {
			ManagedDomain dom = (ManagedDomain) iter.next();
			AgentRegistry reg = dom.getRegistry();
			if (reg.contains(a)) {
				try {
					Agent agent = reg.getAgent(a);
					if (agent instanceof java.lang.reflect.Proxy) {
						Proxy p = (java.lang.reflect.Proxy) agent;
						InvocationHandler invHnd = p.getInvocationHandler(agent);
						if (invHnd instanceof FacadeProxy) {
							agent = ((FacadeProxy) invHnd).getReceiver();
						}
					}
					// System.out.println("Adding " + agent.getIdentity() +
					// " (" + agent.getClass().getName() + ")");
					AgentListObject agob = new AgentListObject(agent.getNamespace() + "." + agent.getName(), agent.getIdentity(), agent.getClass().getName(), dom.getDomainName());
					agob.setResolved(true);
					s.attachAgent(agob);
				} catch (Exception ex) {
					System.err.println("Caught Exception: " + ex.getClass().getName() + "; Msg: " + ex.getMessage());
					ex.printStackTrace();
				}
			}
		}

		System.out.println("putting in attachedAgents map");
		attachedAgents.put(a, new WeakReference<Slice>(s));
	}

	public void attachAgentTypeToSlice(String agentName, int number, Uuid sliceID) {
		System.out.println("Attaching Agent to Slice: " + sliceID);
		Slice s = sliceList.get(sliceID);
		s.attachAgentType(agentName, number);

	}

	public Slice getSliceFor(AgentIdentity a) {
		if (attachedAgents.get(a) != null) {
			System.out.println("Found an agent attached in system");
			return attachedAgents.get(a).get();
		} else
			return null;
	}

	public Slice getSlice(Uuid sliceID) {
		return sliceList.get(sliceID);
	}

	public void displaySliceInfo(String sliceName) {
		// TODO Complete method stub for displaySliceInfo

		Slice currentSlice = findSlice(sliceName);
		if (currentSlice != null) {
			System.out.println("Chosen slice is");
			System.out.printf("\tName:\t%s\n", currentSlice.getName());
			System.out.printf("\tShortDesc:\t%s\n", currentSlice.getShortDescription());
			System.out.printf("\tLongDesc:\t%s\n", currentSlice.getLongDescription());
			System.out.printf("\tSliceID:\t%s\n", currentSlice.getSliceID());
			System.out.printf("\tParentID:\t%s\n", currentSlice.getParentSliceID());
			System.out.println("Agents registered to slice:");
			List<AgentIdentity> ids = currentSlice.getAgentIDs();
			for (AgentIdentity id : ids)
				System.out.printf("\t%s\n", id);
		}
	}

	public void packSlice(String sliceName) {

		Slice slice = findSlice(sliceName);
		List<AgentIdentity> agents = slice.getAgentIDs();

		// Find all the agents for the slice on the network
		ServiceItem[] agtSvcs = cache.lookup(new ServiceItemFilter() {

			public boolean check(ServiceItem item) {
				// TODO Complete method stub for check
				if (item.service instanceof AgentService)
					return true;
				return false;
			}
		}, Integer.MAX_VALUE);
		List<AgentIdentity> checkSpaceAgentList = new ArrayList<AgentIdentity>();
		try {
			for (AgentIdentity agent : agents) {
				for (ServiceItem si : agtSvcs) {
					AgentService agt = (AgentService) si.service;
					AgentServiceAdmin asa = (AgentServiceAdmin) agt.getAdmin();
					if (asa.containsAgent(agent))
						System.out.println("Found agent in service: " + si.serviceID);
				}
			}
		} catch (RemoteException e) {
			// TODO Handle RemoteException
			e.printStackTrace();
		}
	}

	/**
	 * @param sliceName
	 * @return
	 */
	public Slice findSlice(String sliceName) {
		String[] pathParts = sliceName.split("/");
		for (String s : pathParts)
			System.out.println(s);
		// pathParts[0] contains the application Name
		// We do a lookup into the applist to find the sliceID
		Uuid appId = appIDList.get(pathParts[0]);
		if (appId != null) {
			System.out.println("Found application: " + pathParts[0]);
		} else
			System.out.println("Application \"" + pathParts[0] + "\" cannot be found");
		Slice currentSlice = sliceList.get(appId);
		for (int i = 1; i < pathParts.length; i++) {
			Uuid id = currentSlice.getSlice(pathParts[i]);
			if (id != null)
				currentSlice = sliceList.get(id);
			else {
				System.out.println("Slice " + pathParts[i] + " could not be found in " + currentSlice.getName());
				currentSlice = null;
				break;
			}
		}
		return currentSlice;
	}

	public Slice getApplication(String applicationName) {
		Uuid appId = appIDList.get(applicationName);
		return sliceList.get(appId);
	}

	public void createApplication(String applicationName) {
		DefaultApplication defapp = new DefaultApplication();
		defapp.setName(applicationName);

		deploySlice(defapp);

	}

	public void attachSubSlice(Slice parentSlice, Slice subslice) {
		parentSlice.addSlice(subslice.getSliceID(), subslice.getName());
		subslice.setParentSliceID(parentSlice.getSliceID());
	}

	public void store(Slice s) throws IOException {
		sliceStore.storeSlice(s);
	}

	public static void main(String[] args) {
		try {
			System.setSecurityManager(new RMISecurityManager());
			SliceManagerImpl slicer = new SliceManagerImpl(ConfigurationProvider.getInstance(new String[] { "bin/conf/slicemgr.config" }));
			long l = System.currentTimeMillis();
			slicer.createApplication("a" + l);
			Slice s = slicer.getApplication("a" + l);
			Slice b = new DefaultSlice("b");
			Slice c = new DefaultSlice("c");
			Slice d = new DefaultSlice("d", "db", "Database Access Slice");

			slicer.deploySlice(b);
			slicer.deploySlice(c);
			slicer.deploySlice(d);

			slicer.attachSubSlice(c, d);
			slicer.attachSubSlice(b, c);
			slicer.attachSubSlice(s, b);

			slicer.displaySliceInfo("a" + l + "/b/c/d");

			if (args.length == 1) {
				AgentIdentity agt = new AgentIdentity(args[0]);
				slicer.attachAgentToSlice(agt, "a/b/c/d");
			}
			slicer.showSpaceAgents("TestSpace");
			slicer.packSlice("a" + l + "/b/c/d");
			XMLSliceStorage store = new XMLSliceStorage("storage/slices", slicer);
			store.storeSlice(s);
		} catch (ConfigurationException e) {
			// TODO Handle ConfigurationException
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Handle IOException
			e.printStackTrace();
		}

	}

	public void serviceAdded(ServiceDiscoveryEvent event) {
		// TODO Complete method stub for serviceAdded

	}

	public void serviceChanged(ServiceDiscoveryEvent event) {
		// TODO Complete method stub for serviceChanged

	}

	public void serviceRemoved(ServiceDiscoveryEvent event) {
		// TODO Complete method stub for serviceRemoved

	}

	public void createSlice(String slicepath, String newSliceName, SliceType type) {
		// TODO Complete method stub for createSlice
		Slice slice = findSlice(slicepath);
		Slice newSlice;
		switch (type) {
		case ATTRACTOR:
			newSlice = new AttractorSlice();
			break;
		case REDUNDANT:
			newSlice = new RedundantSlice();
			break;
		case REPELLER:
			newSlice = new RepellerSlice();
			break;
		case FACTORY:
		case VIRTUAL:
		default:
			newSlice = new DefaultSlice();
		}
		newSlice.setName(newSliceName);
		attachSubSlice(slice, newSlice);
	}

	/**
	 * getApplicationNames
	 * 
	 * @return String []
	 */
	public java.util.Set getApplicationNames() {
		return appIDList.keySet();
	}

}
