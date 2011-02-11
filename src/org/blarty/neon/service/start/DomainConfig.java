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

package org.jini.projects.neon.service.start;

import java.util.ArrayList;
import java.util.List;

/*
 * DomainConfig.java Created Fri Mar 18 11:38:58 GMT 2005
 */

/**
 * @author calum
 */

public class DomainConfig {
	private String name;

	private AgentCatalog agentCatalog;
	private DelegateCatalog delegateCatalog;

	private List<String> receiveCallsFromDomains = new ArrayList<String>();
	private List<String> sendCallsToDomains = new ArrayList<String>();
	private List<String> allowedClasses;
	private List<String> allowedNamespaces;
	private int securityLevel;
	private boolean encryptDomainInfo;
	private boolean encryptAgentStorage;
	private boolean encryptOtherData;
	private boolean failIfEncryptionAvailable;

	public DomainConfig(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AgentCatalog getAgentCatalog() {
		return agentCatalog;
	}

	public void setAgentCatalog(AgentCatalog agentCatalog) {
		this.agentCatalog = agentCatalog;
	}

	public DelegateCatalog getDelegateCatalog() {
		return delegateCatalog;
	}

	public void setDelegateCatalog(DelegateCatalog delegateCatalog) {
		this.delegateCatalog = delegateCatalog;
	}

	public List<String> getReceiveCallsFromDomains() {
		return receiveCallsFromDomains;
	}

	public void setReceiveCallsFromDomains(List<String> receiveCallsFromDomains) {
		this.receiveCallsFromDomains = receiveCallsFromDomains;
	}

	public List<String> getSendCallsToDomains() {
		return sendCallsToDomains;
	}

	public void setSendCallsToDomains(List<String> sendCallsToDomains) {
		this.sendCallsToDomains = sendCallsToDomains;
	}

	public List<String> getAllowedClasses() {
		return allowedClasses;
	}

	public void setAllowedClasses(List<String> allowedClasses) {
		this.allowedClasses = allowedClasses;
	}

	public List<String> getAllowedNamespaces() {
		return allowedNamespaces;
	}

	public void setAllowedNamespaces(List<String> allowedNamespaces) {
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

	public String toString() {
		return "Domain Details: " + name + "\n" + "\tSecurityLevel: " + securityLevel + "\n" + "\tEncrypt Domain Information: " + encryptDomainInfo + "\n" + "\tEncrypt Agent Storage: " + encryptAgentStorage + "\n" + "\tEncryptOther Data: "
				+ encryptOtherData;

	}

}
