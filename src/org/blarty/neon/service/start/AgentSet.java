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


package org.jini.projects.neon.service.start;

/*
* AgentSet.java
*
* Created Fri Mar 18 11:46:56 GMT 2005
*/

/**
*
* @author  calum
*
*/

public class AgentSet{
	private java.util.List<AgentDef> agents;

	/**
	* getAgents
	* @return java.util.List
	*/
	public java.util.List<AgentDef> getAgents(){
		return agents;
	}
	/**
	* setAgents
	* @param agents
	*/
	public void setAgents(java.util.List<AgentDef> agents){
		this.agents=agents;
	}
	public void addAgent(AgentDef agent){
		agents.add(agent);	
	}
	
	/**
	* AgentSet
	* @param agents
	*/
	public AgentSet(java.util.List agents){
		this.agents=agents;
	}
	
	public AgentSet(){
		this.agents = new java.util.ArrayList<AgentDef>();
	}
	

}
