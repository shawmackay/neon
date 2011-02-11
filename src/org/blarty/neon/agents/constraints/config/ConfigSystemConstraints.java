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
 * neon : org.jini.projects.neon.agents.newconstraints.config
 * ConfigSystemConstraints.java
 * Created on 09-Mar-2005
 *ConfigSystemConstraints
 */
package org.jini.projects.neon.agents.constraints.config;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;

import org.jini.projects.neon.agents.constraints.SystemConstraints;

/**
 * @author calum
 */
public class ConfigSystemConstraints implements SystemConstraints{
	
	private Configuration config;
	
	public ConfigSystemConstraints(Configuration config){
		this.config = config;
	}

	public String getEndian() {
		// TODO Complete method stub for getEndian
		try {
			return (String) config.getEntry("org.jini.projects.neon.agents.restrictions","endian", String.class,"any");
		} catch (ConfigurationException e) {
			// TODO Handle ConfigurationException
			e.printStackTrace();
		}
		return "any";
	}

	public String getJVMVersion() {
		// TODO Complete method stub for getJVMVersion
		try {
			return (String) config.getEntry("org.jini.projects.neon.agents.restrictions","jvmVersion", String.class,"any");
		} catch (ConfigurationException e) {
			// TODO Handle ConfigurationException
			e.printStackTrace();
		}
		return "any";
	}

	public int getMemMinimumMBFree() {
		// TODO Complete method stub for getMemMinimumMBFree
		try {
			return ((Integer) config.getEntry("org.jini.projects.neon.agents.restrictions","memoryMinimumMB", Integer.class,new Integer(1))).intValue();
		} catch (ConfigurationException e) {
			// TODO Handle ConfigurationException
			e.printStackTrace();
		}
		return 0;
	}

	public int getMemPercentFree() {
		// TODO Complete method stub for getMemPercentFree
		try {
			return ((Integer) config.getEntry("org.jini.projects.neon.agents.restrictions","memoryPCfree", Integer.class,new Integer(1))).intValue();
		} catch (ConfigurationException e) {
			// TODO Handle ConfigurationException
			e.printStackTrace();
		}
		return 0;
	}

	public int getMinCPUNumber() {
		try {
			return ((Integer)config.getEntry("org.jini.projects.neon.agents.restrictions","cpuNumber", Integer.class,new Integer(1))).intValue();
		} catch (ConfigurationException e) {
			// TODO Handle ConfigurationException
			e.printStackTrace();
		}
		return 1;
	}

	public String getOSArchitecture() {
		try {
			return (String) config.getEntry("org.jini.projects.neon.agents.restrictions","osarch", String.class,"any");
		} catch (ConfigurationException e) {
			// TODO Handle ConfigurationException
			e.printStackTrace();
		}
		return "any";
	}

	public String getOSFamily() {
		try {
			return (String) config.getEntry("org.jini.projects.neon.agents.restrictions","osfamily", String.class,"any");
		} catch (ConfigurationException e) {
			// TODO Handle ConfigurationException
			e.printStackTrace();
		}
		return "any";
	}

	public String getOSVersion() {
		try {
			return (String) config.getEntry("org.jini.projects.neon.agents.restrictions","osversion", String.class,"any");
		} catch (ConfigurationException e) {
			// TODO Handle ConfigurationException
			e.printStackTrace();
		}
		return "any";
	}

	public String getStatus() {
		try {
			return ((String) config.getEntry("org.jini.projects.neon.agents.restrictions","status", String.class,"any"));
		} catch (ConfigurationException e) {
			// TODO Handle ConfigurationException
			e.printStackTrace();
		}	
		return "any";
	}
}
