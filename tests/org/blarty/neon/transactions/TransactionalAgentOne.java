
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
 * TransactionalAgentOne.java
 * Created on 12-Nov-2003
 *TransactionalAgentOne
 */
package org.jini.projects.neon.transactions;

import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.TransactionalAgent;



/**
 * 
 * @author calum
 */
public class TransactionalAgentOne extends AbstractAgent implements TransactionalAgent, TxnMessageIntf {
    public static final long  serialVersionUID = 4941127014263569014L;
    String message;
    String previousMessage;

    /**
     * 
     */
    public TransactionalAgentOne() {
        
        super();
        name = "T1";
        this.namespace="transaction.tests";
        // URGENT Complete constructor stub for TransactionalAgentOne
    }

    /* @see org.jini.projects.neon.agents.Agent#init()
     */
    public boolean init() {
        // TODO Complete method stub for init
        message="<None>";
        previousMessage = message;
        return true;
    }

 



    /* @see org.jini.projects.neon.host.transactions.Transactional#prepare()
     */
    public boolean prepare() {
        // TODO Complete method stub for prepare
        return true;
    }

    /* @see org.jini.projects.neon.host.transactions.Transactional#commit()
     */
    public void commit() {
        // TODO Complete method stub for commit
        System.out.println("TX1 has been Commited");
        previousMessage = message;
    }

    /* @see org.jini.projects.neon.host.transactions.Transactional#abort()
     */
    public void abort() {
        // TODO Complete method stub for abort
        System.out.println("TX 1 has been Aborted");
        message = previousMessage;        
    }
    
    public void setMessage(String message){
        this.message = message; 
        try {
            this.getAgentLogger().finest("Propagating Txn - YAY");            
        } catch (Exception e) {
            // URGENT Handle NoSuchAgentException
            e.printStackTrace();
        }
    }
    
    public String getMessage(){
        return this.message;
    }

}
