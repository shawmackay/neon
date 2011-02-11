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

package org.jini.projects.neon.agents.tests.payload;

/*
 * SimpleLocalAgent.java
 *
 * Created Fri Mar 18 10:26:59 GMT 2005
 */
import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.AgentState;
import org.jini.projects.neon.agents.LocalAgent;
/**
 *
 * @author  calum
 *
 */

public class SimpleLocalAgent extends AbstractAgent implements LocalAgent, Runnable{
    
    public SimpleLocalAgent(){
        this.name="Simple";
        this.namespace  = "payloadTest";
    }
    
    /**
     * setAgentState
     * @param newstate
     */
    public void setAgentState(AgentState  newstate){
        super.setAgentState(newstate);
    }
    
    /**
     * init
     * @return boolean
     */
    public boolean init(){
        System.out.println("SimpleLocalAgent initialised");
        return true;
    }
    
    
    public void run(){
        System.out.println("SimpleLocalAgent started");
    }
    
    /**
     * stop
     */
    public void stop(){
        System.out.println("SimpleLocalAgent stopped");
    }
    
    /**
     * complete
     */
    public void complete(){
        System.out.println("SimpleLocalAgent completed");
    }
    
}
