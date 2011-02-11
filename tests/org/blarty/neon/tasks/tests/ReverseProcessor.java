package org.jini.projects.neon.tasks.tests;

import org.jini.projects.neon.tasks.DefaultTask;
import org.jini.projects.neon.tasks.Task;
import org.jini.projects.neon.tasks.TaskProcessor;
import org.jini.projects.neon.tasks.TaskResponse;

public class ReverseProcessor implements TaskProcessor{
	public TaskResponse execute(Task t) {
		TaskResponse response = new TaskResponse();
		String toReverse = (String) t.getArguments()[0];
		String[] parts = toReverse.split(" ");
		StringBuffer b = new StringBuffer();
		for(int i=parts.length-1;i<0;i++)
			b.append(parts[i] + " ");
		response.addOutputTask(new DefaultTask("ReversedString", new Object[]{b.toString().trim()}));
		response.setSuccess(true);
		return response;
	}
}
