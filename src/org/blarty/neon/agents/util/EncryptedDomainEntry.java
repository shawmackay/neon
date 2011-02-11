package org.jini.projects.neon.agents.util;

import net.jini.entry.AbstractEntry;

import org.jini.projects.neon.util.encryption.CryptoHolder;
import org.jini.projects.neon.util.encryption.EncryptedHolder;

public class EncryptedDomainEntry extends AbstractEntry implements CryptoHolder {
	public String name;
	public String referentServiceID;

	public EncryptedHolder encryptedData;

	public EncryptedHolder getEncryptedObject() {
		// TODO Auto-generated method stub
		return encryptedData;
	}

	
	

	public void setEncryptedObject(EncryptedHolder encrypted) {
		// TODO Auto-generated method stub
		encryptedData = encrypted;
	}
	
	public String toString(){
		return this.getClass().getName() + "= name:" + name + "; svcID:" + referentServiceID + "; encryptedData: " + encryptedData;
	}
}
