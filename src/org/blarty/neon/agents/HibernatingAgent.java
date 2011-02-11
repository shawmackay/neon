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

/**
 * Provides capabilities in an agent to sleep, formally this allows an agent framework to
 * store an agent to disk for a given amount of time. This is useful in situations where an
 * agent cannot fulfil it's job at the current time, but can wait
 */
public interface HibernatingAgent extends Agent{
    /**
     * Requests that the agent hibernate for the default period of time, as specified
     * by the agent itself
     * @return whether the agent is now stored
     */
    public boolean hibernate();

    /**
     * Requests that the agent hibernate for a set period of time, which will always be less
     * than or equal to the maximum hibernation time by the agent itself
     * @param time length of wait for the agent in ms
     * @return whether the agent is now stored
     */
    public boolean hibernateFor(long time);

    /**
     * Asks the agent to begin processing after the
     * hibernation cycle is complete<br>
     * This is in addition to the normal lifecycle
     */
    public void wake();

    /**
     * Gets the maximum hibernation time that the hosts supports. 
     * @return the maximum hibernation wait that this host will allow
     */
    public long getMaxHibernationTime();

    /**
     * Checks whether the agent can do it's processing, or whether it should be put back into hibernation
     * @return true if the agent is now ready to do it's work, otherwise put back into hibernation
     */
    public boolean checkHibernation();
}
