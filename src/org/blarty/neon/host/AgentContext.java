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

import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceID;
import net.jini.id.Uuid;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.sensors.SensorFilter;
import org.jini.projects.neon.collaboration.Response;
import org.jini.projects.zenith.messaging.messages.Message;



/**
 * A restricted view of the agent Host as seen by an agent
 */
public interface AgentContext {
    /**
     * Send a message to an identified agent, if it exists
     * @param agent the identity of the agent
     * @param mesg the message to be sent
     * @return a wrappered value, representing the return value from the message call
     * @throws NoSuchAgentException if the agent does not exists within the domain or any connected domains
     */
    public Response sendMessage(Uuid agent, Message mesg) throws NoSuchAgentException;
    /**
     *  Send a message to a member of an agent category, if it exists
     * @param agentName the agent category/name to find
     * @param messagename the message topic name
     * @param parameters an object array representing the parameters sent to the message handler
     * @return a wrappered value, representing the return value from the message call
     * @throws NoSuchAgentException if no member of the agent category is available, or none exists
     */
    public Response sendMessage(String agentName, String messagename, Object[] parameters)throws NoSuchAgentException;
    /**
     * Send a message to a member of an agent category, if it exists
     * @param agentName the agent category/name to find
     * @param message the message to be sent
     * @return a wrappered value, representing the return value from the message call
     * @throws NoSuchAgentException if no member of the agent category is available, or none exists
     */
    public Response sendMessage(String agentName, Message message)throws NoSuchAgentException;    
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
    
    public Object attachAgent(String name) throws NoSuchAgentException;
    
    public Object attachAgent(String name, int timeout) throws NoSuchAgentException;
    
    public Object attachAgent(String name, Entry[] metaMatches, int timeout) throws NoSuchAgentException;
    
    public Object attachAgent(String name, Entry[] metaMatches) throws NoSuchAgentException;
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
    
    /**
     * Detaches an agent, previously obtained through <code>attachAgent(...)</code>, i.e. changes the AgentState to
     * <code>AgentState.AVAILABLE</code>. Will return true, if the agent state could be reset, but will return false, if the agent is still working under a transaction
     * consider using the following code fragment <code>while(!detachAgent(myAgentRef));</code>
     * @param reference
     * @return whether or not the agent could be detahced at that point in time.
     */
    public boolean detachAgent(Object reference);
    
    
    public Object getAgent(AgentIdentity id) throws NoSuchAgentException;
    
    /**
     * Returns an agent, or object <i>representing</i> an agent where methods invoked on that agent are guaranteed to block until
     * the method returns
     */
    public Object getAgent(String name) throws NoSuchAgentException ;
    
    /**
     * Returns an agent, or object <i>representing</i> an agent where methods invoked on that agent are guaranteed to block until
     * the method returns.  This method blocks until either:<br/>
     * <ul><li> a matching agent is found OR</li><li>the specified timeout has expired, upon which a NoSuchAgentException is thrown</li>
     * </ul>
     * A timeout of zero indicates that the method will block indefinitely.
     */
    public Object getAgent(String name,int timeout) throws NoSuchAgentException ;
    
    /**
     * Returns an agent, or object <i>representing</i> an agent where methods invoked on that agent are guaranteed to block until
     * the method returns, that also matches the given meta attributes
     */
    public Object getAgent(String name, Entry[] matches) throws NoSuchAgentException ;
    
    /**
     * Returns an agent, or object <i>representing</i> an agent where methods invoked on that agent are guaranteed to block until
     * the method returns, that also matches the given meta attributes. This method blocks until either:<br/>
     * <ul><li> a matching agent is found OR</li><li>the specified timeout has expired, upon which a NoSuchAgentException is thrown</li>
     * </ul>
     * A timeout of zero indicates that the method will block indefinitely. 
     */
    public Object getAgent(String name, Entry[] matches, int timeout) throws NoSuchAgentException ;
    
    /**
     * Returns an agent, or object <i>representing</i> an agent where methods invoked on that agent are guaranteed to return immediately
     * without waiting for the method to complete. <em> Consequently, it is not advised to check the return types of methods once they are called, as they are likely
     * to be null.</em>, The <em>contextHandler</em> parameter should be used to reestablish context
     */
    
    public Object getAsynchronousAgent(String name, AsyncHandler contextHandler) throws NoSuchAgentException ;
	  //public Object getAsynchronousAgent(String name,Entry[] matches, AsyncHandler contextHandler) throws NoSuchAgentException ;
    public Object getAsynchronousAgent(String name) throws NoSuchAgentException;
//	public Object getAsynchronousAgent(String name,Entry[] matches) throws NoSuchAgentException;
}
