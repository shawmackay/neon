package org.jini.projects.neon.host.transactions;

import java.rmi.RemoteException;

import net.jini.core.transaction.CannotAbortException;
import net.jini.core.transaction.CannotCommitException;
import net.jini.core.transaction.TimeoutExpiredException;
import net.jini.core.transaction.UnknownTransactionException;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;

public class InternalTransactionImpl implements InternalTransaction {
    private Uuid id;
    
    private TransactionBlackBox txnBB;
    
    public InternalTransactionImpl(Uuid ID, TransactionBlackBox internalBlackBox){
        this.id = ID;
        txnBB = internalBlackBox;
        
    }
    
    public Uuid getInternalID(){
           return this.id;
    }
    
    public void commit() throws UnknownTransactionException, CannotCommitException, RemoteException{
        txnBB.commit(id);
    }
    
    public void abort() throws UnknownTransactionException, CannotAbortException, RemoteException{
        txnBB.abort(id);
    }
}
