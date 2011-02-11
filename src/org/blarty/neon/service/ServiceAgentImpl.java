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


package org.jini.projects.neon.service;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;
import net.jini.core.entry.Entry;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.LookupCache;
import net.jini.lookup.ServiceDiscoveryEvent;
import net.jini.lookup.ServiceDiscoveryListener;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.lookup.ServiceItemFilter;
import net.jini.lookup.entry.Name;

import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.LocalAgent;
import org.jini.projects.neon.agents.SensorAgent;
import org.jini.projects.neon.agents.sensors.SensorFilter;
import org.jini.projects.neon.agents.sensors.SensorListener;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.neon.host.PrivilegedAgentContext;
import org.jini.projects.neon.host.transactions.NonTransactionalResource;
import org.jini.projects.zenith.messaging.messages.EventMessage;
import org.jini.projects.zenith.messaging.messages.MessageHeader;

/**
 * Allows agents to obtain proxies for other Jini services. Also allows agents
 * to be told of when other instances of <code>AgentService</code> have been
 * removed from the lookup service
 *@see org.jini.projects.neon.recovery.RecoveryAgent
 */
public class ServiceAgentImpl extends AbstractAgent implements ServiceDiscoveryListener, LocalAgent,  ServiceAgent, NonTransactionalResource, Runnable {
	
    private Logger svcLog;

    private List AgHostList = new ArrayList();

    private LookupDiscoveryManager ldm;

    private ServiceDiscoveryManager sdm;

    private LookupCache cache;

    private Map sensors = new HashMap();

    public ServiceAgentImpl() {
        this.name = "Services";
        this.namespace = "neon";
    }

