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
package org.jini.projects.neon.host.events;

import org.jini.projects.neon.agents.AgentIdentity;

public class RegistrationEvent {
	public static final int REGISTERED=1;
	public static final int DEREGISTERED=2;
	private AgentIdentity id;
	private String domain;
	private int type;
	public RegistrationEvent(AgentIdentity id, int type, String domain){
		this.id = id;
		this.domain = domain;
		this.type=type;
	}
	public AgentIdentity getId() {
		return id;
	}
	
	public void setId(AgentIdentity id) {
		this.id = id;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	/**
	* getDomain
	* @return String
	*/
	public String getDomain(){
		return domain;
	}
	/**
	* setDomain
	* @param domain
	*/
	public void setDomain(String domain){
		this.domain=domain;
	}
	
}
