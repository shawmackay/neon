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

import java.net.MalformedURLException;

import net.jini.core.entry.Entry;
import net.jini.core.event.RemoteEventListener;
import net.jini.id.Uuid;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;

/**
 * In normal use an agent will only have access to the base
 *  and will therefore not be able to interact with
 * agents or their callbacks directly. However, certain system agents
 * will require more access to the host container
 */
public interface PrivilegedAsyncAgentEnvironment extends AsyncAgentEnvironment  {
	public RemoteEventListener[] getCallbacks(String agentName) throws NoSuchAgentException, SecurityException;
	public RemoteEventListener getCallback(AgentIdentity ID) throws NoSuchAgentException, SecurityException;
	public void unsubscribeAgent(Agent ag) throws NoSuchAgentException, SecurityException;
	public Uuid getHostServiceID();

	public void deployAgent(Agent a);

	public void deployAgent(Agent a, RemoteEventListener c);
	
     public void deployObject(Object o, String configurationLocation, String constraintsLocation) throws MalformedURLException;
    
	public ManagedDomain getAgentHost();
	
    
        public Object getAgent(AgentIdentity id, Agent controller) throws NoSuchAgentException;
        
	public Object getAgent(String name, Agent controller) throws NoSuchAgentException;
	public Object getAgent(String name, int timeout,Agent controller) throws NoSuchAgentException;
	
	public Object getAgent(String name, Entry[] matchTo, Agent controller) throws NoSuchAgentException;
    public Object getAgent(String name, Entry[] matchTo, int timeout,Agent controller) throws NoSuchAgentException;
	
    public Object attachAgent(String name, Agent controller) throws NoSuchAgentException;
	public Object attachAgent(String name, int timeout,Agent controller) throws NoSuchAgentException;
	
	public Object attachAgent(String name, Entry[] matchTo, Agent controller) throws NoSuchAgentException;
    public Object attachAgent(String name, Entry[] matchTo, int timeout,Agent controller) throws NoSuchAgentException;
	
    public boolean detachAgent(Object reference);
    
	public Object getAsyncAgent(String name, AsyncHandler contextHandler, Agent controller);
    public Object getAsyncAgent(String name, Entry[] matchTo, AsyncHandler contextHandler, Agent controller);
    public Object getAsyncAgent(String name,  Agent controller);
    public Object getAsyncAgent(String name,  Entry[] matchTo, Agent controller);

}
