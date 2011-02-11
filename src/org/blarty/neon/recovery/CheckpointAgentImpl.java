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
 * neon : org.jini.projects.neon.agents.standard
 * CheckpointAgentImpl.java Created on 18-Jul-2003
 */

package org.jini.projects.neon.recovery;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.transaction.CannotAbortException;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.UnknownTransactionException;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.event.EventMailbox;
import net.jini.io.MarshalledInstance;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lease.LeaseRenewalService;
import net.jini.space.JavaSpace;
import net.jini.space.JavaSpace05;

import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentConstraints;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.AgentState;
import org.jini.projects.neon.agents.Crypto;
import org.jini.projects.neon.agents.DelegatedAgent;
import org.jini.projects.neon.agents.LocalAgent;
import org.jini.projects.neon.agents.util.AgentConstraintsEntry;
import org.jini.projects.neon.agents.util.AgentEntry;
import org.jini.projects.neon.agents.util.EncryptedAgentConstraintsEntry;
import org.jini.projects.neon.agents.util.EncryptedAgentEntry;
import org.jini.projects.neon.agents.util.EncryptedLocalAgentConstraintsEntry;
import org.jini.projects.neon.agents.util.LocalAgentConstraintsEntry;
import org.jini.projects.neon.callbacks.CallbacksAgent;
import org.jini.projects.neon.dynproxy.FacadeProxy;
import org.jini.projects.neon.dynproxy.NeonProxy;
import org.jini.projects.neon.dynproxy.PojoFacadeProxy;
import org.jini.projects.neon.host.AgentDomain;
import org.jini.projects.neon.host.ManagedDomain;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.neon.host.PrivilegedAgentContext;
import org.jini.projects.neon.service.ServiceAgent;
import org.jini.projects.neon.util.encryption.EncryptionUtils;

import com.sun.jini.constants.TimeConstants;

/**
 * System agent that allows the checkpointing of an agent by storing it into a
 * space. This facilitates host recovery and fault tolerance
 * 
 * @author calum
 */
public class CheckpointAgentImpl extends AbstractAgent implements LocalAgent, CheckpointAgent, Crypto {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1282851639721508033L;

	private Logger checkPointLog;

	private LeaseRenewalManager lrm = new LeaseRenewalManager();

	private String spaceName;

	private boolean useCrypto;

	/**
	 * 
	 */
	public CheckpointAgentImpl() {
		super();
		this.name = "CheckPoint";
		this.namespace = "neon";
	}

	public boolean init() {
		// TODO Complete method stub for init
		checkPointLog = Logger.getLogger("org.jini.projects.neon.checkPoint");
		try {
			spaceName = (String) getAgentConfiguration().getEntry("org.jini.projects.neon.CheckPoint", "spaceName", String.class, "");
			checkPointLog.fine("Checkpoint look for space " + spaceName);
		} catch (Exception ex) {
			System.err.println("Caught Exception: " + ex.getClass().getName() + "; Msg: " + ex.getMessage());
			ex.printStackTrace();
		}

		return true;
	}

