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
 * neon : org.jini.projects.neon.service.admin.constrainable
 * AgentAdminProxy.java
 * Created on 22-Sep-2003
 */
package org.jini.projects.neon.service.admin.constrainable;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import net.jini.core.constraint.MethodConstraints;
import net.jini.core.constraint.RemoteMethodControl;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.entry.Entry;
import net.jini.id.Uuid;

import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.service.admin.AgentServiceAdmin;
import org.jini.projects.neon.service.admin.DomainDescription;

/**
 * @author calum
 */
public class AgentAdminProxy implements AgentServiceAdmin, Serializable {

	/**
	 * AgentAdminProxy
	 * @author calum
	 *
	 */

	private static AgentServiceAdmin constrainServer(AgentServiceAdmin server, MethodConstraints methodConstraints) {
		return (AgentServiceAdmin) ((RemoteMethodControl) server).setConstraints(methodConstraints);
	}
	
	
	final static class ConstrainableProxy extends AgentAdminProxy implements RemoteMethodControl {

		public RemoteMethodControl setConstraints(MethodConstraints constraints) {
			return new AgentAdminProxy.ConstrainableProxy(backend, proxyID, constraints);
		}

		/** {@inheritDoc} */
		public MethodConstraints getConstraints() {
			return ((RemoteMethodControl) backend).getConstraints();
		}

		/**
		 * @param server
		 * @param id
		 * @param object
		 */
		public ConstrainableProxy(AgentServiceAdmin server, Uuid id, MethodConstraints methodConstraints) {

			super(constrainServer(server, methodConstraints), id);
			l.fine("Creating a secure proxy");

		}

	}
	private static final long serialVersionUID = 1287686345L;
	transient Logger l = Logger.getLogger("org.jini.projects.neon.service.admin");

	final AgentServiceAdmin backend;
	final Uuid proxyID;

	public static AgentAdminProxy create(AgentServiceAdmin server, Uuid id) {
		if (server instanceof RemoteMethodControl) {

			return new AgentAdminProxy.ConstrainableProxy(server, id, null);
		} else
			return new AgentAdminProxy(server, id);
	}

	private AgentAdminProxy(AgentServiceAdmin backend, Uuid proxyID) {
		this.backend = backend;
		this.proxyID = proxyID;

	}

	/**
	 * @param attrSets
	 * @throws java.rmi.RemoteException
	 */
	public void addLookupAttributes(Entry[] attrSets) throws RemoteException {
		backend.addLookupAttributes(attrSets);
	}

	/**
	 * @param groups
	 * @throws java.rmi.RemoteException
	 */
	public void addLookupGroups(String[] groups) throws RemoteException {
		backend.addLookupGroups(groups);
	}

	/**
	 * @param locators
	 * @throws java.rmi.RemoteException
	 */
	public void addLookupLocators(LookupLocator[] locators) throws RemoteException {
		backend.addLookupLocators(locators);
	}

	/**
	 * @throws java.rmi.RemoteException
	 */
	public void destroy() throws RemoteException {
		backend.destroy();
	}


	public DomainDescription[] getDomains() throws RemoteException {
		return backend.getDomains();
	}


	public String getGlobalConfigFile() throws RemoteException {
		return backend.getGlobalConfigFile();
	}

	
	public Entry[] getLookupAttributes() throws RemoteException {
		return backend.getLookupAttributes();
	}

	
	public String[] getLookupGroups() throws RemoteException {
		return backend.getLookupGroups();
	}

	
	public LookupLocator[] getLookupLocators() throws RemoteException {
		return backend.getLookupLocators();
	}

	/**
	 * @return
	 * @throws RemoteException
	 */
	public int getMaxAgentsOfAncestor() throws RemoteException {
		return backend.getMaxAgentsOfAncestor();
	}

	/**
	 * @return
	 * @throws RemoteException
	 */
	public int getMaxAgentsOfType() throws RemoteException {
		return backend.getMaxAgentsOfType();
	}

	/**
	 * @return
	 * @throws RemoteException
	 */
	public boolean isUsingThreadGroups() throws RemoteException {
		return backend.isUsingThreadGroups();
	}

	/**
	 * @param attrSetTemplates
	 * @param attrSets
	 * @throws java.rmi.RemoteException
	 */
	public void modifyLookupAttributes(Entry[] attrSetTemplates, Entry[] attrSets) throws RemoteException {
		backend.modifyLookupAttributes(attrSetTemplates, attrSets);
	}

	/**
	 * @param groups
	 * @throws java.rmi.RemoteException
	 */
	public void removeLookupGroups(String[] groups) throws RemoteException {
		backend.removeLookupGroups(groups);
	}

	/**
	 * @param locators
	 * @throws java.rmi.RemoteException
	 */
	public void removeLookupLocators(LookupLocator[] locators) throws RemoteException {
		backend.removeLookupLocators(locators);
	}

	/**
	 * @param groups
	 * @throws java.rmi.RemoteException
	 */
	public void setLookupGroups(String[] groups) throws RemoteException {
		backend.setLookupGroups(groups);
	}

	/**
	 * @param locators
	 * @throws java.rmi.RemoteException
	 */
	public void setLookupLocators(LookupLocator[] locators) throws RemoteException {
		backend.setLookupLocators(locators);
	}

	public boolean containsAgent(AgentIdentity id) throws RemoteException { 
		return backend.containsAgent(id);
	}

	public boolean packAgent(AgentIdentity id) throws RemoteException {
		return backend.packAgent(id);
	}

	public void updateDomainDescription(DomainDescription desc) throws RemoteException {
		// TODO Auto-generated method stub
		backend.updateDomainDescription(desc);
	}

	public void removeDomain(String name) throws RemoteException {
		// TODO Auto-generated method stub
		backend.removeDomain(name);
	}

}
