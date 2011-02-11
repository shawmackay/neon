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
 * AgentConstraintsEntry.java
 * Created on 18-Jul-2003
 */
package org.jini.projects.neon.agents.util;

import net.jini.entry.AbstractEntry;
import net.jini.id.Uuid;

import org.jini.projects.neon.agents.AgentConstraints;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.Meta;

/**
 * Represents a persisted version of an agents' constraints. 
 * @author calum
 */
public class AgentConstraintsEntry extends AbstractEntry {
	private static final long serialVersionUID = 6L;
	public String referentAgentIdentity;
	public AgentConstraints agentConstraints;
	public Uuid lastHost;
	public String state;
    public String domain;
    public Meta meta;
	public String name;
    public String namespace;
    public Integer loadingPriority;
    
	public AgentConstraintsEntry(String ID, AgentConstraints ac, Uuid lastHost, String state, String domain, Meta meta, String name, String namespace){
		this.referentAgentIdentity = ID;
		this.agentConstraints = ac;
		this.lastHost = lastHost;
		this.state = state;
        this.domain = domain;
        this.meta = meta;
        this.name = name;
        this.namespace = namespace;
	}
	
	/**
	 * 
	 */
	public AgentConstraintsEntry() {
		
		// TODO Complete constructor stub for AgentConstraintsEntry
	}

	public String toString(){
		return "Uuid: " +referentAgentIdentity.toString() +"; State:  " + state +"; Name: " + name +"." + namespace;
	}
}
