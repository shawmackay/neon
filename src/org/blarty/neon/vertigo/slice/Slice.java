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
 * neon : org.jini.projects.neon.vertigo.slice
 * Slice.java
 * Created on 09-May-2005
 *Slice
 */
package org.jini.projects.neon.vertigo.slice;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.jini.config.Configuration;
import net.jini.core.lookup.ServiceID;
import net.jini.id.Uuid;

import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.constraints.AgentRequirements;
import org.jini.projects.neon.vertigo.application.Initialiser;
import org.jini.projects.neon.vertigo.management.AgentAction;
import org.jini.projects.neon.vertigo.management.AgentListObject;
import org.jini.projects.neon.vertigo.management.SliceManager;
import org.jini.projects.neon.vertigo.management.VetoResult;



/**
 * A Slice is the base type for all application management.
 * @author calum
 */
public interface Slice extends Serializable{
	public String getName();
	public String getShortDescription();
    public String getSliceType();
	public String getLongDescription();	
	public Configuration getConfiguration();
	public AgentRequirements getSliceRequirements();
	public List getAgentIDs();
	public void attachAgent(AgentListObject ag);	
	public List getAgentTypeReferences();
	public void attachAgentType(String name, int number);	
	public void removeAgent(AgentIdentity id);		
    public void setName(String name);	
	public void setShortDescription(String s_description);
	public void setLongDescription(String l_description);
	public void setInitialiser(Initialiser init);
	
	public boolean init();
	public Uuid[] getSubSliceIDs();
	public Map getSubSlices();
	public void addSlice(Uuid sliceID, String sliceName);
	public boolean removeSlice(String sliceName);
	public Collection getSliceNames();
	public Uuid getSlice(String slicename);	
	public Uuid getSliceID();
    public void  setSliceID(Uuid sliceID);
	public Uuid getParentSliceID();
	public void setParentSliceID(Uuid parentSliceID);
    
    public VetoResult vetoes(AgentAction action,AgentIdentity agent, SliceManager sliceManager, ServiceID preAction, ServiceID postAction);
    public boolean enforcesPolicy();
    public void setEnforced(boolean enforcePolicy);
    public boolean isEnforceable();
}
