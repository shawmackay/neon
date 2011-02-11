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
* ServiceConfig.java
*
* Created Fri Mar 18 11:36:50 GMT 2005
*/

/**
*
* @author  calum
*
*/

public class ServiceConfig{	
	private int theMaxAgentsOfType;
	private int theMaxOfAncestor;
	private boolean useThreadGroups;
	
	/**
	* getTheMaxAgentsOfType
	* @return int
	*/
	public int getTheMaxAgentsOfType(){
		return theMaxAgentsOfType;
	}
	/**
	* setTheMaxAgentsOfType
	* @param theMaxAgentsOfType
	*/
	public void setTheMaxAgentsOfType(int theMaxAgentsOfType){
		this.theMaxAgentsOfType=theMaxAgentsOfType;
	}
	/**
	* getTheMaxOfAncestor
	* @return int
	*/
	public int getTheMaxOfAncestor(){
		return theMaxOfAncestor;
	}
	/**
	* setTheMaxOfAncestor
	* @param theMaxOfAncestor
	*/
	public void setTheMaxOfAncestor(int theMaxOfAncestor){
		this.theMaxOfAncestor=theMaxOfAncestor;
	}
	/**
	* getUseThreadGroups
	* @return boolean
	*/
	public boolean getUseThreadGroups(){
		return useThreadGroups;
	}
	/**
	* setUseThreadGroups
	* @param useThreadGroups
	*/
	public void setUseThreadGroups(boolean useThreadGroups){
		this.useThreadGroups=useThreadGroups;
	}

}
