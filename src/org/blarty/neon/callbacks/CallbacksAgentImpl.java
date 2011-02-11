/*
 `* Copyright 2005 neon.jini.org project 
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

package org.jini.projects.neon.callbacks;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;

import org.jini.glyph.Exportable;
import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.LocalAgent;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.neon.host.PrivilegedAgentContext;

/**
 * Local agent that handles distribution of messages from agents to any callback
 * that they may have attached NOTE: Agents must carry any and all callbacks
 * with them, if they migrate from one host to another. This enables an agent
 * which cannot communicate with it's originator, the ability to move into
 * broadcast range
 * 
 * @agents
 */
@Exportable
public class CallbacksAgentImpl extends AbstractAgent implements CallbacksAgent,LocalAgent {
    long broadcastseqNum = 0;

    long directseqNum = 0;

    // String name="Callback";
    public CallbacksAgentImpl() {
        this.name = "Callback";
        this.namespace = "neon";
    }

    public boolean init() {
        return true;
    }

    

    /**
     * Requests the sending of a callback from a host to any attached listener.
     * This is essentially an agent Broadcast - All agents of this type will
     * receive this message.<br>
     * <br> * NOT IMPLEMENTED *
     * 
     * @param name
     *                   the Agent name
     * @param message
     * @return
     */
    public boolean broadcast(String name, Object message) throws RemoteException {

        return true;
    }

    /**
     * Requests the sending of a callback from a host to any attached listener.
     * This is essentially an agent Broadcast - All agents of this type will
     * receive this message.
     * 
     * @param ID
     *                   the distinct Agent Identity
     * @param message
     *                   the message to sen
     * @return whether the message was successfully sent
     */
    public boolean sendCallback(AgentIdentity ID, Object message) throws RemoteException {
        System.out.println("Sending a callback now");
        // Interrogate the Agent Registry.
        // NEED A PrivilegedAgent class
        PrivilegedAgentContext context = (PrivilegedAgentContext) this.context;
        try {
            RemoteEventListener acb = context.getCallback(ID);
            if (acb != null) {
                System.out.println("ID: " + ID.getID());
                RemoteEvent ev = new RemoteEvent(message, 1L, directseqNum++, null);
                acb.notify(ev);
            } else
                Logger.getAnonymousLogger().log(Level.INFO, "Callback requested but no callback registered - ignoring request");
        } catch (NoSuchAgentException e) {

            System.out.println("Err: " + e.getMessage());
            e.printStackTrace();
        } catch (SecurityException e) {

            System.out.println("Err: " + e.getMessage());
            e.printStackTrace();
        } catch (UnknownEventException e) {

            System.out.println("Err: " + e.getMessage());
            e.printStackTrace();
        } catch (RemoteException e) {

            System.out.println("Err: " + e.getMessage());
            e.printStackTrace();
        }
        return true;
    }

    public RemoteEventListener getMyCallback(Agent ag) throws RemoteException {
        PrivilegedAgentContext context = (PrivilegedAgentContext) this.context;
        try {
            return context.getCallback(ag.getIdentity());
        } catch (NoSuchAgentException e) {

            System.out.println("Err: " + e.getMessage());
            e.printStackTrace();
        } catch (SecurityException e) {

            System.out.println("Err: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
