package org.jini.projects.neon.tasks;

import java.util.Date;

import net.jini.entry.AbstractEntry;

public class TaskEntry extends AbstractEntry {
	public String name;
	public Object[] arguments;
	public Date created;

	public TaskEntry(){}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public TaskEntry(String name, Object[] arguments, Date created) {
		super();
		this.name = name;
		this.arguments = arguments;
		this.created = created;
	}
}
