package org.jini.projects.neon.host;

import java.util.TreeMap;

import org.jini.projects.neon.agents.AgentIdentity;


public class RegistrationSet extends TreeMap<String, AgentIdentity> {
	protected boolean isRemoted = false;
	
	public RegistrationSet(boolean isRemoted){
		super();
		this.isRemoted = isRemoted;
	}
	
	public boolean isRemoted(){
		return this.isRemoted;
	}
}
