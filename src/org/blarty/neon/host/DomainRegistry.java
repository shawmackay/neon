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
 * neon : org.jini.projects.neon.host
 *
 *
 * DomainRegistry.java
 * Created on 26-Sep-2003
 *
 */
package org.jini.projects.neon.host;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.core.lookup.ServiceID;

import org.jini.projects.neon.agents.Meta;
import org.jini.projects.neon.host.transactions.TransactionBlackBox;
import org.jini.projects.neon.recovery.AgentKillerImpl;
import org.jini.projects.neon.recovery.CheckpointAgentImpl;
import org.jini.projects.neon.recovery.RecoveryAgent;
import org.jini.projects.neon.service.ServiceAgentImpl;
import org.jini.projects.neon.service.start.DelegateCatalog;
import org.jini.projects.zenith.bus.ApolloBus;
import org.jini.projects.zenith.bus.Bus;
import org.jini.projects.zenith.bus.BusManager;



/**
 * Handles individual agent registries and message buses for domains. Also
 * manages inter-domain access control
 * 
 * @author calum
 */
public class DomainRegistry {
    private static DomainRegistry theRegistry;
    static {
        theRegistry = new DomainRegistry();
    }

    public static void configure(Configuration configuration) {
        config = configuration;
    }

        private Map<String,ManagedDomain> domains = new HashMap<String,ManagedDomain>();

		private ServiceID sid;

		private Configuration serviceConfig;

		private DelegateCatalog defaultDelegateCatalog;

    private static Configuration config;

    /**
     */
    private DomainRegistry() {
        super();
    }

    /**
     * Adds a domain to the list of active domains and sets up any interdomain
     * access control. Also sets up the domain's message bus, transaction
     * management classes, and adds in the <i>AgentKillerImpl</i> agent
     * 
     * @param domain
     *            a new domain instance to be configured
     */
    public void addDomain(ManagedDomain domain) {
        

        setupDomain(domain);
    }

