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

/*
 * neon : org.jini.projects.neon.callbacks
 * CallbackFactory.java
 * Created on 01-Sep-2003
 */
package org.jini.projects.neon.callbacks;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.rmi.MarshalledObject;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.logging.Logger;

import net.jini.core.event.EventRegistration;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.event.EventMailbox;
import net.jini.event.MailboxRegistration;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.http.HttpServerEndpoint;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lease.LeaseRenewalService;
import net.jini.lease.LeaseRenewalSet;

import org.jini.projects.neon.callbacks.messages.server.ProducerIntf;

import com.sun.jini.constants.TimeConstants;

/**
 * A callback containing an event mailboxed, externally leased remote event listener
 * @author calum
 */
public class DisconnectedCallback implements Externalizable {

    private static final long serialVersionUID = 157676326862L;

    transient EventMailbox mb = null;
    transient MailboxRegistration mbr = null;

    RemoteEventListener mercListener = null;
    transient LeaseRenewalService lrs = null;
    transient Logger l = Logger.getLogger("MercuryClient");

    boolean newRegistration = false;

    private transient LeaseRenewalManager lrm;

    RemoteEventListener callback;
    private transient LeaseRenewalSet leaseSet;
    private transient ObjectOutputStream oos;
    private transient EventRegistration evReg;
    //private transient File f;
    private transient Exporter exp;
    /**
     * 
     */
    public DisconnectedCallback() {
        super();
        // TODO Complete constructor stub for CallbackFactory
    }

    public RemoteEventListener getRegistrableListener() {
        return mercListener;
    }

    public boolean isNewlyRegistered() {
        return newRegistration;
    }

    
    public RemoteEventListener create(RemoteEventListener callback, EventMailbox mb, LeaseRenewalService lrs) {
        evReg = null;
        this.callback = callback;
        exp = new BasicJeriExporter(HttpServerEndpoint.getInstance(0), new BasicILFactory());       
            loadRegistration();       
        return null;
    }
    /**
     * Delegates a callback function to an EventMailbox and LeaseRenewalService. The two service must be
     * available on the ServiceRegistrar that is passed to this method. 
     * @param callback
     * @param reg
     * @return
     */
    public RemoteEventListener create(RemoteEventListener callback, ServiceRegistrar reg) {
        lrm = new LeaseRenewalManager();
        try {
            Class[] merc = new Class[] { EventMailbox.class };
            Class[] norm = new Class[] { LeaseRenewalService.class };
            Class[] producer = new Class[] { ProducerIntf.class };

            ServiceTemplate merctmp = new ServiceTemplate(null, merc, null);
            ServiceTemplate normtmp = new ServiceTemplate(null, norm, null);
            mb = (EventMailbox) reg.lookup(merctmp);
            if (mb != null) {
                l.info("EventMailbox found");
            }
            lrs = (LeaseRenewalService) reg.lookup(normtmp);
            if (lrs != null) {
                l.info("LeaseRenewalService found");
            }
        } catch (Exception ex) {

        }

        evReg = null;
        this.callback = callback;
        exp = new BasicJeriExporter(HttpServerEndpoint.getInstance(0), new BasicILFactory());
        negotiateRegistrations(mb, lrs);
        return mercListener;
    }
    /**
     * Checks whether we this is a new registration, and if so, registers with EventMailbox
     * stores a reference to the MailboxListener, and creates LeaseRenewalSet
     * @param mb
     * @param lrs
     */

    void negotiateRegistrations(EventMailbox mb, LeaseRenewalService lrs) {
        try {
            if (!newRegistration) {

                newRegistration = true;
                //mb = (EventMailbox) regs[0].lookup(merctmp);
                if (mb != null) {
                    l.info("EventMailbox found");
                    mbr = mb.register(Lease.FOREVER);
                    mercListener = mbr.getListener();
                }
                leaseSet = lrs.createLeaseRenewalSet(15 * TimeConstants.MINUTES);
                l.info("RemoteEvent Registered and lease registered with norm");
            } else
                l.info("Reconnected existing file");
        } catch (RemoteException e4) {
            // TODO Handle RemoteException
            e4.printStackTrace();
        } catch (LeaseDeniedException e4) {
            // TODO Handle LeaseDeniedException
            e4.printStackTrace();
        }
    }

    //TODO : Check Expiration on lease and re-register if required,
    void loadRegistration() {
        System.out.println("Serialised registration exists....re-using");
        try {
            System.out.println("Enabling delivery");
            mbr.enableDelivery((RemoteEventListener) exp.export(this.callback));
            //mbr = (MailboxRegistration) mis.get(true);
        } catch (IOException e2) {
            // TODO Handle IOException
            e2.printStackTrace();
        }
    }

    public void setDeliveryListener(RemoteEventListener callback) throws NoSuchObjectException, RemoteException {
        this.callback = callback;
        try {
            System.out.println("Reconnecting to " + callback.toString());
            mbr.enableDelivery(this.callback);
        } catch (ExportException e) {
            // TODO Handle ExportException
            e.printStackTrace();
        } catch (RemoteException e) {
            // TODO Handle RemoteException
          
            e.printStackTrace();
            if (e instanceof NoSuchObjectException)
                throw e;
        }
    }

    public void addEventRegistration(EventRegistration reg) {
        if (newRegistration) {
            evReg = reg;
            try {
                mbr.enableDelivery((RemoteEventListener) exp.export(callback));
                if (evReg != null)
                    leaseSet.renewFor(evReg.getLease(), Lease.FOREVER);
                leaseSet.renewFor(mbr.getLease(), Lease.FOREVER);
                newRegistration = false;
            } catch (RemoteException e) {
                // TODO Handle RemoteException
                System.out.println("AddEventReg: EnableDelivery Error");
                e.printStackTrace();
            }
        }
    }
    /* (non-Javadoc)
     * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        // TODO Complete method stub for readExternal
        MarshalledObject mis;
        mis = (MarshalledObject) in.readObject();
        mbr = (MailboxRegistration) mis.get();
        mercListener = mbr.getListener();
        leaseSet = (LeaseRenewalSet) ((MarshalledObject) in.readObject()).get();
        evReg = (EventRegistration) in.readObject();
    }

    /* (non-Javadoc)
     * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        // TODO Complete method stub for writeExternal
        out.writeObject(new MarshalledObject(mbr));
        out.writeObject(new MarshalledObject(leaseSet));
        out.writeObject(evReg);
        out.flush();
    }

}
