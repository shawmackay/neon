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
 * neon : org.jini.projects.neon.service.admin
 *n * AgentAdminImpl.java
 * Created on 22-Sep-2003
 */
package org.jini.projects.neon.service.admin;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.activation.ActivationID;
import java.rmi.activation.ActivationSystem;
import java.util.Collection;
import java.util.Iterator;

import net.jini.activation.ActivationGroup;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.entry.Entry;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.JoinManager;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.host.AgentDomainImpl;
import org.jini.projects.neon.host.AgentRegistry;
import org.jini.projects.neon.host.DomainRegistry;
import org.jini.projects.neon.host.ManagedDomain;
import org.jini.projects.neon.host.SecurityOptions;
import org.jini.projects.neon.kernel.Kernel;
import org.jini.projects.neon.service.start.DomainConfig;

/**
 * @author calum
 */
public class AgentAdminImpl implements AgentServiceAdmin {
    LookupDiscoveryManager ldm;

    JoinManager jm;

    private boolean term = false;

    private ActivationID theID;

    public AgentAdminImpl(JoinManager jm, LookupDiscoveryManager ldm, ActivationID theID) throws RemoteException {
        this.jm = jm;
        this.ldm = ldm;
        this.theID = theID;

    }

    public void destroy() throws RemoteException {
        Thread termThread = new Thread(new Terminationthread());
        termThread.setDaemon(false);
        termThread.start();
    }

    private class Terminationthread extends Thread {
        /**
         * Main processing method for the termthread object
         * 
         * @since
         */

