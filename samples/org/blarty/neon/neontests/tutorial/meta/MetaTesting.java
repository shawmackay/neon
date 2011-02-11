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

package org.jini.projects.neon.neontests.tutorial.meta;

import net.jini.config.Configuration;
import net.jini.lookup.entry.Name;

import org.jini.projects.neon.agents.AbstractAgent;

public class MetaTesting extends AbstractAgent implements MetaTest {

    Integer configuredValue = 0;

    public MetaTesting(){
        this.name = "Meta";
        this.namespace = "neon.testing";
    }
    
    @Override
    public boolean init() {
        // TODO Auto-generated method stub
        try {
            
            Configuration config = getAgentConfiguration();
            configuredValue = (Integer) config.getEntry("org.jini.projects.neon.testing.meta", "configuredValue", Integer.class);
            this.getMetaAttributes().addAttribute(new Name("MetaTest" +configuredValue));
            System.out.println("Obtained configuration and set COnfiguration value to " + configuredValue);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

   

    public Integer getValue() {
        // TODO Auto-generated method stub
        return configuredValue;
    }
}
