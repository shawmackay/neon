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
 * neon : org.jini.projects.neon.recovery
 * HostRecover.java
 * Created on 08-Sep-2003
 */
package org.jini.projects.neon.recovery;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.lookup.ServiceID;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.entry.UnusableEntriesException;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;
import net.jini.lease.LeaseRenewalManager;
import net.jini.space.JavaSpace;
import net.jini.space.JavaSpace05;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.AgentState;
import org.jini.projects.neon.agents.DelegatedAgent;
import org.jini.projects.neon.agents.util.AgentConstraintsEntry;
import org.jini.projects.neon.agents.util.AgentEntry;
import org.jini.projects.neon.agents.util.EncryptedAgentConstraintsEntry;
import org.jini.projects.neon.agents.util.EncryptedAgentEntry;
import org.jini.projects.neon.agents.util.EncryptedLocalAgentConstraintsEntry;
import org.jini.projects.neon.agents.util.LocalAgentConstraintsEntry;
import org.jini.projects.neon.agents.util.meta.AgentDependencies;
import org.jini.projects.neon.host.AgentDomain;
import org.jini.projects.neon.host.AgentRegistry;
import org.jini.projects.neon.host.ManagedDomain;
import org.jini.projects.neon.service.ServiceAgent;
import org.jini.projects.neon.util.encryption.EncryptionUtils;

import com.sun.jini.constants.TimeConstants;

/**
 * @author calum
 */
public class HostRecover {
	AgentDomain ah;

	private Collection agentConstraintReferences = null;

	// private Collection fullAgentReferences = null;

	private JavaSpace05 space;

	Logger recLog = Logger.getLogger("org.jini.projects.neon.agents.standard.Recovery");

	private TransactionManager txMgr;

	private LeaseRenewalManager lrm = new LeaseRenewalManager();

	private Map actualAgentMap;

	private List returnList;

	private int numberToTake;

	private int timeToWait;

	/**
	 * 
	 */
	public HostRecover(AgentDomain ah, JavaSpace05 space, TransactionManager txMgr, int numberToTake, int timeToWait) {
		super();
		// TODO Complete constructor stub for HostRecover
		this.ah = ah;
		this.space = space;
		this.txMgr = txMgr;
		this.numberToTake = numberToTake;
		this.timeToWait = timeToWait;
	}

	public void getCheckPointedAgents() {

		ServiceID Sid = ah.getAgentServiceID();
		Uuid uid = UuidFactory.create(Sid.getMostSignificantBits(), Sid.getLeastSignificantBits());
		AgentConstraintsEntry ace = new AgentConstraintsEntry(null, null, uid, AgentState.getStateName(AgentState.DUMPED), ah.getDomainName(), null, null, null);
		LocalAgentConstraintsEntry local_ace = new LocalAgentConstraintsEntry(null, null, null, ah.getDomainName(), uid, null, null, null, null, AgentState.getStateName(AgentState.DUMPED));
		reloadAgents(ace, local_ace);
	}

