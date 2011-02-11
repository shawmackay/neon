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
 * AgentContextAdapter.java
 * Created on 11-Nov-2003
 */

package org.jini.projects.neon.host;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Logger;

import net.jini.core.entry.Entry;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lookup.ServiceID;
import net.jini.id.Uuid;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentConstraints;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.Meta;
import org.jini.projects.neon.agents.sensors.SensorFilter;
import org.jini.projects.neon.agents.util.meta.AgentDependencies;
import org.jini.projects.neon.collaboration.Response;
import org.jini.projects.neon.collaboration.SimpleResponse;
import org.jini.projects.neon.host.transactions.TransactionBlackBox;
import org.jini.projects.neon.host.transactions.TransactionalResource;
import org.jini.projects.zenith.bus.Bus;
import org.jini.projects.zenith.bus.BusManager;
import org.jini.projects.zenith.messaging.channels.MessageChannel;
import org.jini.projects.zenith.messaging.channels.ReceiverChannel;
import org.jini.projects.zenith.messaging.channels.connectors.PublishingQConnector;
import org.jini.projects.zenith.messaging.messages.EventMessage;
import org.jini.projects.zenith.messaging.messages.InvocationMessage;
import org.jini.projects.zenith.messaging.messages.Message;
import org.jini.projects.zenith.messaging.messages.MessageHeader;
import org.jini.projects.zenith.messaging.system.ChannelException;
import org.jini.projects.zenith.messaging.system.MessagingManager;

/**
 * Wrappers the context adding in the agent as a reference to some calls.
 * Required for features such as transaction propagation,etc. This also
 * maintains the message correlation functions Provides a facade between
 * AsyncAgentEnvironment and ManagedDomain Separates AgentDomainImpl (where
 * messages are inherently Asynchronous), from the client which thinks the
 * Context handles messages synchronously.
 * 
 * @author calum
 */
public class AgentContextAdapter implements PrivilegedAgentContext, ManagedDomain {
    private Agent agentProducer;

    private Meta agentMeta;

    private AgentDependencies depends;

    ManagedDomain dom;

    private Logger l = Logger.getLogger("org.jini.projects.neon.host");

    PrivilegedAsyncAgentEnvironment link;

    // URGENT Change to use other messaging managers!!!
    private MessagingManager mgr;

    /**
     * 
     */
    public AgentContextAdapter(Agent linkTo, PrivilegedAsyncAgentEnvironment actualContext, ManagedDomain domain) {
        this.agentProducer = linkTo;
        this.agentMeta = linkTo.getMetaAttributes();
        List l = agentMeta.getMetaOfType(AgentDependencies.class);
        if (l.size() == 0) {
            depends = new AgentDependencies();
            agentMeta.addAttribute(depends);
        } else {
            depends = (AgentDependencies) l.get(0);
        }
        this.link = actualContext;
        this.dom = domain;
        mgr = MessagingManager.getManager();
        // URGENT Complete constructor stub for AgentContextAdapter
    }

    /**
     * @param transmitter
     * @param receiver
     * @param name
     */
    public void addMessageLock(Uuid transmitter, Uuid receiver, String name) {
        dom.addMessageLock(transmitter, receiver, name);
    }

    /**
     * @param cag
     */
    public void advertise(Agent cag) {
        dom.advertise(cag);
    }

    /**
     * @param constraints
     * @return
     */
    public boolean canAccept(AgentConstraints constraints) {
        return dom.canAccept(constraints);
    }

    /**
     * @param transmitter
     */
    public void clearMessageLock(Uuid transmitter) {
        dom.clearMessageLock(transmitter);
    }

    /**
     * @param a
     */
    public void deployAgent(Agent a) {
        link.deployAgent(a);
    }

    /**
     * @param a
     * @param c
     */
    public void deployAgent(Agent a, RemoteEventListener c) {
        link.deployAgent(a, c);
    }

    /**
     * @param ag
     * @return
     */
    public boolean removeAgent(Agent ag) {
        return link.removeAgent(ag);
    }

