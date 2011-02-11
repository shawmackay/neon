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

package org.jini.projects.neon.neontests.callbacks.client.constrainable;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import net.jini.core.constraint.MethodConstraints;
import net.jini.core.constraint.RemoteMethodControl;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.id.ReferentUuids;
import net.jini.id.Uuid;

import org.jini.projects.neon.service.AgentCallback;

/**
 * Smart Proxy for the callback
 */
public class CallbackProxy implements AgentCallback,Serializable{
     private static final long serialVersionUID = 4L;

    final RemoteEventListener listener;
    final Uuid proxyID;



    public static CallbackProxy create(RemoteEventListener listener, Uuid id) {
        if (listener instanceof RemoteMethodControl) {
            Logger.getAnonymousLogger().info("Registering a secure proxy");
            return new CallbackProxy.ConstrainableProxy(listener, id, null);
        }
        else
            return new CallbackProxy(listener, id);
    }




    public CallbackProxy(RemoteEventListener listener, Uuid proxyID) {
        this.listener = listener;
        this.proxyID = proxyID;
    }

    public Uuid getReferentUuid() {
        return proxyID;
    }

     /** Proxies for servers with the same proxyID have the same hash code. */
    public int hashCode() {
        return proxyID.hashCode();
    }

    /**
     * Proxies for servers with the same <code>proxyID</code> are
     * considered equal.
     */
    public boolean equals(Object o) {
        return ReferentUuids.compare(this, o);
    }

    public void notify(RemoteEvent theEvent)
            throws UnknownEventException, RemoteException {
        listener.notify(theEvent);
    }


    final static class ConstrainableProxy extends CallbackProxy implements RemoteMethodControl{

        private ConstrainableProxy(RemoteEventListener server, Uuid id, MethodConstraints methodConstraints) {
                    super(constrainServer(server,methodConstraints),id);

                }
         public RemoteMethodControl setConstraints(MethodConstraints constraints)
        {
            return new CallbackProxy.ConstrainableProxy(listener, proxyID,
                constraints);
        }

        /** {@inheritDoc} */
        public MethodConstraints getConstraints() {
            return ((RemoteMethodControl) listener).getConstraints();
        }


         private static RemoteEventListener constrainServer(RemoteEventListener server, MethodConstraints methodConstraints)
        {
            return (RemoteEventListener)
                ((RemoteMethodControl)server).setConstraints(methodConstraints);
        }




    }
}
