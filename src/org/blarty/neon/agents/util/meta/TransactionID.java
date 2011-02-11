package org.jini.projects.neon.agents.util.meta;

import net.jini.core.transaction.Transaction;
import net.jini.entry.AbstractEntry;

public class TransactionID extends AbstractEntry {
	public String transactionID;
	public Transaction theTransaction;

	public TransactionID(String transactionID, Transaction distTxn) {
		super();
		this.transactionID = transactionID;
		System.out.println("Creating transaction ID Meta Localised: " + transactionID + "; Dist txn: " + distTxn) ;
		this.theTransaction = distTxn;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public Transaction getTheTransaction() {
		return theTransaction;
	}
	
	public String toString(){
		return "Localised TxnID: " + transactionID + "; Dist Transaction: " + theTransaction;
	}
}
