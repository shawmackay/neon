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
 * neon : org.jini.projects.neon.host.transactions
 * TransactionBlackBox.java Created on 11-Nov-2003
 *TransactionBlackBox
 */

package org.jini.projects.neon.host.transactions;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.CannotAbortException;
import net.jini.core.transaction.CannotCommitException;
import net.jini.core.transaction.CannotJoinException;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.UnknownTransactionException;
import net.jini.core.transaction.server.CrashCountException;
import net.jini.core.transaction.server.ServerTransaction;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.core.transaction.server.TransactionParticipant;
import net.jini.export.Exporter;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.lease.LeaseListener;
import net.jini.lease.LeaseRenewalManager;

import org.jini.glyph.chalice.DefaultExporterManager;
import org.jini.glyph.chalice.ExporterManager;
import org.jini.projects.neon.host.ManagedDomain;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.neon.service.ServiceAgent;

/**
 * Handles the majority of the communication between Neon and the Transaction
 * Managers<br>
 * Maintains a map between the local transaction holder and the global
 * transaction Each individual partition has it's own TransactionBlackBox.
 * 
 * @author calum
 */
public class TransactionBlackBox {
	Map transactionMap = new ConcurrentHashMap();
	int transactioncount = 0;
	ManagedDomain domain;
	Logger tbbLog = Logger.getLogger("org.jini.projects.neon.transactions.blackbox");
	ExporterManager mgr = DefaultExporterManager.getManager();
	LeaseRenewalManager lrm = new LeaseRenewalManager();

	/**
	 * 
	 */
	public TransactionBlackBox(ManagedDomain dom) {
		super();
		this.domain = dom;
		tbbLog.info("Transaction - Blackbox created");
		
		// URGENT Complete constructor stub for TransactionBlackBox
	}

	/**
	 * Starts commit processing for a local transaction ID
	 * 
	 * @param transactionID
	 *            the local transaction identifier
	 * @throws UnknownTransactionException
	 * @throws CannotCommitException
	 * @throws RemoteException
	 */
	public void commit(Uuid transactionID) throws UnknownTransactionException, CannotCommitException, RemoteException {
		tbbLog.finer("Going to commit; local transaction ID : " + transactionID);
		TransactionHolder tr = (TransactionHolder) transactionMap.get(transactionID);

		tr.doCommit();
		tbbLog.info("Scheduling removal of transaction: " + transactionID);
		//transactionMap.remove(transactionID);
		mgr.scheduleRelinquish("transaction", transactionID, 5000);
	}

	/**
	 * Starts abort processing for a local transaction ID
	 * 
	 * @param transactionID
	 *            the local transaction identifier
	 * @throws UnknownTransactionException
	 * @throws CannotCommitException
	 * @throws RemoteException
	 */
	public void abort(Uuid transactionID) throws UnknownTransactionException, CannotAbortException, RemoteException {
		tbbLog.finest("Going to abort; local transaction ID : " + transactionID);
		TransactionHolder tr = (TransactionHolder) transactionMap.get(transactionID);
		tr.doAbort();
		tbbLog.info("Removing transaction: " + transactionID);
		//transactionMap.remove(transactionID);
		mgr.scheduleRelinquish("transaction", transactionID, 5000);
	}

	/**
	 * Registers (enlists) a resource under a local transaction set, which in
	 * turn refers back to a distributed transaction
	 * 
	 * @param resource
	 * @param transactionID
	 * @throws TransactionException
	 */
	public void register(TransactionalResource resource, Uuid transactionID) throws TransactionException {
		tbbLog.finest("A Resource has registered with Transaction ID " + transactionID + " {" + resource.toString() + "} ");
		TransactionHolder tr = (TransactionHolder) transactionMap.get(transactionID);
		if (tr == null)
			tbbLog.warning("Transaction Holder is null!!!!");
		try {
			tr.enlistResource(resource);
		} catch (RuntimeException e) {
			// TODO Handle RuntimeException
			System.err.println("Error while trying to enlist a resource under transaction: " + transactionID);
			throw new TransactionException("Error while trying to enlist a resource under transaction: " + transactionID
					+ ": Caused by: " + e.getMessage());

		}
	}

