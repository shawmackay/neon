package org.jini.projects.neon.tasks;

import java.util.ArrayList;
import java.util.List;

public class TaskResponse {
	private boolean success;
	private List<Task> outputTasks = new ArrayList<Task>();
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public List<Task> getOutputTasks() {
		return outputTasks;
	}
	public void setOutputTasks(List<Task> outputTasks) {
		this.outputTasks = outputTasks;
	}

	public void addOutputTask(Task t){
		outputTasks.add(t);
	}
}
