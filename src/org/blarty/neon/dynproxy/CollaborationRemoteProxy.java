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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.logging.Logger;

import net.jini.id.UuidFactory;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.host.AsyncAgentEnvironment;
import org.jini.projects.neon.host.ManagedDomain;
import org.jini.projects.neon.host.PrivilegedAgentContext;
import org.jini.projects.neon.host.PrivilegedAsyncAgentEnvironment;
import org.jini.projects.neon.host.transactions.NonTransactionalResource;
import org.jini.projects.neon.host.transactions.RemoteTransactionalResource;
import org.jini.projects.neon.host.transactions.TransactionAgent;
import org.jini.projects.neon.host.transactions.TransactionalResource;
import org.jini.projects.neon.util.GenericsMethodViewer;
import org.jini.projects.neon.util.MethodViewer;
import org.jini.projects.zenith.messaging.messages.ExceptionMessage;
import org.jini.projects.zenith.messaging.messages.InvocationMessage;
import org.jini.projects.zenith.messaging.messages.Message;
import org.jini.projects.zenith.messaging.messages.MessageHeader;
import org.jini.projects.zenith.messaging.system.MessageAudit;
import org.jini.projects.zenith.messaging.system.MessagingListener;
import org.jini.projects.zenith.messaging.system.MessagingManager;

/**
 * Dynamic Proxy class that represents an agent during collaboration and works
 * on behalf of the agent. Each call is blocking and hence synchronous
 * 
 * @author calum
 */

public class CollaborationRemoteProxy implements InvocationHandler {
        AsyncAgentEnvironment ctx;

        AgentIdentity linkedAgent;

        Agent controller;

        String name;
        
        Object linkObject;

        Logger l = Logger.getLogger("org.jini.projects.neon.dynproxy");

        boolean notified = false;

        public boolean thrownException = false;

        /**
         * 
         */
        public CollaborationRemoteProxy(AsyncAgentEnvironment ctx, AgentProxyInfo linkedAgent, Agent controller) {
                super();
                // URGENT Complete constructor stub for CollaborationProxy
                this.ctx = ctx;
                this.linkedAgent = linkedAgent.getId();
               //  this.linkObject = linkedAgent.getRemoteAgent();
                 this.name=linkedAgent.getAgentName();
                this.controller = controller;
        }

        public AgentIdentity getIdentity() {
                return linkedAgent;
        }

        /*
         * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
         *      java.lang.reflect.Method, java.lang.Object[])
         */
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // TODO Complete method stub for invoke
//        	if (controller instanceof RemoteTransactionalResource) {
//                l.finest("...trying to Propagate Transaction");
//                RemoteTransactionalResource res = (RemoteTransactionalResource) controller;
//                if (res.getRemoteTransaction() != null) {
//                    String txnIdentifier = res.getRemoteTransaction();
//                    l.finest("Remote transaction Propagation Confirmed CP = " + txnIdentifier);
//                    Class[] intfs = linkObject.getClass().getInterfaces();
//                    System.out.println("LinkObject for the transaction implementing:");
//                    for(Class agClass: intfs){
//                    	System.out.println("\t" + agClass.getName());
//                    }
//                    if(!(linkObject instanceof NonTransactionalResource)){
//                        RemoteTransactionalResource theAgentTxnRes = (RemoteTransactionalResource) linkObject;
//                        if(theAgentTxnRes.inRemoteTransaction() ){
//                            if(!theAgentTxnRes.getRemoteTransaction().equals(txnIdentifier))
//                                l.severe("Attempting to propagate transaction remotely but called agent is in another txn.");
//                        } else{
//                        	String[] parts = txnIdentifier.split(":");
//                        	String txnAgent = parts[0];
//                        	TransactionAgent tx = (TransactionAgent) ((PrivilegedAsyncAgentEnvironment) ctx).getAgent(new AgentIdentity(txnAgent),null);
//                        	l.info("Obtained Transaction Agent " + txnAgent + " attempting attach");
//                            theAgentTxnRes.attachRemoteTransaction(txnIdentifier, tx.getTransactionFor(UuidFactory.create(parts[1])));
//                            l.info("Attach successful");
//                        }
//                        
//                    } else {
//                    	System.out.println("The LinkedObject (" + this.name + ") implements Nontransactional Resource");                    	
//                    }
//                    
//                    
//                }
//                else
//                	l.info(" It appears that the controller: " + controller.getNamespace() + "." + controller.getName() + " is notin a transaction!");
//        	}
        	throw new UnsupportedOperationException("This proxy should not be used");
                //return method.invoke(linkObject, args);
        }

        private Throwable extractAppropriateException(Throwable parent, Throwable e, Class[] exceptionTypes) {
                if (e instanceof InvocationTargetException)
                        return extractAppropriateException(e, e.getCause(), exceptionTypes);
                if (e instanceof UndeclaredThrowableException)
                        return extractAppropriateException(e, e.getCause(), exceptionTypes);
                for (int i = 0; i < exceptionTypes.length; i++) {
                        // System.out.println("\t\tChecking exception: " +
                        // exceptionTypes[i].getName());
                        if (exceptionTypes[i].isInstance(e)) {
                                // System.out.println("More app. Exception is :
                                // " + e.getClass().getName());
                                return e;
                        }
                }
                return null;
        }

        public String toString() {
                return "CollaborationRemoteProxy for Agent: " + controller.getNamespace() + "." + controller.getName() + ":[" + controller.getIdentity().getID() + "]";

        }

        public Object getRemoteAgent(){
        	return linkObject;
        }
        
}
