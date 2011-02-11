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
 * neon : org.jini.projects.neon.dynproxy.invoker
 * PropagatedTransactionDelegate.java Created on 11-Nov-2003
 *PropagatedTransactionDelegate
 */

package org.jini.projects.neon.dynproxy.invoker;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import net.jini.core.transaction.CannotJoinException;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.core.transaction.UnknownTransactionException;
import net.jini.core.transaction.server.CrashCountException;
import net.jini.core.transaction.server.ServerTransaction;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;

import org.jini.glyph.chalice.DefaultExporterManager;
import org.jini.glyph.chalice.ProxyPair;
import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.AgentState;
import org.jini.projects.neon.agents.RemoteAgentState;
import org.jini.projects.neon.collaboration.Collaborative;
import org.jini.projects.neon.dynproxy.AgentProxyInfo;
import org.jini.projects.neon.dynproxy.CollaborationRemoteProxy;
import org.jini.projects.neon.host.AgentContext;
import org.jini.projects.neon.host.ManagedDomain;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.neon.host.PrivilegedAgentContext;
import org.jini.projects.neon.host.PrivilegedAsyncAgentEnvironment;
import org.jini.projects.neon.host.transactions.NonTransactionalResource;
import org.jini.projects.neon.host.transactions.RemoteTransactionalResource;
import org.jini.projects.neon.host.transactions.TransactionAccessor;
import org.jini.projects.neon.host.transactions.TransactionAgent;
import org.jini.projects.neon.host.transactions.TransactionBlackBox;
import org.jini.projects.neon.host.transactions.TransactionalResource;
import org.jini.projects.neon.host.transactions.TransactionalResourceAdapter;
import org.jini.projects.zenith.endpoints.Producer;

/**
 * Handles attaching and maintaining whether an agent is currently under the
 * scope of a transaction or not, including, for those agents that implement
 * <code>TransactionAccessor</code> informing the agent of the jini
 * transaction
 * 
 * @author calum
 */
public class PropagatedRemoteTransactionDelegate implements InvokerDelegate {
	ManagedDomain domain;
	Uuid transaction;
	String managerAndTxnRef;
	Logger log = Logger.getLogger("org.jini.projects.neon.dynproxy.invoker");

	public PropagatedRemoteTransactionDelegate() {

	}

