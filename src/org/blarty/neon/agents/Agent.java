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

import java.io.Serializable;
import java.net.URL;
import java.util.logging.Logger;

import org.jini.projects.neon.collaboration.Advertiser;
import org.jini.projects.neon.host.AgentContext;
import org.jini.projects.zenith.endpoints.Producer;
import org.jini.projects.zenith.messaging.messages.Message;



/**
 * The minimum interface that a class must implement in order to be an agent.<br>
 * Provides notion of agent identity and description, used in agent querying and reporting.
 * At a minimum there can be two types of subclass:
 * RemoteAgent (extending <code>java.rmi.Remote</code>)
 * and
 * LocalAgent (extending <code>java.io.Serializable</code>)
 */
public interface Agent extends Serializable, Advertiser, Producer {

    /**
     * Returns the set of AgentConstraints that the agent hosts need to satisfy in order
     * to execute this agent or the current agent task
     * @return the agents current Constraints
     */
    public AgentConstraints getConstraints();

    /**
     * Enables the setting of a new set of constraints
     * @param newConstraints
     */
    public void setConstraints(AgentConstraints newConstraints);

    /**
     * All agents must have some form of Identity. This is unique across the network
     * @return the AgentIdentity to with which to identify the agent
     */
    public AgentIdentity getIdentity();

    /**
     * All agents must have some form of Identity. This is unique across the network
     * Setting the Identity only occurs iff the agentidentity is not already set; for instance
     * when reconstructing an agent rather than using a serialized form
     * @param the AgentIdentity to with which to identify the agent
     */
    public void setIdentity(AgentIdentity id);
    
    
    
    /** Returns the name of the given agent type
     * Neon allows multiple agents of the same type to be operating within a host.
     * The agent name is most often used to query the container for any instance that is available
     * @return the name of the agent
     */
    public String getName();

    /** Returns the agent's namespace, where to prevent naming collisions
     *@return namespace for the agent 
     */
    
    public String getNamespace();
    
    /**
     * Used instead of a parameter constructor.<br>
     * Called each time an agent is created
     * If this method returns false, the agent is decommissioned.
     * @return whether or not the agent should continue to be started
     */
    public boolean init();

    /**
     * Signals an agent to stop what it is currently doing and ensure that all of it's state is available for storage/transfer/terminiation
     */
    public void stop();
    /**
     * Called prior to agent destruction
     */
    public void complete();

    /**
     * Gets the view of the host that an agent can see, so it can collaborate, etc.
     * @return the agent context for the agent
     */
    public AgentContext getAgentContext();

    /**
     * Sets the view of the host that an agent can see. In most cases a single host
     * will represent a single context. However, it may be possible to host multiple
     * contexts allowing sandboxing of resources
     */
    public void setAgentContext(AgentContext hostContext);

    /**
     * returns a value denoting the agent state
     * @return the current <em>primary</em> state of the agent
     */
    public AgentState getAgentState();
    /**
     * Sets the agent state, possibly setting the secondary state as a side-effect 
     * @param newstate the new primary state.
     */
    public void setAgentState(AgentState newstate);
    
    
    
    /**
     * Gets the secondary state of an agent.
     * @return the secondary state
     */
    public AgentState getSecondaryState();
    
    /**
     *  Requests an agent to stop it's processing. Returnint from this call should not assume that
     *  the agent has stopped. The external entity must check isHalted() before doing any cleanup or calling stop/complete 
     *
     */
    public void askHalt();
    
    /**
     * Indicates to the agent, that the system assumes that the agent is halted
     *
     */
    public void halt();
    /**
     * Returns true if the agent has been asked to halt it's processing, and return from the start() call
     * @see Agent#halt()
     * @return a value indicating that processing should be halted for some reason
     */
    public boolean shouldHalt();
    
    /**
     * Indicates that the agent has stopped it's internal processing. i.e. the agent's start method has completed.
     * @see Agent#halt()
     * @return true if the agent has halted.
     */
    public boolean isHalted();
    /**
     * Indicates whether the agent has finished any completion processing
     * @see Agent#complete()
     * @return the completion state
     */
    public boolean isCompleted();
    /**
         * Indicates whether the agent has finished any stop processing and is in a 
         * stasis state.
         * @see Agent#stop()
         * @return the stop state
         */
    public boolean isStopped();

    /**
     * Called by the agent when the completion processing has finished. so the system may 
     * clean up the agent after 'agent death'
     */
    public void setCompleted();
    /**
         * Called by the agent when the stop processing has finished. so the system may 
         * assume the agent is in stable state, such that it may be stored to a space, etc.
         */
    public void setStopped();
    /**
     * Creates a (or returns an existing) logger for the agent allowing logging throught the standard mechanism.
     * The logger is intended to be transient and only created when required
     * @return a Logger to enable the agent to integrate with the hosts logging mechanisms
     */
    public Logger getAgentLogger();
    
    /**
     * Required for LowLevel messaging thorugh AgentContext.sendMessage
     * @see AgentContext#sendMessage(String, Message)
     * @return Obtains the reply to the last processed message
     */
    public Object getReplyObject();
	/** 
     * Obtain the Configuration Instance that has been created for this agent. This may result in a 
     * new Configuration being created each time this method is called or referring to one that is
     * already set and will remain between calls to the method
     *  
     * @return the Configuration object assigned to this agent
     * @throws net.jini.config.ConfigurationException
	 */
    public net.jini.config.Configuration getAgentConfiguration() throws net.jini.config.ConfigurationException;
    /**
     * Sets the URL of the configuration data, for configuring the agent
     * @param configuration URL location for the configuration
     */
    public void setConfigurationLocation(java.net.URL configuration);
    
    public void setInternalConfiguration(boolean internal);
    
    public void setInternalConfigurationLocation(String location);

    /**
     * Gets the URL of the configuration data, for configuring the agent
     * @return location of the agents configuration information
     */
    public String getConfigurationLocation();

    
    /**
     * Get the set of attributes that can be used for better matching in AgentContext#getAgent
     * @return the set of meta-related attributes
     */
    public Meta getMetaAttributes();
    
    /**
     * Change the set of attributes currently held by the agent
     * @param metaEntries
     */
    public void setMetaAttributes(Meta metaEntries);
}
