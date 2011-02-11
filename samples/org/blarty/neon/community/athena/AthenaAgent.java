package org.jini.projects.neon.community.athena;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.jini.projects.athena.command.Command;
import org.jini.projects.athena.exception.CannotExecuteException;
import org.jini.projects.athena.exception.CannotUpdateException;
import org.jini.projects.athena.exception.EmptyResultSetException;
import org.jini.projects.athena.resultset.AthenaResultSet;
import org.jini.projects.neon.collaboration.Collaborative;

public interface AthenaAgent extends Collaborative,Remote{

	public abstract AthenaResultSet executeQuery(Object command) throws CannotExecuteException, EmptyResultSetException, Exception, RemoteException;

	public abstract AthenaResultSet executeQuery(Object command, Object[] params) throws CannotExecuteException, EmptyResultSetException, Exception, RemoteException;

	public abstract Object executeUpdate(Object command) throws CannotUpdateException, Exception, RemoteException;

	public abstract Command getCommand() throws Exception, RemoteException;

}