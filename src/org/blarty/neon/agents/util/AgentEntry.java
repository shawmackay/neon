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
 * neon : org.jini.projects.neon.agents.util
 * AgentEntry.java
 * Created on 18-Jul-2003
 */
package org.jini.projects.neon.agents.util;


import net.jini.core.event.RemoteEventListener;
import net.jini.entry.AbstractEntry;
import net.jini.io.MarshalledInstance;

import org.jini.projects.neon.agents.AgentIdentity;

/**
 * Allows the storage of an Agent and it's associated callback to be encpasulated within an entry.
 * The primary lookup field is the <b>agentID</b> as retrieved from the associated <code>AgentConstraintsEntry</code>.
 *@author calum
 */
public class AgentEntry extends AbstractEntry{
	private static final long serialVersionUID = 6L;
	
	public AgentIdentity agentID;
	public String agentName;
	public MarshalledInstance encapsulatedAgent;
	public RemoteEventListener clientCallback;
	/**
	 * 
	 */
	public AgentEntry() {
		
		// TODO Complete constructor stub for AgentEntry
	}
	
	public AgentEntry(AgentIdentity ID, String name, MarshalledInstance theAgent, RemoteEventListener clientCallback){
		this.agentID = ID;
		this.agentName = name;
		this.encapsulatedAgent   = theAgent;
		this.clientCallback = clientCallback;
	}

}
