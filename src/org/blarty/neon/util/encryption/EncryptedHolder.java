package org.jini.projects.neon.util.encryption;

import java.io.Serializable;

public final class EncryptedHolder implements Serializable {
	private byte[] data;
	
	public EncryptedHolder(){
		
	}
	
	public EncryptedHolder(byte[] data){
		setEncryptedBytes(data);
	}
	
	public void setEncryptedBytes(byte[] enc){
		this.data = enc;
	}
	
	public byte[] getEncryptedBytes(){
		return data;
	}
	
	public String toString(){
		return "[Encrypted Data]";
	}
}
