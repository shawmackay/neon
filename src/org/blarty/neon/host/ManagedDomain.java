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
 * neon : org.jini.projects.neon.host
 * ManagedDomain.java
 * Created on 26-Sep-2003
 */
package org.jini.projects.neon.host;

import net.jini.id.Uuid;

import org.jini.projects.neon.host.transactions.TransactionBlackBox;
import org.jini.projects.zenith.bus.Bus;
import org.jini.projects.zenith.bus.BusManager;


/**
 * Interface showing more of the setup and configuration methods needed for
 * domains
 * @author calum
 */
public interface ManagedDomain extends AgentDomain {
    /**
     * Get the current agent Registry
     * @return the domains Agent Registry
     */
    public abstract AgentRegistry getRegistry();
    /**
     * Set the registry
     * @param registry the new registry instance
     */
    public abstract void setRegistry(AgentRegistry registry);
    /**
     * Get the internal message bus that the domain is using
     * @return
     */
    public abstract Bus getBus();
    /**
     * Get the BusManager that wrappers the internal message bus
     * @return
     */
    public abstract BusManager getBusManager();
    /**
     * Change the internal message bus
     * @param bus
     *
     */
    public abstract void setBus(Bus bus);
    /**
     * Change the bus manager
     * @param manager
     */
    public abstract void setBusManager(BusManager manager);
    
    /**
     * Get the name of the domain
     */
    public String getDomainName();
    /**
     * Obtain a reference to the transaction management classes
     * @return
     */
    // public TransactionBlackBox getTxnBlackBox();
    
    /**
     * Change the domains manager for transactions
     * @param tbb
     */
    public void setTxnBlackBox(TransactionBlackBox tbb);
    
    /**
     * Get the domains manager for transactions
     *
     */
    public TransactionBlackBox getTxnBlackBox();
    
    void initialise();
    
    /**
     * Create a message lock for an receiving agent under a calling (transmitter) agent
     * @param transmitter
     * @param receiver
     * @param name
     */
    public void addMessageLock(Uuid transmitter, Uuid receiver, String name );
    
    /**
     * Clear all message locks for the supplied identifier and subordinate message locks
     * @param transmitter
     */
    public void clearMessageLock(Uuid transmitter);
    
    /**
     *Instructs the Domain to  shutdown processes and persist Agents to the space
     */
    public void shutdownDomain();
    
    /**
     * Obtains the current security level for the domain
     * @return the Security options defined in this domain
     * @throws SecurityException
     */
    public SecurityOptions getSecurityOptions() throws SecurityException;
}