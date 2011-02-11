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
 * neon : org.jini.projects.neon.kernel
 * Kernel.java
 * Created on 24-Sep-2003
 */

package org.jini.projects.neon.kernel;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.LookupCache;
import net.jini.lookup.ServiceDiscoveryEvent;
import net.jini.lookup.ServiceDiscoveryListener;
import net.jini.lookup.ServiceDiscoveryManager;

import org.jini.projects.neon.host.DomainRegistry;
import org.jini.projects.neon.host.ManagedDomain;
import org.jini.projects.neon.service.ClassFilter;

/**
 * @author calum
 */
public class Kernel implements ServiceDiscoveryListener {
        DomainRegistry domainReg;
        ServiceDiscoveryManager sdm;
        LookupDiscoveryManager ldm;
        static LookupCache cache;
        Logger l = Logger.getLogger("org.jini.projects.neon.kernel");

        /**
         *  
         */
        public Kernel(Configuration kernelConfig) {
                super();
                // TODO Complete constructor stub for Kernel
                DomainRegistry.configure(kernelConfig);
                domainReg = DomainRegistry.getDomainRegistry();
                try {
                        
                        
                        String[] groups = (String[]) kernelConfig.getEntry("org.jini.projects.neon", "kernelSDMGroups", String[].class);
                        for (int i = 0; i < groups.length; i++) {
                                l.finest("Kernel looking in group: " + groups[i]);
                        }
                        ldm = new LookupDiscoveryManager(groups, null, null);
                        
                        l.finer("Defining Kernel SDM and cache now");
                        sdm = new ServiceDiscoveryManager(ldm, null);
                        cache = sdm.createLookupCache(new ServiceTemplate(null, null, null), null, this);
                } catch (RemoteException e) {
                        // URGENT Handle RemoteException
                        e.printStackTrace();
                } catch (ConfigurationException e) {
                        // URGENT Handle ConfigurationException
                        e.printStackTrace();
                } catch (IOException e) {
                        // URGENT Handle IOException
                        e.printStackTrace();
                }
        }

        public void addDomain(ManagedDomain domain) {   
                DomainRegistry.getDomainRegistry().addDomain(domain);
        }

        public void addDomain(ManagedDomain domain, List allowCallsIn, List allowCallsOut) {    
                DomainRegistry.getDomainRegistry().addDomain(domain,allowCallsIn, allowCallsOut);
        }
        
        public ManagedDomain getDomain(String name) {
                return domainReg.getDomain(name);
        }
    
        public static void shutdown(){
            Collection c  =DomainRegistry.getDomainRegistry().getDomains();
            for(Iterator iter = c.iterator();iter.hasNext();){
                ManagedDomain d = (ManagedDomain) iter.next();
                d.shutdownDomain();
            }
        }
        
    public static ServiceItem[] getAllServices(Class cl){
        return cache.lookup(new ClassFilter(cl), 20);
    }

        /*
         * @see net.jini.lookup.ServiceDiscoveryListener#serviceAdded(net.jini.lookup.ServiceDiscoveryEvent)
         */
        public void serviceAdded(ServiceDiscoveryEvent event) {
                // TODO Complete method stub for serviceAdded
        l.finest("Added " + event.getPostEventServiceItem().serviceID  + "(" + event.getPostEventServiceItem().service.getClass() + ")"+ " to kernel cache");
        }

        /*
         * @see net.jini.lookup.ServiceDiscoveryListener#serviceChanged(net.jini.lookup.ServiceDiscoveryEvent)
         */
        public void serviceChanged(ServiceDiscoveryEvent event) {
                // TODO Complete method stub for serviceChanged
        }

        /*
         * @see net.jini.lookup.ServiceDiscoveryListener#serviceRemoved(net.jini.lookup.ServiceDiscoveryEvent)
         */
        public void serviceRemoved(ServiceDiscoveryEvent event) {
                // TODO Complete method stub for serviceRemoved
        }
}
