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
 * neon : org.jini.projects.neon.agents.tests
 * MockAgent.java
 * Created on 09-May-2005
 *MockAgent
 */
package org.jini.projects.neon.agents.tests;

import org.jini.projects.neon.agents.AbstractAgent;

/**
 * @author calum
 */
public class MockAgent extends AbstractAgent implements Runnable {
    
    public MockAgent(String name, String namespace){
        this.name = name;
        this.namespace = namespace;
    }
    
    @Override
    public boolean init() {
        // TODO Complete method stub for init
        getAgentLogger().info("Initialising MockAgent: " + name);
        return true;
    }
    
    
    public void run() {
        // TODO Complete method stub for start
        getAgentLogger().info("Starting MockAgent: " + name);
    }
    
    public void complete() {
        // TODO Complete method stub for complete
        getAgentLogger().info("Completing MockAgent: " + name);
        super.complete();
    }
    
    public void stop() {
        // TODO Complete method stub for stop
        getAgentLogger().info("Stopping MockAgent: " + name);
        super.stop();
    }
    
}
