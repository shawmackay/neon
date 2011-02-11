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
 * DefaultSlice.java
 * Created on 09-May-2005
 *DefaultSlice
 */

package org.jini.projects.neon.vertigo.slice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.jini.config.Configuration;
import net.jini.core.lookup.ServiceID;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.constraints.AgentRequirements;
import org.jini.projects.neon.vertigo.application.AgentTypeReference;
import org.jini.projects.neon.vertigo.application.Initialiser;
import org.jini.projects.neon.vertigo.management.AgentAction;
import org.jini.projects.neon.vertigo.management.AgentListObject;
import org.jini.projects.neon.vertigo.management.SliceManager;
import org.jini.projects.neon.vertigo.management.VetoResult;

/**
 * @author calum
 */
public class DefaultSlice implements Slice {
    
    protected boolean enforced;
    
    protected String type;
    
    protected List agentList = new ArrayList();
    
	private List agentTypeList = new ArrayList();

    protected Configuration appconfiguration;

    protected Initialiser init;

    protected String l_description;

    protected String name;

    protected Uuid parentid;

    protected String s_description;

    protected Map<String, Uuid> sliceList = new HashMap<String, Uuid>();

    protected AgentRequirements slicerequirements;

    protected Uuid uuid;
    
    public DefaultSlice() {
        uuid = UuidFactory.generate();
    }

    public DefaultSlice(String name) {
        this();
        this.name = name;
    }

    public DefaultSlice(String name, String shortDesc) {
        this();
        this.name = name;
    }

    public DefaultSlice(String name, String shortDesc, String longDesc) {
        this();
        this.name = name;
        this.s_description = shortDesc;
        this.l_description = longDesc;
    }

    public void addSlice(Uuid slice, String name) {
        // TODO Complete method stub for addSlice
        sliceList.put(name, slice);
    }

    public void attachAgent(AgentListObject ag) {
        // TODO Complete method stub for attachAgent
        agentList.add(ag);
    }

    public void deployAgent(Agent ag) {
        // TODO Complete method stub for deployAgent
        agentList.add(ag.getIdentity().getID());
    }

    public List getAgentIDs() {
        return agentList;
    }

    public Configuration getConfiguration() {
        // TODO Complete method stub for getConfiguration
        return appconfiguration;
    }

    public String getLongDescription() {
        // TODO Complete method stub for getLongDescription
        return l_description;
    }

    public String getName() {
        // TODO Complete method stub for getName
        return name;
    }

    public Uuid getParentSliceID() {
        // TODO Complete method stub for getParentSliceID
        return parentid;
    }

    public String getShortDescription() {
        // TODO Complete method stub for getShortDescription
        return s_description;
    }

    public Uuid getSlice(String slicename) {
        // TODO Complete method stub for getSlice
        return sliceList.get(slicename);
    }

    public Uuid getSliceID() {
        // TODO Complete method stub for getSliceID
        return uuid;
    }

    public java.util.Collection getSliceNames() {
        return sliceList.keySet();
    }

    public AgentRequirements getSliceRequirements() {
        // TODO Complete method stub for getSliceRequirements
        return slicerequirements;
    }

    public Uuid[] getSubSliceIDs() {
        // TODO Complete method stub for getSlices
        return (Uuid[]) sliceList.keySet().toArray(new Uuid[] {});
    }

    public Map getSubSlices() {
        return this.sliceList;
    }

    public boolean init() {
        // TODO Complete method stub for init
        return init.init(this);
    }

    public void removeAgent(AgentIdentity id) {
        // TODO Complete method stub for removeAgent
        agentList.remove(id);
    }

    public boolean removeSlice(String slicename) {
        // TODO Complete method stub for removeSlice
        Uuid removed = null;
        removed = sliceList.remove(slicename);
        if (removed != null)
            return true;
        else
            return false;
    }

    public void setInitialiser(Initialiser init) {
        // TODO Complete method stub for setInitialiser
        this.init = init;
    }

    public void setLongDescription(String l_description) {
        // TODO Complete method stub for setLongDescription
        this.l_description = l_description;
    }

    public void setName(String name) {
        // TODO Complete method stub for setName
        this.name = name;
    }

    public void setParentSliceID(Uuid parentSliceID) {
        // TODO Complete method stub for setParentSliceID
        this.parentid = parentSliceID;
    }

    public void setShortDescription(String s_description) {
        this.s_description = s_description;
    }

    public VetoResult vetoes(AgentAction action, AgentIdentity agent, SliceManager sliceManager, ServiceID preAction, ServiceID postAction) {
        // TODO Auto-generated method stub
        
        return VetoResult.YES;
    }

    public boolean enforcesPolicy() {
        // TODO Auto-generated method stub
        return enforced;
    }

    public void setEnforced(boolean enforcePolicy) {
        // TODO Auto-generated method stub
        this.enforced = enforcePolicy;
    }

    public boolean isEnforceable() {
        // TODO Auto-generated method stub
        return false;
    }

    public String getSliceType() {
        // TODO Auto-generated method stub
        String cName = getClass().getName();
        return cName.substring(cName.lastIndexOf("."));
    }

    public void setSliceID(Uuid sliceID) {
        // TODO Auto-generated method stub
        this.uuid = sliceID;
    }

	public void attachAgentType(String name, int number) {
		// TODO Auto-generated method stub
		agentTypeList.add(new AgentTypeReference(name, number));
	}

	public List getAgentTypeReferences(){
		return agentTypeList;
	}
	
	
}
