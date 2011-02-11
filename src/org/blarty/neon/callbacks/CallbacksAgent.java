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
 * neon : org.jini.projects.neon.callbacks
 * CallbacksAgent.java
 * Created on 23-Sep-2003
 */
package org.jini.projects.neon.callbacks;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.jini.core.event.RemoteEventListener;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.collaboration.Collaborative;


/**
 * Messaging interface implemented by <code>CallbacksAgentImpl</code> 
 * @author calum
 */
public interface CallbacksAgent extends Collaborative,Remote {
	/**
	 * Requests the sending of a callback from a host
	 * to any attached listener. This is essentially an agent Broadcast -
	 * All agents of this type will receive this message.
	 * @param name the Agent name
	 * @param message
	 * @return
	 * @throws RemoteException TODO
	 */
	public abstract boolean broadcast(String name, Object message) throws RemoteException;
	/**
	 * Requests the sending of a callback from a host
	 * to any attached listener. This is essentially an agent Broadcast -
	 * All agents of this type will receive this message.
	 * @param ID the distinct Agent Identity
	 * @param message
	 * @return
	 * @throws RemoteException TODO
	 */
	public abstract boolean sendCallback(AgentIdentity ID, Object message) throws RemoteException;
    /**
     * Returns the callback for the given agent.
     * @param ag the agent needed to find the callback
     * @return the callback registered for the agent, if one exists
     * @throws RemoteException TODO
     */
	public abstract RemoteEventListener getMyCallback(Agent ag) throws RemoteException;
}