
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
package org.jini.projects.neon.host;

import java.net.InetAddress;
import java.util.logging.Logger;

import net.jini.core.lookup.ServiceID;
import net.jini.id.Uuid;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.sensors.SensorFilter;
import org.jini.projects.zenith.messaging.messages.Message;



/**
 * A restricted view of the agent Host as seen by an agent
 */
public interface AsyncAgentEnvironment {
    /**
     * Send a message to an identified agent, if it exists
     * @param agent the identity of the agent
     * @param mesg the message to be sent
     * @return a wrappered value, representing the return value from the message call
     * @throws NoSuchAgentException if the agent does not exists within the domain or any connected domains
     */
    public void sendAsyncMessage(Uuid agent, Message mesg) throws NoSuchAgentException;
  
    /**
     * Send a message to a member of an agent category, if it exists
     * @param agentName the agent category/name to find
     * @param message the message to be sent
     * @return a wrappered value, representing the return value from the message call
     * @throws NoSuchAgentException if no member of the agent category is available, or none exists
     */
    public void  sendAsyncMessage(String agentName, Message message)throws NoSuchAgentException;    
    /**
     * Register a listener on another agent 
     * @param ag
     * @param name
     * @param l
     * @return
     */
    public boolean registerSensor(Agent ag,  String name, SensorFilter l);
    /**
     * Get the IP network Address of the current neon host
     * @return
     */
    public InetAddress getCurrentHost();
    /**
     * Get the domain name of the current context
     * @return
     */
    public String getDomainName();
    /**
     * Get the service ID for the currrent host.  
     * @return
     */
    public ServiceID getAgentServiceID();
    /**
     * Make an agent complete it's processing and remove it from the agent registry 
     * @param ag
     * @return
     */
    public boolean removeAgent(Agent ag);
    /**
     * Get the logger for the context
     * @param agentName
     * @return
     */
    public Logger getContextLogger(String agentName);
    
    //public Object getAgent(String name) throws NoSuchAgentException ;
}