	// URGENT NEED TO DECOUPLE THE INVOKE DELEGATE METHOD!!!!
	/*
	 * @see org.jini.projects.neon.dynproxy.invoker.InvokerDelegate#invokeDelegate(java.lang.reflect.Method,
	 *      java.lang.Object, java.lang.Object, java.lang.Object[])
	 */
	public Object invokeDelegate(Method m, Object source, Object origionalObject, Object receiver, Object[] args) throws Throwable {

		if (domain == null) {
			Agent ag = (Agent) receiver;
			domain = (ManagedDomain) ag.getAgentContext();
			// domain =(ManagedDomain) ((Agent) receiver).getAgentContext();
		}
		if (m.getName().equals("attachRemoteTransaction")) {
			String managerAndID = (String) args[0];
			String[] parts = managerAndID.split(":");
			AgentIdentity managerID = new AgentIdentity(parts[0]);
			Uuid txnId = UuidFactory.create(parts[1]);
			Transaction t = (Transaction) args[1];
			TransactionAgent txnAg = (TransactionAgent) ((PrivilegedAgentContext) domain).getAgent(managerID);

			log.finest("Being requested to attach local transaction: " + txnId + " to global transaction " + ((ServerTransaction) t).id);
			txnAg.attachTransaction((ServerTransaction) t, txnId);
			log.finest("Transaction attached");

		}

		if (m.getName().equals("setRemoteTransaction") || m.getName().equals("attachRemoteTransaction")) {
			doRemoteSetAttach(source, receiver, args);

		}
		if (m.getName().equals("getRemoteTransaction")) {
			// synchronized (this) {
			// if(transaction==null)
			// System.out.println("TXN IS NULL!!!!");
			return managerAndTxnRef;
			// }
		}
		if (m.getName().equals("inRemoteTransaction"))
			// synchronized (this) {
			return (transaction != null);
		// }
		if (m.getName().equals("clearRemoteTransaction")) {

			log.finer("Transaction data clearing (Remotely) for " + receiver + "; Txn: " + transaction);
			transaction = null;
			managerAndTxnRef = null;

			Agent a = (Agent) receiver;
			if(a instanceof TransactionAccessor)
				((TransactionAccessor) a).setGlobalTransaction(null);
			log.finest("Clearing Agent (Remotely): " + a.getNamespace() + "." + a.getName() + "; ID: " + a.getIdentity() + ": " +receiver);
			
			synchronized (a) {
                log.finest("Unlocking Agent state: " + a.getNamespace() + "." + a.getName() + "; ID: " + a.getIdentity());
                AgentState prevState = a.getSecondaryState();
                if (prevState.equals(AgentState.BUSY))
                    a.setAgentState(AgentState.AVAILABLE);
                else
                    a.setAgentState(prevState);
            }
			return null;
		}

		if (m.getName().equals("attachTransaction")) {
			String managerAndID = (String) args[0];
			String[] parts = managerAndID.split(":");
			AgentIdentity managerID = new AgentIdentity(parts[0]);
			Uuid txnId = UuidFactory.create(parts[1]);
			Transaction t = (Transaction) args[1];
			TransactionAgent txnAg = (TransactionAgent) ((PrivilegedAgentContext) domain).getAgent(managerID);
			if (txnAg == null)
				txnAg = (TransactionAgent) domain.getRegistry().getAgent("neon.Transaction");
			log.finer("Being requested to attach local transaction: " + txnId + " to global transaction " + ((ServerTransaction) t).id);
			txnAg.attachTransaction((ServerTransaction) t, txnId);
			log.finest("Transaction attached");

		}

		if (m.getName().equals("setTransaction") || m.getName().equals("attachTransaction")) {
			doSetAttach(source, receiver, args);

		}
		if (m.getName().equals("getTransaction")) {
			// synchronized (this) {
			// if(transaction==null)
			// System.out.println("TXN IS NULL!!!!");
			return managerAndTxnRef;
			// }
		}
		if (m.getName().equals("inTransaction"))
			// synchronized (this) {
			return (transaction != null);
		// }
		if (m.getName().equals("clearTransaction")) {

			log.info("(Remote)Transaction data cleared for " + receiver + "; Txn: " + transaction);
			transaction = null;
			managerAndTxnRef = null;
			CountDownLatch latch = (CountDownLatch) args[0];
			latch.countDown();
			Agent a = (Agent) receiver;
			if(a instanceof TransactionAccessor)
				((TransactionAccessor) a).setGlobalTransaction(null);
			log.info("Clearing Agent: " + a.getNamespace() + "." + a.getName() + "; ID: " + a.getIdentity() + ": " + receiver);
			synchronized (a) {
                log.info("Unlocking Agent state: " + a.getNamespace() + "." + a.getName() + "; ID: " + a.getIdentity());
                AgentState prevState = a.getSecondaryState();
                if (prevState.equals(AgentState.BUSY))
                    a.setAgentState(AgentState.AVAILABLE);
                else
                    a.setAgentState(prevState);
            }
			return null;
		}

		return null;

	}

