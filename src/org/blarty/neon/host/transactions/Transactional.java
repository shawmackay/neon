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
 * neon : org.jini.projects.neon.agents
 * Transactional.java
 * Created on 12-Nov-2003
 *Transactional
 */
package org.jini.projects.neon.host.transactions;


/**
 * @author calum
 */
public interface Transactional {
    
    /**
     * Allows the resource to indicate whether it is prepared to commit the transaction.<br>
     * Any resource that returns false to this method, will cause the call for <code>prepare()</code> from the TransactionManager to return
     * ABORTED
     * @return true if it has prepared successfully, false otherwise
     */
    public boolean prepare();
    /**
     * Informs the resource to roll forward any changes<br>
     */
    public void commit();
    /**
     * Informs the resource to rollback any changes<br> 
     */
    public void abort();
}