	private void reloadAgents(AgentConstraintsEntry ace, LocalAgentConstraintsEntry local_ace) {
		try {

			recLog.finer("Checking for agents.....stored from the " + ah.getDomainName() + " domain");
			Map agentMap = new HashMap();
			Map idMap = new HashMap();

			int numReadOnCurrentPass = -1;
			int currentPassPriority = 0;
			long start = System.currentTimeMillis();

			while (numReadOnCurrentPass != 0) {
				recLog.fine("Looking at Priority " + currentPassPriority + " agents");
				Transaction.Created txf = TransactionFactory.create(txMgr, 50000);
				lrm.renewFor(txf.lease, Lease.FOREVER, null);
				returnList = new ArrayList();

				try {
					List constraintsTempl = new ArrayList();
					ace.loadingPriority = currentPassPriority;
					local_ace.loadingPriority = currentPassPriority;
					constraintsTempl.add(local_ace);
					constraintsTempl.add(ace);
					if (((ManagedDomain) ah).getSecurityOptions().isEncryptAgentStorage()) {
						try {
							EncryptedLocalAgentConstraintsEntry enc_local_ace = new EncryptedLocalAgentConstraintsEntry();
							EncryptionUtils.initialiseEncryptedVersion(local_ace, false, enc_local_ace);
							EncryptedAgentConstraintsEntry enc_ace = new EncryptedAgentConstraintsEntry();
							EncryptionUtils.initialiseEncryptedVersion(ace, false, enc_ace);
							enc_ace.loadingPriority = currentPassPriority;
							enc_local_ace.loadingPriority = currentPassPriority;
					
							constraintsTempl.add(enc_local_ace);
							constraintsTempl.add(enc_ace);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					// List agentTempl = new ArrayList();
					// FIXME add a ServiceID field to AgentEntry so
					// we don't have to
					// pull
					// all agents out of the space
					// agentTempl.add(new AgentEntry(null, null, null, null));
					recLog.fine("Loading " + numberToTake + " full agent entries");
					agentConstraintReferences = space.take(constraintsTempl, txf.transaction, 1000, numberToTake);

					// System.out.println("No. of Full agent references = " +
					// fullAgentReferences.size());
					long deployStart = System.currentTimeMillis();
					numReadOnCurrentPass = agentConstraintReferences.size();
					recLog.fine("Read " + numReadOnCurrentPass + " agents in this reading of priority " + currentPassPriority);
					while (agentConstraintReferences.size() > 0) {

						// recoverStoredAgents(local_ace);
						// recoverStoredAgents(ace);

						List orderedRecoveryList = new ArrayList();
						Ranking ranking = new Ranking();
						ranking.createRank(0);
						Collection scratchList = agentConstraintReferences;
						List postList = new ArrayList();
						// Do initial ranking
						int iteration = 0;
						int listsize = scratchList.size();
						do {
							listsize = scratchList.size();
							// System.out.println("Rank loop: " +
							// iteration++);

							// Change this to handle Encrypted Entries!!!!!!
							rankAgents(ranking, scratchList, postList, agentMap, idMap);
							dumpPostList(postList);
							scratchList = postList;
							postList = new ArrayList();

						} while (scratchList.size() < listsize);

						// System.out.println(ranking);

						HashMap map = new HashMap();
						// System.out.println("IDList: " + agentMap);
						for (int i = 0; i < ranking.getNumberofRanks(); i++) {
							Rank r = ranking.getRankFor(i);
							List l = r.getAgentsInRank();
							for (Iterator iter = l.iterator(); iter.hasNext();) {
								String agentName = (String) iter.next();
								// System.out.println("Deploying
								// agent type: " + agentName);
								List idList = (List) agentMap.get(agentName);

								for (Iterator idIter = idList.iterator(); idIter.hasNext();) {
									AgentIdentity agentID = new AgentIdentity((String) idIter.next());
									Object o = idMap.get(agentID.getExtendedString());
									if (o instanceof EncryptedLocalAgentConstraintsEntry) {
										try {
											o = (LocalAgentConstraintsEntry) EncryptionUtils.decryptObjectWithDefaultKey(((EncryptedLocalAgentConstraintsEntry) o).getEncryptedObject().getEncryptedBytes());
										} catch (Exception ex) {
											ex.printStackTrace();
										}
									}

									if (o instanceof EncryptedAgentConstraintsEntry) {
										try {
											o = (AgentConstraintsEntry) EncryptionUtils.decryptObjectWithDefaultKey(((EncryptedAgentConstraintsEntry) o).getEncryptedObject().getEncryptedBytes());
										} catch (Exception ex) {
											ex.printStackTrace();
										}
									}

									if (o instanceof AgentConstraintsEntry) {
										try {
											extractAgentsFromSpace(o, txf.transaction, agentID);
										} catch (UnusableEntryException e) {
											e.printStackTrace();
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									}
									if (o instanceof LocalAgentConstraintsEntry) {
										try {
											extractLocalAgentsFromSpace(o);
										} catch (UnusableEntryException e) {
											e.printStackTrace();
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									}
								}
							}
							try {
								if (i < ranking.getNumberofRanks() - 1) {
									recLog.fine("Waiting for " + timeToWait + " seconds: Deployed rank" + i);
									Thread.sleep(timeToWait * TimeConstants.SECONDS);
								}
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
						if (scratchList.size() > 0) {
							this.recLog.warning("There are " + scratchList.size() + " agents that could not be deployed because of dependencies....returning to space, and increasing loading priority");
							for (Object o : scratchList) {
								System.out.println("Object: " + o);
								if (o instanceof LocalAgentConstraintsEntry)
									((LocalAgentConstraintsEntry) o).loadingPriority++;
								if (o instanceof AgentConstraintsEntry)
									((AgentConstraintsEntry) o).loadingPriority++;
								if (o instanceof EncryptedLocalAgentConstraintsEntry)
									((EncryptedLocalAgentConstraintsEntry) o).loadingPriority++;
								if (o instanceof EncryptedAgentConstraintsEntry)
									((EncryptedAgentConstraintsEntry) o).loadingPriority++;
							}

						}
						// System.out.println("No. of Full agent references = "
						// +
						// fullAgentReferences.size());
						// System.out.println("No. of Full agent references (in
						// actualAgentMap)= " + actualAgentMap.size());
						// recLog.info("Full Agent Map size: " +
						// actualAgentMap.size());
						if (scratchList.size() > 0) {

							List durationList = new ArrayList();
							for (Object o : scratchList)
								durationList.add(new Long(Lease.FOREVER));

							space.write(new ArrayList(scratchList), txf.transaction, durationList);

						}
						// if (actualAgentMap.size() > 0) {
						//
						// for (Object o : actualAgentMap.values()) {
						// recLog.info("Returning agent: " + o);
						// returnList.add((Entry) o);
						// }
						// List returnDurationList = new ArrayList();
						// for (Object o : returnList)
						// returnDurationList.add(new Long(Lease.FOREVER));
						//
						// space.write(returnList, txf.transaction,
						// returnDurationList);
						// }
						agentConstraintReferences = space.take(constraintsTempl, txf.transaction, 1000, numberToTake);
						// fullAgentReferences = space.take(agentTempl,
						// txf.transaction, 5000, numberToTake);
						// System.out.println("No. of Full agent references at
						// loop
						// end= " + fullAgentReferences.size());
					}
					currentPassPriority++;
					txf.transaction.commit();
					recLog.fine("Commited recovery transaction");
					long end = System.currentTimeMillis();
					recLog.info("Cycle Recovery took " + (end - start) + "ms (" + ((end - start) / 1000) + "s)");

					recLog.info("Redeployment only took " + (end - deployStart) + "ms (" + ((end - deployStart) / 1000) + "s)");
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnusableEntriesException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TransactionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (LeaseDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void dumpPostList(List postList) {
		// TODO Auto-generated method stub
		recLog.warning("POSTLIST OUTPUT\nThe following agents could not be deployed in the current rank set\n");
		for (Object o : postList) {
			System.out.println("\t" + o.toString());
		}
	}

	private void rankAgents(Ranking ranking, Collection scratchList, List postList, Map agentMap, Map idMap) {
		skipIter: for (Iterator iter = scratchList.iterator(); iter.hasNext();) {
			Entry ent = (Entry) iter.next();
			List l = null;
			String name = null;
			String namespace = null;
			int highestRankLevel = 0;

			try {
				if (ent instanceof EncryptedLocalAgentConstraintsEntry)
					ent = (Entry) EncryptionUtils.decryptObjectWithDefaultKey(((EncryptedLocalAgentConstraintsEntry) ent).getEncryptedObject().getEncryptedBytes());
				if (ent instanceof EncryptedAgentConstraintsEntry)
					ent = (Entry) EncryptionUtils.decryptObjectWithDefaultKey(((EncryptedAgentConstraintsEntry) ent).getEncryptedObject().getEncryptedBytes());
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (ent instanceof LocalAgentConstraintsEntry) {

				LocalAgentConstraintsEntry lacE = (LocalAgentConstraintsEntry) ent;
				l = lacE.meta.getMetaOfType(AgentDependencies.class);

				name = lacE.name;
				namespace = lacE.namespace;
				if (!agentMap.containsKey(namespace + "." + name)) {
					List idList = new ArrayList();
					agentMap.put(namespace + "." + name, idList);

				}
				List idList = (ArrayList) agentMap.get(namespace + "." + name);
				idList.add(lacE.referentAgentIdentity);
				idMap.put(lacE.referentAgentIdentity, lacE);
			}
			if (ent instanceof AgentConstraintsEntry) {
				AgentConstraintsEntry acE = (AgentConstraintsEntry) ent;
				l = acE.meta.getMetaOfType(AgentDependencies.class);
				// .println("Full Scale Agent: " + acE.namespace
				// + "." + acE.name + ": " + l.size() + "
				// dependent agents");
				name = acE.name;
				namespace = acE.namespace;
				if (!agentMap.containsKey(namespace + "." + name)) {
					List idList = new ArrayList();
					agentMap.put(namespace + "." + name, idList);
				}
				List idList = (ArrayList) agentMap.get(namespace + "." + name);
				idList.add(acE.referentAgentIdentity);
				idMap.put(acE.referentAgentIdentity, acE);
			}
			if (l.size() != 0) {
				AgentDependencies depends = (AgentDependencies) l.get(0);
				Set s = depends.getDependencies();

				if (s.size() == 0) {
					Rank rank = ranking.getRankFor(0);
					rank.addNameSpaceToRank(namespace + "." + name);
				} else {
					for (Iterator dependsIter = s.iterator(); dependsIter.hasNext();) {
						String dependsname = (String) dependsIter.next();
						if (dependsname.indexOf('.') == -1)
							dependsname = namespace + "." + dependsname;
						int level = ranking.getRankLevelFor(dependsname);

						if (level > highestRankLevel)
							highestRankLevel = level;
						if (level == -1) {
							// System.out.println("Could
							// not find level for "
							// + namespace + "." +
							// name);
							recLog.fine("Current rank set does not contain dependent agent: '" + dependsname + "' for agent '" + namespace + "." + name + "'");
							ManagedDomain mgdDomain = (ManagedDomain) ah;
							AgentRegistry reg = mgdDomain.getRegistry();
							if (reg.contains(dependsname)) {
								recLog.finest("Dependent agent is already deployed");
								level = highestRankLevel;
							} else {
								postList.add(ent);
								continue skipIter;
							}
						}
					}
					Rank rank = ranking.getRankFor(highestRankLevel + 1);
					recLog.fine("Adding [" + namespace + "." + name + "] to rankings " + (highestRankLevel + 1));
					rank.addNameSpaceToRank(namespace + "." + name);
				}
			} else {
				// System.out.println("No dependencies
				// registered for " + namespace + "." + name +
				// ", adding at Level 0");
				Rank rank = ranking.getRankFor(0);
				rank.addNameSpaceToRank(namespace + "." + name);
			}
		}

	}

	private Map buildAgentLookupTable(Collection c) {
		Map agentMap = new HashMap();
		for (Iterator iter = c.iterator(); iter.hasNext();) {
			AgentEntry entry = (AgentEntry) iter.next();
			agentMap.put(entry.agentID, entry);
		}
		return agentMap;
	}

	private void extractAgentsFromSpace(Object entry, Transaction txf, AgentIdentity id) throws UnusableEntryException, TransactionException, InterruptedException, RemoteException {
		final AgentConstraintsEntry dumpedEntry = (AgentConstraintsEntry) entry;
		AgentEntry findAgent = new AgentEntry(id, null, null, null);
		AgentEntry found = (AgentEntry) space.take(findAgent, txf, 2000);

		if (found == null && ((ManagedDomain) ah).getSecurityOptions().isEncryptAgentStorage()) {
			recLog.fine("Looking for Encrypted Agent Data");
			try {
				EncryptedAgentEntry enc_findAgent = (EncryptedAgentEntry) EncryptionUtils.findEncryptedEncryptedVersion(findAgent);
				if (enc_findAgent != null)
					found = (AgentEntry) EncryptionUtils.decryptObjectWithDefaultKey(enc_findAgent.getEncryptedObject().getEncryptedBytes());
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		// AgentIdentity actualAgent = dumpedEntry.referentAgentIdentity;
		if (found != null && (dumpedEntry.agentConstraints == null || ah.canAccept(dumpedEntry.agentConstraints))) {
			final AgentEntry agent = found;
			try {

				final Agent toDeploy = (Agent) agent.encapsulatedAgent.get(false);
				recLog.info("Redeploying " + toDeploy.getName() + ": " + toDeploy.getIdentity().getID() + " into the " + ah.getDomainName() + " domain");
				if (toDeploy instanceof DelegatedAgent)
					((DelegatedAgent) toDeploy).reconnect();
				ah.deployAgent(toDeploy, agent.clientCallback);
				toDeploy.setAgentState(AgentState.stateFor(dumpedEntry.state));
				// actualAgentMap.remove(toDeploy.getIdentity());
				recLog.info("Removed " + toDeploy.getIdentity() + " from Full Agent Collection");
			} catch (IOException e1) {
				// TODO Handle IOException
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				// TODO Handle ClassNotFoundException
				e1.printStackTrace();
			}
		} else if (found != null && !(ah.canAccept(dumpedEntry.agentConstraints))) {
			recLog.warning("Agent " + dumpedEntry.namespace + "." + dumpedEntry.name + "(" + dumpedEntry.referentAgentIdentity + ") cannot be deployed because of constraints - putting full agent back to space");
			space.write(found, txf, 2 * TimeConstants.MINUTES);
		}
	}

	private void extractLocalAgentsFromSpace(Object entry) throws UnusableEntryException, TransactionException, InterruptedException, RemoteException {
		final LocalAgentConstraintsEntry dumpedEntry = (LocalAgentConstraintsEntry) entry;
		AgentIdentity actualAgent = new AgentIdentity(dumpedEntry.referentAgentIdentity);
		try {
			Class cl = Class.forName(dumpedEntry.className);
			Object o = cl.newInstance();
			if (!(ah.canAccept(dumpedEntry.agentConstraints))) {
				recLog.warning("Agent " + dumpedEntry.namespace + "." + dumpedEntry.name + "(" + dumpedEntry.referentAgentIdentity + ") cannot be deployed because of constraints");
				returnList.add(dumpedEntry);
			} else {
				if (o instanceof Agent) {
					final Agent ag = (Agent) o;
					ag.setIdentity(actualAgent);
					ag.setMetaAttributes(dumpedEntry.meta);
					if (dumpedEntry.configurationURL != null)
						ag.setConfigurationLocation(new URL(dumpedEntry.configurationURL));
					if (dumpedEntry.agentConstraints != null)
						ag.setConstraints(dumpedEntry.agentConstraints);
					Thread t = new Thread(new Runnable() {
						public void run() {
							ah.deployAgent(ag);
							// ag.setAgentState(AgentState.stateFor(dumpedEntry.state));
						}
					});
					t.start();

				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getCheckPointedAgentsForService(ServiceID Sid) {
		try {
			recLog.info("Waiting for 30secs before attempting reload");
			Thread.sleep(30 * 1000);
		} catch (Exception ex) {

		}
		Uuid uid = UuidFactory.create(Sid.getMostSignificantBits(), Sid.getLeastSignificantBits());
		AgentConstraintsEntry ace = new AgentConstraintsEntry(null, null, uid, AgentState.getStateName(AgentState.DUMPED), ah.getDomainName(), null, null, null);
		LocalAgentConstraintsEntry local_ace = new LocalAgentConstraintsEntry(null, null, null, ah.getDomainName(), uid, null, null, null, null, AgentState.getStateName(AgentState.DUMPED));
		reloadAgents(ace, local_ace);

	}

	class Ranking {
		private TreeMap rankTable = new TreeMap();

		public void createRank(int level) {
			rankTable.put(level, new Rank());
		}

		public Rank getRankFor(String namespace) {
			for (Iterator iter = rankTable.entrySet().iterator(); iter.hasNext();) {
				Map.Entry ent = (Map.Entry) iter.next();
				if (((Rank) ent.getValue()).containsNamespace(namespace))
					return (Rank) ent.getValue();
			}
			return null;
		}

		public Rank getRankFor(int level) {
			if (!rankTable.containsKey(level))
				createRank(level);
			return (Rank) rankTable.get(level);
		}

		public int getRankLevelFor(String namespace) {
			for (Iterator iter = rankTable.entrySet().iterator(); iter.hasNext();) {
				Map.Entry ent = (Map.Entry) iter.next();
				if (((Rank) ent.getValue()).containsNamespace(namespace))
					return ((Integer) ent.getKey()).intValue();
			}
			return -1;
		}

		public int getNumberofRanks() {
			return rankTable.size();
		}

		public String toString() {
			StringBuffer buffer = new StringBuffer();
			for (Iterator iter = rankTable.entrySet().iterator(); iter.hasNext();) {
				Map.Entry entry = (Map.Entry) iter.next();
				buffer.append("Rank " + entry.getKey() + " => " + entry.getValue() + "\n");
			}
			return buffer.toString();
		}
	}

	class Rank {
		private ArrayList agentsOfRank = new ArrayList();

		public void addNameSpaceToRank(String namespace) {
			if (!agentsOfRank.contains(namespace))
				agentsOfRank.add(namespace);
		}

		public boolean containsNamespace(String namespace) {
			return agentsOfRank.contains(namespace);
		}

		public List getAgentsInRank() {
			return this.agentsOfRank;
		}

		public String toString() {
			return agentsOfRank.toString();
		}
	}

}
