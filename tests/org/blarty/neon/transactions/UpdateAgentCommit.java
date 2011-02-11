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
 * UpdateAgentCommit.java
 * 
 * Created Mon Mar 21 13:55:04 GMT 2005
 */

import java.rmi.RemoteException;
import java.util.List;

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
import org.jini.projects.neon.agents.util.meta.AgentDependencies;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.neon.host.transactions.InternalTransaction;
import org.jini.projects.neon.host.transactions.TransactionAgent;

import com.sun.jini.constants.TimeConstants;
import java.util.Random;

/**
 * 
 * @author calum
 * 
 */

public class UpdateAgentCommit extends AbstractAgent implements NonContinuousAgent, Runnable{
        private transient InternalTransaction itxn;
static Random randEmpIDGen;
    public UpdateAgentCommit() {
                this.namespace = "neon.transactions";
                this.name = "EmployeeUpdaterCommit";
                if(randEmpIDGen==null)
            randEmpIDGen = new Random(System.currentTimeMillis());
        }

        public boolean init() {
                getAgentLogger().fine("UpdaterAgentCommit init'ed");
                return true;
        }

        public void run() {
                
                setAgentState(AgentState.BUSY);
                JavaSpaceOps ops = null;
                TransactionAgent txn = null;
                try {
                        
                        
                        String spaceName = (String) getAgentConfiguration().getEntry("neon.transactions.tests", "updateSpace", String.class);
                        while (txn == null) {
                                if (txn == null) {
                                        try {
                                                txn = (TransactionAgent) context.attachAgent("neon.Transaction",0);
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
                                              ops = (JavaSpaceOps) context.attachAgent("JavaSpaceOps", new Entry[]{new Name(spaceName)});
                                      } catch (NoSuchAgentException e) {
                                              // TODO Handle NoSuchAgentException
                                              synchronized (this) {
                                                      wait(100);
                                              }
                                      }

                      }
                      EmployeeEntry entr = new EmployeeEntry(this.randEmpIDGen.nextInt(100),"Sea Captain", "G'arr");
                      ops.write(entr, 5 * TimeConstants.MINUTES);
                      System.out.println("UpdateAgentCommit commiting transaction");
                        itxn.commit();
                        getAgentLogger().info("UpdateAgentCommit Transaction commited");
                } catch (Exception e) {
                	getAgentLogger().warning("Transaction couldn't commit");
                getAgentLogger().info("UpdateAgentCommit received an Exception of Type: " + e.getClass().getName());
                    e.printStackTrace();
            getAgentLogger().warning("Going to abort transaction");
                    try {
                        itxn.abort();
                        getAgentLogger().info("Transaction aborted");
                    } catch (Exception e2){
                        e2.printStackTrace();
                        getAgentLogger().severe("Transaction couldn't abort");
                    }
                }
                System.out.println("Detaching Javaspace Ops from UpdateAgentCommit");
                if(ops!=null)
                    while(!context.detachAgent(txn));
                context.detachAgent(ops);
                setAgentState(AgentState.AVAILABLE);
        
        }
}
