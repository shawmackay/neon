/*
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



package org.jini.projects.neon.agents;

import java.io.File;
import java.net.URL;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationProvider;

import org.jini.projects.neon.agents.constraints.AgentRequirements;
import org.jini.projects.neon.agents.constraints.config.ConfigAgentRequirements;

/**
 * @author calum
 */
public class ConfigurationConstraints implements AgentConstraints{
	Configuration config;
	ConfigurationConstraints restricted;

    public ConfigurationConstraints(URL constraintLocation) {
        this(constraintLocation.toExternalForm());
    }

    public ConfigurationConstraints(File localConstraints) {
			this(localConstraints.getAbsolutePath());
    }

    public ConfigurationConstraints(String constraints) {
		try {
            config= ConfigurationProvider.getInstance(new String[] {constraints});
        } catch (Exception e) {

            System.out.println("Err: " + e.getMessage());
            e.printStackTrace();
        }
    }
	
	
	
	public AgentRequirements getConstraints() {
		// TODO Complete method stub for getConstraints
		return new ConfigAgentRequirements(config);
	}

}
