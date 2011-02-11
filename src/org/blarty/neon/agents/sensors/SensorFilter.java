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
* SensorFilter.java
*
* Created Mon Feb 28 11:08:38 GMT 2005
*/

/**
 * Used to determine what kind of events an agent is likely to be interested in. Evaluated at the sensor
 * rather than at the receiver, in order to reduce unneeded messages.<br>
* The Filter must be serializable and available in the agent dl file.
* @author  calum
*
*/

public interface SensorFilter extends java.io.Serializable{
	public boolean accept(Object notification);
}