    public void setupDomain(ManagedDomain domain) {
        /*
         * The RegHolder that determines the access rights between domains
         * cannot work out which domain you are adding so for the global domain,
         * we have to revoke calling out to itself Otherwise we will get into a
         * loop
         */
        if (domain.getDomainName().toLowerCase().equals("global"))
        	System.out.println("Ignoring global in ACL");
        	//domains.get("global").getSecurityOptions().getReceiveCallsFromDomains().remove("global");
        else {            
            domains.get("global").getSecurityOptions().getReceiveCallsFromDomains().add(domain.getDomainName());
           
        }
        
        domain.setRegistry(new AgentRegistry(domain.getDomainName()));
        Bus domainBus = new ApolloBus(domain.getDomainName().toLowerCase());
        domain.setBus(domainBus);
        domain.setBusManager(new BusManager(domainBus, new DomainRouter(domain, domainBus), config));
        domains.put(domain.getDomainName().toLowerCase(), domain);
        domain.setTxnBlackBox(new TransactionBlackBox(domain));
        domain.initialise();

        Meta m = new Meta();
        m.addAttribute(new BootstrappedAgent(true));

        AgentKillerImpl killer = new AgentKillerImpl(domain.getRegistry());
        ServiceAgentImpl svcAgent = new ServiceAgentImpl();
        CheckpointAgentImpl checkPoint = new CheckpointAgentImpl();
        RecoveryAgent recovery = new RecoveryAgent();

        try {
            svcAgent.setConfigurationLocation((URL) config.getEntry("org.jini.projects.neon", "bootAgentConfig", URL.class));
            svcAgent.setMetaAttributes(m);
            checkPoint.setConfigurationLocation((URL) config.getEntry("org.jini.projects.neon", "bootAgentConfig", URL.class));
            checkPoint.setMetaAttributes(m);
            recovery.setConfigurationLocation((URL) config.getEntry("org.jini.projects.neon", "bootAgentConfig", URL.class));
            recovery.setMetaAttributes(m);

            killer.setMetaAttributes(m);
           
            domain.deployAgent(svcAgent);
            try {
                Thread.sleep(10*1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            domain.deployAgent(checkPoint);
            
            domain.deployAgent(recovery);
            domain.deployAgent(killer);
        } catch (ConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Adds a domain to the list of active domains and sets up any interdomain
     * access control. Also sets up the domain's message bus, transaction
     * management classes, and adds in the <i>AgentKillerImpl</i> agent
     * 
     * @param domain
     *            a new domain instance to be configured
     * @param allowIn
     *            a list of domains that can send messages into this domain
     * @param allowOut
     *            a list of domains that we can send messages to
     */
    public void addDomain(ManagedDomain domain, List allowIn, List allowOut) {
    	System.out.println("Calling wrong addDomain Settings");
        /*
         * The RegHolder that determines the access rights between domains
         * cannot work out which domain you are adding so for the global domain,
         * we have to revoke calling out to itself Otherwise we will get into a
         * loop
         */
        // if (domain.getDomainName().toLowerCase().equals("global"))
        // domainAccess.revokeDomainOut("global");
        // domainACL.put(domain.getDomainName().toLowerCase(), domainAccess);
        // domain.setRegistry(domainAccess.getAgentRegistry());
        // Bus domainBus = new ApolloBus(domain.getDomainName().toLowerCase());
        // domain.setBus(domainBus);
        // domain.setBusManager(new BusManager(domainBus, new
        // DomainRouter(domain, domainBus), config));
        // domains.put(domain.getDomainName().toLowerCase(), domain);
        // domain.setTxnBlackBox(new TransactionBlackBox(domain));
        // domain.initialise();
        // domain.deployAgent(new
        // AgentKillerImpl(domainAccess.getAgentRegistry()));
        setupDomain(domain);
    }

    /**
     * Obtain the domain with the given name
     * 
     * @param domainName
     *            name of the required domain
     * @return the domain instance
     */
    public ManagedDomain getDomain(String domainName) {
        if (domains.containsKey(domainName.toLowerCase()))
            return (ManagedDomain) domains.get(domainName.toLowerCase());
        else
            return null;
    }

    /**
     */

    public List<String> getAllowedDomainsIn(String domainName) {
    	ManagedDomain acl = (ManagedDomain) domains.get(domainName.toLowerCase());
        return acl.getSecurityOptions().getReceiveCallsFromDomains();
        
    }
    
    

    /**
     * Get the list of domains that this domain can send messages to
     * 
     * @param domainName
     * @return the allowed domains
     */
    public List<String> getAllowedDomainsOut(String domainName) {
        ManagedDomain acl = (ManagedDomain) domains.get(domainName.toLowerCase());
        return acl.getSecurityOptions().getSendCallsToDomains();
    }

    /**
     * Get the registered domains
     * 
     * @return the list of domains
     */
    public Collection getDomains() {
        return this.domains.values();
    }

    /**
     * Get a singleton reference to the domain Registry
     * 
     * @return
     */
    public static DomainRegistry getDomainRegistry() {
        return theRegistry;
    }

//    private class RegHolder {
//        private AgentRegistry agentRegistry;
//
//        private List allowedDomainsIn;
//
//        private List allowedDomainsOut;
//        
//        private List allowedClasses;
//        
//        private List allowedNamespaces;
//
//        public RegHolder(String name) {
//            agentRegistry = new AgentRegistry(name);
//            allowedDomainsIn = new ArrayList();
//            allowedDomainsOut = new ArrayList();
//            allowedClasses = new ArrayList();
//            allowedNamespaces = new ArrayList();
//            allowedDomainsOut.add("global");
//        }
//
//        public RegHolder(List allowDomainsIn, List allowDomainsOut, String name) {
//            agentRegistry = new AgentRegistry(name);
//            allowedDomainsIn = allowDomainsIn;
//            allowedDomainsOut = allowDomainsOut;
//            if (!allowedDomainsOut.contains("global"))
//                allowedDomainsOut.add("global");
//        }
//
//        public boolean canCallOutTo(String domainName) {
//            return allowedDomainsOut.contains(domainName);
//        }
//
//        public boolean allowsCallsInFrom(String domainName) {
//            return allowedDomainsIn.contains(domainName);
//        }
//
//        public Bus getBusForDomain(String name) {
//            if (domains.containsKey(name)) {
//                ManagedDomain d = (ManagedDomain) domains.get(name);
//                return d.getBus();
//            }
//            return null;
//        }
//
//        public void addDomainIn(String name) {
//            allowedDomainsIn.add(name);
//        }
//
//        public void addDomainOut(String name) {
//            allowedDomainsOut.add(name);
//        }
//
//        public void revokeDomainIn(String name) {
//            allowedDomainsIn.remove(name);
//        }
//
//        public void revokeDomainOut(String name) {
//            allowedDomainsOut.remove(name);
//        }
//
//        public AgentRegistry getAgentRegistry() {
//            return agentRegistry;
//        }
//
//        /**
//         * @return
//         */
//        public List getAllowedDomainsIn() {
//            return allowedDomainsIn;
//        }
//
//        /**
//         * @return
//         */
//        public List getAllowedDomainsOut() {
//            return allowedDomainsOut;
//        }
//
//    }

    
    	public void setDomainServiceID(ServiceID sid){
    		this.sid = sid;
    	}
    	
    	public void setServiceConfig(Configuration config){
    		this.serviceConfig = config;
    	}

		public ServiceID getSid() {
			return sid;
		}

		public void setSid(ServiceID sid) {
			this.sid = sid;
		}

		public Configuration getServiceConfig() {
			return serviceConfig;
		}

		public DelegateCatalog getDefaultDelegateCatalog() {
			// TODO Auto-generated method stub
			return this.defaultDelegateCatalog;
		}
		
		public void setDefaultDelegateCatalog(DelegateCatalog defaultDelegates){
			this.defaultDelegateCatalog = defaultDelegates;
		}
		
		public void removeDomain(String domainName){
			domains.remove(domainName);
		}
}
