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
* AgentCatalog.java
*
* Created Fri Mar 18 11:41:23 GMT 2005
*/

/**
*
* @author  calum
*
*/

public class AgentCatalog{
	private AgentSet preSet;
	private int waitTime;
	private AgentSet postSet;
	/**
	* getPreSet
	* @return org.jini.projects.neon.service.start.AgentSet
	*/
	public AgentSet getPreSet(){
		return preSet;
	}
	/**
	* setPreSet
	* @param preSet
	*/
	public void setPreSet(AgentSet preSet){
		this.preSet=preSet;
	}
	/**
	* getWaitTime
	* @return int
	*/
	public int getWaitTime(){
		return waitTime;
	}
	/**
	* setWaitTime
	* @param waitTime
	*/
	public void setWaitTime(int waitTime){
		this.waitTime=waitTime;
	}
	/**
	* getPostSet
	* @return org.jini.projects.neon.service.start.AgentSet
	*/
	public AgentSet getPostSet(){
		return postSet;
	}
	/**
	* setPostSet
	* @param postSet
	*/
	public void setPostSet(AgentSet postSet){
		this.postSet=postSet;
	}

}