    public void deployObject(Object o, String configurationLocation, String constraintsLocation) throws MalformedURLException{
            link.deployObject(o,configurationLocation, constraintsLocation);
    }
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        return link.equals(obj);
    }

    /*
     * @see org.jini.projects.neon.host.AgentContext#getAgent(java.lang.String)
     */
    public Object getAgent(String name) throws NoSuchAgentException {
        // TODO Complete method stub for getAgent
        addDependency(name);
        return link.getAgent(name, this.agentProducer);
    }

    /*
     * @see org.jini.projects.neon.host.PrivilegedAgentContext#getAgent(java.lang.String,
     *      org.jini.projects.neon.agents.Agent)
     */
    public Object getAgent(String name, Agent controller) throws NoSuchAgentException {
        // TODO Complete method stub for getAgent
        addDependency(name);
        return link.getAgent(name, this.agentProducer);
    }

    public Object getAgent(String name, Entry[] matchTo) throws NoSuchAgentException {
        // TODO Complete method stub for getAgent
        addDependency(name);
        return link.getAgent(name, matchTo, this.agentProducer);
    }

    public Object getAgent(String name, Entry[] matches, int timeout) throws NoSuchAgentException {
        // TODO Complete method stub for getAgent
        addDependency(name);
        return link.getAgent(name, matches, timeout, this.agentProducer);
    }

    public Object getAgent(String name, int timeout) throws NoSuchAgentException {
        // TODO Complete method stub for getAgent
        addDependency(name);
        return link.getAgent(name, timeout, this.agentProducer);
    }

    public Object attachAgent(String name, Entry[] metaMatches, int timeout) throws NoSuchAgentException {
        // TODO Complete method stub for attachAgent
        addDependency(name);
        return link.attachAgent(name, metaMatches, timeout, agentProducer);
    }

    public Object attachAgent(String name, Entry[] metaMatches) throws NoSuchAgentException {
        // TODO Complete method stub for attachAgent
        addDependency(name);
        return link.attachAgent(name, metaMatches, agentProducer);
    }

    public Object attachAgent(String name, int timeout) throws NoSuchAgentException {
        // TODO Complete method stub for attachAgent
        addDependency(name);
        return link.attachAgent(name, timeout, agentProducer);
    }

    public Object attachAgent(String name) throws NoSuchAgentException {
        // TODO Complete method stub for attachAgent
        addDependency(name);

        return link.attachAgent(name, agentProducer);
    }

    /**
     * @return
     */
    public ManagedDomain getAgentHost() {
        return link.getAgentHost();
    }

    /**
     * @return
     */
    public ServiceID getAgentServiceID() {
        return link.getAgentServiceID();
    }

    public Object getAsynchronousAgent(String name) throws NoSuchAgentException {
        return link.getAsyncAgent(name, this.agentProducer);
    }

    public Object getAsynchronousAgent(String name, AsyncHandler contextHandler) throws NoSuchAgentException {
        return link.getAsyncAgent(name, contextHandler, this.agentProducer);
    }

    /**
     * @return
     */
    public Bus getBus() {
        return dom.getBus();
    }

    /**
     * @return
     */
    public BusManager getBusManager() {
        return dom.getBusManager();
    }

    /**
     * @param ID
     * @return
     * @throws NoSuchAgentException
     * @throws SecurityException
     */
    public RemoteEventListener getCallback(AgentIdentity ID) throws NoSuchAgentException, SecurityException {
        return link.getCallback(ID);
    }

    /**
     * @param agentName
     * @return
     * @throws NoSuchAgentException
     * @throws SecurityException
     */
    public RemoteEventListener[] getCallbacks(String agentName) throws NoSuchAgentException, SecurityException {
        return link.getCallbacks(agentName);
    }

    /**
     * @param agentName
     * @return
     */
    public Logger getContextLogger(String agentName) {
        return link.getContextLogger(agentName);
    }

    /**
     * @return
     */
    public InetAddress getCurrentHost() {
        return link.getCurrentHost();
    }

    /**
     * @return
     */
    public String getDomainName() {
        return link.getDomainName();
    }

    /**
     * @return
     */
    public Uuid getHostServiceID() {
        return link.getHostServiceID();
    }

    /**
     * @return
     */
    public AgentRegistry getRegistry() {
        return dom.getRegistry();
    }

    /*
     * @see org.jini.projects.neon.host.AgentDomain#getTemporaryChannel()
     */
    public ReceiverChannel getTemporaryChannel() {

        return dom.getTemporaryChannel();
    }

    /**
     * @return
     */
    // public TransactionBlackBox getTxnBlackBox() {
    // return dom.getTxnBlackBox();
    // }
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return link.hashCode();
    }

    /**
     * 
     */
    public void initialise() {
        dom.initialise();
    }

    /**
     * @param ag
     * @param name
     * @param l
     * @return
     */
    public boolean registerSensor(Agent ag, String name, SensorFilter l) {
        return link.registerSensor(ag, name, l);
    }

    /*
     * @see org.jini.projects.neon.host.AgentDomain#returnTemporaryChannel(org.jini.projects.zenith.messaging.channels.MessageChannel)
     */
    public void returnTemporaryChannel(MessageChannel channel) {
        dom.returnTemporaryChannel(channel);
    }

    /**
     * @param agentName
     * @param message
     * @throws NoSuchAgentException
     */
    public void sendAsyncMessage(String agentName, Message message) throws NoSuchAgentException {
        dom.sendAsyncMessage(agentName, message);
    }

    /**
     * @param agentID
     * @param mesg
     * @throws NoSuchAgentException
     */
    public void sendAsyncMessage(Uuid agentID, Message mesg) throws NoSuchAgentException {
        dom.sendAsyncMessage(agentID, mesg);
    }

    /**
     * 
     * Translates an old style message into using an InvocationMessage and using
     * channels, instead of the synchronous bus
     * 
     * @param agentName
     * @param message
     * @return
     * @throws NoSuchAgentException
     */
    public Response sendMessage(String agentName, Message message) throws NoSuchAgentException {
        PublishingQConnector sending = null;
        if (agentName.indexOf(".") == -1) {
            agentName = this.agentProducer.getNamespace() + "." + agentName;
        }
        try {
            sending = mgr.getPublishingConnector(agentName);
        } catch (ChannelException e) {
            // URGENT Handle ChannelException
            e.printStackTrace();
        }
        if (sending != null) {
            MessageHeader header = new MessageHeader();
            if (agentProducer instanceof TransactionalResource) {
                l.finest("trying to Propagate Transaction");
                TransactionalResource res = (TransactionalResource) agentProducer;
                if (res.getTransaction() != null) {
                    String txnIdentifier = res.getTransaction();
                    l.finest("transaction Propagation Confirmed = " + txnIdentifier);
                    header.setArbitraryField("txnID", txnIdentifier);
                }
            }
            header.setReplyAddress(agentProducer.getIdentity().getID().toString());
            // InvocationMessage m = (InvocationMessage) message;
            sending.sendMessage(message);
            if (!(message instanceof EventMessage)) {
                synchronized (this) {
                    try {
                        System.out.println("Starting wait for response......");
                        wait(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                SimpleResponse resp = new SimpleResponse();
                resp.setResponseObject(this.agentProducer.getReplyObject());
                return resp;
            } else {
                System.out.println("Message is an event message: not waiting response");
            }

        }
        throw new NoSuchAgentException("Agent could not be found");
    }

    /**
     * @param agentName
     * @param messagename
     * @param parameters
     * @return
     * @throws NoSuchAgentException
     */
    public Response sendMessage(String agentName, String messagename, Object[] parameters) throws NoSuchAgentException {
        if (agentName.indexOf(".") == -1) {
            agentName = this.agentProducer.getNamespace() + "." + agentName;
        }
        MessageHeader header = new MessageHeader();
        if (agentProducer instanceof TransactionalResource) {
            l.finest("trying to Propagate Transaction 2");
            TransactionalResource res = (TransactionalResource) agentProducer;
            if (res.getTransaction() != null) {
                String txnIdentifier = res.getTransaction();
                l.finest("transaction Propagation Confirmed = " + txnIdentifier);
                header.setArbitraryField("txnID", txnIdentifier);
            }
        }
        header.setReplyAddress(agentProducer.getIdentity().getID().toString());
        InvocationMessage mesg = new InvocationMessage(header, messagename, parameters, null);
        link.sendAsyncMessage(agentName, mesg);
        synchronized (this) {
            try {
                wait(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        SimpleResponse resp = new SimpleResponse();
        resp.setResponseObject(this.agentProducer.getReplyObject());
        return resp;
    }

    /**
     * @param agent
     * @param mesg
     * @return
     * @throws NoSuchAgentException
     */
    public Response sendMessage(Uuid agent, Message mesg) throws NoSuchAgentException {
        link.sendAsyncMessage(agent, mesg);
        if (!(mesg instanceof EventMessage)) {
            while (agentProducer.getReplyObject() == null) {
                synchronized (this) {
                    try {
                        wait(50);
                    } catch (InterruptedException e) {
                        // URGENT Handle InterruptedException
                        e.printStackTrace();
                    }
                }
            }
            Response r = new SimpleResponse();
            r.setResponseObject(agentProducer.getReplyObject());
            return r;
        } else
            return null;
    }

    /**
     * @param bus
     */
    public void setBus(Bus bus) {
        dom.setBus(bus);
    }

    /**
     * @param manager
     */
    public void setBusManager(BusManager manager) {
        dom.setBusManager(manager);
    }

    /**
     * @param registry
     */
    public void setRegistry(AgentRegistry registry) {
        dom.setRegistry(registry);
    }

    /**
     * @param tbb
     */
    public void setTxnBlackBox(TransactionBlackBox tbb) {
        dom.setTxnBlackBox(tbb);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return link.toString();
    }

    /**
     * @param ag
     * @throws NoSuchAgentException
     * @throws SecurityException
     */
    public void unsubscribeAgent(Agent ag) throws NoSuchAgentException, SecurityException {
        link.unsubscribeAgent(ag);
    }

    public boolean detachAgent(Object reference) {
        // TODO Complete method stub for detachAgent

        return link.detachAgent(reference);
    }

    public TransactionBlackBox getTxnBlackBox() {
        // TODO Complete method stub for getTxnBlackBox
        return dom.getTxnBlackBox();
    }
    
    private void addDependency(String name){
        if(!depends.hasCurrentDependency(name))
            depends.addDependency(name);
    }

public void shutdownDomain() {
        dom.shutdownDomain();
}

    public Object getAgent(AgentIdentity id) throws NoSuchAgentException {
        return link.getAgent(id, this.agentProducer);
    }

	public SecurityOptions getSecurityOptions() throws SecurityException {
		// TODO Auto-generated method stub
		return dom.getSecurityOptions();
	}
    
}
