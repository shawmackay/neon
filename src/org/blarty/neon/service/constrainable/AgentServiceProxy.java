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


package org.jini.projects.neon.service.constrainable;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import net.jini.admin.Administrable;
import net.jini.core.constraint.MethodConstraints;
import net.jini.core.constraint.RemoteMethodControl;
import net.jini.core.entry.Entry;
import net.jini.core.event.RemoteEventListener;
import net.jini.id.ReferentUuids;
import net.jini.id.Uuid;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.dynproxy.AgentProxyInfo;
import org.jini.projects.neon.dynproxy.MethodInvocation;
import org.jini.projects.neon.host.AgentDomain;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.neon.service.AgentBackendService;
import org.jini.projects.neon.service.AgentService;
import org.jini.projects.neon.service.CodebaseObject;
import org.jini.projects.zenith.messaging.messages.Message;




/**
 * Smart Proxy for the agent service. 
 */
public class AgentServiceProxy implements AgentService, Administrable, Serializable{
    private static final long serialVersionUID = 2L;
	transient Logger l = Logger.getLogger("org.jini.projects.neon.service");
	
    final AgentBackendService backend;    
    final Uuid proxyID;



    public void deploySeeding(URL seedFromLocation) throws RemoteException {
		// TODO Complete method stub for deploySlice
		
	}

public AgentProxyInfo getAgentProxyInfo(AgentIdentity id, String domain) throws RemoteException {
	// TODO Auto-generated method stub
	return backend.getAgentProxyInfo(id, domain);
}


	public Object getStatelessAgent(String name, String domain) throws RemoteException {
		// TODO Complete method stub for getStatelessAgent
		return backend.getExportableAgent(name,domain);
	}




	public static AgentServiceProxy create(AgentBackendService server, Uuid id) {
        if (server instanceof RemoteMethodControl) {
			
            return new AgentServiceProxy.ConstrainableProxy(server,  id, null);
        }
        else
            return new AgentServiceProxy(server,  id);
    }




    private AgentServiceProxy(AgentBackendService backend,  Uuid proxyID) {
        this.backend = backend;
        this.proxyID = proxyID;
        
    }

    public Uuid getReferentUuid() {
        return proxyID;
    }

     /** Proxies for servers with the same proxyID have the same hash code. */
    public int hashCode() {
        return proxyID.hashCode();
    }

    /**
     * Proxies for servers with the same <code>proxyID</code> are
     * considered equal.
     */
    public boolean equals(Object o) {
        return ReferentUuids.compare(this, o);
    }
    

    public void deployAgent(Agent a) throws RemoteException {
        
        try {
			backend.deployAgent(a.getConstraints(),new CodebaseObject(a));
		} catch (RemoteException e) {
			// URGENT Handle RemoteException
			
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			// URGENT Handle IOException
			e.printStackTrace();
		}
    }

    public void deployAgent(Agent a, RemoteEventListener callback) throws RemoteException {
        try {
			backend.deployAgent(a.getConstraints(),new CodebaseObject(a),callback);
		} catch (RemoteException e) {
			// URGENT Handle RemoteException
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			// URGENT Handle IOException
			e.printStackTrace();
		}
    }

    public void setAgentHost(AgentDomain host) throws RemoteException {
        backend.setAgentHost(host);
    }

    public Object sendMessage(String agentName, String domain, Message message) throws RemoteException {

        try {
            return backend.sendMessage(agentName, domain, message);
        } catch (NoSuchAgentException e) {
			//l.warning("Err: Agent " + agentName + " does not exist on this host - " + e.getMessage());            
            System.out.println("Err: Agent " + agentName + " does not exist on this host - " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    final static class ConstrainableProxy extends AgentServiceProxy implements RemoteMethodControl{
		private static final long serialVersionUID = 4L;
        private ConstrainableProxy(AgentBackendService server, Uuid id, MethodConstraints methodConstraints) {
                    super(constrainServer(server,methodConstraints), id);
					l.fine("Creating a secure proxy");
                }
         public RemoteMethodControl setConstraints(MethodConstraints constraints)
        {
            return new AgentServiceProxy.ConstrainableProxy(backend, proxyID,
                constraints);
        }

        /** {@inheritDoc} */
        public MethodConstraints getConstraints() {
            return ((RemoteMethodControl) backend).getConstraints();
        }


         private static AgentBackendService constrainServer(AgentBackendService server, MethodConstraints methodConstraints)
        {
            return (AgentBackendService)
                ((RemoteMethodControl)server).setConstraints(methodConstraints);
        }
    }
	/* (non-Javadoc)
	 * @see net.jini.admin.Administrable#getAdmin()
	 */
	public Object getAdmin() throws RemoteException {
		// TODO Complete method stub for getAdmin						
		try {
			return backend.getAdmin();
		} catch (RemoteException e) {
			// TODO Handle RemoteException
			e.printStackTrace();
		}
		return null;
	}
	/* @see org.jini.projects.neon.service.AgentService#getAgent(java.lang.String)
	 */
	public AgentProxyInfo getAgentProxyInfo(String name, String domain) throws RemoteException {
		// TODO Complete method stub for getAgent
		return backend.getAgentProxyInfo(name, domain);
	}
    
    public AgentProxyInfo getAgentProxyInfo(String name, Entry[] meta, String domain) throws RemoteException {
        // TODO Complete method stub for getAgent
        return backend.getAgentProxyInfo(name, meta, domain);
    }




public void deployPOJOAgent(Object POJO, String constraintsLocation, String configurationLocation) throws MalformedURLException,RemoteException {
        backend.deployPOJOAgent(POJO, constraintsLocation, configurationLocation);
}

public void createAgent(String classname, String constraintsurl, String configurl, String domain) throws RemoteException {
	backend.createAgent(classname, constraintsurl, configurl, domain);
}

public void deployAgent(Agent agent, String domain) throws RemoteException {
	try {
		System.out.println("Deploying an agent in the " + domain +" domain");
		backend.deployAgent(agent.getConstraints(),new CodebaseObject(agent), domain);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

public void deployAgent(Agent agent, String domain, RemoteEventListener callback) throws RemoteException {
	try {
		System.out.println("Deploying an agent in the " + domain +" domain");
		backend.deployAgent(agent.getConstraints(),new CodebaseObject(agent), domain, callback);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

public void deployPOJOAgent(Object POJO, String constraintsLocation, String configurationLocation, String domain) throws MalformedURLException, RemoteException {
	backend.deployPOJOAgent(POJO, constraintsLocation, configurationLocation, domain);
}

@Override
public Object invoke(AgentIdentity identity, MethodInvocation invocation) throws RemoteException {
	return backend.invoke(identity, invocation);
}
    


}
