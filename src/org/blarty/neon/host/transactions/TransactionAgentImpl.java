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
 * TransactionCreatorAgent.java Created on 11-Nov-2003
 *TransactionCreatorAgent
 */

package org.jini.projects.neon.host.transactions;

import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.transaction.CannotJoinException;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.core.transaction.UnknownTransactionException;
import net.jini.core.transaction.server.CrashCountException;
import net.jini.core.transaction.server.ServerTransaction;
import net.jini.core.transaction.server.TransactionParticipant;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;

import org.jini.glyph.Exportable;
import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.LocalAgent;
import org.jini.projects.neon.host.DomainRegistry;
import org.jini.projects.neon.host.ManagedDomain;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.neon.host.PrivilegedAgentContext;
import org.jini.projects.zenith.messaging.channels.connectors.PublishingQConnector;
import org.jini.projects.zenith.messaging.messages.KVPair;
import org.jini.projects.zenith.messaging.messages.MessageHeader;
import org.jini.projects.zenith.messaging.messages.ObjectMessage;
import org.jini.projects.zenith.messaging.messages.StringMessage;
import org.jini.projects.zenith.messaging.system.ChannelException;
import org.jini.projects.zenith.messaging.system.MessagingManager;

/**
 * This agent does not implement Transaction resource, because this agent needs
 * to 'tag' other agents with transactions, and is not allowed to be part of a
 * transactional scope.
 *@author calum
 */

@Exportable
public class TransactionAgentImpl extends AbstractAgent implements TransactionAgent,LocalAgent,  NonTransactionalResource {
    int loop = 0;

    MessagingManager mgr;

    TransactionParticipant ts;

    
    TransactionBlackBox internalBlackBox;
    /**
     * 
     */
    public TransactionAgentImpl() {

        super();
     
        this.namespace = "neon";
    }

    /*
     * @see org.jini.projects.neon.agents.Agent#init()
     */
    public boolean init() {
        // TODO Complete method stub for init
        mgr = MessagingManager.getManager();
        internalBlackBox = DomainRegistry.getDomainRegistry().getDomain(context.getDomainName()).getTxnBlackBox();
        return true;
    }

  

    public InternalTransaction createTransaction(AgentIdentity id) throws TransactionException, LeaseDeniedException {
    	
       
        // Create a transaction ID, and send it to the TransactionChannel
        // for registration
    	System.out.println("Creating a transaction for "  + id);
        ManagedDomain md = ((PrivilegedAgentContext) context).getAgentHost();
        
        
        Uuid tid = internalBlackBox.buildTransaction();
        InternalTransaction internalTxn = new InternalTransactionImpl(tid,internalBlackBox);
        Agent ag = null;
        try {
			ag = md.getRegistry().getAgent(id.getID());
			//internalBlackBox.register((TransactionalResource)ag,tid);
			System.out.println("Setting transaction ID on " + id);
			((TransactionalResource)ag).setTransaction(new String(this.ID.getExtendedString()+":" + tid));
			System.out.println("Transaction set on " + id);
			
		} catch (NoSuchAgentException e) {
			// TODO Handle NoSuchAgentException
			e.printStackTrace();
		} catch (NullPointerException e){
            if(ag!=null)
			System.out.println("Error while setting transaction on agent: " + ag.getIdentity() + "; Src Agent: " + getIdentity()  +"; TID: " +tid );
            else 
                System.out.println("Error while retrieving agent " + id);
			throw new TransactionException("Error while setting transaction on agent: " + id + "; Src Agent: " + getIdentity()  +"; TID: " +tid );
		}
		return internalTxn;
    }

   
    
    public void enlistInTransaction(TransactionalResource res,Uuid transactionID) throws TransactionException{
    	internalBlackBox.register(res,transactionID);
    	
    }
    
    /*
     * @see org.jini.projects.neon.host.transactions.TransactionAgent#getTransaction()
     */
    public Transaction getTransaction() {
        // // TODO Complete method stub for getTransaction
        // if (currentMessage.getSource() instanceof TransactionalResource) {
        // ManagedDomain md = ((PrivilegedAgentContext) context).getAgentHost();
        // Uuid txnId = ((TransactionalResource)
        // currentMessage.getSource()).getTransaction();
        // System.out.println("Getting global transaction " + txnId);
        // md.getTxnBlackBox().getTransactionFor(txnId);
        // }
        return null;
    }

	public Uuid attachTransaction(ServerTransaction tx, Uuid transactionID) throws UnknownTransactionException, CannotJoinException, CrashCountException {
		// TODO Complete method stub for attachTransaction
		System.out.println("Attempting Attach of transaction in TransactionAgentImpl");
		return internalBlackBox.attachTransaction(tx,transactionID);
	}

	public Transaction getTransactionFor(Uuid id) {
		// TODO Complete method stub for getTransactionFor
		return internalBlackBox.getTransactionFor(id);
	}
}
