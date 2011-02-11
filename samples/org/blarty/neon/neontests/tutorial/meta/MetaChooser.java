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

import net.jini.core.entry.Entry;

import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.host.NoSuchAgentException;



public class MetaChooser extends AbstractAgent implements Runnable {
    @Override
    public boolean init() {
        // TODO Auto-generated method stub
        return true;
    }

    
public void run() {
    // TODO Auto-generated method stub
    try {
        for(int i=0;i<10;i++){
    MetaTest ag1 =(MetaTest)  getAgentContext().getAgent("neon.testing.Meta",new Entry[]{new net.jini.lookup.entry.Name("MetaTest1")});
    System.out.println("Requesting Meta(1) returned: " + ag1.getValue());
        }
        
        for(int i=0;i<10;i++){
            MetaTest ag1 =(MetaTest)  getAgentContext().getAgent("neon.testing.Meta",new Entry[]{new net.jini.lookup.entry.Name("MetaTest2")});
            System.out.println("Requesting Meta(2) returned: " + ag1.getValue());
                }
        for(int i=0;i<10;i++){
            MetaTest ag1 =(MetaTest)  getAgentContext().getAgent("neon.testing.Meta");
            System.out.println("Requesting Meta(<Any>) returned: " + ag1.getValue());
                }
    } catch (NoSuchAgentException nsaex){
        nsaex.printStackTrace();
    }
}
}
