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
 * CallbackHandler.java
 * Created on 01-Sep-2003
 */
package org.jini.projects.neon.callbacks;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.jini.core.event.RemoteEventListener;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.export.Exporter;
import net.jini.id.UuidFactory;

//import org.jini.projects.neon.callbacks.constrainable.CallbackProxy;

/**
 *Handles callback registrations for Mercury/Norm based
 * event registrations
 * @author calum
 */
public class CallbackFactory {
    static HashMap callbacks;
    static ServiceRegistrar theRegistrar;
    static {
        load();
    }
    /**
     *  Loads a list of stored event registrations from disk.
     */
    public static synchronized void load() {
        System.out.println("Loading list of stored event registrations");
        File f = new File("mercuryregs.ser");
        if (f.exists()) {

            try {
                ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));
                callbacks = (HashMap) ois.readObject();
                ois.close();
            } catch (FileNotFoundException e) {
                // TODO Handle FileNotFoundException
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Handle IOException
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Handle ClassNotFoundException
                e.printStackTrace();
            }

        } else
            callbacks = new HashMap();
    }

    /**
     * Change the registrar that will be used to find the <i>EventMailbox</i> and <i>LeaseRenewal</i> Services
     * @param reg the new Registrar
     */
    public static void setRegistrar(ServiceRegistrar reg) {
        theRegistrar = reg;
    }

    /**
     * Stores the current set of event registrations to disk
     *
     */
    public static synchronized void store() {
        System.out.println("Storing list of stored event registrations");
        File f = new File("mercuryregs.ser");

        try {
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
            oos.writeObject(callbacks);
            oos.flush();
            oos.close();
        } catch (FileNotFoundException e) {
            // TODO Handle FileNotFoundException
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Handle IOException
            e.printStackTrace();
        }

    }

    /**
     * Connects a set of named callbacks to their required handlers, recreating the handler if required 
     * @param callbackNames
     * @param listeners
     */
    public static void connect(String[] callbackNames, RemoteEventListener[] listeners) {
        Set callbackSet = callbacks.entrySet();
        Iterator iter = callbackSet.iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String name = (String) entry.getKey();
            for (int i = 0; i < callbackNames.length; i++) {
                if (callbackNames[i].equals(name)) {
                    DisconnectedCallback storedCallback = (DisconnectedCallback) entry.getValue();
                    try {
                        storedCallback.setDeliveryListener(listeners[i]);
                    } catch (NoSuchObjectException nsoex) {
                        DisconnectedCallback mercuryListener = new DisconnectedCallback();
                        RemoteEventListener ls = mercuryListener.create(listeners[i], theRegistrar);
                        callbacks.put(name, mercuryListener);
                        store();
                    } catch(RemoteException e){                    
                        e.printStackTrace();
                    }
                }
            }
        }
        store();
    }

    private static DisconnectedCallback create(String name, RemoteEventListener callback) {

        if (!callbacks.containsKey(name)) {
            DisconnectedCallback mercuryListener = new DisconnectedCallback();
            RemoteEventListener ls = mercuryListener.create(callback, theRegistrar);
            callbacks.put(name, mercuryListener);
            store();
            return mercuryListener;
        } else
            return (DisconnectedCallback) callbacks.get(name);

    }
    /**
     * Creates a disconnected callback for a standard non-leased Remote Event Listener, and exports it. 
     * Also stores the name and original callback in it's disk based store for re-creation on startup. 
     * @param referredName the name under which the callback the callback will be stored
     * @param regs  a set of registrars, like those retuned from <i>discovered()</i> 
     * @param exporter an exporter that will be used to export the original callback
     * @param theCallback a normal RemoteEventListener instance
     * @return a mailboxed, externally leased representation of the original callback, for passing to other services.
     * 
     * @throws ExportException if the original callback cannot be exported
     */
    public static DisconnectedCallback buildCallback(String referredName, ServiceRegistrar[] regs, Exporter exporter, RemoteEventListener theCallback) throws ExportException {
        RemoteEventListener proxy = (RemoteEventListener) exporter.export(theCallback);
        //Exported Proxy
       // CallbackProxy cproxy = CallbackProxy.create(proxy, UuidFactory.generate());
        //Create a named DisconnectableCallback for this proxy on this registrar
        CallbackFactory.setRegistrar(regs[0]);
        //DisconnectedCallback cf = CallbackFactory.create(referredName, cproxy);
       // cf.addEventRegistration(null);
        //CallbackFactory.connect(new String[] { referredName }, new RemoteEventListener[] { proxy });
        return null;
    }
    /**
     * 
     * @param referredName  the name under which the callback the callback will be stored
     * @param exporter an exporter that will be used to export the original callback
     * @param theCallback a normal RemoteEventListener instance
     * @return a mailboxed, externally leased representation of the original callback, for passing to other services.
     * @throws ExportException if the original callback cannot be exported
     */
    public static DisconnectedCallback buildCallback(String referredName, Exporter exporter, RemoteEventListener theCallback) throws ExportException {
            RemoteEventListener proxy = (RemoteEventListener) exporter.export(theCallback);
            //Exported Proxy
            //CallbackProxy cproxy = CallbackProxy.create(proxy, UuidFactory.generate());
            //Create a named DisconnectableCallback for this proxy on this registrar
            
            //DisconnectedCallback cf = CallbackFactory.create(referredName, cproxy);
           // cf.addEventRegistration(null);
            //CallbackFactory.connect(new String[] { referredName }, new RemoteEventListener[] { proxy });
            return null;
        }

    //	public static MercuryListener create(String name, RemoteEventListener callback, ServiceRegistrar regs) {
    //		if (!callbacks.containsKey(name)) {
    //			MercuryListener mercuryListener = new MercuryListener();
    //			mercuryListener.create(callback, regs);
    //			callbacks.put(name, mercuryListener);
    //			store();
    //			return mercuryListener;
    //		} else        
    //			return (MercuryListener) callbacks.get(name);
    //	}

}
