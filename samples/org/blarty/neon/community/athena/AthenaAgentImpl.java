package org.jini.projects.neon.community.athena;

import java.rmi.RemoteException;

import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.lease.UnknownLeaseException;
import net.jini.core.transaction.Transaction;
import net.jini.lease.LeaseRenewalManager;

import org.jini.glyph.Exportable;
import org.jini.glyph.Injection;
import org.jini.projects.athena.command.Command;
import org.jini.projects.athena.connection.AthenaConnection;
import org.jini.projects.athena.exception.CannotExecuteException;
import org.jini.projects.athena.exception.CannotUpdateException;
import org.jini.projects.athena.exception.EmptyResultSetException;
import org.jini.projects.athena.exception.HostException;
import org.jini.projects.athena.resultset.AthenaResultSet;
import org.jini.projects.athena.service.AthenaRegistration;
import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.host.transactions.TransactionAccessor;

@Exportable
@Injection
public class AthenaAgentImpl extends AbstractAgent implements AthenaAgent,TransactionAccessor {
	AthenaConnection athenaconn;

	Transaction txn;

	LeaseRenewalManager lrm = new LeaseRenewalManager();

	public AthenaAgentImpl() {
	this.name="Athena";
	this.namespace = "neon.samples";
	}
	
	
	public void setGlobalTransaction(Transaction txn) {
		this.txn = txn;
	}

	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		return true;

	}

	public void setAthena(AthenaRegistration service) {
		
		try {
			athenaconn = service.getConnection("Neon Athena Agent", 20000L);
			lrm.renewFor(athenaconn.getLease(), Lease.FOREVER, null);
			getAgentLogger().info("Athena connection obtained and leased");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LeaseDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		super.stop();
		try {
			if (athenaconn.canRelease(true)) {
				athenaconn.release();
				lrm.cancel(athenaconn.getLease());
			}
		} catch (UnknownLeaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.jini.projects.neon.community.athena.AthenaAgent#executeQuery(java.lang.Object)
	 */
	public AthenaResultSet executeQuery(Object command) throws CannotExecuteException, EmptyResultSetException, Exception, RemoteException {

		return athenaconn.executeQuery(command);
	}

	/* (non-Javadoc)
	 * @see org.jini.projects.neon.community.athena.AthenaAgent#executeQuery(java.lang.Object, java.lang.Object[])
	 */
	public AthenaResultSet executeQuery(Object command, Object[] params) throws CannotExecuteException, EmptyResultSetException, Exception, RemoteException {

		return athenaconn.executeQuery(command, params);
	}

	/* (non-Javadoc)
	 * @see org.jini.projects.neon.community.athena.AthenaAgent#executeUpdate(java.lang.Object)
	 */
	public Object executeUpdate(Object command) throws CannotUpdateException, Exception, RemoteException {

		return athenaconn.executeUpdate(command, txn);
	}

	/* (non-Javadoc)
	 * @see org.jini.projects.neon.community.athena.AthenaAgent#getCommand()
	 */
	public Command getCommand() throws Exception, RemoteException {
		return athenaconn.getCommand();
	}
}
