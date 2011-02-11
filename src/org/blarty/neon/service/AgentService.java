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


package org.jini.projects.neon.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Remote;
import java.rmi.RemoteException;

import net.jini.admin.Administrable;
import net.jini.core.entry.Entry;
import net.jini.core.event.RemoteEventListener;
import net.jini.id.Uuid;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.Meta;
import org.jini.projects.neon.dynproxy.AgentProxyInfo;
import org.jini.projects.neon.dynproxy.MethodInvocation;
import org.jini.projects.neon.dynproxy.PojoAgentProxyFactory;
import org.jini.projects.neon.host.AgentDomain;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.zenith.messaging.messages.Message;




/**
 * Represents a service capable of hosting agents.
 */
public interface AgentService extends Administrable, Remote{
    public void setAgentHost(AgentDomain host) throws RemoteException;
    /** Send a message along the internal message bus */
    public Object sendMessage(String agentName, String domain, Message message) throws NoSuchAgentException,RemoteException;
   // public boolean canAccept(AgentConstraints constraints) throws RemoteException;
    /**
     * Deploy a serializable POJO, wrapping it internally as it does so with the appropriate
     * constraints and configuration information.
     * @param POJO a serializable instance
     * @param constraintsLocation a url for the agent constraints
     * @param configurationLocation url pointing to the agents configuration
     * @throws MalformedURLException if the constraints or configuration urls are not correctly formatted
     * @throws RemoteException
     */
    public void deployPOJOAgent(Object POJO, String constraintsLocation, String configurationLocation) throws MalformedURLException,RemoteException;
    /**
     * Deploy a serializable POJO as an agent to the specified domain, overriding constraints, wrapping it internally as it does so with the appropriate
     * constraints and configuration information.
     * @param POJO a serializable instance
     * @param constraintsLocation a url for the agent constraints
     * @param configurationLocation url pointing to the agents configuration
     * @param domain name of the domain to deploy the agent to
     * @throws MalformedURLException if the constraints or configuration urls are not correctly formatted
     * @throws RemoteException
     */
    public void deployPOJOAgent(Object POJO, String constraintsLocation, String configurationLocation, String domain) throws MalformedURLException,RemoteException;
    /**
     * Instruct the service to create a new agents of the specified class, with the appropriate constraints and configuration info, in the specified domain.
     * The class must exist on the classpath of the service, and have a no-arg constructor
     * @param classname Name of class to be created
    * @param constraintsLocation a url for the agent constraints
     * @param configurationLocation url pointing to the agents configuration
     * @param domain name of the domain to deploy the agent to
     * @throws RemoteException
     */
    public void createAgent(String classname, String constraintsurl, String configurl, String domain) throws RemoteException;
    
    /**
     * Deploy an existing agent into the system. If the constraints do not specify  domain, the agent will be placed in the Global domain
     * @param agent the agent instance
     * @throws RemoteException
     */
    public void deployAgent( Agent agent) throws RemoteException;
    /**
     * Deploy an existing agent into the system, into the specified domain
     * @param agent the agent instance
     * @throws RemoteException
     */
    public void deployAgent( Agent agent, String domain) throws RemoteException;
    
    /**
     * Deploy an existing agent with a callback listener into the system. If the constraints do not specify  domain, the agent will be placed in the Global domain.
     * @param agent the agent instance
     * @param callback a {@link RemoteEventListener} for receiving messages back from the agent 
     * @throws RemoteException
     */
    public void deployAgent( Agent agent, RemoteEventListener callback) throws RemoteException;
    public void deployAgent( Agent agent, String domain, RemoteEventListener callback) throws RemoteException;
	public void deploySeeding(URL seedFromLocation) throws RemoteException;
    public AgentProxyInfo getAgentProxyInfo(String name, String domain) throws RemoteException;
    public AgentProxyInfo getAgentProxyInfo(String name, Entry[] meta, String domain) throws RemoteException;
    public AgentProxyInfo getAgentProxyInfo(AgentIdentity id, String domain) throws RemoteException;
    public Object getStatelessAgent(String name, String domain) throws RemoteException;  
    
    public Object invoke(AgentIdentity identity, MethodInvocation invocation) throws RemoteException;
}
