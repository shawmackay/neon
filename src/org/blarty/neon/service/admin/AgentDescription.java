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
 * neon : org.jini.projects.neon.service.admin
 * AgentDescription.java
 * Created on 22-Sep-2003
 */
package org.jini.projects.neon.service.admin;

import java.io.Serializable;

import net.jini.id.Uuid;

import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.AgentState;



/**
 * @author calum
 */
public class AgentDescription implements Serializable{
    
    String name;
    
    AgentIdentity identity;
    
    MessageDescription[] messageTypes;
    
    AgentState state;
    
    AgentState secondaryState;
    /**
     *
     */
    public AgentDescription(String name, AgentIdentity identity, MessageDescription[] messages, AgentState state, AgentState secondaryState) {
        this.name = name;
        this.identity = identity;
        this.messageTypes = messages;
        this.state = state;
        this.secondaryState = secondaryState;
    }
    
    
    
    /**
     * Obtains the Uuid as specified by AgentIdentity#getID()
     * @return the uuid part of the agents identity
     */
    public AgentIdentity getIdentity() {
        
        return identity;
    }
    
    /**
     * <strong>Changing in 0.2alpha</strong>
     * @return types of message this agent can accept
     */
    public MessageDescription[] getMessageTypes() {
        return messageTypes;
    }
    
    /**
     * Get the name of the agent
     * @return name of the agent
     */
    public String getName() {
        return name;
    }
    
    /**
     * Get the current state of the agent
     * @return current <em>primary</em> state of the agent
     */
    public AgentState getState() {
        return state;
    }
    
    public String toString(){
        return identity + ":" + state.toString();
        
    }
    
    
    
    public AgentState getSecondaryState() {
        return secondaryState;
    }
    
}
