package org.jini.projects.neon.tasks;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.server.ServerTransaction;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.entry.UnusableEntriesException;

import net.jini.lease.LeaseRenewalManager;
import net.jini.space.JavaSpace;
import net.jini.space.JavaSpace05;
import org.jini.glyph.Injection;
import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.SensorAgent;
import org.jini.projects.neon.agents.sensors.SensorException;
import org.jini.projects.neon.agents.sensors.SensorFilter;
import org.jini.projects.neon.agents.sensors.SensorListener;
import org.jini.projects.neon.host.AgentContext;
import org.jini.projects.neon.host.NoSuchAgentException;

@Injection
public class SpaceTaskManagementImpl extends AbstractAgent implements Runnable, SensorAgent {

	public static class ListenerHolder {
		private AgentIdentity listener;
		private SensorFilter filter;

		public ListenerHolder(AgentIdentity listener, SensorFilter filter) {
			super();
			this.listener = listener;
			this.filter = filter;
		}

		public AgentIdentity getListener() {
			return listener;
		}

		public void setListener(AgentIdentity listener) {
			this.listener = listener;
		}

		public SensorFilter getFilter() {
			return filter;
		}

		public void setFilter(SensorFilter filter) {
			this.filter = filter;
		}
	}

	private Random r = new Random(System.currentTimeMillis());
	HashMap<String, ArrayList<ListenerHolder>> sensors = new HashMap<String, ArrayList<ListenerHolder>>();
	private transient JavaSpace05 space;
	private transient TransactionManager tm;
	private transient LeaseRenewalManager lrm;
	private transient ServerTransaction tx;

	private List<Entry> spaceTemplates = new ArrayList<Entry>();

	public SpaceTaskManagementImpl() {
		this.namespace = "neon.tasks";
		this.name = "TaskManager";

	}

	private static class TaskStatus {
		boolean completed;

		public boolean isCompleted() {
			return completed;
		}

		public void setCompleted(boolean completed) {
			this.completed = completed;
		}

		public TaskStatus() {
			super();

		}

	}

	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		lrm = new LeaseRenewalManager();
		System.out.println("Name set now");
		return true;
	}

	public void setTaskSpace(JavaSpace05 space) {
		this.space = space;
	}

	public void setTransactionManager(TransactionManager tm) {
		this.tm = tm;
	}

	public void run() {

		Collection taken = null;
		ArrayList<TaskStatus> completionStatus = new ArrayList<TaskStatus>();
		try {
			while (true) {

				if (this.sensors.size() > 0) {
					Transaction.Created txc = TransactionFactory.create(tm, 200000L);
					lrm.renewUntil(txc.lease, 200000L, null);
					tx = (ServerTransaction) txc.transaction;

					taken = space.take(spaceTemplates, tx, 200000L, 200);
					System.out.println("Taken Entry in transaction");
					for (Object o : taken) {

						if (taken != null) {
							TaskStatus theStatus = new TaskStatus();
							completionStatus.add(theStatus);
							TaskEntry takenTask = (TaskEntry) o;
							processedTask(takenTask, theStatus);

							TaskRunner taskRunner = new TaskRunner(takenTask, theStatus, sensors.get(takenTask.name), getAgentContext(), space, tx);
							Thread t = new Thread(taskRunner);
							t.start();
						}
					}

					boolean readyToComplete;
					do {
						readyToComplete = true;
						for (TaskStatus s : completionStatus) {
							if (!s.isCompleted()) {
								readyToComplete = false;
								break;
							}

						}
						try {
							Thread.sleep(50);

						} catch (Exception ex) {
							ex.printStackTrace();
						}
					} while (!readyToComplete);
					System.out.println("Commiting Take");
					tx.commit();
					completionStatus.clear();
				} else
					synchronized (this) {
						wait(1000);
					}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (TransactionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LeaseDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnusableEntriesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Agent has been asked to halt");
	}

	private void processedTask(TaskEntry taken, TaskStatus s) {
		// TODO Auto-generated method stub

	}

	private static class TaskRunner implements Runnable {

		private TaskEntry taken;
		private TaskStatus status;
		private ArrayList<ListenerHolder> workers;
		private AgentContext ctx;
		private JavaSpace05 space;
		private Random r = new Random(System.currentTimeMillis());
		private Transaction tx;

		public TaskRunner(TaskEntry taken, TaskStatus status, ArrayList<ListenerHolder> workers, AgentContext ctx, JavaSpace05 space, Transaction tx) {
			super();
			this.taken = taken;
			this.status = status;
			this.workers = workers;
			this.ctx = ctx;
			this.space = space;
			this.r = r;
			this.tx = tx;
		}

		public void run() {
			//System.out.println("Processing task: " + taken.name);
			Task t = new DefaultTask(taken.name, taken.arguments);
			boolean taskProcessed = false;

			int pos = r.nextInt(workers.size());
			
			ListenerHolder holder = workers.get(pos);
			SensorFilter filter = (SensorFilter) holder.getFilter();
			AgentIdentity address = (AgentIdentity) holder.getListener();

			if (filter.accept(t)) {
				

				try {
					SensorListener aListener = (SensorListener) ctx.getAgent(address);
			
					Object o = aListener.sensorTriggered("TaskReceived", t);
			
					if (o instanceof TaskResponse) {
						TaskResponse response = (TaskResponse) o;
						taskProcessed = response.isSuccess();
						if (taskProcessed) {
							List<Task> tasksForSpace = response.getOutputTasks();
							List<TaskEntry> taskEntries = new ArrayList<TaskEntry>();
							List<Long> taskLeases = new ArrayList<Long>();

							for (Task task : tasksForSpace) {
								taskEntries.add(new TaskEntry(task.getActionName(), task.getArguments(), new java.util.Date()));
								taskLeases.add(new Long(Lease.FOREVER));
							}
							if (taskEntries.size() > 0)
								space.write(taskEntries, tx, taskLeases);
						} else {
							 System.err.println("Task: " + t.getActionName() + " was NOT processed successfully");
							space.write(taken, tx, Lease.FOREVER);
						}
					}

				} catch (NoSuchAgentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					taskProcessed = false;
				} catch (SensorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					taskProcessed = false;
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TransactionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			status.setCompleted(true);
		}
	}

	public boolean addListener(AgentIdentity listener, SensorFilter filter) throws RemoteException {
		// TODO Auto-generated method stub
		ListenerHolder holder = new ListenerHolder(listener, filter);
		TaskFilter tFilter = (TaskFilter) filter;
		ArrayList<ListenerHolder> holderList = null;
		String[] taskNames = tFilter.getTaskNames();
		for (String taskName : taskNames) {
			if (sensors.containsKey(taskName)) {
				holderList = sensors.get(taskName);
			} else {
				holderList = new ArrayList<ListenerHolder>();
				sensors.put(taskName, holderList);
			//	System.out.println("Adding holderList for: " + taskName);
				spaceTemplates.add(new TaskEntry(taskName, null, null));

			}

			holderList.add(holder);
		}
		displayTemplates();
		return true;
	}

	public boolean removeListener(AgentIdentity a) throws RemoteException {
		sensors.remove(a);
		return false;
	}

	private void displayTemplates() {
//		System.out.println("Task Entries are now filtered to:");
//		for (Entry e : spaceTemplates) {
//			TaskEntry t = (TaskEntry) e;
//
//			System.out.println("\t" + t.name);
//		}
	}
}
