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
package org.jini.projects.neon.host.transactions;

/*
* TransactionAccessor.java
*
* Created Mon Mar 21 10:17:00 GMT 2005
*/

import net.jini.core.transaction.Transaction;

/**
* Allows an agent to be informed of the Jini distributed transaction, when it is enlisted in a transaction
* via another agent.
* @author  calum
*
*/

public interface TransactionAccessor{
    /**
     * Provides access to the Jini transaction for an agent that implements this interface
     * @param txn the Jini transaction
     *
     * @see org.jini.projects.neon.transactions.JavaSpaceOps
     */
	public void setGlobalTransaction(Transaction txn);
}
