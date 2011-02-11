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
 * JavaSpaceOpsAgent.java
 *
 * Created Mon Mar 21 10:19:22 GMT 2005
 */
import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.jini.config.Configuration;
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.event.EventRegistration;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.core.transaction.server.ServerTransaction;
import net.jini.lookup.entry.Name;
import net.jini.space.JavaSpace;

import org.jini.glyph.Exportable;
import org.jini.glyph.Injection;
import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.Crypto;
import org.jini.projects.neon.agents.LocalAgent;
import org.jini.projects.neon.agents.util.LocalAgentConstraintsEntry;
import org.jini.projects.neon.host.transactions.TransactionAccessor;
import org.jini.projects.neon.service.ServiceAgent;
import org.jini.projects.neon.users.agents.KeyStoreUtility;
import org.jini.projects.neon.util.encryption.CryptoHolder;
import org.jini.projects.neon.util.encryption.EncryptionUtils;

/**
 *
 * @author  calum
 *
 */
@Injection
@Exportable
public class JavaSpaceOpsAgent extends AbstractAgent implements JavaSpaceOps, TransactionAccessor, LocalAgent, Remote,Crypto {

    Transaction aTransaction;
    String spaceToUse;
    JavaSpace space;
    private boolean useCrypto = false;


    public JavaSpaceOpsAgent() {
        this.namespace = "neon.transactions";
        this.name = "JavaSpaceOps";

    }

    public void setSpaceName(String spacename) {
        spaceToUse = spacename;
    }

    /**
     * init
     * @return boolean
     */
    public boolean init() {
        useCrypto(false);
        try {
            Configuration config = this.getAgentConfiguration();
            //String spaceToUse  = (String) config.getEntry(namespace + "." + name,"spaceName", String.class);
            ServiceAgent svcs = (ServiceAgent) getAgentContext().getAgent("neon.Services");
            getMetaAttributes().addAttribute(new Name(spaceToUse));
            List l = svcs.getNamedService(spaceToUse, JavaSpace.class);
            if (l.size() > 0) {
                space = (JavaSpace) ((ServiceItem) l.get(0)).service;
                getAgentLogger().info("JavaSpaceOps has found required space: " + spaceToUse);
                return true;
            } else {
                getAgentLogger().severe("Requested space " + spaceToUse + " not found...returning true anyway for testing");
                return true;
            }
        } catch (Exception ex) {
            System.err.println("Caught Exception: " + ex.getClass().getName() + "; Msg: " + ex.getMessage());
            ex.printStackTrace();
        }
       
        return false;
    }

    public void setGlobalTransaction(Transaction aTransaction) {
        //if(aTransaction!=null)
        //System.out.println("\t ****JAVASPACEOPS setGlobalTransaction now set to " + ((ServerTransaction)aTransaction).id);
        getAgentLogger().info("JAVASPACEOPS DIST. TRANSACTION NOW SET TO: " + aTransaction);
        this.aTransaction = aTransaction;
    }

