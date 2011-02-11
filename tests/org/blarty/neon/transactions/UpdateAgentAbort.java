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

package org.jini.projects.neon.transactions;

/*
 * UpdateAgentAbort.java
 * 
 * Created Mon Mar 21 13:55:14 GMT 2005
 */
import java.rmi.RemoteException;

import java.util.Random;
import net.jini.config.ConfigurationException;
import net.jini.core.entry.Entry;
import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.transaction.CannotAbortException;
import net.jini.core.transaction.TransactionException;
import net.jini.core.transaction.UnknownTransactionException;
import net.jini.lookup.entry.Name;

import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.AgentState;
import org.jini.projects.neon.agents.NonContinuousAgent;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.neon.host.transactions.InternalTransaction;
import org.jini.projects.neon.host.transactions.TransactionAgent;

/**
 * 
 * @author calum
 * 
 */

public class UpdateAgentAbort extends AbstractAgent implements NonContinuousAgent, Runnable {

    private transient InternalTransaction itxn;

    static Random randEmpIDGen;
    
    public UpdateAgentAbort() {
        this.namespace = "neon.transactions";
        this.name = "EmployeeUpdaterAbort";
        if(randEmpIDGen==null)
            randEmpIDGen = new Random(System.currentTimeMillis());
        
    }

    /**
     * init
     * 
     * @return boolean
     */
    public boolean init() {
        getAgentLogger().fine("UpdaterAgentAbort init'ed");
        return true;
    }

    /**
     * start
     */
    public void run() {
        setAgentState(AgentState.BUSY);
        JavaSpaceOps ops = null;
        TransactionAgent txn = null;
        try {

            String spaceName = (String) getAgentConfiguration().getEntry("neon.transactions.tests", "updateSpace", String.class);
            while (txn == null) {
                if (txn == null) {
                    try {
                        txn = (TransactionAgent) context.attachAgent("neon.Transaction", 0);
                        itxn = txn.createTransaction(this.getIdentity());
                    } catch (LeaseDeniedException e) {
                        // TODO Handle LeaseDeniedException
                        e.printStackTrace();
                    } catch (NoSuchAgentException e) {
                        // TODO Handle NoSuchAgentException
                        synchronized (this) {
                            wait(100);
                        }
                    } catch (TransactionException e) {
                        // TODO Handle TransactionException
                        e.printStackTrace();
                    }
                }
            }
            while (ops == null) {
                if (ops == null)
                    try {
                        ops = (JavaSpaceOps) context.attachAgent("JavaSpaceOps", new Entry[] { new Name(spaceName) }, 0);
                    } catch (NoSuchAgentException e) {
                        // TODO Handle NoSuchAgentException
                        System.out.println(e.getMessage());
                        synchronized (this) {
                            wait(100);
                        }
                    }

            }
            
            EmployeeEntry entr = new EmployeeEntry(this.randEmpIDGen.nextInt(100),"Bottomless Pete", "Nature's cruelest mistake");
            System.out.println("Writing an entry in transactioN!");
            ops.write(entr, Lease.FOREVER);
            System.out.println("Entry written");
            itxn.abort();
        } catch (Exception e) {
            System.out.println("UpdateAgentAbort received an Exception of Type: " + e.getClass().getName());
            e.printStackTrace();
            getAgentLogger().fine("GOing to abort transaction");
            try {
                itxn.abort();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        while (!context.detachAgent(txn))
            ;
        context.detachAgent(ops);
        setAgentState(AgentState.AVAILABLE);
    }
}
