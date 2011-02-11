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
 * neon : org.jini.projects.neon.vertigo.management
 * SliceManager.java
 * Created on 12-May-2005
 *SliceManager
 */
package org.jini.projects.neon.vertigo.management;

import java.io.IOException;

import net.jini.core.lookup.ServiceID;
import net.jini.id.Uuid;

import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.vertigo.slice.Slice;

/**
 * @author calum
 */
public interface SliceManager{
	public boolean deploySlice(Slice s) ;
	public void displaySliceInfo(String sliceName) ;
	public void createApplication(String applicationName);
	public void attachSubSlice(Slice parentSlice, Slice subslice);
	public Slice getSlice(Uuid sliceID);
    public Slice getSliceFor(AgentIdentity a);
	public void attachAgentToSlice(AgentIdentity a, String slicename);
    public void attachAgentToSlice(AgentIdentity a, Uuid sliceID);
    public void attachAgentTypeToSlice(String agentName, int number, Uuid sliceID);
	public void createSlice(String slicepath, String newSliceName, SliceType type);
    public ServiceID getHostingServiceID(AgentIdentity ag);
	public Slice findSlice(String slicepath);
	public java.util.Set getApplicationNames();
	public void store(Slice s) throws IOException;
}