	/**
	 * Get the Distributed transaction that is currently registered against the
	 * given local transaction identifier
	 * 
	 * @param id
	 *            the local transaction ID
	 * @return the distributed transaction for this local transaction ID
	 */
	public Transaction getTransactionFor(Uuid id) {

		if (transactionMap.containsKey(id)) {
			TransactionHolder tr = (TransactionHolder) transactionMap.get(id);
			tr.listEnlistedResources();
			return tr.getGlobalTransaction();
		} else {
			tbbLog.warning("\t ***TRANSACTION BLACKBOX UNABLE TO FIND A TXNHOLDER FOR " + id);
			return null;
		}

	}

	/**
	 * Attach a distributed transaction to a given transaction ID, if the
	 * transaction is already being handle by the system it will return the ID
	 * of the existing local transaction
	 * 
	 * @param tx
	 *            the distrinbuted transaction
	 * @param transactionID
	 *            a local transactionID to use, if the distributed transaction
	 *            is not already being managed
	 * @return the local transaction ID
	 * @throws UnknownTransactionException
	 * @throws CannotJoinException
	 * @throws CrashCountException
	 */
	public Uuid attachTransaction(ServerTransaction tx, Uuid transactionID) throws UnknownTransactionException,
			CannotJoinException, CrashCountException {
		try {
			//System.out.println("Checking transaction: " + tx.id);
			for (Iterator iter = transactionMap.entrySet().iterator(); iter.hasNext();) {
				Map.Entry keypair = (Map.Entry) iter.next();
				TransactionHolder th = (TransactionHolder) keypair.getValue();
				//System.out.println("Checking transaction: " + tx.id + " against " + th.getGlobalTransaction().toString());

				if (th.getGlobalTransaction().equals(tx)) {

					return (Uuid) keypair.getKey();
				}
			}
			tbbLog.finest("Joining transaction");
			TransactionHolder holder = new TransactionHolder(tx, transactionID);
			Remote proxy = mgr.exportProxy(holder, "transaction", transactionID);
			System.out.println("Proxy class: " + proxy.getClass().getName());
			tx.join((TransactionParticipant) proxy, holder.getCrashCount());
			transactionMap.put(transactionID, holder);
			return transactionID;
		} catch (ExportException e) {
			// URGENT Handle ExportException
			e.printStackTrace();
		} catch (RemoteException e) {
			// URGENT Handle RemoteException
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Create a new distributed Transaction, and create a local transaction
	 * holder
	 * 
	 * @return the local transaction id
	 * @throws LeaseDeniedException
	 * @throws UnknownTransactionException
	 * @throws CannotJoinException
	 */
	public Uuid buildTransaction() throws LeaseDeniedException, UnknownTransactionException, CannotJoinException {
		try {
			ServiceAgent agt = null;
			int timer = 0;
			while (agt == null && timer < 5000) {
				try {
					agt = (ServiceAgent) domain.getRegistry().getAgent("neon.Services");
				} catch (NoSuchAgentException e) {
					// TODO Handle NoSuchAgentException
					try {
						wait(50);
					} catch (Exception ex) {
						timer += 50;
					}
				}
			}
			TransactionManager tm = (TransactionManager) agt.getSingleService(TransactionManager.class);
			while (tm == null) {

				System.out.println("Can't find transaction manager - waiting");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				tm = (TransactionManager) agt.getSingleService(TransactionManager.class);
			}
			final Transaction.Created tc = TransactionFactory.create(tm, 60000);
			lrm.renewFor(tc.lease, Lease.FOREVER, null);
			ServerTransaction tx = (ServerTransaction) tc.transaction;
			Uuid transactionID = UuidFactory.generate();
			TransactionHolder holder = new TransactionHolder(tx, transactionID);
		//	DefaultExporterManager.displayManagers();
			Remote proxy = mgr.exportProxy(holder, "Transaction", transactionID);
			System.out.println("Proxy class: " + proxy.getClass().getName());
			tx.join((TransactionParticipant) proxy, holder.getCrashCount());
			tbbLog.info("Created transaction: " + transactionID + " for global transaction: " + tx.id);
			transactionMap.put(transactionID, holder);

			return transactionID;
		} catch (UnknownTransactionException e) {
			e.printStackTrace();
		} catch (ExportException e) {
			// URGENT Handle ExportException
			e.printStackTrace();
		} catch (RemoteException e) {
			// URGENT Handle RemoteException
			e.printStackTrace();
		} catch (CrashCountException e) {
			// URGENT Handle CrashCountException
			e.printStackTrace();
		}
		tbbLog.severe("Cannot build a transaction!");
		return null;
	}
}
