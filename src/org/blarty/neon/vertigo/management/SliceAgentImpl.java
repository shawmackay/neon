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

package org.jini.projects.neon.vertigo.management;

/*
 * SliceAgentImpl.java
 * 
 * Created Tue May 17 09:46:27 BST 2005
 */

import java.io.IOException;

import net.jini.id.Uuid;

import org.jini.projects.neon.host.*;
import org.jini.projects.neon.agents.*;

import net.jini.core.entry.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.reflect.*;

import net.jini.core.lookup.*;

import org.jini.projects.neon.dynproxy.*;
import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.LocalAgent;
import org.jini.projects.neon.vertigo.slice.Slice;
import org.jini.projects.neon.service.ServiceAgent;

/**
 * 
 * @author calum
 * 
 */

public class SliceAgentImpl extends AbstractAgent implements SliceAgent, LocalAgent { 

    private transient SliceManager manager;

    public SliceAgentImpl() {
        this.name = "Slice";
        this.namespace = "vertigo";
    }

    /**
     * init
     * 
     * @return boolean
     */
    public boolean init() {
        try {

            manager = new SliceManagerImpl(getAgentConfiguration());
           
            Meta m = this.getMetaAttributes();
            AgentContext ctx = this.getAgentContext();
            m.addAttribute(new SIDEntry(ctx.getAgentServiceID()));

        } catch (Exception ex) {
            System.err.println("Caught Exception: " + ex.getClass().getName() + "; Msg: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
        return true;
    }



    /**
     * getApplicationNames
     * 
     * @return String []
     */
    public String[] getApplicationNames() {
        java.util.Set s = manager.getApplicationNames();
        String[] arr = (String[]) s.toArray(new String[0]);
        return arr;
    }

    public Map getAllAgentInfo(String filter) {
        Map hostagentMap = new HashMap();
        try {
            ServiceAgent svcAg = (ServiceAgent) getAgentContext().getAgent("neon.Services");
            List l = svcAg.getAgentHosts();
            for (Iterator iter = l.iterator(); iter.hasNext();) {
                ServiceItem item = (ServiceItem) iter.next();
                SIDEntry sid = new SIDEntry(item.serviceID);
                System.out.println("Looking for a slice agent with a sid of:  " + item.serviceID);
                SliceAgent ag;
                if (item.serviceID.equals(context.getAgentServiceID())) {

                    System.out.println("SID matches my SID using local agent");
                    ag = this;
                } else
                    ag = (SliceAgent) getAgentContext().getAgent("vertigo.Slice", new Entry[] { sid });
                if (ag != null)
                    hostagentMap.put(item.serviceID.toString(), ag.getHostAgentInfo(filter));
                else
                    System.out.println("SliceAgent not found in service: " + item.serviceID);
            }
        } catch (Exception ex) {
            System.err.println("Caught Exception: " + ex.getClass().getName() + "; Msg: " + ex.getMessage());
            ex.printStackTrace();
        }

        return hostagentMap;
    }

    public Map getHostAgentInfo(String filter) {
    	Map agentMap = new TreeMap();
        Collection c = DomainRegistry.getDomainRegistry().getDomains();
        Pattern patt = null;
        if (filter != null && !(filter.equals("")))
            patt = Pattern.compile(filter);
        for (Iterator iter = c.iterator(); iter.hasNext();) {
            ManagedDomain dom = (ManagedDomain) iter.next();
            AgentRegistry reg = dom.getRegistry();
            Agent[] agents = reg.getAllAgents();
            List l = getDistinctNames(agents);
            
            for (int i = 0; i < agents.length; i++) {
                Agent agent = agents[i];
                List subList = null;
                if(!agentMap.containsKey(agent.getNamespace() + "." + agent.getName())){
                	 subList = new ArrayList();
                     agentMap.put(agent.getNamespace() + "." + agent.getName(), subList);
                } else
                	subList = (List) agentMap.get(agent.getNamespace() + "." + agent.getName());
                	
                if (agent instanceof java.lang.reflect.Proxy) {
                    Proxy p = (java.lang.reflect.Proxy) agent;
                    InvocationHandler invHnd = p.getInvocationHandler(agent);
                    if (invHnd instanceof FacadeProxy) {
                        agent = ((FacadeProxy) invHnd).getReceiver();
                    }
                }
                if (patt != null) {
                    Matcher m = patt.matcher(agent.getClass().getName());
                    if (m.find()) {
                        System.out.println("Adding " + agent.getIdentity() + " (" + agent.getClass().getName() + ")");
                        subList.add(new AgentListObject(agent.getNamespace() + "." + agent.getName(), agent.getIdentity(), agent.getClass().getName(), dom.getDomainName()));
                    }
                } else {
                    System.out.println("Adding " + agent.getIdentity() + " (" + agent.getClass().getName() + ")");
                    subList.add(new AgentListObject(agent.getNamespace() + "." + agent.getName(), agent.getIdentity(), agent.getClass().getName(), dom.getDomainName()));
                }

            }
        }
        return agentMap;
    }

    private List getDistinctNames(Agent[] agents) {
    	List returnList = new ArrayList();
    	for(Agent a : agents){
    		if(!returnList.contains(a.getNamespace() + "." + a.getName()))
    			returnList.add(a.getNamespace() + "." + a.getName());
    	}
		// TODO Auto-generated method stub
		return returnList;
	}

	public void attachAgentToSlice(AgentIdentity a, String slicename) {
        manager.attachAgentToSlice(a, slicename);
    }

    public void attachAgentToSlice(AgentIdentity a, Uuid sliceID) {
        manager.attachAgentToSlice(a, sliceID);
    }
    
    public void attachSubSlice(Slice parentSlice, Slice subslice) {
        manager.attachSubSlice(parentSlice, subslice);
    }

    public void createApplication(String applicationName) {
        manager.createApplication(applicationName);
    }

    public void createSlice(String slicepath, String newSliceName, SliceType type) {
        manager.createSlice(slicepath, newSliceName, type);
    }

    public boolean deploySlice(Slice s) {
        return manager.deploySlice(s);
    }

    public void displaySliceInfo(String sliceName) {
        manager.displaySliceInfo(sliceName);
    }

    public Slice findSlice(String slicepath) {
        return manager.findSlice(slicepath);
    }

    public ServiceID getHostingServiceID(AgentIdentity ag) {
        return manager.getHostingServiceID(ag);
    }

    public Slice getSlice(Uuid sliceID) {
        return manager.getSlice(sliceID);
    }

    public void storeSlice(Slice s) throws IOException {
        manager.store(s);
    }

    public Slice getSliceFor(AgentIdentity a) {
        // TODO Auto-generated method stub
        return manager.getSliceFor(a);
    }

	public void attachAgentTypeToSlice(String agentName, int number,Uuid sliceID) {
		// TODO Auto-generated method stub
		manager.attachAgentTypeToSlice(agentName, number, sliceID);
	}

}
