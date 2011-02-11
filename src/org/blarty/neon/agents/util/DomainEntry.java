package org.jini.projects.neon.agents.util;

import java.util.ArrayList;
import java.util.List;

import net.jini.entry.AbstractEntry;

public class DomainEntry extends AbstractEntry {
    public String name;
    public String referentServiceID;
    public List delegateList;
    public List receiveCallsFrom;
    public List sendCallsTo;
    
    public List allowedClasses;
    public List allowedNamespaces;
    public Integer securityLevel;
    public Boolean encryptDomainInfo;
    public Boolean encryptAgentStorage;
    public Boolean encryptOtherData;
    public Boolean failIfEncryptionAvailable;
    
    public DomainEntry(String name, String referentServiceID, List delegateList, List receiveCallsFrom, List sendCallsTo, List allowedClasses, List allowedNamespaces, int securityLevel, boolean encryptDomainInfo, boolean encryptAgentStorage,
			boolean encryptOtherData, boolean failIfEncryptionAvailable) {
		super();
		this.name = name;
		this.referentServiceID = referentServiceID;
		this.delegateList = delegateList;
		this.receiveCallsFrom = receiveCallsFrom;
		this.sendCallsTo = sendCallsTo;
		this.allowedClasses = allowedClasses;
		this.allowedNamespaces = allowedNamespaces;
		this.securityLevel = securityLevel;
		this.encryptDomainInfo = encryptDomainInfo;
		this.encryptAgentStorage = encryptAgentStorage;
		this.encryptOtherData = encryptOtherData;
		this.failIfEncryptionAvailable = failIfEncryptionAvailable;
	}

	public DomainEntry(){
        
    }
    
    public DomainEntry(String name,String referentServiceId,List delegates,  List from, List to) {
        super();
        // TODO Auto-generated constructor stub
        this.referentServiceID = referentServiceId;
        delegateList = delegates;
        this.name = name;
        receiveCallsFrom = from;
        sendCallsTo = to;
        allowedClasses=new ArrayList();
        allowedNamespaces = new ArrayList();
        securityLevel = 0;
        encryptAgentStorage= false;
        encryptDomainInfo = false;
        encryptOtherData = false;
        failIfEncryptionAvailable = false;
    }
    
}
