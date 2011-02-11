//
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



package org.jini.projects.neon.agents.constraints;

import java.util.List;
/**
 * Handles the requirements that are placed upon an agent's deployment, in order to ensure, that if these
 * requirements are not met by a host, the agent will not deploy to that host. 
 * @author Calum
 *
 */
public interface AgentRequirements{
    /**
     * If an agent has to be deployed into a particular domain, the host must be running a partition in that domain
     * @return
     */
	public String getDomain();
	/**
     * Return the set of System restrictions that must be met by the host before the agent can deploy 
     * @return
	 */
	public Restrictions getSystemRestrictions();
	/**
     * NOT USED at the moment 
     * @return
	 */
	public List getPrivileges();
	
}