	/**
	 * @param source
	 * @param receiver
	 * @param args
	 * @throws NoSuchAgentException
	 * @throws TransactionException
	 */
	private void doRemoteSetAttach(Object source, Object receiver, Object[] args) throws NoSuchAgentException, TransactionException, RemoteException {
		Agent ag = (Agent) receiver;
		String managerAndID = (String) args[0];
		managerAndTxnRef = managerAndID;
		String[] parts = managerAndID.split(":");
		AgentIdentity managerID = new AgentIdentity(parts[0]);
		Uuid newtxn = UuidFactory.create(parts[1]);
		Object gotAgent = ((AgentContext) domain).getAgent(managerID);
		Class[] intfs = gotAgent.getClass().getInterfaces();
//		System.out.println("Got an agent for the transaction implementing:");
//		for (Class agClass : intfs) {
//			System.out.println("\t" + agClass.getName());
//		}
		TransactionAgent txnAg = (TransactionAgent) gotAgent;
		log.info("Propagated TxnDel: Trying to set Transaction to:" + newtxn + " for Agent: " + ag.getIdentity() + "=>" + ag.getNamespace()+"." + ag.getName());
		int count=0;
		if (transaction != null) {
			log.info("Transaction is not null in Agent: " + ag.getIdentity() + " Txn current: " + transaction + "=>" + ag.getNamespace()+"." + ag.getName());
			if (!(transaction.equals(newtxn))) {
				while (transaction != null) {
					System.out.println("Loop Count = " + ++count);
					try {
//						System.out.println("Transaction Agent is remote? : " + (txnAg instanceof RemoteAgentState));
//						Class[] txnintfs = gotAgent.getClass().getInterfaces();
//						System.out.println("Got an agent for the transaction implementing:");
//						for (Class agClass : intfs) {
//							System.out.println("\t" + agClass.getName());
//						}
						Transaction txn = txnAg.getTransactionFor(transaction);
				
						if (txn != null) {
							System.out.println("Exisitng Transaction ID: " + txn.toString());
							synchronized (this) {
								wait(1000);
							}
						} else {
							transaction = null;
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}

		log.info("Transaction current: " + transaction + "; Change-to: " + newtxn);
		if (transaction == null && newtxn != null) {

			Agent rec = (Agent) receiver;
			// synchronized (rec) {
			rec.setAgentState(AgentState.LOCKED);
			setTransactionReference(newtxn);
			log.info("Transaction now set to " + transaction.toString() + " for " + receiver);
			Thread.yield();
			intfs = receiver.getClass().getInterfaces();
//			System.out.println("Object to enlist in transaction implementing:");
//			for (Class agClass : intfs) {
//				System.out.println("\t" + agClass.getName());
//			}
			Proxy txnAgProxy = (Proxy) txnAg;
			TransactionalResource mainReceiver;
			InvocationHandler ivh = txnAgProxy.getInvocationHandler(txnAgProxy);
			if (ivh instanceof CollaborationRemoteProxy) {
				System.out.println("We need to export the receiver here");
				System.out.println("Getting remote agent");
				mainReceiver = new TransactionalResourceAdapter((RemoteTransactionalResource)getRemoteReceiver(receiver));
			} else{
				System.out.println("We do not need to export the receiver here");
				mainReceiver = (TransactionalResource)receiver;
			}
			txnAg.enlistInTransaction( mainReceiver, transaction);
			if (receiver instanceof TransactionAccessor)
				((TransactionAccessor) receiver).setGlobalTransaction(txnAg.getTransactionFor(transaction));
			if (source != null) {
				Producer p = (Producer) source;
				Uuid s = ag.getSubscriberIdentity();

				log.info("Adding Message Lock");
				domain.addMessageLock(p.getProducerIdentity(), s, ag.getNamespace() + "." + ag.getName());
			} else
				log.info("Source is null");

			// }
			return;
		} else if (!(transaction.equals(newtxn)))
			log.severe("Transaction was not set Problem between current " + transaction + " and new " + newtxn);
		if (newtxn != null)
			if (!(transaction.equals(newtxn)))
				throw new TransactionException(receiver + ": Already registered under another transaction Current:" + transaction + ", Dest:" + newtxn);
	}

	private Remote getRemoteReceiver(Object receiver) throws ExportException {
		System.out.println("Exporting");
		ProxyPair proxyPair = DefaultExporterManager.getManager().exportProxyAndPair((Remote) receiver, "standard", UuidFactory.generate());
		Object smartProxy = proxyPair.getSmartProxy();
		ArrayList arr = new ArrayList();
		getInterfaces(receiver.getClass(), arr);
		if(receiver instanceof NonTransactionalResource)
			arr.add(NonTransactionalResource.class);
		if(receiver instanceof TransactionalResource)
			arr.add(RemoteTransactionalResource.class);
		Class[] intf = (Class[]) arr.toArray(new Class[] {});
		return (Remote)smartProxy;
	}

	private void setTransactionReference(Uuid newtxnRef) {
		System.out.println("Setting Transaction Reference");
		transaction = newtxnRef;

	}

	private void doSetAttach(Object source, Object receiver, Object[] args) throws NoSuchAgentException, TransactionException, RemoteException {
		try {
			Agent ag = (Agent) receiver;
			String managerAndID = (String) args[0];
			managerAndTxnRef = managerAndID;
			String[] parts = managerAndID.split(":");
			AgentIdentity managerID = new AgentIdentity(parts[0]);
			Uuid newtxn = UuidFactory.create(parts[1]);
			TransactionAgent txnAg = (TransactionAgent) ((PrivilegedAgentContext) domain).getAgent(managerID);
			if (txnAg == null) {
				txnAg = (TransactionAgent) domain.getRegistry().getAgent("neon.Transaction");

			}
			log.info("Propagated TxnDel: Trying to set Transaction to:" + newtxn + " for Agent: " + ag.getIdentity());

			if (transaction != null) {
				log.info("Transaction is not null in Agent: " + ag.getIdentity() + " Txn current: " + transaction);
				if (!(transaction.equals(newtxn))) {
					while (transaction != null) {
						try {
							Transaction txn = txnAg.getTransactionFor(transaction);
							if (txn != null) {
								synchronized (this) {
									wait(100);
								}
							} else {
								transaction = null;
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}

			log.info("Transaction current: " + transaction + "; Change-to: " + newtxn);
			if (transaction == null && newtxn != null) {

				Agent rec = (Agent) receiver;
				// synchronized (rec) {
				rec.setAgentState(AgentState.LOCKED);
				setTransactionReference(newtxn);
				log.info("Transaction now set to " + transaction.toString() + " for " + receiver);
				Thread.yield();
				log.info("Recevier is Null: " + (receiver == null));
				txnAg.enlistInTransaction((TransactionalResource) receiver, transaction);
				if (receiver instanceof TransactionAccessor)
					((TransactionAccessor) receiver).setGlobalTransaction(txnAg.getTransactionFor(transaction));
				if (source != null) {
					Producer p = (Producer) source;
					Uuid s = ag.getSubscriberIdentity();

					log.info("Adding Message Lock");
					domain.addMessageLock(p.getProducerIdentity(), s, ag.getNamespace() + "." + ag.getName());
				} else
					log.info("Source is null");

				// }
				return;
			} else if (!(transaction.equals(newtxn)))
				log.severe("Transaction was not set Problem between current " + transaction + " and new " + newtxn);
			if (newtxn != null)
				if (!(transaction.equals(newtxn)))
					throw new TransactionException(receiver + ": Already registered under another transaction Current:" + transaction + ", Dest:" + newtxn);
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/*
	 * @see org.jini.projects.neon.dynproxy.invoker.InvokerDelegate#setDomain(org.jini.projects.neon.host.ManagedDomain)
	 */
	public void setDomain(ManagedDomain domain) {
		// TODO Complete method stub for setDomain
		this.domain = domain;
	}

	private void getInterfaces(Class cl, ArrayList arr) {
		// System.out.println("Checking interfaces...." + cl.getName());
		Class[] classes = cl.getInterfaces();
		// Class[] classes = cl.getClasses();
		for (int i = 0; i < classes.length; i++) {
			Class subclass = classes[i];
			// System.out.println("Class: " + subclass.getName());
			if (subclass.isInterface()) {
				getInterfaces(subclass, arr);
			}
			if (subclass.equals(Collaborative.class)) {
				if (!cl.getName().startsWith("$Proxy")) {
					arr.add(cl);
				}
			}
		}
		
	}
	
}
