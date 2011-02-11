/*
 * athena.jini.org : org.jini.projects.athena.service.constrainable
 * 
 * 
 * AthenaTransactionParticipantProxy.java
 * Created on 06-Apr-2004
 * 
 * AthenaTransactionParticipantProxy
 *
 */
package org.jini.projects.neon.host.transactions.constrainable;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import net.jini.core.constraint.MethodConstraints;
import net.jini.core.constraint.RemoteMethodControl;
import net.jini.core.transaction.UnknownTransactionException;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.core.transaction.server.TransactionParticipant;
import net.jini.id.ReferentUuid;
import net.jini.id.ReferentUuids;
import net.jini.id.Uuid;

/**
 * @author calum
 */
public class NeonTransactionParticipantProxy implements TransactionParticipant,
		Serializable, ReferentUuid {

	final static class ConstrainableNeonTransactionParticipantProxy extends
			NeonTransactionParticipantProxy implements RemoteMethodControl {

		public ConstrainableNeonTransactionParticipantProxy(
				TransactionParticipant participant, Uuid id,
				MethodConstraints methodConstraints) {
			super(constrainServer(participant, methodConstraints), id);
			l.fine("Creating a secure proxy");
		}

		public RemoteMethodControl setConstraints(MethodConstraints constraints) {
			return new NeonTransactionParticipantProxy.ConstrainableNeonTransactionParticipantProxy(
					backend, proxyID, constraints);
		}

		/** {@inheritDoc} */
		public MethodConstraints getConstraints() {
			return ((RemoteMethodControl) backend).getConstraints();
		}

	}

	private static final long serialVersionUID = 267682616263L;
	transient Logger l = Logger
			.getLogger("org.jini.projects.neon.transactions");

	final TransactionParticipant backend;
	final Uuid proxyID;

	/**
	 * 
	 */
	public NeonTransactionParticipantProxy(TransactionParticipant backend,
			Uuid proxyID) {
		super();
		this.backend = backend;
		this.proxyID = proxyID;
		// URGENT Complete constructor stub for AthenaRegistrationProxy
	}

	private static TransactionParticipant constrainServer(
			TransactionParticipant server, MethodConstraints methodConstraints) {
		return (TransactionParticipant) ((RemoteMethodControl) server)
				.setConstraints(methodConstraints);
	}

	/**
	 * @param mgr
	 *            Transaction Manager.
	 * @param id
	 *            Transaction ID
	 * @throws net.jini.core.transaction.UnknownTransactionException
	 * @throws java.rmi.RemoteException
	 */
	public void abort(TransactionManager mgr, long id)
			throws UnknownTransactionException, RemoteException {
		// System.out.println("Sending Abort for transaction: " + id);
		this.backend.abort(mgr, id);
		// System.out.println("Abort complete for transaction: " + id);

	}

	/**
	 * @param mgr
	 * @param id
	 * @throws net.jini.core.transaction.UnknownTransactionException
	 * @throws java.rmi.RemoteException
	 */
	public void commit(TransactionManager mgr, long id)
			throws UnknownTransactionException, RemoteException {
		System.out.println("Sending Commit for transaction: ");
		this.backend.commit(mgr, id);
		System.out.println("Commit complete for transaction: ");
	}

	/**
	 * @param mgr
	 *            Transaction Manager.
	 * @param id
	 *            Transaction ID
	 * @return either PREPARE or ABORT
	 * @throws net.jini.core.transaction.UnknownTransactionException
	 * @throws java.rmi.RemoteException
	 */
	public int prepare(TransactionManager mgr, long id)
			throws UnknownTransactionException, RemoteException {
		System.out.println("Sending Prepare for transaction:");
		System.out.println("Backend null?: " + (this.backend == null));
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int returnValue = 0;
		try {
			returnValue = this.backend.prepare(mgr, id);
		} catch (UnknownTransactionException ute) {
			System.out.println(ute.getMessage());
			ute.printStackTrace();
			throw ute;
		} catch (RemoteException re) {
			System.out.println(re.getMessage());
			re.printStackTrace();
			throw re;
		} catch (RuntimeException rex){
			System.out.println("Runtime Exception: " + rex.getMessage());
			rex.printStackTrace();
		}
		System.out.println("Prepare complete for transaction: ");
		return returnValue;
	}

	/**
	 * @param mgr
	 *            Transaction Manager.
	 * @param id
	 *            Transaction ID
	 * @return either COMMIT or ABORT
	 * @throws net.jini.core.transaction.UnknownTransactionException
	 * @throws java.rmi.RemoteException
	 */
	public int prepareAndCommit(TransactionManager mgr, long id)
			throws UnknownTransactionException, RemoteException {
		System.out.println("Sending PrepareAndCommit for transaction");
		int returnValue = this.backend.prepareAndCommit(mgr, id);
		System.out.println("PrepareAndCommit complete for transaction");
		return returnValue;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Neon Transaction Participant";
	}

	/*
	 * @see net.jini.id.ReferentUuid#getReferentUuid()
	 */
	public Uuid getReferentUuid() {
		// TODO Complete method stub for getReferentUuid
		return proxyID;
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return ReferentUuids.compare(this, obj);
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return backend.hashCode();
	}
}
