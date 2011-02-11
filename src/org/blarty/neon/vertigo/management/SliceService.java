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
 * neon : org.jini.projects.neon.vertigo.management
 * SliceService.java
 * Created on 16-May-2005
 *SliceService
 */

package org.jini.projects.neon.vertigo.management;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.jini.id.Uuid;

import org.jini.projects.neon.agents.AgentIdentity;

/**
 * @author calum
 */
public interface SliceService extends Remote {
	public void createApplication(String applicationName) throws RemoteException;

	public void createSlice(String slicepath,String newsubslice, SliceType type) throws RemoteException;

	public Uuid getSliceID(String slicepath) throws RemoteException;

	public Uuid getApplicationID(String slicepath) throws RemoteException;

	public void attachSubslice(Uuid parent, Uuid child) throws RemoteException;

	public void displaySliceInfo(String slicepath) throws RemoteException;

	public void attachAgentToSlice(AgentIdentity agentid, String slicename) throws RemoteException;
}
