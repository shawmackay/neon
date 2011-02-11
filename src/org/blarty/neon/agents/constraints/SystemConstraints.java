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
 * neon : org.jini.projects.neon.agents.newconstraints
 * SystemConstraints.java
 * Created on 09-Mar-2005
 *SystemConstraints
 */
package org.jini.projects.neon.agents.constraints;

/**
 * Imposes a number of physical system constraints upon the deployment of an agent
 * @author calum
 */
public interface SystemConstraints {
    /**
     * Get the OS Family name, i.e. "Windows XP", "Linux"
     * @return os name
     */
	public String getOSFamily();
    /**
     * Get the OS Version number i.e. "5.1","2.6.9-3"
     * @return os name
     */
	public String getOSVersion();
    /**
     * Get the OS Architecture , i.e. "x86", "i386"
     * @return os name
     */
	public String getOSArchitecture();
	
    /**
     * What kind of load the system should be under in order to deploy
     * @return
     */
	public String getStatus();
	/** 
     * Get the minimum number of CPUs that the machine/JVM needs to be using, in order
     * for this agent to deploy
     * @return  
	 */
	public int getMinCPUNumber();
    /** 
     * Get the minimum percent of available free memory, that this agent requires for deployment
     * for this agent to deploy
     * @return  
     */
	public int getMemPercentFree();
    /** 
     * Get the minimum amount in MB of available free memory, that this agent requires for deployment    
     *  @return  
     */
	public int getMemMinimumMBFree();
	/**
     * Get the minimum JDK version that this agent requires for execution.
     * @return
	 */
	public String getJVMVersion();
    /**
     * Get the binary endian format for this agent - useful if agent needs to deal with binary data from other systems.
     * @return
     */
	public String getEndian();
}
