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

import java.io.Serializable;
import java.net.URL;

import org.jini.projects.neon.agents.constraints.AgentRequirements;



/**
 * Remote clients should use this to reference constraints for agents
 * At the agent host the URL is downloaded and parsed
 */
public class ReferenceableConstraints implements AgentConstraints, Serializable {
    URL location;

    public ReferenceableConstraints(URL location) {
        this.location = location;
    }

	/**
	 * Because JAXB classes are not inherently <code>Serializable</code>, Referenceable Constraints
	 * delegate Constraint creation to an <code>XMLAgentContraint</code>s instance at runtime, effectively
	 * allowing the unpacking of constraints stored at a URL 
	 */	
    public AgentRequirements getConstraints() {

        try {
			
            return new ConfigurationConstraints(location).getConstraints();
        } catch (Exception e) {

            System.out.println("Err: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean hasConstraint(String constraintName) {
        return false;
    }
}
