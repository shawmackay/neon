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

package org.jini.projects.neon.host;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.NonContinuousAgent;



/**
 * Creates a thread to run an agent. For every agent there is a thread that it
 * will execute in For many agents, they will be initialised, their synchronous
 * portion (as implemented in the <code>start()</code> method) executed and
 * then they will complete. However, if the encapsulated agent, implements
 * <code>ContinuousAgent</code> the complete call will not be activated,
 * instead the thread will be allowed to finish, and any collaborative calls
 * will be executed in the callers thread.
 */
public class ThreadHarness
        extends
        Thread implements AgentHarness {
    Agent worker = null;
    
    Logger l = Logger.getLogger("org.jini.projects.neon.threads");
    
    public ThreadHarness(Agent worker) {
        this.worker = worker;
        this.setName(worker.getName());
        // System.out.println(this.getName());
    }
    
    public void run() {
               
        boolean initialised = false;
        initialised = worker.init();
        if(!initialised){
            l.warning("Initialisation of " + worker.getName() + " not completed =>Decommisioning");
            worker.complete();
        }
        boolean started = false;
        if (initialised) {            
            Runnable runner = (Runnable) worker;
            runner.run();
            if (worker instanceof NonContinuousAgent) {
                worker.stop();
                worker.complete();
            }            
        }
        l.finest("Thread completed: " + this.getName());
        worker = null;
        
    }
    
    
    
    public Agent getWorker() {
        return worker;
    }
    
    public void go() {
        start();
    }
    
        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#finalize()
         */
    protected void finalize() throws Throwable {
        // URGENT Complete method stub for finalize
        
        super.finalize();
        
    }
    
}
