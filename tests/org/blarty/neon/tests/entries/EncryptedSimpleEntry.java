package org.jini.projects.neon.tests.entries;

import net.jini.entry.AbstractEntry;

import org.jini.projects.neon.util.encryption.CryptoHolder;
import org.jini.projects.neon.util.encryption.EncryptedHolder;

public class EncryptedSimpleEntry extends AbstractEntry implements CryptoHolder {

	public String key;
	public EncryptedHolder encryptedData;
	
	public EncryptedSimpleEntry(){
		
	}

	public EncryptedHolder getEncryptedObject() {
		// TODO Auto-generated method stub
		return encryptedData;
	}

	public void setEncryptedObject(EncryptedHolder encrypted) {
		// TODO Auto-generated method stub
		encryptedData = encrypted;
	}

	
}
