package org.jini.projects.neon.integration;

import net.jini.config.ConfigurationException;
import net.jini.core.entry.Entry;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.transaction.TransactionException;
import net.jini.lookup.entry.Name;

import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.AgentState;
import org.jini.projects.neon.agents.TransactionalAgent;
import org.jini.projects.neon.community.athena.AthenaAgent;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.neon.host.transactions.InternalTransaction;
import org.jini.projects.neon.host.transactions.TransactionAgent;


import org.jini.projects.neon.integration.grid.FileCreatorAgent;
import org.jini.projects.neon.transactions.EmployeeEntry;
import org.jini.projects.neon.transactions.JavaSpaceOps;


import com.sun.jini.constants.TimeConstants;

/**
 * Show how transaction work under Neon
 * 
 * @author calum
 * 
 */
public class AthenaSpaceIntegration extends org.jini.projects.neon.agents.AbstractAgent implements Runnable {

	InternalTransaction  itxn = null;
	
	public AthenaSpaceIntegration() {
		// TODO Auto-generated constructor stub

	}

	@Override
	public boolean init() {
		// TODO Auto-generated method stub

		return true;
	}

	public void run() { 
		// TODO Auto-generated method stub
		try {
			getAgentLogger().info("Getting athena Agent");
			AthenaAgent athena = (AthenaAgent) getAgentContext().attachAgent("neon.samples.Athena");
			getAgentLogger().info("Getting FileCreator Agent");
			FileCreatorAgent fc = (FileCreatorAgent) getAgentContext().attachAgent("neon.samples.FileCreator");
			String spaceName = (String) getAgentConfiguration().getEntry("neon.integration", "updateSpace", String.class);
			getAgentLogger().info("Getting JavaspaceOps Agent");
			org.jini.projects.neon.transactions.JavaSpaceOps ops = (JavaSpaceOps) getAgentContext().attachAgent("neon.transactions.JavaSpaceOps", new Entry[]{new Name(spaceName)});
			getAgentLogger().info("All AGents acquired - running updates");
			runUpdates(ops, athena, fc);
		} catch (NoSuchAgentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void runUpdates(JavaSpaceOps ops, AthenaAgent athena, FileCreatorAgent fc) {
	setAgentState(AgentState.BUSY);
     	TransactionAgent txn = null;
		try {

			
			while (txn == null) {
				if (txn == null) {
					try {
						txn = (TransactionAgent) context.attachAgent("neon.Transaction", 0);
						itxn = txn.createTransaction(this.getIdentity());
					} catch (LeaseDeniedException e) {
						// TODO Handle LeaseDeniedException
						e.printStackTrace();
					} catch (NoSuchAgentException e) {
						// TODO Handle NoSuchAgentException
						synchronized (this) {
							wait(100);
						}
					} catch (TransactionException e) {
						// TODO Handle TransactionException
						e.printStackTrace();
					}
				}
			}
			
			EmployeeEntry entr = new EmployeeEntry(new Integer(12),"Homer Simpson", "D'oh");
			ops.write(entr, 10L * TimeConstants.MINUTES);
			athena.executeUpdate("INSERT INTO TEST(description, unitprice, quantity) Values ('Krusty" +
					" clock', 59.99, 20)");
			fc.setMessage("Home rSimpson Added as Employee");
			itxn.commit();
		} catch (Exception e) {
			getAgentLogger().info("UpdateAgentCommit received an Exception of Type: " + e.getClass().getName());
			e.printStackTrace();
			getAgentLogger().fine("Going to abort transaction");
			try {
				itxn.abort();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		if (ops != null)
			while (!context.detachAgent(ops))
				;
		context.detachAgent(txn);
		setAgentState(AgentState.AVAILABLE);
	}
}
