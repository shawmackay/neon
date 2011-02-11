package org.jini.projects.neon.host.transactions;

import java.rmi.RemoteException;

import net.jini.core.transaction.CannotAbortException;
import net.jini.core.transaction.CannotCommitException;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.UnknownTransactionException;
import net.jini.id.Uuid;

public interface InternalTransaction {
    public Uuid getInternalID();

    public void abort() throws UnknownTransactionException, CannotAbortException, RemoteException;

    public void commit() throws UnknownTransactionException, CannotCommitException, RemoteException;

}