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
* AgentDef.java
*
* Created Fri Mar 18 11:47:45 GMT 2005
*/

/**
*
* @author  calum
*
*/

public class AgentDef{
	private String classname;
	private int number;
	private int waitAfterInit;
	private String configurationURL;
    private String constraints;
	/**
	* getClassname
	* @return String
	*/
	public String getClassname(){
		return classname;
	}
	/**
	* setClassname
	* @param classname
	*/
	public void setClassname(String classname){
		this.classname=classname;
	}
	/**
	* getNumber
	* @return int
	*/
	public int getNumber(){
		return number;
	}
	/**
	* setNumber
	* @param number
	*/
	public void setNumber(int number){
		this.number=number;
	}
	
	/**
	* AgentDef
	* @param classname
	* @param number
	* @param configurationURL
	*/
	public AgentDef(String classname, int number, String configurationURL, String waitAfterInit, String constraints){
		this.classname=classname;
		this.number=number;
		this.configurationURL=configurationURL;
        this.constraints = constraints;
		if(waitAfterInit!=null){			
			try {
				this.waitAfterInit = Integer.parseInt(waitAfterInit);
			} catch (NumberFormatException ex){
				this.waitAfterInit = 0;
			}
		}else{
			this.waitAfterInit=0;
		}
	}
	
	/**
	* getConfigurationURL
	* @return String
	*/
	public String getConfigurationURL(){
		return configurationURL;
	}
	/**
	* setConfigurationURL
	* @param configurationURL
	*/
	public void setConfigurationURL(String configurationURL){
		this.configurationURL=configurationURL;
	}
	
	/**
	* getWaitAfterInit
	* @return int
	*/
	public int getWaitAfterInit(){
		return waitAfterInit;
	}
	/**
	* setWaitAfterInit
	* @param waitAfterInit
	*/
	public void setWaitAfterInit(int waitAfterInit){
		this.waitAfterInit=waitAfterInit;
	}
        public String getConstraints() {
                return constraints;
        }
        public void setConstraints(String constraints) {
                this.constraints = constraints;
        }
	
}
