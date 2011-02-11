/*
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



package org.jini.projects.neon.agents;



import java.rmi.Remote;
import java.rmi.RemoteException;

import org.jini.projects.neon.agents.sensors.SensorFilter;
import org.jini.projects.neon.collaboration.Collaborative;
/**
 * Allows the registration of listeners onto an agent. In effect, a third party
 * registers interest for when an event occurs, or the sensor is triggered.
 * For parallels, see the standard Java event model.
 */
public interface SensorAgent extends Collaborative, Remote {
	/**
	 * Collaborative form of Sensor.addListener
	 * @param listener
	 * @return result of registration
	 */
	public boolean addListener(AgentIdentity listener, SensorFilter filter) throws RemoteException;
    
    /**
     * Remove the listener registered for the given agent
     * @param a The agent deregistering it's interest in these events.
     * @return Success of deregistration
     */
    public boolean removeListener(AgentIdentity a) throws RemoteException;
}
