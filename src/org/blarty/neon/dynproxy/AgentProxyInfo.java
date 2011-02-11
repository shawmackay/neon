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
 * neon : org.jini.projects.neon.dynproxy
 * AgentProxyInfo.java
 * Created on 11-Mar-2004
 *AgentProxyInfo
 */
package org.jini.projects.neon.dynproxy;

import java.io.Serializable;

import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.service.AgentBackendService;
import org.jini.projects.neon.service.AgentService;


/**
 * @author calum
 */
public class AgentProxyInfo implements Serializable{
    AgentIdentity id ;
    String agentName;
    Class[] collaborativeInterfaces;
    String domain;
    AgentBackendService theAgentService;
	/**
	 * @param id
	 * @param agentName
	 * @param collaborativeInterfaces
	 */
	public AgentProxyInfo(AgentIdentity id, String agentName, Class[] collaborativeInterfaces, AgentBackendService remoteAgent, String domain) {
		super();
		this.id = id;
		this.agentName = agentName;
		this.collaborativeInterfaces = collaborativeInterfaces;
		this.theAgentService = remoteAgent;
		this.domain = domain;
	}
    
    
	public String getDomain() {
		return domain;
	}


	/**
	 * @return Returns the agentName.
	 */
	public String getAgentName() {
		return this.agentName;
	}
	/**
	 * @return Returns the collaborative Interfaces.
	 */
	public Class[] getCollaborativeInterfaces() {
		return this.collaborativeInterfaces;
	}
	/**
	 * @return Returns the id.
	 */
	public AgentIdentity getId() {
		return this.id;
	}


	public AgentBackendService getAgentService() {
		return theAgentService;
	}
}
