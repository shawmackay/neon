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
 * neon : org.jini.projects.neon.transactions
 * TransactionSourceAgent.java Created on 12-Nov-2003
 *TransactionSourceAgent
 */

package org.jini.projects.neon.transactions;

import java.rmi.RemoteException;

import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.transaction.TransactionException;

import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.neon.host.transactions.InternalTransaction;
import org.jini.projects.neon.host.transactions.TransactionAgent;

/**
 * 
 *@author calum
 */
public class TransactionSourceAgent extends AbstractAgent implements  Runnable {
    /**
     *
     */
    public TransactionSourceAgent() {
        super();
        // URGENT Complete constructor stub for TransactionSourceAgent
        this.name="TransactionSource";
        this.namespace = "transaction.tests";
    }
    
        /*
         * @see org.jini.projects.neon.agents.Agent#init()
         */
    public boolean init() {
        // TODO Complete method stub for init
        return true;
    }
    
    
    public void run() {
        try {
            // TODO Complete method stub for start
            try {
                Thread.sleep(10000);
            } catch (InterruptedException iex) {
            }
            TransactionAgent txnAgent = (TransactionAgent) context.getAgent("neon.Transaction");
            InternalTransaction itxn = txnAgent.createTransaction(getIdentity());
            TxnMessageIntf tx1 = (TxnMessageIntf) context.getAgent("T1");
            TxnMessageIntf tx2 = (TxnMessageIntf) context.getAgent("T2");
            tx1.setMessage("This is some test data 20th Jan");
            System.out.println(tx1.getMessage());
            tx2.setMessage("....and some more test data 20th Jan");
            System.out.println(tx2.getMessage());
            itxn.commit();
            getAgentLogger().info("Starting Transaction Again!!!");
            itxn = txnAgent.createTransaction(this.getIdentity());
            tx1.setMessage("This is some test data 20th Jan");
            System.out.println(tx1.getMessage());
            tx2.setMessage("....and some more test data 20th Jan");
            System.out.println(tx2.getMessage());
            itxn.commit();
            try {
                System.out.println("Waiting");
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                // URGENT Handle InterruptedException
                e1.printStackTrace();
            }
            //System.out.println("TX1: " + tx1.getMessage());
            //System.out.println("TX2: " + tx1.getMessage());
            //System.out.println("Message:" + context.sendMessage("T1",
            // "GetMessage", null).getResponseObject());
            //System.out.println("Message:" + context.sendMessage("T2",
            // "GetMessage", null).getResponseObject());
            System.out.println("TransactionalSourceAgent complete");
        } catch (NoSuchAgentException e) {
            // URGENT Handle NoSuchAgentException
            e.printStackTrace();
        } catch (TransactionException e) {
            // URGENT Handle TransactionException
            e.printStackTrace();
        }catch (LeaseDeniedException e) {
            // URGENT Handle TransactionException
            e.printStackTrace();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    
}
