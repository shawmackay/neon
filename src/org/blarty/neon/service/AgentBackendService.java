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
 * neon : org.jini.projects.neon.service
 * AgentBackendService.java
 * Created on 19-Feb-2004
 *AgentBackendService
 */
package org.jini.projects.neon.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Remote;
import java.rmi.RemoteException;

import net.jini.admin.Administrable;
import net.jini.core.entry.Entry;
import net.jini.core.event.RemoteEventListener;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentConstraints;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.dynproxy.AgentProxyInfo;
import org.jini.projects.neon.dynproxy.MethodInvocation;
import org.jini.projects.neon.host.AgentDomain;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.zenith.messaging.messages.Message;

/**
 * @author calum
 */
public interface AgentBackendService extends Administrable, Remote {
	public void setAgentHost(AgentDomain host) throws RemoteException;

	public Object sendMessage(String agentName, String domain, Message message) throws NoSuchAgentException, RemoteException;

	public void deploySeeding(URL seedFromLocation) throws RemoteException;

	public void deployAgent(AgentConstraints constraints, CodebaseObject agent) throws RemoteException;

	public void deployAgent(AgentConstraints constraints, CodebaseObject agent, RemoteEventListener callback) throws RemoteException;

	public void deployAgent(AgentConstraints constraints, CodebaseObject agent, String domain, RemoteEventListener callback) throws RemoteException;

	public void deployAgent(AgentConstraints constraints, CodebaseObject agent, String domain) throws RemoteException;

	public AgentProxyInfo getAgentProxyInfo(String name, String domain) throws RemoteException;

	public AgentProxyInfo getAgentProxyInfo(AgentIdentity id, String domain) throws RemoteException;

	public AgentProxyInfo getAgentProxyInfo(String name, Entry[] meta, String domain) throws RemoteException;

	public void createAgent(String classname, String constraintsurl, String configurl, String domain) throws RemoteException;

	public Object getStatelessAgent(String name, String domain) throws RemoteException;

	public Object getExportableAgent(String name, String domain) throws RemoteException;

	public void deployPOJOAgent(Object pojo, String constraintsLocation, String configurationLocation) throws MalformedURLException, RemoteException;

	public void deployPOJOAgent(Object pojo, String constraintsLocation, String configurationLocation, String domain) throws MalformedURLException, RemoteException;
	
	public Object invoke(AgentIdentity identity, MethodInvocation invocation) throws RemoteException;
}