	public boolean checkpointAgentSet(List agentList, boolean dumped) {
		System.out.println("Checkpointing agent set");
		try {
			List writeSet = new ArrayList();
			ServiceAgent svcs = (ServiceAgent) context.getAgent("Services");
			System.out.println("Checkpointing to: " + spaceName);
			JavaSpace05 space = (JavaSpace05) svcs.getSingleNamedService(spaceName, JavaSpace05.class);
			TransactionManager tm = (TransactionManager) svcs.getSingleService(TransactionManager.class);
			Transaction.Created txnc = TransactionFactory.create(tm, 20000);
			lrm.renewFor(txnc.lease, 10 * TimeConstants.MINUTES, null);
			for (int i = 0; i < agentList.size(); i++) {
				Agent ag = (Agent) agentList.get(i);
				try {
					AgentConstraints agentCon = ag.getConstraints();
					checkPointLog.finer("Agent type: " + ag.getClass().getName() + "(" + ag.getName() + ")");
					if (agentCon != null)
						checkPointLog.finest("AgentConstraints: " + agentCon.getClass().getName());
					PrivilegedAgentContext pcontext = (PrivilegedAgentContext) this.context;
					Entry constraintsEntry;
					AgentConstraintsEntry ace = null;
					LocalAgentConstraintsEntry local_ace = null;
					if (ag instanceof LocalAgent)
						local_ace = new LocalAgentConstraintsEntry(ag.getConstraints(), getActualAgent(ag).getClass().getName(), ag.getConfigurationLocation(), pcontext.getDomainName(), pcontext.getHostServiceID(), ag.getMetaAttributes(), ag.getName(),
								ag.getNamespace(), ag.getIdentity().getExtendedString(), (dumped ? AgentState.DUMPED.toString() : ag.getAgentState().toString()));
					else
						ace = new AgentConstraintsEntry(ag.getIdentity().getExtendedString(), agentCon, pcontext.getHostServiceID(), (dumped ? AgentState.DUMPED.toString() : ag.getAgentState().toString()), pcontext.getDomainName(),
								ag.getMetaAttributes(), ag.getName(), ag.getNamespace());
					AgentEntry ae = null;

					CallbacksAgent cbi = (CallbacksAgent) context.getAgent("Callback");
					RemoteEventListener callback = cbi.getMyCallback(ag);

					writeTerminateMessages("Output  [" + new Date() + "] (Checkpoint) " + "Callback Requested");
					Agent actual = getActualAgent(ag);
					try {
						if (!(ag instanceof LocalAgent))
							ae = new AgentEntry(ag.getIdentity(), ag.getName(), new MarshalledInstance(actual), null);
					} catch (IOException e1) {
						// TODO Handle IOException
						System.out.println("Error Storing: " + ag.getNamespace() + "." + ag.getName());
						e1.printStackTrace();
					}
					if (callback == null) {
						if (System.getProperty("terminating") != null)
							System.out.println("Output  [" + new Date() + "] (Checkpoint) " + "Callback is null");
					} else
						ae.clientCallback = callback;

					if (space != null) {
						try {

							// Removing any existing savepoint data in the space
							// for this agent.
							try {
								removeExistingAgentEntries(ag, txnc, space);
							} catch (UnusableEntryException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (TransactionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (ag instanceof LocalAgent) {
								if (useCrypto && ((ManagedDomain)getAgentContext()).getSecurityOptions().isEncryptAgentStorage()) {
									try {
										EncryptedLocalAgentConstraintsEntry enc_local_ace = (EncryptedLocalAgentConstraintsEntry) EncryptionUtils.findEncryptedEncryptedVersion(local_ace);
										writeSet.add(enc_local_ace);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else
									writeSet.add(local_ace);

							} else {
								if (useCrypto && ((ManagedDomain)getAgentContext()).getSecurityOptions().isEncryptAgentStorage()) {
									try {
										EncryptedAgentConstraintsEntry enc_ace = (EncryptedAgentConstraintsEntry) EncryptionUtils.findEncryptedEncryptedVersion(ace);
										EncryptedAgentEntry enc_ae = (EncryptedAgentEntry) EncryptionUtils.findEncryptedEncryptedVersion(ae);
										writeSet.add(enc_ace);
										writeSet.add(enc_ae);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else {
									writeSet.add(ace);
									writeSet.add(ae);
								}
							}

							writeTerminateMessages("Output  [" + new Date() + "] (Checkpoint) " + "Agent " + ag.getIdentity() + " stored");

						} catch (RemoteException e) {
							// TODO Handle Remotjava FIleHRaneseException
							e.printStackTrace();

						}
					}
				} catch (NoSuchAgentException e) {
					e.printStackTrace();

					return false;
				} catch (NullPointerException npe) {
					System.out.println("NPE:");
					npe.printStackTrace();
				}

			}
			List durationList = new ArrayList();
			for (int loop = 0; loop < writeSet.size(); loop++) {
				durationList.add(new Long(10 * TimeConstants.MINUTES));
			}
			if (writeSet.isEmpty()) {
				System.out.println("Not writing empty Set..... ");
			} else {
				// System.out.println("Writing agent set [" + writeSet + "
				// items]");
				space.write(writeSet, txnc.transaction, durationList);
			}
			txnc.transaction.commit();
			return true;
		} catch (LeaseDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAgentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransactionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	} /*
		 * (non-Javadoc)
		 * 
		 * @see org.jini.projects.neon.recovery.CheckpointAgent#handleCheckpointAgent(org.jini.projects.neon.agents.Agent)
		 */

	public boolean clearAgentCheckpoints(Agent ag, AgentState state) {
		try {
			checkPointLog.finest("Getting TransactionManager");

			ServiceAgent svcs = (ServiceAgent) context.attachAgent("neon.Services");
			TransactionManager tm = (TransactionManager) svcs.getSingleService(TransactionManager.class);
			System.out.println("Checkpoint=>Services returned value(Should be txnmgr):" + tm.getClass().getName());
			Object o = svcs.getSingleService(JavaSpace.class);
			System.out.println("Checkpoint=>Services returned value(Should be space):" + o.getClass().getName());
			o = svcs.getSingleService(EventMailbox.class);
			System.out.println("Checkpoint=>Services returned value(Should be EventMailbox):" + o.getClass().getName());
			o = svcs.getSingleService(LeaseRenewalService.class);
			System.out.println("Checkpoint=>Services returned value(Should be LeaseRenewalService):" + o.getClass().getName());
			if (true)
				return true;
			checkPointLog.finest("TransactionManager obtained");
			List spaces = null;
			Transaction.Created txnc = null;
			try {
				txnc = TransactionFactory.create(tm, 20000);
				lrm.renewFor(txnc.lease, 10 * TimeConstants.MINUTES, null);
				checkPointLog.finest("Getting Space list");
				spaces = svcs.getNamedService(spaceName, JavaSpace.class);
				context.detachAgent(svcs);
				checkPointLog.finest("Spaces obtained");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (spaces.size() != 0) {
				checkPointLog.finest("Space(s) located");
				JavaSpace s = (JavaSpace) ((ServiceItem) spaces.get(0)).service;
				checkPointLog.finer("Clearing " + state + " checkpoint Information for agent " + ag.getIdentity());
				try {
					// Removing any existing savepoint data in the space for
					// this agent.
					AgentConstraintsEntry existingConstraintsEntry = new AgentConstraintsEntry(ag.getIdentity().getExtendedString(), null, null, AgentState.getStateName(state), null, null, null, null);
					AgentEntry existingAgentEntry = new AgentEntry(ag.getIdentity(), null, null, null);
					Entry results = s.readIfExists(existingConstraintsEntry, txnc.transaction, 5000L);
					if (results != null) {
						results = s.takeIfExists(existingConstraintsEntry, txnc.transaction, 5000L);
						results = s.takeIfExists(existingAgentEntry, txnc.transaction, 5000L);
					} else
						checkPointLog.finest("No " + AgentState.getStateName(state) + " checkpoint information for " + ag.getIdentity());
					txnc.transaction.commit();
					checkPointLog.finer("Agent " + ag.getIdentity() + " " + AgentState.getStateName(state) + " checkpoint information removed");
					return true;
				} catch (RemoteException e) {
					// TODO Handle Remotjava FIleHRaneseException
					e.printStackTrace();
					try {
						txnc.transaction.abort();
					} catch (UnknownTransactionException e2) {
						e2.printStackTrace();
					} catch (CannotAbortException e2) {
						e2.printStackTrace();
					}
				} catch (TransactionException e) {
					e.printStackTrace();
					try {
						txnc.transaction.abort();
					} catch (UnknownTransactionException e2) {
						e2.printStackTrace();
					} catch (CannotAbortException e2) {
						e2.printStackTrace();
					}
				} catch (UnusableEntryException e) {
					e.printStackTrace();
					try {
						txnc.transaction.abort();
					} catch (UnknownTransactionException e2) {
						e2.printStackTrace();
					} catch (CannotAbortException e2) {
						e2.printStackTrace();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					try {
						txnc.transaction.abort();
					} catch (UnknownTransactionException e2) {
						System.out.println("***** UnknownTransaction!! ************");
						e2.printStackTrace();
					} catch (CannotAbortException e2) {
						e2.printStackTrace();
					}
				}
			}
		} catch (NoSuchAgentException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Stores an agent to persistent store Also ensures that any dynamic proxies
	 * are removed before persisting agent to disk
	 */
	public boolean checkpointAgent(Agent ag) throws RemoteException {
		try {
			AgentConstraints agentCon = ag.getConstraints();
			checkPointLog.finer("Agent type: " + ag.getClass().getName() + "(" + ag.getName() + ")");
			if (agentCon != null)
				checkPointLog.finest("AgentConstraints: " + agentCon.getClass().getName());
			PrivilegedAgentContext pcontext = (PrivilegedAgentContext) this.context;
			Entry constraintsEntry;
			AgentConstraintsEntry ace = null;
			LocalAgentConstraintsEntry local_ace = null;
			if (ag instanceof LocalAgent)
				local_ace = new LocalAgentConstraintsEntry(ag.getConstraints(), getActualAgent(ag).getClass().getName(), ag.getConfigurationLocation(), pcontext.getDomainName(), pcontext.getHostServiceID(), ag.getMetaAttributes(), ag.getName(), ag
						.getNamespace(), ag.getIdentity().getExtendedString(), ag.getAgentState().toString());
			else
				ace = new AgentConstraintsEntry(ag.getIdentity().getExtendedString(), agentCon, pcontext.getHostServiceID(), AgentState.getStateName(ag.getAgentState()), pcontext.getDomainName(), ag.getMetaAttributes(), ag.getName(), ag.getNamespace());
			AgentEntry ae = null;
			// Obtain a transactionManager Instance
			// Obtain the agents callback

			try {
				ServiceAgent svcs = (ServiceAgent) context.getAgent("Services");
				writeTerminateMessages("Output  [" + new Date() + "] (Checkpoint) " + "Getting TransactionManager");
				TransactionManager tm = (TransactionManager) svcs.getSingleService(TransactionManager.class);
				writeTerminateMessages("Output  [" + new Date() + "] (Checkpoint) " + "Transaction Manager obtained");
				Transaction.Created txnc = TransactionFactory.create(tm, 20000);
				lrm.renewFor(txnc.lease, 10 * TimeConstants.MINUTES, null);
				writeTerminateMessages("Output  [" + new Date() + "] (Checkpoint) " + "Transaction Created and obtained");

				CallbacksAgent cbi = (CallbacksAgent) context.getAgent("Callback");
				RemoteEventListener callback = cbi.getMyCallback(ag);

				writeTerminateMessages("Output  [" + new Date() + "] (Checkpoint) " + "Callback Requested");
				Agent actual = getActualAgent(ag);
				try {
					if (!(ag instanceof LocalAgent))
						ae = new AgentEntry(ag.getIdentity(), ag.getName(), new MarshalledInstance(actual), null);
				} catch (IOException e1) {
					// TODO Handle IOException
					e1.printStackTrace();
				}
				if (callback == null) {
					if (System.getProperty("terminating") != null)
						System.out.println("Output  [" + new Date() + "] (Checkpoint) " + "Callback is null");
				} else
					ae.clientCallback = callback;
				ArrayList spaces = (ArrayList) svcs.getNamedService(spaceName, JavaSpace.class);
				if (spaces.size() != 0) {
					writeTerminateMessages("Output  [" + new Date() + "] (Checkpoint) " + "Space(s) located");
					JavaSpace s = (JavaSpace) ((ServiceItem) spaces.get(0)).service;
					writeTerminateMessages("Output  [" + new Date() + "] (Checkpoint) " + "Beginning Storage to space for agent " + ag.getIdentity());
					try {
						// Removing any existing savepoint data in the space
						// for this agent.
						removeExistingAgentEntries(ag, txnc, s);
						if (ag instanceof LocalAgent)
							s.write(local_ace, txnc.transaction, 5 * TimeConstants.MINUTES);
						else {
							s.write(ace, txnc.transaction, 5 * TimeConstants.MINUTES);
							s.write(ae, txnc.transaction, 5 * TimeConstants.MINUTES);
						}
						txnc.transaction.commit();
						writeTerminateMessages("Output  [" + new Date() + "] (Checkpoint) " + "Agent " + ag.getIdentity() + " stored");
						return true;
					} catch (RemoteException e) {
						// TODO Handle Remotjava FIleHRaneseException
						e.printStackTrace();
						try {
							txnc.transaction.abort();
						} catch (UnknownTransactionException e2) {
							e2.printStackTrace();
						} catch (CannotAbortException e2) {
							e2.printStackTrace();
						}
					} catch (TransactionException e) {
						e.printStackTrace();
						try {
							txnc.transaction.abort();
						} catch (UnknownTransactionException e2) {
							e2.printStackTrace();
						} catch (CannotAbortException e2) {
							e2.printStackTrace();
						}
					} catch (UnusableEntryException e) {
						e.printStackTrace();
						try {
							txnc.transaction.abort();
						} catch (UnknownTransactionException e2) {
							e2.printStackTrace();
						} catch (CannotAbortException e2) {
							e2.printStackTrace();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
						try {
							txnc.transaction.abort();
						} catch (UnknownTransactionException e2) {
							System.out.println("***** UnknownTransaction!! ************");
							e2.printStackTrace();
						} catch (CannotAbortException e2) {
							e2.printStackTrace();
						}
					}
				}
			} catch (NoSuchAgentException e) {
				e.printStackTrace();
			} catch (LeaseDeniedException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			return false;
		} catch (NullPointerException npe) {
			System.out.println("NPE:");
			npe.printStackTrace();
		}
		return false;
	}

	private void removeExistingAgentEntries(Agent ag, Transaction.Created txnc, JavaSpace s) throws UnusableEntryException, TransactionException, InterruptedException, RemoteException {
		Entry existingConstraintsEntry;

		if (!(ag instanceof LocalAgent))
			existingConstraintsEntry = new AgentConstraintsEntry(ag.getIdentity().getExtendedString(), null, null, null, null, null, null, null);
		else
			existingConstraintsEntry = new LocalAgentConstraintsEntry(null, null, null, null, null, null, null, null, ag.getIdentity().getExtendedString(), null);
		AgentEntry existingAgentEntry = new AgentEntry(ag.getIdentity(), null, null, null);

		Entry results = s.takeIfExists(existingConstraintsEntry, null, 5000L);
		if (results != null) {

			results = s.takeIfExists(existingAgentEntry, null, 5000L);
		}
	}

	private Agent getActualAgent(Agent ag) {
		Agent actual = null;
		if (ag instanceof Proxy) {
			InvocationHandler handler = Proxy.getInvocationHandler(ag);
			if (handler instanceof NeonProxy)
				actual = ((NeonProxy) handler).getReceiver();
			if (actual instanceof Proxy) {
				System.out.println("Got POJO Agent");
				InvocationHandler pojoHandler = Proxy.getInvocationHandler(actual);
				System.out.println(pojoHandler.getClass().getName());
				if (pojoHandler instanceof PojoFacadeProxy) {
					System.out.println("Casting to DelegatedAgent");
					actual = ((PojoFacadeProxy) pojoHandler).getReceiver();
					System.out.println("Narrowed to: " + actual.getClass().getName());

				}
			}
		} else
			actual = ag;
		return actual;
	}

	/**
	 * reloadAgent
	 * 
	 * @param aAgentIdentity
	 */
	public void reloadAgent(AgentIdentity aAgentIdentity) {

		try {
			ServiceAgent svcs = (ServiceAgent) context.getAgent("Services");

			TransactionManager tm = (TransactionManager) svcs.getSingleService(TransactionManager.class);

			Transaction.Created txnc = TransactionFactory.create(tm, 20000);
			lrm.renewFor(txnc.lease, 10 * TimeConstants.MINUTES, null);
			Transaction txn = txnc.transaction;
			ArrayList spaces = (ArrayList) svcs.getNamedService(spaceName, JavaSpace.class);
			if (spaces.size() != 0) {
				JavaSpace space = (JavaSpace) ((ServiceItem) spaces.get(0)).service;

				AgentConstraintsEntry ace = new AgentConstraintsEntry(aAgentIdentity.getExtendedString(), null, null, null, null, null, null, null);
				try {
					Object entry = null;
					do {
						entry = space.takeIfExists(ace, txn, 5000);
						if (entry != null) {

							AgentConstraintsEntry dumpedEntry = (AgentConstraintsEntry) entry;
							AgentIdentity actualAgent = new AgentIdentity(dumpedEntry.referentAgentIdentity);
							if (dumpedEntry.agentConstraints == null || ((AgentDomain) getAgentContext()).canAccept(dumpedEntry.agentConstraints)) {
								AgentEntry agent = (AgentEntry) space.takeIfExists(new AgentEntry(actualAgent, null, null, null), txn, 1000);
								try {
									Agent toDeploy = (Agent) agent.encapsulatedAgent.get(false);
									((AgentDomain) getAgentContext()).deployAgent(toDeploy, agent.clientCallback);
									getAgentLogger().finer("Redeployed " + toDeploy.getName() + ": " + toDeploy.getIdentity().getID() + " into the " + getAgentContext().getDomainName() + " domain");
									txn.commit();
								} catch (IOException e1) {
									// TODO Handle IOException
									e1.printStackTrace();
								} catch (ClassNotFoundException e1) {
									// TODO Handle ClassNotFoundException
									e1.printStackTrace();
								}
							}
						}
					} while (entry != null);
				} catch (RemoteException e) {
					// TODO Handle RemoteException
					e.printStackTrace();
				} catch (UnusableEntryException e) {
					// TODO Handle UnusableEntryException
					e.printStackTrace();
				} catch (TransactionException e) {
					// TODO Handle TransactionException
					txn.abort();
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Handle InterruptedException
					e.printStackTrace();
				}
			}
		} catch (Exception ex) {
			System.err.println("Caught Exception: " + ex.getClass().getName() + "; Msg: " + ex.getMessage());
			ex.printStackTrace();
		}

	}

	public void writeTerminateMessages(String mesg) {
		if (System.getProperty("terminating") != null)
			System.out.println(mesg);
	}

	public void useCrypto(boolean cryptoOn) {
		// TODO Auto-generated method stub
		useCrypto = cryptoOn;
	}
}
