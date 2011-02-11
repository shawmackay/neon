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
 * AgentAdmin.java
 * Created on 22-Sep-2003
 */
package org.jini.projects.neon.service.admin;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.jini.projects.neon.agents.AgentIdentity;

/**
 * @author calum
 */
public interface AgentAdmin extends Remote {
	/**
	 * Get the set of local domains that this instance is currently running
	 * @return description objects for all the partitions (local domains) in this instance
	 * @throws RemoteException
	 */
	public DomainDescription[] getDomains() throws RemoteException;
	/**
	 * Gets the name of the instance configuration file
	 * @return name of the Global Configuration file
	 * @throws RemoteException
	 */
	public String getGlobalConfigFile() throws RemoteException;
	/**
	 * Returns whether Neon is using Thread Groups
	 * @return currently always false
	 * @throws RemoteException
	 */
	public boolean isUsingThreadGroups() throws RemoteException;
	/**
	 * Get the maximum number of any one agent class that can run on this instance
	 * @return description objects for all the partitions (local domains) in this instance
	 * @throws RemoteException
	 */
	public int getMaxAgentsOfType() throws RemoteException;
	/**
	 * Get the maximum number of agents that can have a common ancestor class (excluding <code>AbstractAgent</code>)
	 * @return max number of derivable agents from any one base class
	 * @throws RemoteException
	 */
	public int getMaxAgentsOfAncestor() throws RemoteException;
	/**
	 * Determine whether an agent with the given identity is currently hosted in this instance (irrespective of domain)
	 * @param id Identity of the agent
	 * @return whether the agent is locatable in this instance or not
	 * @throws RemoteException
	 */
	public boolean containsAgent(AgentIdentity id) throws RemoteException;
	/**
	 * <strong>Unused</strong>
	 * Packs an agent ready for deployment elsewhere
	 * @param id Identity of agent to be packed
	 * @return whether the pack process was successful or not
	 * @throws RemoteException
	 */
	public boolean packAgent(AgentIdentity id) throws RemoteException;
	
	public void updateDomainDescription(DomainDescription desc) throws RemoteException;
	
	public void removeDomain(String name) throws RemoteException;
}
