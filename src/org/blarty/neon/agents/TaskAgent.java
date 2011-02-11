package org.jini.projects.neon.agents;

import org.jini.projects.neon.tasks.Task;
import org.jini.projects.neon.tasks.TaskResponse;

public interface TaskAgent extends Agent {
	public TaskResponse handleTask(Task t);
}
