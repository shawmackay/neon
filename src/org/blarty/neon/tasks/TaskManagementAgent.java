package org.jini.projects.neon.tasks;

import net.jini.core.entry.Entry;
import net.jini.core.event.EventRegistration;

import org.jini.projects.neon.agents.TaskAgent;
import org.jini.projects.neon.collaboration.Collaborative;

public interface  TaskManagementAgent extends Collaborative {
	public EventRegistration notify(Entry template, TaskListener listener);
}
