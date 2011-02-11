package org.jini.projects.neon.ui;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.jini.core.discovery.LookupLocator;
import net.jini.core.entry.Entry;

import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.AgentState;
import org.jini.projects.neon.service.admin.AgentAdmin;
import org.jini.projects.neon.service.admin.AgentDescription;
import org.jini.projects.neon.service.admin.AgentServiceAdmin;
import org.jini.projects.neon.service.admin.DomainDescription;
import org.jini.projects.neon.service.admin.MessageDescription;



public class MockAgentAdmin implements AgentServiceAdmin {
	
	private Map<String,DomainDescription> domainDescs;
	private String globalConfigFile = "conf/neon/config";
	private int maxAgentsOfAncestor = 30;
	private int maxAgentsOfType = 10;
	private boolean isUsingThreadGroups = false;
	
	public MockAgentAdmin(){
		List globalIn = new ArrayList();
		List globalOut = new ArrayList();
		List testDomainIn = new ArrayList();
		List testDomainOut = new ArrayList();
		List persistDomainIn = new ArrayList();
		List persistDomainOut = new ArrayList();
		List secureDomainIn = new ArrayList();
		
		List secureDomainOut = new ArrayList();
		
		globalIn.add("testDomain");
		globalIn.add("Persistence");
		globalIn.add("Secure");
		
		testDomainOut.add("Global");
		persistDomainIn.add("testDomain");
		persistDomainIn.add("Secure");
		persistDomainOut.add("Global");
		secureDomainOut.add("Global");
		
		DomainDescription global = new DomainDescription("Global","blah1",null,globalIn,globalOut);
		DomainDescription test = new DomainDescription("testDomain","blah1",null,testDomainIn,testDomainOut);
		DomainDescription persist = new DomainDescription("Persistence","blah1",null,persistDomainIn,persistDomainOut);
		DomainDescription secure = new DomainDescription("Secure","blah1",null,secureDomainIn,secureDomainOut);
		domainDescs = new TreeMap<String,DomainDescription>();
		domainDescs.put(global.getName(),global);
		domainDescs.put(test.getName(), test);
		domainDescs.put(persist.getName(), persist);
		domainDescs.put(secure.getName(),secure);
		MessageDescription msgDesc = new MessageDescription("sayHello",null, null);
		AgentDescription agentDesc  = new AgentDescription("mock.simple.Agent",new AgentIdentity(), new MessageDescription[] {msgDesc},AgentState.AVAILABLE, null);
		AgentDescription agentDesc2  = new AgentDescription("mock.simple.Agent",new AgentIdentity(), new MessageDescription[] {msgDesc},AgentState.AVAILABLE, null);
		AgentDescription agentDesc3  = new AgentDescription("mock.simple.Agent",new AgentIdentity(), new MessageDescription[] {msgDesc},AgentState.AVAILABLE, null);
		AgentDescription agentDesc4  = new AgentDescription("mock.simple.Agent",new AgentIdentity(), new MessageDescription[] {msgDesc},AgentState.AVAILABLE, null);

		global.setAgents(new AgentDescription[]{agentDesc});
		test.setAgents(new AgentDescription[]{agentDesc2});
		persist.setAgents(new AgentDescription[]{agentDesc3});
		secure.setAgents(new AgentDescription[]{agentDesc4});
	}

	public boolean containsAgent(AgentIdentity id) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	public DomainDescription[] getDomains() throws RemoteException {
		// TODO Auto-generated method stub
		return (DomainDescription[])domainDescs.values().toArray(new DomainDescription[]{});
	}

	public String getGlobalConfigFile() throws RemoteException {
		// TODO Auto-generated method stub
		return globalConfigFile;
	}

	public int getMaxAgentsOfAncestor() throws RemoteException {
		// TODO Auto-generated method stub
		return maxAgentsOfAncestor;
	}

	public int getMaxAgentsOfType() throws RemoteException {
		// TODO Auto-generated method stub
		return maxAgentsOfType;
	}

	public boolean isUsingThreadGroups() throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean packAgent(AgentIdentity id) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void createDomain(String domainName){
		domainDescs.put(domainName,new DomainDescription(domainName,"blah",null,new ArrayList(),new ArrayList()));
	}

	public void removeDomain(String domainName){
		domainDescs.remove(domainName);
	}
	
	

	public void updateDomainDescription(DomainDescription desc) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("Updating domain: " + desc.getName());
		domainDescs.put(desc.getName(),desc);
	}

	public void destroy() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	public void addLookupAttributes(Entry[] attrSets) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	public void addLookupGroups(String[] groups) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	public void addLookupLocators(LookupLocator[] locators) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	public Entry[] getLookupAttributes() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getLookupGroups() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public LookupLocator[] getLookupLocators() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void modifyLookupAttributes(Entry[] attrSetTemplates, Entry[] attrSets) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	public void removeLookupGroups(String[] groups) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	public void removeLookupLocators(LookupLocator[] locators) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	public void setLookupGroups(String[] groups) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	public void setLookupLocators(LookupLocator[] locators) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
}
