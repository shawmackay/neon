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
 * neon : org.jini.projects.neon.host
 * VirtualProducer.java
 * Created on 29-Jan-2004
 *VirtualProducer
 */
package org.jini.projects.neon.host;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;

import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.id.Uuid;

import org.jini.projects.neon.host.transactions.TransactionalResource;
import org.jini.projects.zenith.endpoints.Producer;


/**
 *Created by the domain Router to simulate transaction propagation through a
 * remotely defined global transaction
 *@author calum
 */
public class VirtualProducer implements Producer, TransactionalResource, Serializable {
    public static final long serialVersionUID = -6047324851303155112L;
    Uuid virtualIdentity;
    String txnref;
    String virtualNS;
	/**
	 * 
	 */
	public VirtualProducer(String namespace, Uuid pid) {
		super();
		// URGENT Complete constructor stub for VirtualProducer
        virtualIdentity = pid;
        this.virtualNS = namespace;
	}

	/* @see org.jini.projects.zenith.endpoints.Producer#getProducerIdentity()
	 */
	public Uuid getProducerIdentity() {
		// TODO Complete method stub for getProducerIdentity
		return virtualIdentity;
	}

	/* @see org.jini.projects.neon.host.transactions.TransactionalResource#setTransaction(java.lang.Integer)
	 */
	public void setTransaction(String i) throws TransactionException {
		// TODO Complete method stub for setTransaction
        System.out.println("VirtualProducer - setting transaction");
        txnref = i;
	}

	/* @see org.jini.projects.neon.host.transactions.TransactionalResource#getTransactionIdentifier()
	 */
	public String getTransaction() {
		// TODO Complete method stub for getTransactionIdentifier
        System.out.println("VirtualProducer - getting transaction");
		return txnref;
	}

	/* @see org.jini.projects.neon.host.transactions.TransactionalResource#clearTransaction()
	 */
	public void clearTransaction(CountDownLatch latch) {
		// TODO Complete method stub for clearTransaction
        System.out.println("VirtualProducer - clearing transaction");
        txnref=null;
		latch.countDown();
	}

	/* @see org.jini.projects.zenith.endpoints.Producer#getNamespace()
	 */
	public String getNamespace() {
		// TODO Complete method stub for getNamespace
		return virtualNS;
	}
	/* @see org.jini.projects.neon.host.transactions.TransactionalResource#attachTransaction(net.jini.id.Uuid, net.jini.core.transaction.Transaction)
	 */
	public void attachTransaction(Uuid i, Transaction txn) {
		// TODO Complete method stub for attachTransaction
        System.out.println("UNIMPLEMENTED - VirtualProducer.attachTransaction");
	}

	public boolean inTransaction() {
		// TODO Complete method stub for inTransaction
		return (txnref!=null);
	}

	public void attachTransaction(String managerAndID, Transaction txn) {
		// TODO Complete method stub for attachTransaction
		System.out.println("UNIMPLEMENTED - VirtualProducer.attachTransaction");
	}
}
