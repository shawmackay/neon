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

package org.jini.projects.neon.transactions;

/*
 * UpdaterAgent.java
 *
 * Created Mon Mar 21 11:55:33 GMT 2005
 */
import net.jini.core.entry.Entry;
import net.jini.core.lease.Lease;
import net.jini.lookup.entry.Name;

import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.NonContinuousAgent;

import com.sun.jini.constants.TimeConstants;
import java.util.Random;

/**
 *
 * @author  calum
 *
 */

public class UpdaterAgent extends AbstractAgent implements NonContinuousAgent, Runnable{
    
    static Random randEmpIDGen;
    
    public UpdaterAgent(){
        this.namespace = "neon.transactions";
        this.name = "EmployeeUpdater";
        if(randEmpIDGen==null)
            randEmpIDGen = new Random(System.currentTimeMillis());
    }
    
    /**
     * init
     * @return boolean
     */
    public boolean init(){
        getAgentLogger().info("UpdaterAgent init'ed");
        return true;
    }
    
    /**
     * start
     */
    public void run(){
        try{
            
            getAgentLogger().info("Writing an entry outside of a transaction");
            String spaceName = (String) getAgentConfiguration().getEntry("neon.transactions.tests","updateSpace", String.class);
            JavaSpaceOps ops = (JavaSpaceOps) context.getAgent("JavaSpaceOps",new Entry[]{new Name(spaceName)});
            
            EmployeeEntry entr = new EmployeeEntry(this.randEmpIDGen.nextInt(100),"Joe Bloggs", "Janitor");
            ops.write(entr, 5*TimeConstants.MINUTES);
        }catch(Exception ex){
            System.err.println("Caught Exception: "+ ex.getClass().getName() + "; Msg: " + ex.getMessage());
            ex.printStackTrace();
        }
        
    }
    
}
