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
 * ConfigRestrictions.java
 * Created on 09-Mar-2005
 *ConfigRestrictions
 */
package org.jini.projects.neon.agents.constraints.config;

import java.util.ArrayList;
import java.util.List;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;

import org.jini.projects.neon.agents.constraints.Restrictions;
import org.jini.projects.neon.agents.constraints.SystemConstraints;

/**
 * A set of restrictions  built from a Jini configuration file.
 * Uses the <code>org.jini.projects.neon.agents.restrictions</code> configuration component name
 * @author calum
 */
public class ConfigRestrictions implements Restrictions {
	
	private Configuration config;
	
	public ConfigRestrictions(Configuration config){
		this.config = config;
	}

    /**
     * Created from <code>org.jini.projects.neon.agents.restrictions.meta</code><br>
     * Match against a set of meta data defined as Entries. Any entries used
     * in the meta information, must be available on the local classpath of the neon instance
     * and not in the agent download (dl) file, as constraints are evaluated before the agent is sent
     * to the instance.
     * @return a list of <code>Entry</code> objects.
     */
	public List getMeta() {
		ArrayList arr= new ArrayList();

		try {
			String[] privs = (String[])config.getEntry("org.jini.projects.neon.agents.restrictions","meta", String.class,new String[0]);
			for(int i=0;i<privs.length;i++)
				arr.add(privs[i]);
		} catch (ConfigurationException e) {
			// TODO Handle ConfigurationException
			e.printStackTrace();
		}
		return arr;
	}
/**
 *  Created from <code>org.jini.projects.neon.agents.restrictions.requiredClasses</code><br>
 *  Get a list of classes that should be available on the host system, within the main system ClassLoader
     * Such that <code>Class.forName(<i>X</i>)</code> return true for each X in the list  
     * @return a list of Strings representing class names of type
 */
    
	public List getRequiredClasses() {
		ArrayList arr= new ArrayList();

		try {
			String[] privs = (String[])config.getEntry("org.jini.projects.neon.agents.restrictions","requiredClasses", String.class,new String[0]);
			for(int i=0;i<privs.length;i++)
				arr.add(privs[i]);
		} catch (ConfigurationException e) {
			// TODO Handle ConfigurationException
			e.printStackTrace();
		}
		return arr;
	}

	public SystemConstraints getSystemConstraints() {
		// TODO Complete method stub for getSystemConstraints
		return new ConfigSystemConstraints(config);
	}
/**
 *  Created from <code>org.jini.projects.neon.agents.restrictions.requiredInstances</code><br>
 *    Return a list of agent names (<i>&quot;namespace&quot;.&quot;name&quot;</i>) that the domain
     * needs to be hosting at deployment time. <i>Currently, not evaluated</i>
     * @return a list of Strings of agent names (i.e <i>String[]{"neon.Services","neon.CheckPoint"}</i>,etc 
 */
	public List getInstances() {
		ArrayList arr= new ArrayList();

		try {
			String[] instances = (String[])config.getEntry("org.jini.projects.neon.agents.restrictions","requiredInstances", String.class,new String[0]);
			for(int i=0;i<instances.length;i++)
				arr.add(instances[i]);
		} catch (ConfigurationException e) {
			// TODO Handle ConfigurationException
			e.printStackTrace();
		}
		return arr;
	}

}
