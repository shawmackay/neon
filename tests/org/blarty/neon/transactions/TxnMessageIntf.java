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
 * TxnMessageIntf.java
 * Created on 12-Nov-2003
 *TxnMessageIntf
 */
package org.jini.projects.neon.transactions;

import org.jini.projects.neon.collaboration.Collaborative;

/**
 * Simple Interface for showing an example for transactions.
 * @author calum
 */
public interface TxnMessageIntf extends Collaborative{
    /**
     * Set some data on an agent
     * @param message string that will be set
     */
    public abstract void setMessage(String message);
    /**
     * Get data from the agent
     * @return the requested data
     */
    public abstract String getMessage();
}
