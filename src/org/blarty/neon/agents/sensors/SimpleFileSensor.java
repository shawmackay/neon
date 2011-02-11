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

package org.jini.projects.neon.agents.sensors;

/*
 * SimpleFileSensor.java
 *
 * Created Mon Feb 28 14:43:35 GMT 2005
 */

import java.rmi.RemoteException;

import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.SensorAgent;
import org.jini.projects.neon.host.AgentContext;
import org.jini.projects.neon.host.NoSuchAgentException;

/**
 *
 * @author  calum
 *
 */

public class SimpleFileSensor extends AbstractAgent  implements SensorListener{
    
    public SimpleFileSensor(){
        this.name = "SimpleFileSensor";
        this.namespace="tests";
    }
    
    /**
     * init
     * @return boolean
     */
    public boolean init(){
        AgentContext ctx = getAgentContext();
        try {
            SensorAgent sens = (SensorAgent) ctx.getAgent("neon.FileSensor");
            sens.addListener(this.getIdentity(),null);
        } catch (NoSuchAgentException e) {
            // TODO Handle NoSuchAgentException
            e.printStackTrace();
        } catch (RemoteException e){
        	e.printStackTrace();
        }
        return true;
    }
    
  
    
    /**
     * stop
     */
    public void stop(){
        
    }
    
    /**
     * sensorTriggered
     * @param aString
     * @param aObject
     */
    public Object sensorTriggered(String  aString, Object  aObject) throws SensorException{
       
        getAgentLogger().fine("Notified Evt: " + aString + ": " + aObject.toString());
        return null;
    }
    
    /**
     * getFilter
     * @return SensorFilter
     */
    public SensorFilter getFilter(){
        return new FileSensorFilter("/tmp",".*");
    }
    
}
