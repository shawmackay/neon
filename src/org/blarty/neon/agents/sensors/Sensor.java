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
 * Sensor.java
 * Created on 10-Sep-2003
 */
package org.jini.projects.neon.agents.sensors;

import org.jini.projects.neon.agents.AgentIdentity;

/**
 *  Enables implementing classes to be used as event sources.
 * @author calum
 */
public interface Sensor {
	/**
	 * Add a listener for events, to a given unique agent. An agent may only have one registration
	 * on any sensorable resource at any one pijnt in time, subsequent registrations will replace the current one
	 * @param a The interested agent
	 * @param l The listener
	 * @return Success of registration
	 */
	public boolean addListener(AgentIdentity a, SensorListener l);
    /**
     * Remove the listener registered for the given agent
     * @param a The agent deregistering it's interest in these events.
     * @return Success of deregistration
     */
    public boolean removeListener(AgentIdentity a);
}
