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
 * TransactionAgent.java
 * Created on 11-Nov-2003
 *TransactionAgent
 */
package org.jini.projects.neon.host.transactions;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.transaction.CannotAbortException;
import net.jini.core.transaction.CannotCommitException;
import net.jini.core.transaction.CannotJoinException;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.core.transaction.UnknownTransactionException;
import net.jini.core.transaction.server.CrashCountException;
import net.jini.core.transaction.server.ServerTransaction;
import net.jini.id.Uuid;

import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.collaboration.Collaborative;
import org.jini.projects.zenith.annotations.Concurrent;



/**
 * Provides tha main interface into Neon's Transaction system. Instances of <code>TransactionAgent</code>
 * are required to be obtained by an agent in order for that agent to enter a transactional state.
 * @author calum
 */
public interface TransactionAgent extends Collaborative,Remote,NonTransactionalResource{
    /**
     * Creates a distributed transaction.<br>Implementations should join that transaction in the TransactionManager, 
     * and optionally create a local transaction object in order to facilitate enlistment of local resources, rather than repeated construction of <code>TransactionParticipant</code>s
     * @throws TransactionException
     * @throws LeaseDeniedException
     */
	@Concurrent
    public InternalTransaction createTransaction(AgentIdentity id) throws TransactionException, LeaseDeniedException,RemoteException;
    /**
     * Obtain the transaction the caller is in
     * @return the current transaction 
     */
    public Transaction getTransaction() throws RemoteException;
//    /**
//     * Attempt to commit the transaction that this agent is under.
//     * @throws CannotCommitException
//     * @throws UnknownTransactionException
//     */
//    public abstract void commit() throws CannotCommitException, UnknownTransactionException;
//    /**
//     * Attempt to commit the transaction that this agent is under.
//     * @throws CannotAbortException
//     * @throws UnknownTransactionException
//     */
//    public abstract void abort() throws CannotAbortException, UnknownTransactionException;
//    
    /**
     * Requests that a transactional resource by enlisted in a local transaction, and thus a distributed transaction
     * @param res
     * @param transactionID
     * @throws TransactionException
     */
    @Concurrent
    public void enlistInTransaction(TransactionalResource res,Uuid transactionID) throws TransactionException,RemoteException;
    /**
     * Requests that a transactional resource by enlisted in a local transaction, and join a specified distributed transaction
     * @param tx the distributed transaction proxy required for the host to join with a Jini Transaction Manager
     * @param transactionID a local transaction ID to use for maintaining the local set of participating resources
     * @return a new local transaction ID
     * @throws UnknownTransactionException
     * @throws CannotJoinException
     * @throws CrashCountException
     */
    public Uuid attachTransaction(ServerTransaction tx, Uuid transactionID) throws UnknownTransactionException, CannotJoinException, CrashCountException,RemoteException;
    /**
     * Obtain the distributed transaction that is attributed to the given local transaction ID
     * @param id the local transaction Uuid
     * @return the distributed transaction that correlates to the local transaction ID
     */
    public Transaction getTransactionFor(Uuid id) throws RemoteException;
}
