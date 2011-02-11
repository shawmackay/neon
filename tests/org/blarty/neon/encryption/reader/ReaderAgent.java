package org.jini.projects.neon.encryption.reader;

import java.rmi.RemoteException;

import net.jini.core.entry.UnusableEntryException;
import net.jini.core.transaction.TransactionException;

import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.neon.tests.entries.EncryptedSimpleEntry;
import org.jini.projects.neon.tests.entries.SimpleEntry;
import org.jini.projects.neon.transactions.JavaSpaceOps;
import org.jini.projects.neon.util.encryption.EncryptionUtils;

public class ReaderAgent extends AbstractAgent{
@Override
public boolean init() {
	// TODO Auto-generated method stub
	
	SimpleEntry entry = new SimpleEntry();
	
	try {
		JavaSpaceOps ops = (JavaSpaceOps) context.getAgent("neon.transactions.JavaSpaceOps");

	
		SimpleEntry returnedEntry  = (SimpleEntry) ops.read(entry, 2000L);
		//System.out.println("Read ENtry " + returnedEntry.key + "=> " + returnedEntry.value);
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
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return true;
	
}
}
