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
* neon : org.jini.projects.neon.host
*
*
* AgentDispatcher.java
* Created on 02-Mar-2004
*
* AgentDispatcher
*
*/
package org.jini.projects.neon.host;

import net.jini.id.Uuid;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentState;
import org.jini.projects.zenith.messaging.endpoints.Dispatchable;
import org.jini.projects.zenith.messaging.messages.Message;
import org.jini.projects.zenith.messaging.system.ChannelException;
import org.jini.projects.zenith.messaging.system.MessagingManager;

/**
 * A simple class that is bound into a MessageDispatcher and dispatches a message to the correct channel
* @author calum
*/
public class AgentDispatcher implements Dispatchable {
	Uuid privateChannel = null;
	Agent a;
	private boolean avail = true;
	public AgentDispatcher(Agent a){
		privateChannel = a.getIdentity().getID();
		this.a =a;
	}
	
	/* @see org.jini.projects.zenith.messaging.endpoints.Dispatchable#process(org.jini.projects.zenith.messaging.messages.Message)
	*/
	public void process(Message m) {
		// TODO Complete method stub for process
		
		try { 
			
			MessagingManager.getManager().getPublishingConnector(privateChannel.toString()).sendMessage(m);
		} catch (ChannelException e) {
			// URGENT Handle ChannelException
			e.printStackTrace();
		}
	}
	
	/* @see org.jini.projects.zenith.messaging.endpoints.Dispatchable#isAvailable()
	*/
	public boolean isAvailable() {
		// TODO Complete method stub for isAvailable
		return a.getAgentState().equals(AgentState.AVAILABLE);
	}
	
	/* @see org.jini.projects.zenith.messaging.endpoints.Dispatchable#setAvailable(boolean)
	*/
	public void setAvailable(boolean avail) {
		//this.avail = avail;
	}
	
	/* @see java.lang.Object#equals(java.lang.Object)
	*/
	public boolean equals(Object obj) {
		// TODO Complete method stub for equals
		if(obj instanceof AgentDispatcher){
			AgentDispatcher cmp = (AgentDispatcher) obj;
			if(cmp.privateChannel.equals(privateChannel))
				return true;
			else
			return false;
		}
		
		if(obj instanceof Uuid){
			
			if (((Uuid) obj).equals(privateChannel))
				return true;
		}
		return false;
	}
	/* @see java.lang.Object#hashCode()
	*/
	public int hashCode() {
		// TODO Complete method stub for hashCode
		return a.hashCode();
	}
	/* @see java.lang.Object#toString()
	*/
	public String toString() {
		// TODO Complete method stub for toString
		return "Dispatcher for " + privateChannel + " [" + a.getNamespace() + "." + a.getName() +"]";
	}
	/* @see net.jini.id.ReferentUuid#getReferentUuid()
	*/
	public Uuid getReferentUuid() {
		// TODO Complete method stub for getReferentUuid
		return privateChannel;
	}
}
