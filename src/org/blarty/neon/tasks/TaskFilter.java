package org.jini.projects.neon.tasks;

import org.jini.projects.neon.agents.sensors.SensorFilter;

public class TaskFilter implements SensorFilter {

	private String[] taskNames;

	public TaskFilter(String tasknames) {
		this.taskNames = tasknames.split("|");
	}

	public TaskFilter(String[] tasknames) {
		this.taskNames = tasknames;
	}
	
	public String[] getTaskNames() {
		return taskNames;
	}

	public void setTaskNames(String[] taskNames) {
		this.taskNames = taskNames;
	}

	public boolean accept(Object notification) {
		Task t = (Task) notification;
		for (String taskName : taskNames)
			if (taskName.equals(t.getActionName()))
				return true;

		return false;
	}
}
