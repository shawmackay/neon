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
 * TransferAgent.java
 * Created on 23-Sep-2003
 */
package org.jini.projects.neon.service;

import java.rmi.RemoteException;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.collaboration.Collaborative;


/**
 * @author calum
 */
public interface TransferAgent extends Collaborative, java.rmi.Remote{
	public abstract void transferAgent(Agent ag) throws RemoteException;
	public abstract void transferAgent(AgentIdentity ag) throws RemoteException;
	public abstract Boolean transferAgentTo(Agent ag, AgentService host) throws RemoteException;
	public abstract Boolean transferAgentTo(AgentIdentity ag, AgentService host) throws RemoteException;
	public void moveDomain(Agent ag, String newDomain) throws RemoteException;
	public void moveDomain(AgentIdentity ag, String newDomain) throws RemoteException;
	public void freeze(Agent ag) throws RemoteException;
	public void thaw(AgentIdentity agentIdentity) throws RemoteException;
}
