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
 * neon : org.jini.projects.neon.agents.sensors
 * Receiver.java
 * Created on 10-Sep-2003
 */
package org.jini.projects.neon.agents.sensors;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.jini.projects.neon.collaboration.Collaborative;

/**
 * Allows classes to be called back in response to sensors being triggered. 
 *@author calum
 */
public interface SensorListener extends Collaborative{

        /**
         * Gets the filter that the sensor will check against in order to notify the 
         * Listener
         */

        
        
        /**
         * Informs the interested party, that an event has occured.
         * @param sensortype The name of the sensor
         * @param value A value representing the event, etc.
         * @return TODO
         * @throws SensorException TODO
         */
        public Object sensorTriggered(String sensortype, Object value) throws SensorException ;   
}
