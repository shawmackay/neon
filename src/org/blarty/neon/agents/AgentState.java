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

/**
 * Defines the range of possible 'standard' states an agent may be in Because
 * this is required in 1.4, this class uses old style singleton based
 * enumerations.
 * 
 * @author calum
 */
public class AgentState implements Serializable {
    static final long serialVersionUID = 9102353390635358505L;

    private int state;

    /**
     * An agent that can be used to complete a task or subtask. <br>
     * Mostly used by continuous utility agents that will switch between BUSY
     * and AVAILABLE states many times during their lifecycle.
     */
    public static AgentState AVAILABLE = new AgentState(0);

    /**
     * An agent that is currently undertaking a task. Agents are only <i>BUSY</i>
     * for the duration of the current message, after which they become
     * available for processing by other agents.<br>
     * <i>BUSY</i> agents are stateless, i.e. sending a message to set some
     * data, and then another to retrieve that data does not guarantee that the
     * same agent is used both times, if more than one agent of the same
     * category exists, unless the message is specifically directed using the
     * agents' ID.
     */
    public static AgentState BUSY = new AgentState(1);

    /**
     * An agent that is currently undertaking a task or waiting but cannot be
     * released by the framework back to the work pool. <b>Note: </b>
     * <i>Difference between <b>BUSY</b> and <b>LOCKED</b> states</i><br>
     * LOCKED agents are currently executing under a transaction, and are locked
     * by the system until the transaction completes.<br>
     * There is no need to have full <i>Transactional</i> agents, transactions
     * can be used to lock agents in a stateful manner, ensuring that multiple
     * calls to a member of an agent category, are routed to the same agent
     * during the transaction. Unlike BUSY agents, LOCKED agents say unavailable
     * to message routing until the transaction completes.
     */
    public static AgentState LOCKED = new AgentState(2);

    /**
     * When an agent requests to transfer from one host to another. <br>
     * This special state is used to ensure that the agent and it's data is in a
     * state of flux until it has been transferred and restarted at another
     * host. In most cases the STATE changes will be<br>
     * BUSY, TRANSFER, BUSY (at the receiving system). it may also be LOCKED,
     * TRANSFER, LOCKED
     */
    public static AgentState TRANSFER = new AgentState(3);

    /**
     * Used to indicate an agent which is completing it's task and will
     * subsequently disappear from the host and the network. That being said, an
     * agent in <code>DEATH</code> state, may be reactivated at some point in
     * the future.
     */
    public static AgentState DEATH = new AgentState(4);

    /**
     * Used to indicate an agent which will sleep for a given amount of time.
     * Hibernation is mainly used in pre-emptive situations i.e. if the database
     * isn't available, hibernate for an hour and retry, or for using in
     * virtualisation
     */
    public static AgentState HIBERNATED = new AgentState(5);

    /**
     * Set when agents are dumped out to store in the event of system shutdown
     * or failure Although HIBERNATED and DUMPED states are very similar in that
     * they represent an agent stored somewhere, ready to be reinstantiated at
     * some point, a DUMPED agent represents a non-intentional storage of the
     * agent at a given point in time. If at all possible if a DUMPED agent
     * becomes visible to another host, it should attempt to deploy it, if
     * constraints allow it to.
     */
    public static AgentState DUMPED = new AgentState(6);

    /**
     * Used whenever an agent is checkpointed to a a backing area.
     */
    public static AgentState SAVEPOINT = new AgentState(7);

    /**
     * Used whenever an agent is attached to a process for exclusive use, but
     * not Locked (or not locked<i>yet</i>)within a transaction
     */
    public static AgentState ATTACHED = new AgentState(8);

    /**
     * Use whenever you want to signal that an agent should not be shown as
     * available, perhaps whilst waiting for an asynchronous call to complete.
     */
    public static AgentState PENDING = new AgentState(9);

    /**
     * Used to denote redundant agents
     */
    public static AgentState ZOMBIE = new AgentState(10);

    /**
     * Returns a readable name for the agent state
     * 
     * @param a
     *            the <code>AgentState</code> reference
     * @return the State name
     */
    public static String getStateName(AgentState a) {
        String[] names = new String[] { "Available", "Busy", "Locked", "Transfer", "Death", "Hibernated", "Dumped", "Savepoint", "Attached", "Pending", "Zombie" };
        return names[a.state];
    }

    public static AgentState stateFor(String stateName) {
        String name = stateName.toLowerCase();
        if (name.equals("available"))
            return AgentState.ATTACHED;
        if (name.equals("busy"))
            return BUSY;
        if (name.equals("locked"))
            return LOCKED;
        if (name.equals("transfer"))
            return TRANSFER;
        if (name.equals("death"))
            return DEATH;
        if (name.equals("hibernated"))
            return HIBERNATED;
        if (name.equals("dumped"))
            return DUMPED;
        if (name.equals("savepoint"))
            return SAVEPOINT;
        if (name.equals("attached"))
            return ATTACHED;
        if (name.equals("pending"))
            return PENDING;
        if (name.equals("zombie"))
            return ZOMBIE;
        return null;
    }

    static {

    }

    /**
     */
    private AgentState(int state) {
        super();

        this.state = state;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {

        if (!(obj instanceof AgentState))
            return false;
        if (this.state == ((AgentState) obj).state) {
            // System.err.println(this +"<=>" + obj);
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {

        if (state == 0)
            return "Available";
        if (state == 1)
            return "Busy";
        if (state == 2)
            return "Locked";
        if (state == 3)
            return "Transfer";
        if (state == 4)
            return "Death";
        if (state == 5)
            return "Hibernated";
        if (state == 6)
            return "Dumped";
        if (state == 7)
            return "SavePoint";
        if (state == 8)
            return "Attached";
        if (state == 9)
            return "Pending";
        if (state == 10)
            return "Zombie";

        return "UNKNOWN state " + state;
    }

}
