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
 * neon : org.jini.projects.neon.service.admin
 * DomainDescription.java
 * Created on 22-Sep-2003
 */
package org.jini.projects.neon.service.admin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author calum
 */
public class DomainDescription implements Serializable {
	private String name;
	private String configfile;

	private AgentDescription[] agents;

	private List domainsIn;
	private List domainsOut;
	private List allowedClasses;
	private List allowedNamespaces;
	private int securityLevel;
	private boolean encryptDomainInfo;
	private boolean encryptAgentStorage;
	private boolean encryptOtherData;
	private boolean failIfEncryptionAvailable;

	public DomainDescription(String name, String configfile, AgentDescription[] agents, List domainsIn, List domainsOut, List allowedClasses, List allowedNamespaces, int securityLevel, boolean encryptDomainInfo, boolean encryptAgentStorage,
			boolean encryptOtherData, boolean failIfEncryptionAvailable) {
		super();
		this.name = name;
		this.configfile = configfile;
		this.agents = agents;
		this.domainsIn = domainsIn;
		this.domainsOut = domainsOut;
		this.allowedClasses = allowedClasses;
		this.allowedNamespaces = allowedNamespaces;
		this.securityLevel = securityLevel;
		this.encryptDomainInfo = encryptDomainInfo;
		this.encryptAgentStorage = encryptAgentStorage;
		this.encryptOtherData = encryptOtherData;
		this.failIfEncryptionAvailable = failIfEncryptionAvailable;
	}

	/**
	 * 
	 */
	public DomainDescription(String name, String configfile, AgentDescription[] agents, List domainsIn, List domainsOut) {
		this.name = name;
		this.configfile = configfile;
		this.agents = agents;
		this.domainsIn = domainsIn;
		this.domainsOut = domainsOut;
		this.allowedClasses = new ArrayList();
		this.allowedNamespaces = new ArrayList();
		this.securityLevel = 0;
		this.encryptDomainInfo = false;
		this.encryptAgentStorage = false;
		this.encryptOtherData = false;
		this.failIfEncryptionAvailable = false;

		// TODO Complete constructor stub for DomainDescription
	}

	public String getName() {
		return name;
	}

	/**
	 * Obtain a list describing all of the agents in this domain
	 * 
	 * @return Set of agent descriptions
	 */
	public AgentDescription[] getAgents() {
		return agents;
	}

	/**
	 * @return the name of the configuration file
	 */
	public String getConfigfile() {
		return configfile;
	}

	public List getDomainsIn() {
		return domainsIn;
	}

	public void setDomainsIn(List domainsIn) {
		this.domainsIn = domainsIn;
	}

	public List getDomainsOut() {
		return domainsOut;
	}

	public void setDomainsOut(List domainsOut) {
		this.domainsOut = domainsOut;
	}

	public List getAllowedClasses() {
		return allowedClasses;
	}

	public void setAllowedClasses(List allowedClasses) {
		this.allowedClasses = allowedClasses;
	}

	public List getAllowedNamespaces() {
		return allowedNamespaces;
	}

	public void setAllowedNamespaces(List allowedNamespaces) {
		this.allowedNamespaces = allowedNamespaces;
	}

	public int getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(int securityLevel) {
		this.securityLevel = securityLevel;
	}

	public boolean isEncryptDomainInfo() {
		return encryptDomainInfo;
	}

	public void setEncryptDomainInfo(boolean encryptDomainInfo) {
		this.encryptDomainInfo = encryptDomainInfo;
	}

	public boolean isEncryptAgentStorage() {
		return encryptAgentStorage;
	}

	public void setEncryptAgentStorage(boolean encryptAgentStorage) {
		this.encryptAgentStorage = encryptAgentStorage;
	}

	public boolean isEncryptOtherData() {
		return encryptOtherData;
	}

	public void setEncryptOtherData(boolean encryptOtherData) {
		this.encryptOtherData = encryptOtherData;
	}

	public boolean isFailIfEncryptionAvailable() {
		return failIfEncryptionAvailable;
	}

	public void setFailIfEncryptionAvailable(boolean failIfEncryptionAvailable) {
		this.failIfEncryptionAvailable = failIfEncryptionAvailable;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setConfigfile(String configfile) {
		this.configfile = configfile;
	}

	public void setAgents(AgentDescription[] agents) {
		this.agents = agents;
	}

}
