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
 * TrasactionHolder.java Created on 11-Nov-2003
 *TrasactionHolder
 */

package org.jini.projects.neon.host.transactions;

import java.lang.reflect.Proxy;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import net.jini.core.transaction.CannotAbortException;
import net.jini.core.transaction.CannotCommitException;
import net.jini.core.transaction.TimeoutExpiredException;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.UnknownTransactionException;
import net.jini.core.transaction.server.ServerTransaction;
import net.jini.core.transaction.server.TransactionConstants;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.core.transaction.server.TransactionParticipant;
import net.jini.id.Uuid;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentState;

import com.sun.jini.constants.TimeConstants;

/**
 * Transaction Participant class, maintaining a set of local resources that are
 * enlisted within the distributed transaction.<br> This class is both the controller of the transaction
 * and a participant within that transaction
 * @author calum
 */
public class TransactionHolder implements TransactionParticipant {
    private Transaction txn;

    private Uuid localID;

    private int crashcount = 0;

    private Logger l = Logger.getLogger("org.jini.projects.neon.host.transactions");

    // private List activeId = new ArrayList();
    CopyOnWriteArrayList enlistedresources = new CopyOnWriteArrayList();

    /**
     * Create a new TransactionHolder, referencing the given distributed
     * transaction and representing the given local Transaction ID
     */
    public TransactionHolder(Transaction txn, Uuid transactionID) {
        super();
        this.txn = txn;
        this.localID = transactionID;
    }

    private void incCrashCount() {
        crashcount++;
    }

    public int getCrashCount() {
        return crashcount;
    }

    /**
     * Enlist a local resource under the control of this distributed transaction
     * 
     * @param tr
     *            the resource to be enlisted
     */
    public void enlistResource(TransactionalResource tr) {
        l.fine("Adding resource:" + tr + " to resources");
        if (!enlistedresources.contains(tr) && tr instanceof TransactionalResource) {
            enlistedresources.add(tr);
        }

    }

    public void listEnlistedResources(){
    	System.out.println("Number of enlisted resources:  " + enlistedresources.size());
    }
    
    /**
     * Start the commit processing of this transaction.0
     * 
     * @throws UnknownTransactionException
     * @throws CannotCommitException
     * @throws RemoteException
     */
    public void doCommit() throws UnknownTransactionException, CannotCommitException, RemoteException {
         l.info("Initiating commit of local txn: " + localID + ": Remote txn:" + ((ServerTransaction) txn).id);
        ServerTransaction stxn = (ServerTransaction) txn;
         displayTxnState(stxn);
        boolean done = false;
        while (!done) {
            try {
            	l.info("Trying to commit");
                txn.commit();
                done = true;
            } catch (Exception e) {
                // TODO Handle TimeoutExpiredExcepti"on
                l.warning("Transaction timeout expired for: " + localID + "....retrying");
                displayTxnState(stxn);
            }
        }
        l.fine("Commit called for local txn: " + localID);
    }

