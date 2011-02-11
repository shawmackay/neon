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
package org.jini.projects.neon.dynproxy;

/*
* CollaborationAsyncProxy.java
*
* Created Tue Apr 12 15:08:52 BST 2005
*/
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.logging.Logger;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.host.AsyncAgentEnvironment;
import org.jini.projects.neon.host.AsyncHandler;
import org.jini.projects.neon.host.transactions.TransactionalResource;
import org.jini.projects.neon.util.GenericsMethodViewer;
import org.jini.projects.neon.util.MethodViewer;
import org.jini.projects.zenith.messaging.messages.InvocationMessage;
import org.jini.projects.zenith.messaging.messages.Message;
import org.jini.projects.zenith.messaging.messages.MessageHeader;
import org.jini.projects.zenith.messaging.system.MessagingListener;
import org.jini.projects.zenith.messaging.system.MessagingManager;

/**
* Creates an <i>'ad-hoc'</i> Asynchronous interface invocation handler.
* @author  calum
*
*/

public class CollaborationAsyncProxy implements InvocationHandler, MessagingListener {
	AsyncAgentEnvironment ctx;
	AgentIdentity linkedAgent;
	Agent controller;
	AsyncHandler contextHandler;
	HashMap correlatedMessages;
	Object returnObject;
	Logger l = Logger.getLogger("org.jini.projects.neon.dynproxy");
	/**
 */
	public CollaborationAsyncProxy(AsyncAgentEnvironment ctx, AgentIdentity linkedAgent, Agent controller, AsyncHandler contextHandler) {
		super();
		// URGENT Complete constructor stub for CollaborationProxy
		this.ctx = ctx;
		this.linkedAgent = linkedAgent;
		this.controller = controller;
		this.contextHandler= contextHandler;
		this.correlatedMessages= new HashMap();
	}
	
	public AgentIdentity getIdentity(){
		return linkedAgent;
	}
	
	/*
	* @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
	*           java.lang.reflect.Method, java.lang.Object[])
	*/
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// TODO Complete method stub for invoke
		l.fine("Calling proxy " + controller.getNamespace() + "." + controller.getName() +"["+controller.getIdentity()+"]");
		MessageHeader header = new MessageHeader();
		MessagingManager.getManager().registerOnChannel("reply"+controller.getIdentity().getID().toString(), this);
		if (controller instanceof TransactionalResource){
			l.finest("...trying to Propagate Transaction");
			TransactionalResource res = (TransactionalResource) controller;
			if(res.getTransaction()!=null){
				String txnIdentifier = res.getTransaction();
				l.finest("transaction Propagation Confirmed CP = " + txnIdentifier);
				header.setArbitraryField("txnID", txnIdentifier);
				
			}
		}
		
		header.setReplyAddress("reply"+controller.getIdentity().getID().toString());
		header.setDestinationAddress(linkedAgent.getID().toString());
		//We don't use guaranteed messaging for method invocations.
		header.setGuaranteed(false);
		Message m = new InvocationMessage(header, method.getName(), args,GenericsMethodViewer.getMethodShortGenericString(method));
		ctx.sendAsyncMessage(linkedAgent.getID(),m);
		correlatedMessages.put(m.getHeader().getRequestID(),m);
		l.finest("Message sent async");
		return null;
	}
	
    
    
	/*
	* @see org.jini.projects.zenith.messaging.system.MessagingListener#messageReceived(org.jini.projects.zenith.messaging.messages.Message)
	*/
	public void messageReceived(Message m) {
		// TODO Complete method stub for messageReceived
		Object o = m.getMessageContent();
		returnObject = o;
		l.finest("Reply Message received");
		synchronized (this) {
			try {
				//contextHandler.replied(o, (Message) correlatedMessages.get(m.getHeader().getCorrelationID()), o instanceof Throwable);
				correlatedMessages.remove(m.getHeader().getCorrelationID());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}

