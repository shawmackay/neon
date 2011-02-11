package org.jini.projects.neon.host.transactions;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.concurrent.CountDownLatch;

import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;

public class TransactionalResourceAdapter implements TransactionalResource, Serializable {

	RemoteTransactionalResource backend;
	
	
	
	public TransactionalResourceAdapter(RemoteTransactionalResource backend) {
		super();
		this.backend = backend;
	}

	public void attachTransaction(String managerAndID, Transaction txn) {
		// TODO Auto-generated method stub
		System.out.println("\t *** Attaching your remote Transaction");
		try {
			backend.attachRemoteTransaction(managerAndID, txn);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void clearTransaction(CountDownLatch latch) {
		// TODO Auto-generated method stub
		System.out.println("\t *** Clearing your remote transaction!");
		try {
			backend.clearRemoteTransaction();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getTransaction() {
		// TODO Auto-generated method stub
		System.out.println("\t *** Getting your remote transaction!");
		try {
			return backend.getRemoteTransaction();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public boolean inTransaction() {
		// TODO Auto-generated method stub
		System.out.println("\t *** Enquiring about your remote transaction!");
		try {
			return backend.inRemoteTransaction();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public void setTransaction(String managerAndID) throws TransactionException {
		// TODO Auto-generated method stub
		System.out.println("\t *** Setting your remote transaction!");
		try {
			backend.setRemoteTransaction(managerAndID);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
