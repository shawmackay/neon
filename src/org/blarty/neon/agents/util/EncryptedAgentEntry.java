package org.jini.projects.neon.agents.util;

import net.jini.entry.AbstractEntry;

import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.util.encryption.EncryptedHolder;

public class EncryptedAgentEntry extends AbstractEntry implements org.jini.projects.neon.util.encryption.CryptoHolder {
	public AgentIdentity agentID;
	public String agentName;
	public EncryptedHolder encryptedVersion;
	
	public EncryptedHolder getEncryptedObject() {
		// TODO Auto-generated method stub
		return encryptedVersion;
	}

	public void setEncryptedObject(EncryptedHolder encrypted) {
		// TODO Auto-generated method stub
		this.encryptedVersion = encrypted;
	}

}
