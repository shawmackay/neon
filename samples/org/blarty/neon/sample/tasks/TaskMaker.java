package org.jini.projects.neon.sample.tasks;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

import net.jini.core.event.RemoteEvent;
import net.jini.core.event.UnknownEventException;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;



import org.jini.glyph.Injection;
import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.SensorAgent;
import org.jini.projects.neon.agents.sensors.SensorException;
import org.jini.projects.neon.agents.sensors.SensorListener;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.neon.render.EngineInstruction;
import org.jini.projects.neon.render.PresentableAgent;
import org.jini.projects.neon.tasks.Task;
import org.jini.projects.neon.tasks.TaskEntry;
import org.jini.projects.neon.tasks.TaskFilter;
import org.jini.projects.neon.tasks.TaskListener;
import org.jini.projects.neon.tasks.TaskProcessor;
import org.jini.projects.neon.tasks.TaskResponse;

@Injection
public class TaskMaker extends AbstractAgent implements PresentableAgent, SensorListener {

	private JavaSpace space;

	private ArrayList<SquareHolder> sentTasks = new ArrayList<SquareHolder>();
	private TaskProcessor processor;
	private TaskFilter filter;

	private static class SquareHolder {
		private int value;
		private int square = -1;
		private int cube = -1;
		private int quad = -1;
		private int quint = -1;

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public int getSquare() {
			return square;
		}

		public void setSquare(int square) {
			//System.out.printf("Cube Power of %d has been set to %d\n", value, square);
			this.square = square;
		}

		public int getCube() {
		 return cube;
		}

		public void setCube(int cube) {
			//System.out.printf("Cube Power of %d has been set to %d\n", value, cube);
			this.cube = cube;
		}

		public int getQuad() {

			return quad;
		}

		public void setQuad(int quad) {
			//System.out.printf("Quad Power of %d has been set to %d\n", value, quad);
			this.quad = quad;
		}

		public int getQuint() {
			return quint;
		}

		public void setQuint(int quint) {
		//	System.out.printf("Quint Power of %d has been set to %d\n", value, quint);
			this.quint = quint;
		}

		public SquareHolder(int value) {
			this.value = value;
		}
	}

	@Override
	public boolean init() {
		try {
			SensorAgent sens = (SensorAgent) getAgentContext().getAgent("neon.tasks.TaskManager");
			sens.addListener(this.getIdentity(), filter);
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAgentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int a = 0; a < 10; a++) {
			sentTasks.add(new SquareHolder(a));
			try {
				writeEntryToSpace(new TaskEntry("SquareMultiply", new Object[] { new Integer(a) }, new java.util.Date()));
				writeEntryToSpace(new TaskEntry("CubeMultiply", new Object[] { new Integer(a) }, new java.util.Date()));
				writeEntryToSpace(new TaskEntry("QuadMultiply", new Object[] { new Integer(a) }, new java.util.Date()));
				writeEntryToSpace(new TaskEntry("QuintMultiply", new Object[] { new Integer(a) }, new java.util.Date()));
				//System.out.println("Generated tasks for value: " + a);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (TransactionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	private void writeEntryToSpace(TaskEntry tr) throws TransactionException, RemoteException {
		space.write(tr, null, Lease.FOREVER);

	}

	public EngineInstruction getPresentableFormat(String type, String action, Map params, boolean getTemplate) {
		// TODO Auto-generated method stub
		return null;
	}

	public URL getTemplate(String type, String action) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object sensorTriggered(String sensortype, Object value) throws SensorException {
		TaskResponse tr = new TaskResponse();
		Task t = (Task) value;

		Integer multiplyingValue = (Integer) t.getArguments()[0];
		Integer result = (Integer) t.getArguments()[1];
		System.out.println("Looking for : " + multiplyingValue);
		SquareHolder pwrh = sentTasks.get(multiplyingValue);
		if (t.getActionName().equals("QuadValue")) {
			pwrh.setQuad(result);
		}
		if (t.getActionName().equals("CubeValue")) {
			pwrh.setCube(result);
		}
		if (t.getActionName().equals("SquareValue")) {
			pwrh.setSquare(result);
		}
		if (t.getActionName().equals("QuintValue")) {
			pwrh.setQuint(result);
		}
		tr.setSuccess(true);
		return tr;
	}

	public void setTaskFilter(TaskFilter filter) {
		this.filter = filter;
	//	System.out.println("Set Task filter to: " + filter);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTaskProcessor(TaskProcessor processor) {
		this.processor = processor;
	}


	public void setSpace(JavaSpace space){
		this.space = space;
	}
}