        public void run() {

            if (System.getProperty("debug") != null)
                System.setProperty("terminating", "true");
            System.err.println("Shutting down domains");
            Kernel.shutdown();
            System.err.println("Terminating - checking activation ID");
            
            if (theID != null) {
                try {
                    ActivationSystem sys = ActivationGroup.getSystem();
                    System.out.println("Deregistering from activation system");
                    sys.unregisterObject(theID);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                
                System.out.println("Terminating join managers");
                try {
                    ldm.terminate();
                    jm.terminate();                   
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else
                System.exit(0);
        }
    }

    public void addLookupAttributes(Entry[] entries) throws RemoteException {
        jm.addAttributes(entries, true);
    }

    public void addLookupGroups(String[] strings) throws RemoteException {
        try {
            ldm.addGroups(strings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addLookupLocators(LookupLocator[] lookupLocators) throws RemoteException {
        ldm.addLocators(lookupLocators);
    }

    public Entry[] getLookupAttributes() throws RemoteException {
        return jm.getAttributes();

    }

    public String[] getLookupGroups() throws RemoteException {
        return ldm.getGroups();
    }

    public LookupLocator[] getLookupLocators() throws RemoteException {
        return ldm.getLocators();
    }

    public void modifyLookupAttributes(Entry[] entries, Entry[] entries1) throws RemoteException {
        jm.modifyAttributes(entries, entries1, true);
    }

    public void removeLookupGroups(String[] strings) throws RemoteException {
        ldm.removeGroups(strings);
    }

    public void removeLookupLocators(LookupLocator[] lookupLocators) throws RemoteException {
        ldm.removeLocators(lookupLocators);
    }

    public void setLookupGroups(String[] strings) throws RemoteException {
        try {
            ldm.setGroups(strings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLookupLocators(LookupLocator[] lookupLocators) throws RemoteException {
        ldm.setLocators(lookupLocators);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jini.projects.neon.service.admin.AgentAdmin#getDomains()
     */
    public DomainDescription[] getDomains() throws RemoteException {
        // TODO Complete method stub for getDomains
    	System.out.println("Getting domain & agent information");
        DomainRegistry domainreg = DomainRegistry.getDomainRegistry();
        Collection c = domainreg.getDomains();
        DomainDescription[] descs = new DomainDescription[c.size()];
        int descsLoop = 0;
        for (Iterator iter = c.iterator(); iter.hasNext();) {
            ManagedDomain md = (ManagedDomain) iter.next();
                 
                    
            AgentRegistry reg = md.getRegistry();
            Agent[] agents = reg.getAllAgents();
            AgentDescription[] agentDescriptions = new AgentDescription[agents.length];
            for (int i = 0; i < agents.length; i++) {
                agentDescriptions[i] = new AgentDescription(agents[i].getNamespace() + "." + agents[i].getName(), agents[i].getIdentity(), null, agents[i].getAgentState(), agents[i].getSecondaryState());
            }
            descs[descsLoop++] = new DomainDescription(md.getDomainName(), "<not implemented>", agentDescriptions,md.getSecurityOptions().getReceiveCallsFromDomains(), md.getSecurityOptions().getSendCallsToDomains());
        }
        return descs;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jini.projects.neon.service.admin.AgentAdmin#getGlobalConfigFile()
     */
    public String getGlobalConfigFile() throws RemoteException {
        // TODO Complete method stub for getGlobalConfigFile
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jini.projects.neon.service.admin.AgentAdmin#isUsingThreadGroups()
     */
    public boolean isUsingThreadGroups() throws RemoteException {
        // TODO Complete method stub for isUsingThreadGroups
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jini.projects.neon.service.admin.AgentAdmin#getMaxAgentsOfType()
     */
    public int getMaxAgentsOfType() throws RemoteException {
        // TODO Complete method stub for getMaxAgentsOfType
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jini.projects.neon.service.admin.AgentAdmin#getMaxAgentsOfAncestor()
     */
    public int getMaxAgentsOfAncestor() throws RemoteException {
        // TODO Complete method stub for getMaxAgentsOfAncestor
        return 0;
    }

    public boolean containsAgent(AgentIdentity id) throws RemoteException {
        DomainRegistry reg = DomainRegistry.getDomainRegistry();
        Collection<ManagedDomain> domains = reg.getDomains();
        for (ManagedDomain dom : domains) {
            if (dom.getRegistry().contains(id))
                return true;
        }
        return false;
    }

    public boolean packAgent(AgentIdentity id) throws RemoteException {
        // TODO Complete method stub for packAgent
        return false;
    }

	public void updateDomainDescription(DomainDescription desc) throws RemoteException {
		// TODO Auto-generated method stub
		 DomainRegistry domainreg = DomainRegistry.getDomainRegistry();
		
		ManagedDomain dom = domainreg.getDomain(desc.getName());
		if(dom!=null){
			
			SecurityOptions options = dom.getSecurityOptions();
			options.setAllowedClasses(desc.getAllowedClasses());
			options.setAllowedNamespaces(desc.getAllowedNamespaces());
			options.setReceiveCallsFromDomains(desc.getDomainsIn());
			options.setSendCallsToDomains(desc.getDomainsOut());
			options.setSecurityLevel(desc.getSecurityLevel());
			options.setFailIfEncryptionAvailable(desc.isFailIfEncryptionAvailable());
			options.setEncryptAgentStorage(desc.isEncryptAgentStorage());
			options.setEncryptDomainInfo(desc.isEncryptDomainInfo());
			options.setEncryptOtherData(desc.isEncryptOtherData());
		} else {
			DomainConfig domConfig = new DomainConfig(desc.getName());
			domConfig.setAllowedClasses(desc.getAllowedClasses());
			domConfig.setAllowedNamespaces(desc.getAllowedNamespaces());
			domConfig.setEncryptAgentStorage(desc.isEncryptAgentStorage());
			domConfig.setEncryptDomainInfo(desc.isEncryptDomainInfo());
			domConfig.setEncryptOtherData(desc.isEncryptOtherData());
			domConfig.setFailIfEncryptionAvailable(desc.isFailIfEncryptionAvailable());
			domConfig.setReceiveCallsFromDomains(desc.getDomainsIn());
			domConfig.setSendCallsToDomains(desc.getDomainsOut());
			domConfig.setSecurityLevel(desc.getSecurityLevel());
			domConfig.setDelegateCatalog(domainreg.getDefaultDelegateCatalog());
			ManagedDomain agdom = new AgentDomainImpl(desc.getName(), domainreg.getSid(), domConfig,domainreg.getServiceConfig());
			domainreg.addDomain(agdom);
		}
	}

	public void removeDomain(String name) throws RemoteException {
		// TODO Auto-generated method stub
		ManagedDomain domain = DomainRegistry.getDomainRegistry().getDomain(name);
		domain.shutdownDomain();
		DomainRegistry.getDomainRegistry().removeDomain(name);
	}

}
