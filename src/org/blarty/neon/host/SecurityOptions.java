package org.jini.projects.neon.host;

import java.util.ArrayList;
import java.util.List;

import org.jini.projects.neon.service.start.DomainConfig;

public final class SecurityOptions {
	
	private int securityLevel;
	private boolean encryptAgentStorage;
	private boolean encryptDomainInfo;
	private boolean encryptOtherData;
	private boolean failIfEncryptionAvailable;
	private List receiveCallsFromDomains = new ArrayList();
	private List sendCallsToDomains = new ArrayList();
	private List allowedClasses;
	private List allowedNamespaces;
	
	public void setSecurityLevel(int securityLevel) {
		this.securityLevel = securityLevel;
	}

	public void setEncryptAgentStorage(boolean encryptAgentStorage) {
		this.encryptAgentStorage = encryptAgentStorage;
	}

	public void setEncryptDomainInfo(boolean encryptDomainInfo) {
		this.encryptDomainInfo = encryptDomainInfo;
	}

	public void setEncryptOtherData(boolean encryptOtherData) {
		this.encryptOtherData = encryptOtherData;
	}

	public void setFailIfEncryptionAvailable(boolean failIfEncryptionAvailable) {
		this.failIfEncryptionAvailable = failIfEncryptionAvailable;
	}

	public SecurityOptions(DomainConfig initData){
		encryptAgentStorage = initData.isEncryptAgentStorage();
		encryptDomainInfo = initData.isEncryptDomainInfo();
		encryptOtherData = initData.isEncryptOtherData();
		failIfEncryptionAvailable = initData.isFailIfEncryptionAvailable();
		securityLevel = initData.getSecurityLevel();
		allowedClasses = initData.getAllowedClasses();
		allowedNamespaces = initData.getAllowedNamespaces();
		receiveCallsFromDomains = initData.getReceiveCallsFromDomains();
		sendCallsToDomains = initData.getSendCallsToDomains();
	}

	public int getSecurityLevel() {
		return securityLevel;
	}

	public boolean isEncryptAgentStorage() {
		return encryptAgentStorage;
	}

	public boolean isEncryptDomainInfo() {
		return encryptDomainInfo;
	}

	public boolean isEncryptOtherData() {
		return encryptOtherData;
	}

	public boolean isFailIfEncryptionAvailable() {
		return failIfEncryptionAvailable;
	}

	public List getReceiveCallsFromDomains() {
		return receiveCallsFromDomains;
	}

	public void setReceiveCallsFromDomains(List receiveCallsFromDomains) {
		this.receiveCallsFromDomains = receiveCallsFromDomains;
	}

	public List getSendCallsToDomains() {
		return sendCallsToDomains;
	}

	public void setSendCallsToDomains(List sendCallsToDomains) {
		this.sendCallsToDomains = sendCallsToDomains;
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
}
