package org.jini.projects.neon.tasks;

import java.rmi.RemoteException;

import org.jini.glyph.Injection;
import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.LocalAgent;
import org.jini.projects.neon.agents.SensorAgent;
import org.jini.projects.neon.agents.sensors.SensorException;
import org.jini.projects.neon.agents.sensors.SensorListener;
import org.jini.projects.neon.agents.sensors.TimeFilter;
import org.jini.projects.neon.host.NoSuchAgentException;

@Injection
public class TaskAgent extends AbstractAgent implements LocalAgent, SensorListener{

	private TaskProcessor processor;
	private TaskFilter filter;
	public TaskAgent(){
		this.namespace = "neon.tasks";
	}
	
	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		 try {
			SensorAgent sens = (SensorAgent) getAgentContext().getAgent("TaskManager");
			 sens.addListener(this.getIdentity(), filter);
			 return true;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAgentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public void setTaskFilter(TaskFilter filter){
		this.filter = filter;
		System.out.println("Set Task filter to: " + filter);
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setTaskProcessor(TaskProcessor processor){
		this.processor = processor;
	}

	public Object sensorTriggered(String sensortype, Object value) throws SensorException {
		// TODO Auto-generated method stub
		TaskResponse tr = processor.execute((Task)value);
		return tr;
	}
	
	
}
