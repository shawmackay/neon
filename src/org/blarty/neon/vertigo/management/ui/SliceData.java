package org.jini.projects.neon.vertigo.management.ui;

/*
 * Represents the internal state of a given Slice
 */
public class SliceData {
	private String name;
	private int type;
	private SliceEnforcementPolicy policy;
	
	public static int APPLICATION = 1;
	public static int REPELLER = 3;
	public static int ATTRACTOR =2;
	public static int FACTORY = 4;
	public static int REDUNDANT = 5;
	public static int VIRTUAL = 6;
	public static int DEFAULT=0;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	public void setType(String type) {
		if(type.equalsIgnoreCase("attractor"))
			this.type = ATTRACTOR;
		if(type.equalsIgnoreCase("repeller"))
			this.type = REPELLER;
		if(type.equalsIgnoreCase("factory"))
			this.type = FACTORY;
		if(type.equalsIgnoreCase("redundant"))
			this.type = REDUNDANT;
		if(type.equalsIgnoreCase("virtual"))
			this.type = VIRTUAL;
		if(type.equalsIgnoreCase("default"))
			this.type = DEFAULT;
		if(type.equalsIgnoreCase("application"))
			this.type = APPLICATION;
	}
	
	public SliceData(String name, int type) {
		super();
		this.name = name;
		this.type = type;
	}
	
	public AgentData[] getAttachedAgents(){
		return null;
	}
	
	
	public void setEnforcementPolicy(SliceEnforcementPolicy policy){
		this.policy = policy;
	}
	
	public SliceEnforcementPolicy getEnforcementPolicy(){
		return this.policy;
	}
	
}


