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

package org.jini.projects.neon.agents;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import net.jini.config.Configuration;
import net.jini.id.Uuid;

import org.jini.projects.neon.collaboration.CollabAdvert;
import org.jini.projects.neon.host.AgentContext;
import org.jini.projects.zenith.messaging.messages.Message;
import org.jini.projects.zenith.messaging.messages.MessageHeader;
import org.jini.projects.zenith.messaging.messages.ObjectMessage;

/**
 * @author calum
 * 
 */
/**
 * Utility base class providing default implementations for application agents.
 * <br>
 * Provides default methods for: <br>
 * <ol>
 * <li>Agent Identity creation and querying</li>
 * <li>Agent name querying</li>
 * <li>Context management</li>
 * <li>Advertising for collaborative mechanisms</li>
 * <li>Constraint management</li>
 * </ol>
 * 
 */
public abstract class AbstractAgent implements Agent, RemoteAgentState {
   

	public final static long serialVersionUID = -2060069303999183380L;

    protected transient AgentContext context;

    private Meta metaInformation;

    protected String name;

    protected AgentIdentity ID;

    protected AgentConstraints constraints;

    protected AgentState state;

    protected AgentState secondaryState;

    protected Uuid currentMessagingRequest;

    protected Object messagingReturnObject;

    protected String configurationLocation;

    protected static int LIFECYCLE_HEALTHY = 0;

    protected static int LIFECYCLE_HALT = 1;

    protected static int LIFECYCLE_HALTED = 2;

    protected static int LIFECYCLE_COMPLETED = 3;

    protected static int LIFECYCLE_STOPPED = 4;

    private int completionStatus = LIFECYCLE_HEALTHY;

    private int stopStatus = LIFECYCLE_HEALTHY;

    protected String namespace;

    protected Message currentMessage;

    protected transient CollabAdvert advert;

    protected boolean internalConfig = false;
    
    protected String internalConfigLocation;
    
    public AbstractAgent() {
    //    if (ID == null)
        //    ID = new AgentIdentity();
    }

    public void setIdentity(AgentIdentity id) {
        // TODO Auto-generated method stub
        if (ID == null)
            ID = id;
        else if (!ID.equals(id))
            throw new IllegalArgumentException("Identity has already been set");
    }

    public AgentIdentity getIdentity() {
        if (ID == null)
            ID = new AgentIdentity();
        return ID;
    }

    public String getName() {
        if (name == null) {
            String cname = this.getClass().getName();
            String basename = cname.substring(cname.lastIndexOf('.') + 1);
            String[] derivedname = basename.split("Agent");
            // System.out.println("DerivedName: " + derivedname[0]);
            name = derivedname[0];
        }
        return name;
    }

    public abstract boolean init();
 

    public void complete() {
        try {
            context.removeAgent(this);
            setCompleted();
        } catch (Exception e) {
            // TODO Handle RuntimeException
            e.printStackTrace();
        }
    }

    public void stop() {

        setStopped();
    }

    public AgentContext getAgentContext() {
        return context;
    }

    public void setAgentContext(AgentContext hostContext) {
        this.context = hostContext;
    }

    public AgentState getAgentState() {
        return state;
    }

    /**
     * Does basic method name matching advertising.
     * 
     * @return
     * @see Agent#advertise
     */
    public CollabAdvert advertise() {
        if (this.advert == null)
            advert = new CollabAdvert(this);
        return advert;
    }

    public void unadvertise() {
        context.getContextLogger(name).finest("Unadvertising");
    }

