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
 * SliceServiceImpl.java
 * Created on 16-May-2005
 *SliceServiceImpl
 */
package org.jini.projects.neon.vertigo.management;

import java.rmi.RemoteException;

import net.jini.id.Uuid;

import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.vertigo.slice.Slice;

/**
 * @author calum
 */
public class SliceServiceImpl implements SliceService{
	
	private SliceManager mgr;
	
	public SliceServiceImpl(SliceManager mgr){
		this.mgr =mgr;
	}

	public void attachAgentToSlice(AgentIdentity agentid, String slicename) throws RemoteException {
		// TODO Complete method stub for attachAgentToSlice
		mgr.attachAgentToSlice(agentid, slicename);
	}

	public void attachSubslice(Uuid parentSlice, Uuid childSlice) throws RemoteException {
		// TODO Complete method stub for attachSubslice
		Slice parent = mgr.getSlice(parentSlice);
		Slice child = mgr.getSlice(childSlice);
		if(parent==null)
			System.out.println("The parent is null");
		if(child==null)
			System.out.println("The child is null");
		mgr.attachSubSlice(parent,child);
	}

	public void createApplication(String applicationName) throws RemoteException {
		// TODO Complete method stub for createApplication
		mgr.createApplication(applicationName);
	}

	public void createSlice(String slicepath, String newsubslice, SliceType type) throws RemoteException {
		// TODO Complete method stub for createSlice
		mgr.createSlice(slicepath, newsubslice, type);
	}

	public void displaySliceInfo(String slicepath) throws RemoteException {
		// TODO Complete method stub for displaySliceInfo
		mgr.displaySliceInfo(slicepath);
	}

	public Uuid getApplicationID(String slicepath) throws RemoteException {
		// TODO Complete method stub for getApplicationID
		Slice s = mgr.findSlice(slicepath);
		return s.getSliceID();
	}

	public Uuid getSliceID(String slicepath) throws RemoteException {
		Slice s = mgr.findSlice(slicepath);
		return s.getSliceID();
	}

	
}
