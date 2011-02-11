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
package org.jini.projects.neon.host;

import java.net.InetAddress;
import java.net.MalformedURLException;

import net.jini.core.event.RemoteEventListener;
import net.jini.core.lookup.ServiceID;
import net.jini.id.Uuid;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentConstraints;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.zenith.messaging.channels.MessageChannel;
import org.jini.projects.zenith.messaging.channels.ReceiverChannel;
import org.jini.projects.zenith.messaging.messages.Message;






/**
 * The base interface for a partition that can contain and
 * execute a set of agents. A Host can contain multiple partitions
 * if each partition has a different, unique name. 
 * A Domain is made up of the sum of all partitions with the same name
 */
public interface AgentDomain {
	public static int SECURITY_LEVEL_OPEN=0;
	public static int SECURITY_LEVEL_NO_GLOBAL=1;
	public static int SECURITY_LEVEL_RESTRICT_CALLERS=2;
	public static int SECURITY_LEVEL_DETAILS_ENCRYPTED=3;
	public static int SECURITY_LEVEL_ENCRYPT_STORAGE=4;
	
	/**
	 * Obtain the given agents' collaboration adverts.
	 * This bindsan agent's collaborative methods to the messaging function.
	 * @param cag The agent to advertise
	 */
	void advertise(Agent cag);

	/**
	 * Sends a message to a unique agent
	 * @param agentID The Identity of the agent you wish to contact 
	 * @param mesg The Message to be sent
	 * @throws NoSuchAgentException if the agent does not exist or cannot be contactable.
	 */
	public void sendAsyncMessage(Uuid agentID,Message mesg) throws NoSuchAgentException;
	
    /**
     * 
		 * Sends a message to a unique agent
		 * @param agentName The agent Name <i>(as in Agent.getAgentName())</i>
		 * @param message The Message to be sent
		 * @throws NoSuchAgentException if the agent does not exist or cannot be contactable.
		 */
	public void  sendAsyncMessage(String agentName, Message message) throws NoSuchAgentException;

    public ReceiverChannel getTemporaryChannel();
    
    public void returnTemporaryChannel(MessageChannel channel);
	/**
	 * Obtains the IP Address of the system that the partition is running on.
	 * @return
	 */
	InetAddress getCurrentHost();
	/**
	 * Get the name of the domain that this partition is logically part of
	 * @return the domain name givem to this partition
	 */
	String getDomainName();
	/**
	 * Return the ID of the agent service, or, if embedded, the service ID of the containing application
	 * @return
	 */
	ServiceID getAgentServiceID();

	/**
	 * Evaluate a set of constraints and requirements that the partition, must be
	 * able to satisfy, in order to host an agent with these requirements. Called
	 * prior to deployAgent();
	 * @param constraints The set of constraits for an agent
	 * @return whether the partition can satisfy these constraints
	 */
	boolean canAccept(AgentConstraints constraints);
	/**
	 * Deploy an agent into this partition. It should be noted that the
	 * agent constraints should be evaluated prior to deployment.
	 * @param a
	 */
	void deployAgent(Agent agent);
	/**
	 * 
	 * Deploy an agent into this partition with the given client callback object. It should be noted that the
	 * agent constraints should be evaluated prior to deployment.	 
	 * @param agent The agent
	 * @param c
	 */
	void deployAgent(Agent agent, RemoteEventListener callback);
	
    
     void deployObject(Object o, String configurationLocation, String constraintsLocation) throws MalformedURLException;
/**
 * Get the list of callbacks for members of an agent group
 * @param agentName The agent group name
 * @return A Callback List
 * @throws NoSuchAgentException
 */

	RemoteEventListener[] getCallbacks(String agentName) throws NoSuchAgentException;
	/**
	 * Get the callback for a particular agent
	 * @param ID The unique Identity of the agent
	 * @return the callback registered or null if once was not registered
	 * @throws NoSuchAgentException
	 */
	RemoteEventListener getCallback(AgentIdentity ID) throws NoSuchAgentException;
   
}
