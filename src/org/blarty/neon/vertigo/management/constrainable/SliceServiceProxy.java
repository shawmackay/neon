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
 * neon : org.jini.projects.neon.vertigo.management.constrainable
 * SliceManagerProxy.java
 * Created on 16-May-2005
 *SliceManagerProxy
 */

package org.jini.projects.neon.vertigo.management.constrainable;

import java.rmi.RemoteException;
import java.util.logging.Logger;

import net.jini.core.constraint.MethodConstraints;
import net.jini.core.constraint.RemoteMethodControl;
import net.jini.id.ReferentUuid;
import net.jini.id.Uuid;

import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.vertigo.management.SliceService;
import org.jini.projects.neon.vertigo.management.SliceType;

/**
 * @author calum
 */
public class SliceServiceProxy implements SliceService, ReferentUuid {

	private static final long serialVersionUID = 2L;
	transient Logger l = Logger.getLogger("org.jini.projects.neon.service");

	final SliceService backend;
	final Uuid proxyID;

	public static SliceServiceProxy create(SliceService server, Uuid id) {
		if (server instanceof RemoteMethodControl) {

			return new SliceServiceProxy.ConstrainableProxy(server, id, null);
		} else
			return new SliceServiceProxy(server, id);
	}
	
    private SliceServiceProxy(SliceService backend,  Uuid proxyID) {
        this.backend = backend;
        this.proxyID = proxyID;
        
    }

	

	final static class ConstrainableProxy extends SliceServiceProxy implements RemoteMethodControl {
		private static final long serialVersionUID = 4L;

		private ConstrainableProxy(SliceService server, Uuid id, MethodConstraints methodConstraints) {
			super(constrainServer(server, methodConstraints), id);
			l.fine("Creating a secure proxy");
		}

		public RemoteMethodControl setConstraints(MethodConstraints constraints) {
			return new SliceServiceProxy.ConstrainableProxy(backend, proxyID, constraints);
		}

		/** {@inheritDoc} */
		public MethodConstraints getConstraints() {
			return ((RemoteMethodControl) backend).getConstraints();
		}

		private static SliceService constrainServer(SliceService server, MethodConstraints methodConstraints) {
			return (SliceService) ((RemoteMethodControl) server).setConstraints(methodConstraints);
		}
	}

	public void attachAgentToSlice(AgentIdentity agentid, String slicename) throws RemoteException {
		// TODO Complete method stub for attachAgentToSlice
		backend.attachAgentToSlice(agentid, slicename);
	}

	public void attachSubslice(Uuid parent, Uuid child) throws RemoteException {
		// TODO Complete method stub for attachSubslice
		backend.attachSubslice(parent, child);
	}

	public void createApplication(String applicationName) throws RemoteException {
		// TODO Complete method stub for createApplication
		backend.createApplication(applicationName);
	}

	public void createSlice(String slicepath, String newsubslice, SliceType type) throws RemoteException {
		// TODO Complete method stub for createSlice
		backend.createSlice(slicepath, newsubslice, type);
	}

	public void displaySliceInfo(String slicepath) throws RemoteException {
		// TODO Complete method stub for displaySliceInfo
		backend.displaySliceInfo(slicepath);
	}

	public Uuid getApplicationID(String slicepath) throws RemoteException {
		// TODO Complete method stub for getApplicationID
		return backend.getApplicationID(slicepath);
	}

	public Uuid getSliceID(String slicepath) throws RemoteException {
		// TODO Complete method stub for getSliceID
		return backend.getSliceID(slicepath);
	}

	public Uuid getReferentUuid() {
		// TODO Complete method stub for getReferentUuid
		return null;
	}

}