    /**
     * Display a simple string representing the Transaction Managers view of the
     * state of the transaction
     * 
     * @param stxn
     *            The distributed transaction to look at
     * @throws UnknownTransactionException
     * @throws RemoteException
     */
    private void displayTxnState(ServerTransaction stxn) {
        try {
            int state = stxn.getState();
            if (state == TransactionConstants.ACTIVE)
                l.finest("Txn: ACTIVE" + stxn.id);
            if (state == TransactionConstants.ABORTED)
            	l.finest("Txn: ABORTED" + stxn.id);
            if (state == TransactionConstants.COMMITTED)
            	l.finest("Txn: COMMITTED" + stxn.id);
            if (state == TransactionConstants.NOTCHANGED)
            	l.finest("Txn: NOTCHANGED" + stxn.id);
            if (state == TransactionConstants.PREPARED)
            	l.finest("Txn: PREPARED" + stxn.id);
            if (state == TransactionConstants.VOTING)
            	l.finest("Txn: VOTING" + stxn.id);
        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

    /**
     * Start the abort processing of this transaction
     * 
     * @throws UnknownTransactionException
     * @throws CannotAbortException
     * @throws RemoteException
     */
    public void doAbort() throws UnknownTransactionException, CannotAbortException, RemoteException {
         l.info("Initiating abort of local txn: " + localID);
        ServerTransaction stxn = (ServerTransaction) txn;
         displayTxnState(stxn);

        boolean done = false;
        while (!done) {
            try {
                txn.abort(120 * TimeConstants.SECONDS);
            	
                done = true;
            } catch (TimeoutExpiredException e) {
                // TODO Handle TimeoutExpiredException
                l.warning("Transaction timeout expired for: " + localID + "....retrying");
                displayTxnState(stxn);
            }
        }
         l.info("Abort called of local txn: " + localID);
    }

    /*
     * @see net.jini.core.transaction.server.TransactionParticipant#abort(net.jini.core.transaction.server.TransactionManager,
     *      long)
     */

    /**
     * Call back by the transaction manager to tell the participant to rollback
     * and release all enlisted resources (agents) that are locked under the
     * scope of this transaction
     */
    public void abort(TransactionManager mgr, long id) throws UnknownTransactionException, RemoteException {
        // TODO Complete method stub for abort
        l.info("Transaction Manager signalled to Abort transaction: " + id);
        CountDownLatch clearLatch = new CountDownLatch(enlistedresources.size());
        for (Iterator iter = enlistedresources.iterator(); iter.hasNext();) {
            Object ob = iter.next();
            synchronized (ob) {
                if (ob instanceof Transactional) {

                    // System.out.println("Calling abort on Object");
                    ((Transactional) ob).abort();
                }
                if(ob instanceof RemoteTransactionalResource){
                	l.info("Clearing remote transaction " + localID + " on " + ob);
                	  ((RemoteTransactionalResource) ob).clearRemoteTransaction();
                }
                if (ob instanceof TransactionalResource) {
                    l.info("Clearing local transaction " + localID + " on " + ob);
                    ((TransactionalResource) ob).clearTransaction(clearLatch);
                    if (ob instanceof TransactionAccessor) {
                        l.info("Setting Accessor Transaction to null");
                        ((TransactionAccessor) ob).setGlobalTransaction(null);
                    }
                    l.info("Checking if object is in Transaction");
                    int timesround = 0;
                    while (((TransactionalResource) ob).getTransaction() != null) {
                        // System.err.println("Waiting for transaction " + id +
                        // " to be reset");
                        // int timesround = 0;
                        synchronized (this) {
                            try {

                                wait(100);
                                timesround++;
                                if (timesround % 10 == 0)
                                    l.info("Waited " + (timesround / 10) + "secs for transaction " + id + " to be reset");
                            } catch (InterruptedException e) {
                                // TODO Handle InterruptedException
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

//        try {
//            clearLatch.await();
//        } catch (InterruptedException e) {
//            // TODO Handle InterruptedException
//            e.printStackTrace();
//        }


    }

    /**
     * Call back by the transaction manager to tell the participant to
     * rollforward any changes and release all enlisted resources (agents) that
     * are locked under the scope of this transaction
     */
    public void commit(TransactionManager mgr, long id) throws UnknownTransactionException, RemoteException {
        // TODO Complete method stub for commit
        l.info("Transaction Manager signalled to Commit global transaction: " + id);

        CountDownLatch clearLatch = new CountDownLatch(enlistedresources.size());
        for (Iterator iter = enlistedresources.iterator(); iter.hasNext();) {
            Object ob = iter.next();

            if (ob instanceof Transactional)
                ((Transactional) ob).commit();
            if(ob instanceof RemoteTransactionalResource){
            	l.info("Clearing remote transaction " + localID + " on " + ob);
            	  ((RemoteTransactionalResource) ob).clearRemoteTransaction();
            }
            if (ob instanceof TransactionalResource) {
                l.info("Clearing local transaction " + localID + " on " + ob);
                ((TransactionalResource) ob).clearTransaction(clearLatch);
                if (ob instanceof TransactionAccessor) {
                    l.info("Setting Accessor Transaction to null");
                    ((TransactionAccessor) ob).setGlobalTransaction(null);
                }

                int timesround = 0;
                while (((TransactionalResource) ob).getTransaction() != null) {
                    synchronized (this) {
                        try {
                            wait(100);
                            timesround++;
                            if (timesround % 100 == 0) {
                                String obtype = ob.getClass().getName();
                                if (ob instanceof Proxy)
                                    obtype = Proxy.getInvocationHandler(ob).getClass().getName();
                                l.info("Waited " + (timesround / 100) * 10 + "secs for transaction " + id + " to be reset on object of type:" + obtype);
                            }
                        } catch (InterruptedException e) {
                            // TODO Handle InterruptedException
                            e.printStackTrace();
                        }
                    }
                }
            }

        }

        // Once all transaction state is cleared we clear all the agent states

//        try {
//            clearLatch.await();
//        } catch (InterruptedException e) {
//            // TODO Handle InterruptedException
//            e.printStackTrace();
//        }

        for (Iterator iter = enlistedresources.iterator(); iter.hasNext();) {
            Object ob = iter.next();
            Agent a = (Agent) ob;
            synchronized (a) {
                l.info("Clearing Agent: " + a.getNamespace() + "." + a.getName() + "; ID: " + a.getIdentity());
                AgentState prevState = a.getSecondaryState();
                if (prevState.equals(AgentState.BUSY))
                    a.setAgentState(AgentState.AVAILABLE);
                else
                    a.setAgentState(prevState);
            }
        }
        l.info("Transaction commit completed and all resources unlocked");
    }

    /**
     * Call back by the transaction manager to tell the participant to prepare
     * reosurces for commit
     */
    public int prepare(TransactionManager mgr, long id) throws UnknownTransactionException, RemoteException {
        l.info("Transaction Manager signalled to Prepare");
        CountDownLatch clearLatch = new CountDownLatch(enlistedresources.size());
        for (Iterator iter = enlistedresources.iterator(); iter.hasNext();) {
            Object ob = iter.next();
            if (ob instanceof Transactional)
                if (!((Transactional) ob).prepare())
                    return TransactionConstants.ABORTED;
        }
        return TransactionConstants.PREPARED;
    }

    /**
     * Call back by the transaction manager to tell the participant to prepare
     * and rollforward any changes and release all enlisted resources (agents)
     * that are locked under the scope of this transaction
     */
    public int prepareAndCommit(TransactionManager mgr, long id) throws UnknownTransactionException, RemoteException {
        // TODO Complete method stub for prepareAndCommit
        l.info("Transaction Manager signalled to Prapare-and-Commit");
        int prep_results = prepare(mgr, id);
        if (prep_results == TransactionConstants.PREPARED) {
            commit(mgr, id);
            return TransactionConstants.COMMITTED;
        } else
            return prep_results;
    }

    /**
     * Get the distributed transaction that this participant is representing
     * 
     * @return
     */
    public Transaction getGlobalTransaction() {
        return this.txn;
    }

}