    public Message getCurrentMessage() {
        return this.currentMessage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jini.projects.neon.agents.Agent#getConstraints()
     */
    public AgentConstraints getConstraints() {

        return this.constraints;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jini.projects.neon.agents.Agent#setConstraints(org.jini.projects.neon.agents.AgentConstraints)
     */
    public void setConstraints(AgentConstraints newConstraints) {

        this.constraints = newConstraints;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jini.projects.neon.collaboration.Advertiser#getSubscriberIdentity()
     */
    public Uuid getSubscriberIdentity() {
        return getIdentity().getID();
    }

    /**
     * Sets the state of an agent to a new state. May also update the secondary
     * state when the primary state is one of <br>
     * <ul>
     * <li>DUMPED</li>
     * <li>HIBERNATED</li>
     * <li>TRANSFER</li>
     * </ul>
     */
    public synchronized void setAgentState(AgentState newstate) {
    	System.out.println("Setting State of " + namespace + "."  + name +" (" + getIdentity() + ") to " + newstate);
        if (state != newstate) {
            if (newstate.equals(AgentState.DUMPED))
                secondaryState = state;
            if (newstate.equals(AgentState.HIBERNATED))
                secondaryState = state;
            if (newstate.equals(AgentState.TRANSFER))
                secondaryState = state;
            if (newstate.equals(AgentState.SAVEPOINT))
                secondaryState = state;
            if (newstate.equals(AgentState.LOCKED))
                secondaryState = state;
            this.state = newstate;
            getAgentLogger().fine(this + ": State is now " + state);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jini.projects.neon.agents.Agent#getSecondaryState()
     */
    public AgentState getSecondaryState() {

        return secondaryState;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable {
        super.finalize();
        // System.out.println("Agent " + getIdentity() + " (" + getName() + ")
        // finalized");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jini.projects.neon.agents.Agent#isCompleted()
     */
    public boolean isCompleted() {
        return (completionStatus == LIFECYCLE_COMPLETED);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jini.projects.neon.agents.Agent#isStopped()
     */
    public boolean isStopped() {
        return (stopStatus == LIFECYCLE_STOPPED);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jini.projects.neon.agents.Agent#setComplete()
     */
    public void setCompleted() {

        completionStatus = LIFECYCLE_COMPLETED;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jini.projects.neon.agents.Agent#setStop()
     */
    public void setStopped() {
        stopStatus = LIFECYCLE_STOPPED;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jini.projects.neon.agents.Agent#shouldHalt()
     */
    public boolean shouldHalt() {

        return stopStatus == LIFECYCLE_HALT;
    }

    public void askHalt() {
        stopStatus = LIFECYCLE_HALT;
    }

    public void halt() {
        // TODO Auto-generated method stub
        stopStatus = LIFECYCLE_HALTED;
    }

    public boolean isHalted() {
        // TODO Auto-generated method stub
        return stopStatus >= LIFECYCLE_HALTED;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jini.projects.neon.agents.Agent#getAgentLogger()
     */
    public Logger getAgentLogger() {
        if (this.context != null)
            return context.getContextLogger(name);
        else {

            return Logger.getLogger(name);
        }

    }

    /*
     * @see org.jini.projects.zenith.endpoints.Producer#getProducererIdentity()
     */
    public Uuid getProducerIdentity() {
        //
        return getIdentity().getID();
    }

    /*
     * @see org.jini.projects.neon.collaboration.Advertiser#clearCurrentMessage()
     */
    public void clearCurrentMessage() {
        // TODO Complete method stub for clearCurrentMessage
        this.currentMessage = null;

        AgentState prevState = getSecondaryState();
        if (prevState == null || prevState.equals(AgentState.BUSY))
            setAgentState(AgentState.AVAILABLE);
        else
            setAgentState(prevState);

    }

    /*
     * @see org.jini.projects.neon.collaboration.Advertiser#setCurrentMessage()
     */
    public synchronized void setCurrentMessage(Message newMessage) {
        // TODO Complete method stub for setCurrentMessage

        this.currentMessage = newMessage;
        if (this.state.equals(AgentState.AVAILABLE))
            ;
        setAgentState(AgentState.BUSY);
    }

    public String toString() {
        return getName() + " agent; ID: " + this.getIdentity() + "[" + getClass().getName() + "]";
    }

    /*
     * @see org.jini.projects.neon.collaboration.Advertiser#isLocked()
     */
    public boolean isLocked() {
        // TODO Complete method stub for isLocked
        return state.equals(AgentState.LOCKED);
    }

    /*
     * @see org.jini.projects.neon.collaboration.Advertiser#isBusy()
     */
    public boolean isBusy() {
        // TODO Complete method stub for isBusy
        return state.equals(AgentState.BUSY);
    }

    /*
     * @see org.jini.projects.neon.agents.Agent#getNamespace()
     */
    public String getNamespace() {
        // TODO Complete method stub for getNamespace
        if (namespace == null) {
            String cname = this.getClass().getName();
            String basename = cname.substring(0, cname.lastIndexOf('.'));

            // System.out.println("DerivedName: " + derivedname[0]);
            namespace = basename;
        }
        return this.namespace;
    }

    /*
     * @see apollo.messaging.system.MessagingListener#messageReceived(apollo.messaging.messages.Message)
     */
    public void messageReceived(Message m) {
        MessageHeader header = m.getHeader();
        // Tie up request/response protocol
        if (m instanceof ObjectMessage)
            if (header.getCorrelationID().equals(this.currentMessagingRequest)) {
                messagingReturnObject = m.getMessageContent();
                this.notify();
            }
    }

    public Object getReplyObject() {
        return messagingReturnObject;
    }

    /**
     * getAgentConfiguration
     * 
     * @return Configuration
     */
    public Configuration getAgentConfiguration() throws net.jini.config.ConfigurationException {
        return net.jini.config.ConfigurationProvider.getInstance(new String[] { configurationLocation });
    }

    /**
     * setConfigurationLocation
     * 
     * @param configLocation -
     *            A URL specifiying tte location of the agent's configuration
     */
    public void setConfigurationLocation(java.net.URL configLocation) {
        if (configLocation != null) {
            this.configurationLocation = configLocation.toExternalForm();
        } else
            configurationLocation = null;
    }

    public Meta getMetaAttributes() {
        if (metaInformation == null)
            metaInformation = new Meta();
        return metaInformation;
    }

    public void setMetaAttributes(Meta metaEntries) {
        metaInformation = metaEntries;
    }

    public String getConfigurationLocation() {
        // TODO Auto-generated method stub
    	if(internalConfig && configurationLocation == null){
    		System.out.println("This agent has internal configuration: " + internalConfigLocation);
    		System.out.println(getClass().getName());
    		setConfigurationLocation(getClass().getResource(internalConfigLocation));
    	}
        return this.configurationLocation;

    }

    public void setRemoteAgentState(AgentState newstate) throws RemoteException {
		// TODO Auto-generated method stub
    	setAgentState(newstate);
	}
    
    public void setInternalConfiguration(boolean internal) {
    	// TODO Auto-generated method stub
    	this.internalConfig = true;
    }
    
    
    public void setInternalConfigurationLocation(String location) {
    	// TODO Auto-generated method stub
    	System.out.println("Internal Cf Loc: " + location);
    	this.internalConfigLocation = location;
    }
}
