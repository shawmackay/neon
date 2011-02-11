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


package org.jini.projects.neon.neontests.tutorial.async;

/*
 * AsyncCallingAgent.java
 *
 * Created Tue Apr 12 15:26:50 BST 2005
 */

import java.lang.reflect.Method;

import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.host.AgentContext;
import org.jini.projects.neon.host.AsyncHandler;
import org.jini.projects.neon.service.AsyncServiceAgent;
/**
 *
 * @author  calum
 *
 */

public class GeneratedAsyncCallingAgent extends AbstractAgent implements Runnable{
    public GeneratedAsyncCallingAgent(){
        
    }
    
    
    public boolean init(){
        return true;
    }
    
    public void run(){
        AgentContext ctx = getAgentContext();
        AsyncHandler myHandler = new AsyncHandler() {
            public void replied(Object handback, Method handbackMethod, boolean isException){
                System.out.println("Generated AsyncHandler has received a reply: " + handback.getClass().getName());
            }
        };
        try{
            AsyncServiceAgent svcAgent = (AsyncServiceAgent) ctx.getAsynchronousAgent("neon.Services");
            Class[] intfs = svcAgent.getClass().getInterfaces();
            System.out.println("Proxy implements these interfaces");
            for(Class c: intfs)
                System.out.println("\t" + c.getName());
            
            svcAgent.getSingleService(net.jini.space.JavaSpace.class, myHandler);
        }catch(Exception ex){
            System.err.println("Caught Exception: "+ ex.getClass().getName() + "; Msg: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
}
