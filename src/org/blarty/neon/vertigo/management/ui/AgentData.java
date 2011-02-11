package org.jini.projects.neon.vertigo.management.ui;

import org.jini.projects.neon.agents.AgentIdentity;

public class AgentData {
	String name;
	AgentIdentity attachedID;
	public boolean reconnect;
	public boolean useAnyMatching;
	
	public AgentData(){
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AgentIdentity getAttachedID() {
		return attachedID;
	}

	public void setAttachedID(AgentIdentity attachedID) {
		this.attachedID = attachedID;
	}

	public boolean isReconnect() {
		return reconnect;
	}

	public void setReconnect(boolean reconnect) {
		this.reconnect = reconnect;
	}

	public boolean isUseAnyMatching() {
		return useAnyMatching;
	}

	public void setUseAnyMatching(boolean useAnyMatching) {
		this.useAnyMatching = useAnyMatching;
	}
}
