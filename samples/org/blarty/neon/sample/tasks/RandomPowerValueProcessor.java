package org.jini.projects.neon.sample.tasks;

import java.util.Random;

import org.jini.projects.neon.tasks.DefaultTask;
import org.jini.projects.neon.tasks.Task;
import org.jini.projects.neon.tasks.TaskEntry;
import org.jini.projects.neon.tasks.TaskProcessor;
import org.jini.projects.neon.tasks.TaskResponse;

public class RandomPowerValueProcessor implements TaskProcessor {

	Random r = new Random(System.currentTimeMillis());

	public TaskResponse execute(Task t) {
		// TODO Auto-generated method stub
		Integer value = (Integer) t.getArguments()[0];
		TaskResponse tr = new TaskResponse();
		Task pushBackTask = null;
				if (t.getActionName().equals("QuadMultiply")) {
			pushBackTask = new DefaultTask("QuadValue", value, value * value * value * value);
		}
		if (t.getActionName().equals("CubeMultiply")) {
			pushBackTask = new DefaultTask("CubeValue", value, value * value * value);
		}
		if (t.getActionName().equals("SquareMultiply")) {
			pushBackTask = new DefaultTask("SquareValue", value, value * value);
		}
		if (t.getActionName().equals("QuintMultiply")) {
			pushBackTask = new DefaultTask("QuintValue", value, value * value * value * value * value);
		}
		if (pushBackTask != null) {
			tr.addOutputTask(pushBackTask);
			tr.setSuccess(true);
		} else
			tr.setSuccess(false);
		return tr;
	}
}
