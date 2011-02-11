package org.jini.projects.neon.service.admin;

public class SeedingAgentDetails {
	private String className;
	private String configurl;
	private String constraintsurl;
	private String domain;
	private String codebase;
	private boolean atServer;
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getConfigurl() {
		return configurl;
	}
	public void setConfigurl(String configurl) {
		this.configurl = configurl;
	}
	public String getConstraintsurl() {
		return constraintsurl;
	}
	public void setConstraintsurl(String constraintsurl) {
		this.constraintsurl = constraintsurl;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getCodebase() {
		return codebase;
	}
	public void setCodebase(String codebase) {
		this.codebase = codebase;
	}
	
	public String toString(){
		StringBuffer buff = new StringBuffer();
		buff.append("Class: " + className);
		buff.append("; config: "  + configurl);
		buff.append("; constraints: "+ constraintsurl);
		buff.append("; domain: " + domain);
		buff.append("; codebase: " + codebase);
		buff.append("; atserver: " + atServer);
		return buff.toString();
	}
	public boolean isAtServer() {
		return atServer;
	}
	public void setAtServer(boolean atServer) {
		this.atServer = atServer;
	}
	
}
