package org.jini.projects.neon.sample.tasks;

import java.util.Map;

import org.jini.projects.neon.tasks.Task;
import org.jini.projects.neon.tasks.TaskProcessor;
import org.jini.projects.neon.tasks.TaskResponse;

public class MultipliedProcessor implements TaskProcessor{

	private Map<Integer, Integer> returns;
	
	
	public TaskResponse execute(Task t) {
		// TODO Auto-generated method stub
		TaskResponse resp = new TaskResponse();
		resp.setSuccess(true);
		return resp;
	}

}
