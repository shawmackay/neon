/*
* Copyright 2005 neon.jini.org project 
* 
* Licensed under the Apache License, Version 2.0 (the "License"); 
* you may not use this file except in compliance with the License. 
* You may obtain a copy of the License at 
* 
*       http://www.apache.org/licenses/LICENSE-2.0 
* 
* Unless required by applicable law or agreed to in writing, software 
* distributed under the License is distributed on an "AS IS" BASIS, 
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
* See the License for the specific language governing permissions and 
* limitations under the License.
*/


package org.jini.projects.neon.transactions;

/*
* JavaSpaceOps.java
*
* Created Mon Mar 21 10:20:09 GMT 2005
*/
import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;

import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.event.EventRegistration;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.TransactionException;

import org.jini.projects.neon.agents.Crypto;
import org.jini.projects.neon.collaboration.Collaborative;

/**
*
* @author  calum
*
*/

public interface JavaSpaceOps extends Collaborative,Remote{
	public Entry snapshot(Entry  aEntry) throws RemoteException;
	public Entry readIfExists(Entry  aEntry,  long  along) throws UnusableEntryException, TransactionException, InterruptedException, RemoteException;
	public Entry take(Entry  aEntry,  long  along) throws UnusableEntryException, TransactionException, InterruptedException, RemoteException;
	public Entry takeIfExists(Entry  aEntry,  long  along) throws UnusableEntryException, TransactionException, InterruptedException, RemoteException;
	public EventRegistration notify(Entry  aEntry,  RemoteEventListener  aRemoteEventListener, long  along, MarshalledObject  aMarshalledObject) throws TransactionException, RemoteException;
	public Lease write(Entry  aEntry,  long  along) throws TransactionException, RemoteException;
	public Entry read(Entry  aEntry,  long  along) throws UnusableEntryException, TransactionException, InterruptedException, RemoteException;
}