    /**
     * readIfExists
     * @param aEntry
     * @param aTransaction
     * @param along
     * @throws net.jini.core.entry.UnusableEntryException,net.jini.core.transaction.TransactionException,java.lang.InterruptedException,java.rmi.RemoteException
     * @return Entry
     */
    public Entry readIfExists(Entry aEntry, long along) throws UnusableEntryException, TransactionException, InterruptedException, RemoteException {
        if (useCrypto) {
            try {
                
                MarshalledObject o = new MarshalledObject(aEntry);
                //We don't store the Object in the Encrypted Form when we read
                Object encryptedEntry = EncryptionUtils.findEncryptedVersion(aEntry, null);
                if (encryptedEntry instanceof Entry && encryptedEntry instanceof CryptoHolder) {
                    Entry ent = space.readIfExists((Entry)  encryptedEntry, aTransaction,  along);
                    if(ent instanceof CryptoHolder){
                        byte[] barr = ((CryptoHolder) ent).getEncryptedObject().getEncryptedBytes();
                        Object unenc = EncryptionUtils.decryptObjectWithDefaultKey(barr);
                        if(unenc instanceof MarshalledObject){
                            Object unmarshalled = ((MarshalledObject)unenc).get();
                            System.out.println("Unmarshalled version:" + unmarshalled.getClass().getName());
                            if(unmarshalled instanceof Entry){
                                return (Entry) unmarshalled;
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(JavaSpaceOpsAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return space.readIfExists(aEntry, aTransaction, along);
    }

    /**
     * take
     * @param aEntry
     * @param aTransaction
     * @param along
     * @throws net.jini.core.entry.UnusableEntryException,net.jini.core.transaction.TransactionException,java.lang.InterruptedException,java.rmi.RemoteException
     * @return Entry
     */
    public Entry take(Entry aEntry, long along) throws UnusableEntryException, TransactionException, InterruptedException, RemoteException {
        getAgentLogger().info("Taking an entry of type " + aEntry.getClass().getName());
        
        if (useCrypto) {
            try {
                
                MarshalledObject o = new MarshalledObject(aEntry);
                //We don't store the Object in the Encrypted Form when we read
                Object encryptedEntry = EncryptionUtils.findEncryptedVersion(aEntry, null);
                if (encryptedEntry instanceof Entry && encryptedEntry instanceof CryptoHolder) {
                    Entry ent = space.take((Entry)  encryptedEntry, aTransaction,  along);
                    if(ent instanceof CryptoHolder){
                        byte[] barr = ((CryptoHolder) ent).getEncryptedObject().getEncryptedBytes();
                        Object unenc = EncryptionUtils.decryptObjectWithDefaultKey(barr);
                        if(unenc instanceof MarshalledObject){
                            Object unmarshalled = ((MarshalledObject)unenc).get();
                            System.out.println("Unmarshalled version:" + unmarshalled.getClass().getName());
                            if(unmarshalled instanceof Entry){
                                return (Entry) unmarshalled;
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(JavaSpaceOpsAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return space.take(aEntry, aTransaction, along);
    }

    /**
     * takeIfExists
     * @param aEntry
     * @param aTransaction
     * @param along
     * @throws net.jini.core.entry.UnusableEntryException,net.jini.core.transaction.TransactionException,java.lang.InterruptedException,java.rmi.RemoteException
     * @return Entry
     */
    public Entry takeIfExists(Entry aEntry, long along) throws UnusableEntryException, TransactionException, InterruptedException, RemoteException {
        if (useCrypto) {
            try {
                
                MarshalledObject o = new MarshalledObject(aEntry);
                //We don't store the Object in the Encrypted Form when we read
                Object encryptedEntry = EncryptionUtils.findEncryptedVersion(aEntry, null);
                if (encryptedEntry instanceof Entry && encryptedEntry instanceof CryptoHolder) {
                    Entry ent = space.takeIfExists((Entry)  encryptedEntry, aTransaction,  along);
                    if(ent instanceof CryptoHolder){
                        byte[] barr = ((CryptoHolder) ent).getEncryptedObject().getEncryptedBytes();
                        Object unenc = EncryptionUtils.decryptObjectWithDefaultKey(barr);
                        if(unenc instanceof MarshalledObject){
                            Object unmarshalled = ((MarshalledObject)unenc).get();
                            System.out.println("Unmarshalled version:" + unmarshalled.getClass().getName());
                            if(unmarshalled instanceof Entry){
                                return (Entry) unmarshalled;
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(JavaSpaceOpsAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return space.takeIfExists(aEntry, aTransaction, along);
    }

    /**
     * notify
     * @param aEntry
     * @param aTransaction
     * @param aRemoteEventListener
     * @param along
     * @param aMarshalledObject
     * @throws net.jini.core.transaction.TransactionException,java.rmi.RemoteException
     * @return EventRegistration
     */
    public EventRegistration notify(Entry aEntry, RemoteEventListener aRemoteEventListener, long along, MarshalledObject aMarshalledObject) throws TransactionException, RemoteException {
        return space.notify(aEntry, aTransaction, aRemoteEventListener, along, aMarshalledObject);
    }

    /**
     * write
     * @param aEntry
     * @param aTransaction
     * @param along
     * @throws net.jini.core.transaction.TransactionException,java.rmi.RemoteException
     * @return Lease
     */
    public Lease write(Entry aEntry, long along) throws TransactionException, RemoteException {
      
        getAgentLogger().finest("Writing an entry of type " + aEntry.getClass().getName() + " to space: " + spaceToUse);
        if (aTransaction == null) {
            getAgentLogger().finest("\t *****Writing an entry but not under a transaction");
        }
        assert space != null : "Space is null";
        if (useCrypto) {
            try {
             
                MarshalledObject o = new MarshalledObject(aEntry);
                Object encryptedEntry = EncryptionUtils.findEncryptedVersion(aEntry, o);
                if (encryptedEntry instanceof Entry) {
                    return space.write((Entry) encryptedEntry, aTransaction, along);
                }
            } catch (Exception ex) {
                Logger.getLogger(JavaSpaceOpsAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return space.write(aEntry, aTransaction, along);
    }

    /**
     * read
     * @param aEntry
     * @param aTransaction
     * @param along
     * @throws net.jini.core.entry.UnusableEntryException,net.jini.core.transaction.TransactionException,java.lang.InterruptedException,java.rmi.RemoteException
     * @return Entry
     */
    public Entry read(Entry aEntry, long along) throws UnusableEntryException, TransactionException, InterruptedException, RemoteException {
    	
    	if (useCrypto) {
            try {
                
                MarshalledObject o = new MarshalledObject(aEntry);
                //We don't store the Object in the Encrypted Form when we read
                Object encryptedEntry = EncryptionUtils.findEncryptedVersion(aEntry, null);
                if (encryptedEntry instanceof Entry && encryptedEntry instanceof CryptoHolder) {
                    Entry ent = space.read((Entry)  encryptedEntry, aTransaction,  along);
                    if(ent instanceof CryptoHolder){
                        byte[] barr = ((CryptoHolder) ent).getEncryptedObject().getEncryptedBytes();
                        Object unenc = EncryptionUtils.decryptObjectWithDefaultKey(barr);
                        if(unenc instanceof MarshalledObject){
                            Object unmarshalled = ((MarshalledObject)unenc).get();
                           
                            if(unmarshalled instanceof Entry){
                                return (Entry) unmarshalled;
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(JavaSpaceOpsAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        return space.read(aEntry, aTransaction, along);
    }

    public Entry snapshot(Entry aEntry) throws RemoteException {
        return space.snapshot(aEntry);
    }

    public void useCrypto(boolean cryptoOn) {
        try {
            this.useCrypto = true;
          
        } catch (Exception ex) {
            Logger.getLogger(JavaSpaceOpsAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String args) {

    }
}
