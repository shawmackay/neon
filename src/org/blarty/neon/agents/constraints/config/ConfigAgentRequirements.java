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
 *neon : org.jini.projects.neon.agents.newconstraints.config
 * ConfigAgentRequirements.java
 * Created on 09-Mar-2005
 *ConfigAgentRequirements
 */
package org.jini.projects.neon.agents.constraints.config;

import java.util.ArrayList;
import java.util.List;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;

import org.jini.projects.neon.agents.constraints.AgentRequirements;
import org.jini.projects.neon.agents.constraints.Restrictions;

/**
 * A set of agent requirements built from a Jini configuration file
 * @author calum
 */
public class ConfigAgentRequirements implements AgentRequirements {

	Configuration config;
	
	public ConfigAgentRequirements(Configuration config){
		this.config = config;
	}
	
	public String getDomain() {
		// TODO Complete method stub for getDomain
		try {
			return (String) config.getEntry("org.jini.projects.neon.agents.restrictions","domain", String.class,"global");
		} catch (ConfigurationException e) {
			// TODO Handle ConfigurationException
			e.printStackTrace();
		}
		return "global";
	}

	public Restrictions getSystemRestrictions() {
		// TODO Complete method stub for getSystemRestrictions
		return new ConfigRestrictions(config);
	}

	public List getPrivileges() {
		ArrayList arr= new ArrayList();

		try {
			String[] privs = (String[])config.getEntry("org.jini.projects.neon.agents.restrictions","privileges", String.class,new String[0]);
			for(int i=0;i<privs.length;i++)
				arr.add(privs[i]);
		} catch (ConfigurationException e) {
			// TODO Handle ConfigurationException
			e.printStackTrace();
		}
		return arr;
	}

}
