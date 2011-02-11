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

import net.jini.id.Uuid;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.host.AsyncAgentEnvironment;
import org.jini.projects.neon.host.AsyncHandler;
import org.jini.projects.neon.host.transactions.NonTransactionalResource;
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

public class CollaborationDirectAsyncMethodProxy implements InvocationHandler{
	
	AgentIdentity linkedAgent;
	Agent controller;
	
	HashMap correlatedMessages;
	Object returnObject;
	Logger l = Logger.getLogger("org.jini.projects.neon.dynproxy");
	/**
 */
	public CollaborationDirectAsyncMethodProxy( AgentIdentity linkedAgent, Agent controller) {
		super();
		// URGENT Complete constructor stub for CollaborationProxy
		
		this.linkedAgent = linkedAgent;
		this.controller = controller;

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
		
		if (controller instanceof TransactionalResource){
            l.finest("...trying to Propagate Transaction");
            TransactionalResource res = (TransactionalResource) controller;
            if (res.getTransaction() != null) {
                String txnIdentifier = res.getTransaction();
                l.finest("transaction Propagation Confirmed CP = " + txnIdentifier);
                if(!(controller instanceof NonTransactionalResource)){
                    TransactionalResource theAgentTxnRes = (TransactionalResource) linkedAgent;
                    if(theAgentTxnRes.inTransaction() ){
                        if(!theAgentTxnRes.getTransaction().equals(txnIdentifier))
                            l.severe("Attempting to propagate transaction but called agent is in another txn.");
                    } else
                        theAgentTxnRes.setTransaction(txnIdentifier);
                    
                }
                
            }
		}
        Object[] newArgs = new Object[args.length - 1];
        for (int i = 0; i < newArgs.length; i++)
            newArgs[i] = args[i];
        
        AsyncHandler handler = (AsyncHandler) args[args.length - 1];
		AsyncHandlerProcess asyncProcess = new AsyncHandlerProcess(handler,method, newArgs);
        Thread t = new Thread(asyncProcess);
        t.start();
		return null;
	}
	
    public class AsyncHandlerProcess implements Runnable{
        
        private AsyncHandler returnHandler;
        
        private Method methodToCall;
        private Object[] args;
        public AsyncHandlerProcess(AsyncHandler handler, Method toCall, Object[] args){
            this.returnHandler = handler;
            this.methodToCall = toCall;
            this.args = args;
        }
        
        public void run() {
            // TODO Auto-generated method stub
            Object handback;
            try {
            	
            	Class[] classes = methodToCall.getParameterTypes();
            	Class[] actualParameters = new Class[classes.length -1];
            	for(int i=0;i<actualParameters.length;i++)
            		actualParameters[i] = classes[i];
            	Method actualMethodToCall = controller.getClass().getMethod(methodToCall.getName(), actualParameters);
            	
                handback = actualMethodToCall.invoke(controller, args);
                returnHandler.replied(handback, methodToCall, false);
            } catch (Exception e) {
                // TODO: handle exception
            	e.printStackTrace();
                returnHandler.replied(e, methodToCall, true);
            }            
            
        }
    }
    
    
}

