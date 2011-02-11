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
 * TimeSensor.java
 * 
 * Created Tue Apr 05 09:16:11 BST 2005
 */
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.jini.id.Uuid;

import org.jini.glyph.Exportable;
import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.SensorAgent;
import org.jini.projects.neon.host.AgentContext;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.zenith.messaging.messages.EventMessage;
import org.jini.projects.zenith.messaging.messages.MessageHeader;

/**
 * Wakes up every second and informs those parties that are interested, what the current time is.
 * @author calum
 * 
 */
@Exportable
public class TimeSensor extends AbstractAgent implements SensorAgent, Runnable {

    HashMap sensors = new HashMap();

    public TimeSensor() {
        this.namespace = "neon.test";
        this.name = "TimeSensor";
    }

    /**
     * init
     * 
     * @return boolean
     */
    public boolean init() {
    	getAgentLogger().info("Time Sensor ID: " + getIdentity());
        return true;
    }

    /**
     * start
     */
    public void run() {

        while (!this.isStopped()) {
            notifyListeners(System.currentTimeMillis());
            try {
                synchronized (this) {
                    wait(1000);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("Time Sensor finished sending notifications");
    }

    public void stop() {
        super.stop();
        System.out.println("Stopping TimeSensor");
    }

    /**
     * addListener
     * 
     * @param aSensorListener
     * @return boolean
     */
    public boolean addListener(AgentIdentity agent, SensorFilter filter) {
        
      
        sensors.put(agent, filter);
        return true;
    }

    public void notifyListeners(long time) {
        for (Iterator iter = sensors.entrySet().iterator(); iter.hasNext();) {
            Map.Entry sensor = (Map.Entry) iter.next();
            SensorFilter filter = (SensorFilter) sensor.getValue();
            AgentIdentity address = (AgentIdentity) sensor.getKey();
            
            Date datetime = new Date(time);

            if (filter.accept(datetime)) {
                AgentContext ctx = getAgentContext();
                try {
					SensorListener aListener = (SensorListener) ctx.getAgent(address);
					aListener.sensorTriggered("TimeChanged", datetime);
				} catch (NoSuchAgentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SensorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
            }
        }
    }

    public boolean removeListener(AgentIdentity a) {
    	sensors.remove(a);
        return true;
    }

}
