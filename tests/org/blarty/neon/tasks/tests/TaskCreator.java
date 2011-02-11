package org.jini.projects.neon.tasks.tests;

import java.rmi.RemoteException;

import net.jini.core.lease.Lease;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;

import org.jini.glyph.Injection;
import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.neon.service.ServiceAgent;
import org.jini.projects.neon.tasks.TaskEntry;

@Injection
public class TaskCreator extends AbstractAgent{

	private JavaSpace space;
	
	@Override
	public boolean init() {
	
		TaskEntry tr = new TaskEntry("ReverseOrder", new Object[]{"The quick brown fox jumped over the lazy dog"}, new java.util.Date());
		try {
			space.write(tr, null, Lease.FOREVER);
			System.out.println("Written task to space");
			return true;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransactionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	public void setSpace(JavaSpace space){
		this.space = space;
	}

}
