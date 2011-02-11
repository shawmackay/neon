package org.jini.projects.neon.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import net.jini.core.entry.Entry;
import net.jini.core.event.RemoteEventListener;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.dynproxy.AgentProxyInfo;
import org.jini.projects.neon.dynproxy.MethodInvocation;
import org.jini.projects.neon.host.AgentDomain;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.neon.service.AgentService;
import org.jini.projects.zenith.messaging.messages.Message;

public class MockAgentService implements AgentService {

	public void deployAgent(Agent agent) throws RemoteException {
		// TODO Auto-generated method stub
		deployAgent(agent, (RemoteEventListener)null);
	}

	public void deployAgent(Agent agent, RemoteEventListener callback) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("Deploying an agent of type: " + agent.getClass().getName());
	}

	public void deployPOJOAgent(Object POJO, String constraintsLocation, String configurationLocation) throws MalformedURLException, RemoteException {
		// TODO Auto-generated method stub
		System.out.println("Deploying a POJO agent of type: " + POJO.getClass().getName());
	}

	public void deploySeeding(URL seedFromLocation) throws RemoteException {
		// TODO Auto-generated method stub

	}

	public AgentProxyInfo getAgentProxyInfo(String name, String domain) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public AgentProxyInfo getAgentProxyInfo(String name, Entry[] meta, String domain) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public AgentProxyInfo getAgentProxyInfo(AgentIdentity id, String domain) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getStatelessAgent(String name, String domain) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object sendMessage(String agentName, String domain, Message message) throws NoSuchAgentException, RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAgentHost(AgentDomain host) throws RemoteException {
		// TODO Auto-generated method stub

	}

	public Object getAdmin() throws RemoteException {
		// TODO Auto-generated method stub
		return new MockAgentAdmin();
	}

	public void createAgent(String classname, String constraintsurl, String configurl, String domain) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("Creating an agent of type " + classname + " at server");
	}

	public void deployAgent(Agent agent, String domain) throws RemoteException {
		
		System.out.println("Deploying an agent of type: " + agent.getClass().getName() + " in " + domain);
	}

	public void deployAgent(Agent agent, String domain, RemoteEventListener callback) throws RemoteException {
		
		System.out.println("Deploying an agent of type: " + agent.getClass().getName() + " in " + domain);
	}

	public void deployPOJOAgent(Object POJO, String constraintsLocation, String configurationLocation, String domain) throws MalformedURLException, RemoteException {
		System.out.println("Deploying an object of type: " + POJO.getClass().getName() + " in " + domain);
	}

	
	public Object invoke(AgentIdentity identity, MethodInvocation invocation) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	

}
