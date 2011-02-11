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
 * neon : org.jini.projects.neon.org.jini.projects.neon.neontests.tutorial.payload
 * PayloadAgent.java
 * Created on 13-Nov-2003
 *PayloadAgent
 */
package org.jini.projects.neon.neontests.tutorial.payload;

import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.host.PrivilegedAgentContext;


/**
 * @author calum
 */
public class PayloadAgent extends AbstractAgent {

    /**
     * 
     */
    public PayloadAgent() {
        super();
        // URGENT Complete constructor stub for PayloadAgent
    }

    /* @see org.jini.projects.neon.agents.Agent#init()
     */
    public boolean init() {
        // TODO Complete method stub for init
        
        ((PrivilegedAgentContext)context).deployAgent(null);
        return true;
    }
    

}
