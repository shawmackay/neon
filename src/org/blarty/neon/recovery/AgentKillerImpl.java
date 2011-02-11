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
 * neon : org.jini.projects.neon.recovery
 * AgentKillerImpl.java
 * Created on 22-Oct-2003
 */
package org.jini.projects.neon.recovery;

import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.LocalAgent;
import org.jini.projects.neon.host.AgentRegistry;
import org.jini.projects.neon.host.NoSuchAgentException;


/**
 * @author calum
 */
public class AgentKillerImpl extends AbstractAgent implements LocalAgent, KillerAgent {
    /**
     * 
     */
    private static final long serialVersionUID = -3052321266563526369L;
    AgentRegistry reg;
    /**
     * 
     */
    public AgentKillerImpl(AgentRegistry reg) {
        this.reg = reg;
        this.name="Killer";
        this.namespace ="neon";
        // URGENT Complete constructor stub for AgentKillerImpl
    }

    /* (non-Javadoc)
     * @see org.jini.projects.neon.agents.Agent#init()
     */
    public boolean init() {
        // URGENT Complete method stub for init
        return true;
    }

  
    public boolean Kill(AgentIdentity ID){
        try {
                System.out.println("Killing agent: " + ID);
                reg.deregisterAgent(reg.getAgent(ID));
               return true;
        } catch (NoSuchAgentException e) {
            // URGENT Handle NoSuchAgentException
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean KillOneOf(String name){
        System.out.println("Running KillOneOf");
        try {
            if(reg!=null){
                
                System.out.println("Killing a " + name + " agent");
                reg.deregisterAgent(reg.getAgent(name));
            }else 
                System.out.println("The Registry is null!!!!!");
        } catch (NoSuchAgentException e) {
            // URGENT Handle NoSuchAgentException
            e.printStackTrace();
        }
        return true;
    }

    public boolean KillAll(String name){
       try {
             Agent[] agents = reg.getAllAgents();
                for(int i=0;i<agents.length;i++){
                    Agent ag = agents[i];
                    if(ag.getName().toLowerCase().equals(name)){
                    
                        System.out.println("Killing agent " + ag.getIdentity() + " of the " + name + " group");
                        reg.deregisterAgent(ag);
                    }
                }
                return true;
        } catch (Exception e) {
            // URGENT Handle RuntimeException
            e.printStackTrace();
            return false;
        }
    }
}
