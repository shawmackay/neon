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

package org.jini.projects.neon.vertigo.management;

/*
* AgentListObject.java
*
* Created Wed May 18 10:31:08 BST 2005
*/
import java.util.Collection;
import java.util.Iterator;

import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.host.AgentRegistry;
import org.jini.projects.neon.host.DomainRegistry;
import org.jini.projects.neon.host.ManagedDomain;


/**
*
* @author  calum
*
*/

public class AgentListObject{
		private String agentFqName;
		private AgentIdentity AgentID;
		private String agentClassName;
		private String domain;
		private boolean resolved;
	/**
	* AgentListObject
	* @param agentFqName
	* @param AgentID
	* @param agentClassName
	*/
	public AgentListObject(String agentFqName, AgentIdentity AgentID, String agentClassName, String domain){
		this.agentFqName=agentFqName;
		this.AgentID=AgentID;
		this.agentClassName=agentClassName;
		this.domain = domain;
		Collection c = DomainRegistry.getDomainRegistry().getDomains();
		for (Iterator iter = c.iterator(); iter.hasNext();) {
			ManagedDomain dom = (ManagedDomain) iter.next();
			AgentRegistry reg = dom.getRegistry();
			if (reg.contains(AgentID)) {
				setResolved(true);
				break;
			}
		}
	}

	public boolean isResolved(){
		return this.resolved;
	}
	
	public void setResolved(boolean resolved){
		this.resolved = resolved;
	}
	
	/**
	* getAgentFqName
	* @return String
	*/
	public String getAgentFqName(){
		return agentFqName;
	}
	/**
	* setAgentFqName
	* @param agentFqName
	*/
	public void setAgentFqName(String agentFqName){
		this.agentFqName=agentFqName;
	}
	public AgentIdentity getAgentID(){
		return AgentID;
	}
	public void setAgentID(AgentIdentity AgentID){
		this.AgentID=AgentID;
	}
	/**
	* getAgentClassName
	* @return String
	*/
	public String getAgentClassName(){
		return agentClassName;
	}
	/**
	* setAgentClassName
	* @param agentClassName
	*/
	public void setAgentClassName(String agentClassName){
		this.agentClassName=agentClassName;
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
