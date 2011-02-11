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
* neon : org.jini.projects.neon.agents.standard
*
*
* TransferAgentImpl.java
* Created on 15-Aug-2003
*
*/

package org.jini.projects.neon.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.rmi.RemoteException;

import net.jini.core.event.RemoteEventListener;

import org.jini.glyph.Exportable;
import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.AgentState;
import org.jini.projects.neon.agents.LocalAgent;
import org.jini.projects.neon.callbacks.CallbacksAgent;
import org.jini.projects.neon.dynproxy.FacadeProxy;
import org.jini.projects.neon.host.DomainRegistry;
import org.jini.projects.neon.host.ManagedDomain;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.neon.host.PrivilegedAgentContext;
import org.jini.projects.neon.recovery.CheckpointAgent;

/**
* @agents
*
* @author calum
*/
@Exportable
public class TransferAgentImpl extends AbstractAgent implements TransferAgent, LocalAgent {
	/**
 */
	public TransferAgentImpl() {
		super();
		this.name = "Transfer";
		this.namespace = "neon";
		// TODO Complete constructor stub for TransferAgentImpl
	}
	
	/*
	
	/*
	* (non-Javadoc)
 *@see org.jini.projects.neon.agents.Agent#init()
	*/
	public boolean init() {
		// TODO Complete method stub for init
		return true;
	}
		
	
	/*
	* (non-Javadoc)
 *@see org.jini.projects.neon.agents.Agent#stop()
	*/
	public void stop() {
		// TODO Complete method stub for stop
		super.stop();
	}
	
	public void transferAgent(Agent ag) throws RemoteException {
	}
	
	public Boolean transferAgentTo(Agent ag, AgentService host) throws RemoteException{
		System.out.println("Got request to Transfer Agent to a designated agent host.....");
		try {
			CheckpointAgent checkpoint = (CheckpointAgent) context.getAgent("CheckPoint");
			CallbacksAgent callback = (CallbacksAgent) context.getAgent("Callback");
			ag.setAgentState(AgentState.TRANSFER);
			checkpoint.checkpointAgent(ag);
			RemoteEventListener myCallback;
			myCallback = callback.getMyCallback(ag);
			ag.setAgentState(AgentState.TRANSFER);
			host.deployAgent(ag, myCallback);
			System.out.println("Transferred agent  Identity is: " + ag.getIdentity().getID());
			callback.sendCallback(getIdentity(), "Transferred");
			ag.stop();
			PrivilegedAgentContext pc = (PrivilegedAgentContext) getAgentContext();
			try {
				pc.removeAgent(ag);
			} catch (SecurityException e4) {
				// TODO Handle SecurityException
				e4.printStackTrace();
			}
			System.out.println("Returning from transfer request");
			return Boolean.valueOf(true);
		} catch (RemoteException e) {
			System.out.println("Err: " + e.getMessage());
			e.printStackTrace();
			return Boolean.valueOf(false);
		} catch (NoSuchAgentException e1) {
			// TODO Handle NoSuchAgentException
			e1.printStackTrace();
			return Boolean.valueOf(false);
		}
	}
	
	public void moveDomain(Agent ag, String newDomain) throws RemoteException{
		try {
			CheckpointAgent checkpoint = (CheckpointAgent) context.getAgent("CheckPoint");
			CallbacksAgent callback = (CallbacksAgent) context.getAgent("Callback");
			ag.setAgentState(AgentState.TRANSFER);
			checkpoint.checkpointAgent(ag);
			RemoteEventListener myCallback;
			myCallback = callback.getMyCallback(ag);
			ag.setAgentState(AgentState.TRANSFER);
			
			System.out.println("Transferred agent  Identity is: " + ag.getIdentity().getID());
			callback.sendCallback(getIdentity(), "Transferred");
			
			ManagedDomain newDom  = DomainRegistry.getDomainRegistry().getDomain(newDomain);
			System.out.println("MOving agent of type: " + ag.getClass().getName());
			
			newDom.deployAgent(ag, myCallback);
			PrivilegedAgentContext pc = (PrivilegedAgentContext) getAgentContext();
			try {
				pc.removeAgent(ag);
			} catch (SecurityException e4) {
				// TODO Handle SecurityException
				e4.printStackTrace();
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAgentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	* sleep
	* @param aAgent
	*/
	public void freeze(Agent  ag) throws RemoteException{
		try{
			CheckpointAgent checkpoint = (CheckpointAgent) context.getAgent("CheckPoint");
			CallbacksAgent callback = (CallbacksAgent) context.getAgent("Callback");
			ag.setAgentState(AgentState.HIBERNATED);
			checkpoint.checkpointAgent(ag);
			RemoteEventListener myCallback;
			myCallback = callback.getMyCallback(ag);
			ag.setAgentState(AgentState.HIBERNATED);
			System.out.println("Hibernated agent  Identity is: " + ag.getIdentity().getID());
			callback.sendCallback(getIdentity(), "Hibernated");
			ag.stop();
			PrivilegedAgentContext pc = (PrivilegedAgentContext) getAgentContext();
			try {
				pc.removeAgent(ag);
			} catch (SecurityException e4) {
				// TODO Handle SecurityException
				e4.printStackTrace();
			}
			System.out.println("Returning from hibernation request");
		}catch(Exception ex){
			System.err.println("Caught Exception: "+ ex.getClass().getName() + "; Msg: " + ex.getMessage());
			ex.printStackTrace();
		}

	}
	
	/*
	* thaw
	* @param aAgentIdentity
	*/
	public void thaw(AgentIdentity  aAgentIdentity) throws RemoteException{

		
	}

	public void moveDomain(AgentIdentity ag, String newDomain)  throws RemoteException{
		// TODO Auto-generated method stub
		try {
			Agent o = ((ManagedDomain) getAgentContext()).getRegistry().getAgent(ag);
			if (o instanceof Proxy) {
				InvocationHandler h = Proxy.getInvocationHandler(o);
				System.out.println("Proxy Handler: " + h.getClass().getName());
                                if(h instanceof FacadeProxy){
                                    FacadeProxy proxy = (FacadeProxy) h;
                                    o = proxy.getReceiver();
                                    System.out.println("Facade Proxy has receiver of : "  + o.getClass().getName());
                                            
                                }
			}
			System.out.println("Agent is null? " + (o==null));
			moveDomain(o, newDomain);
		} catch (NoSuchAgentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void transferAgent(AgentIdentity ag) throws RemoteException {
		// TODO Auto-generated method stub
		try {
			transferAgent((Agent)getAgentContext().getAgent(ag));
		} catch (NoSuchAgentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Boolean transferAgentTo(AgentIdentity ag, AgentService host) throws RemoteException {
		// TODO Auto-generated method stub
		try {
			//FIXME EXTracted AGents don;t implement Agent use teh AgentRegistry Instead....
			return transferAgentTo((Agent)getAgentContext().getAgent(ag), host);
		} catch (NoSuchAgentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
}
