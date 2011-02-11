package org.jini.projects.neon.tasks;

public class DefaultTask implements Task{

	private String actionName;
	private Object[] arguments;
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	public Object[] getArguments() {
		return arguments;
	}
	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}
	public DefaultTask(String actionName, Object... arguments) {
		super();
		this.actionName = actionName;
		this.arguments = arguments;
	}
	
	

}
