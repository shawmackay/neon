package org.jini.projects.neon.encryption.simple;

import java.rmi.RemoteException;

import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;

import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.neon.host.transactions.TransactionAccessor;
import org.jini.projects.neon.tests.entries.SimpleEntry;
import org.jini.projects.neon.transactions.JavaSpaceOps;

public class SimpleMessageImpl extends AbstractAgent implements TransactionAccessor, SimpleMessage {

	Transaction txn;

	String message;

	public void setGlobalTransaction(Transaction txn) {
		this.txn = txn;
	}

	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		this.name = "SimpleMessage";
		this.namespace = "simple";
		SimpleEntry entry = new SimpleEntry();
		entry.key = "Hello";
		entry.value = "There";
		try {
			getAgentLogger().info("Simple Message Impl looking for Javaspace");
			JavaSpaceOps ops = (JavaSpaceOps) context.getAgent("neon.transactions.JavaSpaceOps");
			try {
				getAgentLogger().info("Simple Message Impl writing to Javaspace");
				ops.write(entry, 20000L);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SimpleEntry view = new SimpleEntry();
			view.key = "Hello";
			getAgentLogger().info("Encrypted Read test: " + ops.read(view, 2000L));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAgentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransactionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnusableEntryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		super.stop();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jini.projects.neon.community.athena.SimpleMessage#setMessage(java.lang.String)
	 */
	public void setMessage(String message) {
		this.message = message;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jini.projects.neon.community.athena.SimpleMessage#getMessage()
	 */
	public String getMessage() {
		return message;
	}
}
