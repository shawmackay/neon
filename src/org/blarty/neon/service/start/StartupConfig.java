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
* StartupConfig.java
*
* Created Fri Mar 18 14:50:27 GMT 2005
*/

/**
*
* @author  calum
*
*/

public class StartupConfig{
	
	private java.util.List<DomainConfig> domainConfigs;
	
	/**
	* StartupConfig	
	* @param domainConfigs List of <code>DomainConfig</code> instances listed in the startup file
	*/
	public StartupConfig(java.util.List<DomainConfig> domainConfigs){	
		this.domainConfigs=domainConfigs;
	}

	
	/**
	* getDomainConfigs
	* @return java.util.List
	*/
	public java.util.List<DomainConfig> getDomainConfigs(){
		return domainConfigs;
	}
	/**
	* setDomainConfigs
	* @param domainConfigs
	*/
	public void setDomainConfigs(java.util.List<DomainConfig> domainConfigs){
		this.domainConfigs=domainConfigs;
	}

}