    public boolean init() {
        try {
            svcLog = getAgentLogger();
            {
                // System.out.println("Setting a privileged context");
                context = (PrivilegedAgentContext) context;
                try {
                    RemoteEventListener[] cbs = ((PrivilegedAgentContext) context).getCallbacks("neon.Services");
                } catch (NoSuchAgentException e) {
                    System.out.println("Err: " + e.getMessage());
                    e.printStackTrace();
                } catch (SecurityException e) {
                    System.out.println("Err: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            Configuration config = getAgentConfiguration();
            String[] groups = (String[]) config.getEntry("serviceAgent", "sdmLookupGroups", String[].class);
            for (int i = 0; i < groups.length; i++) {
                getAgentLogger().fine("Service Agent looking in group: " + groups[i]);
            }
            ldm = new LookupDiscoveryManager(groups, null, null);
            getAgentLogger().fine("Defining ServiceAgentImpl SDM now");
            sdm = new ServiceDiscoveryManager(ldm, null);
            cache = sdm.createLookupCache(new ServiceTemplate(null, null, null), null, this);           
           

        } catch (ConfigurationException e) {
            System.out.println("Err: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            System.out.println("Err: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    
    /*
     * We force Neon to run the initialisation for these agents in a separate thread by adding the run method
     *
     */
    public void run() {
       
    }

    public void stop() {
        
        super.stop();
    }

    public List getAgentHosts() {
        AgHostList.clear();
        ServiceItem[] services = cache.lookup(new AgentServiceFilter(), 50);
        for (int i = 0; i < services.length; i++)
            this.AgHostList.add(services[i]);
        return AgHostList;
    }

    public void serviceAdded(ServiceDiscoveryEvent event) {
        ServiceItem si = event.getPostEventServiceItem();
        String name="<none>";
        for(Entry e: si.attributeSets)
        	if(e instanceof Name)
        		name = ((Name) e).name;
        svcLog.finer("Service Added: " + si.serviceID + "{ Name: " + name + "; Class: "+ si.service.getClass().getName() + "}");
       // System.out.println("Service Added: " + si.serviceID + "{ Name: " + name + "; Class: "+ si.service.getClass().getName() + "}");
    }

    public List getNamedService(String name, Class serviceClass){        
        NameFilter filter = new NameFilter(name, serviceClass);
        if(cache==null)
            svcLog.warning("Lookup Cache is null!");
        ServiceItem[] services = cache.lookup(filter, 5);
        ArrayList arr = new ArrayList();
        for (int i = 0; i < services.length; i++){            
            arr.add(services[i]);
        }
        return arr;
    }

    public List getService(Class serviceClass) {
        svcLog.fine("Getting a service of type " + serviceClass.getName());
        ClassFilter filter = new ClassFilter(serviceClass);
        if (cache == null)
            System.out.println("Cache is null!!!");
        if (filter == null)
            System.out.println("Filter is null!!!");
        ServiceItem[] services = cache.lookup(filter, 5);
        svcLog.fine("LUS returned " + services.length + " instances of " + serviceClass.getName());
        ArrayList arr = new ArrayList();
        for (int i = 0; i < services.length; i++)
            arr.add(services[i]);
        return arr;
    }

    public Object getSingleService(Class serviceClass) {

        List arr = getService(serviceClass);
        svcLog.finer("Service Array for : " + serviceClass + " Length: " + arr.size());
        Random rand = new Random(System.currentTimeMillis());
        if (arr.size() >= 2)
            return ((ServiceItem) arr.get(rand.nextInt(arr.size()))).service;
        if (arr.size() == 1)
            return ((ServiceItem) arr.get(0)).service;

        return null;
    }

    public Object getSingleNamedService(String name, Class serviceClass) {
        List arr = getNamedService(name, serviceClass);
        Random rand = new Random(System.currentTimeMillis());
        return ((ServiceItem) arr.get(rand.nextInt(arr.size()))).service;

    }

    public void serviceRemoved(ServiceDiscoveryEvent event) {
        try {
            Object ob = event.getPreEventServiceItem().service;
            Collection c = sensors.entrySet();
            svcLog.finest("Service Removed: " + event.getPreEventServiceItem().serviceID);
            svcLog.finest("Service removed is of type: " + ob.getClass().getName());
            if (ob instanceof AgentService) {
                svcLog.finest("Service removed is anotherAgentService");
                for (Iterator iter = c.iterator(); iter.hasNext();) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    AgentIdentity id = (AgentIdentity) entry.getKey();
//                    MessageHeader header = new MessageHeader();
//                    EventMessage msg = new EventMessage(header, event.getPreEventServiceItem().serviceID, 0, "AgentServiceRemoved");
//                    try {
//                        context.sendMessage(id.getID(), msg);
//                        svcLog.finest("Event message sent");
//                    } catch (NoSuchAgentException e) {
//                        // TODO Handle NoSuchAgentException
//                        e.printStackTrace();
//                    }
                    Object ag = context.getAgent(id);
                    SensorListener listener = (SensorListener)ag;
                    listener.sensorTriggered("AgentServiceRemoved", event.getPreEventServiceItem().serviceID);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void serviceChanged(ServiceDiscoveryEvent event) {
    }

    class AgentServiceFilter implements ServiceItemFilter {
        public boolean check(ServiceItem item) {
            return item.service instanceof AgentService;
        }
    }

    public boolean addListener(AgentIdentity listener, SensorFilter filter) {
        getAgentLogger().finest("Service Listener registered");
        sensors.put(listener, null);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jini.projects.neon.agents.sensors.Sensor#addListener(org.jini.projects.neon.agents.sensors.SensorListener)
     */
    public boolean addListener(AgentIdentity a, SensorListener l) {
        getAgentLogger().finest("Service listener registered(OLD-STYLE)");
        sensors.put(a, l);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jini.projects.neon.agents.sensors.Sensor#removeListener(org.jini.projects.neon.agents.sensors.SensorListener)
     */
    public boolean removeListener(AgentIdentity a) {
        if (sensors.containsKey(a)) {
            sensors.remove(a);
            return true;
        }
        return false;
    }
}
