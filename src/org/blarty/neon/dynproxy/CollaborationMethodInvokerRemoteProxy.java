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

import net.jini.core.transaction.Transaction;
import net.jini.id.UuidFactory;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.Meta;
import org.jini.projects.neon.agents.util.meta.DomainInvocation;
import org.jini.projects.neon.agents.util.meta.TransactionID;
import org.jini.projects.neon.host.ManagedDomain;
import org.jini.projects.neon.host.transactions.NonTransactionalResource;
import org.jini.projects.neon.host.transactions.TransactionAgent;
import org.jini.projects.neon.host.transactions.TransactionalResource;
import org.jini.projects.neon.service.AgentBackendService;
import org.jini.projects.neon.util.MethodViewer;

/**
 * Dynamic Proxy class that represents an agent during collaboration and works
 * on behalf of the agent. Each call is blocking and hence synchronous
 * 
 * @author calum
 */

public class CollaborationMethodInvokerRemoteProxy implements InvocationHandler {
        AgentBackendService  remoteProxy;

        AgentIdentity linkedAgent;

        Agent controller;

        String name;
        
        ManagedDomain dom;

        Logger l = Logger.getLogger("org.jini.projects.neon.dynproxy");

        boolean notified = false;

        public boolean thrownException = false;

        
        public CollaborationMethodInvokerRemoteProxy(AgentIdentity theAgent,AgentBackendService remoteProxy, Agent controller, ManagedDomain theDomain) {
                super();
                // URGENT Complete constructor stub for CollaborationProxy
                linkedAgent = theAgent;
                this.remoteProxy = remoteProxy;
                this.controller = controller;
                this.dom = theDomain;
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
        	Meta methodMeta = new Meta();
        	if(!(controller instanceof NonTransactionalResource)){
        		TransactionalResource txnAgent = (TransactionalResource) controller;
        		if(txnAgent.inTransaction()){
        			String txnID=txnAgent.getTransaction();
        			TransactionAgent txnMgrAgent = (TransactionAgent)  dom.getRegistry().getAgent("neon.Transaction");
        			String localTxnID= txnAgent.getTransaction().split(":")[1];
        			
        			Transaction txn = txnMgrAgent.getTransactionFor(UuidFactory.create(localTxnID));
        			TransactionID txnMeta = new TransactionID(localTxnID,txn);
        			methodMeta.addAttribute(txnMeta);
        		}
        	}
        	methodMeta.addAttribute(new DomainInvocation(dom.getDomainName()));
        	String methSignature = MethodViewer.getMethodShortString(method);
        	MethodInvocation methInv = new MethodInvocation(methodMeta,methSignature,method.getName(),  args );
                return remoteProxy.invoke(linkedAgent, methInv);
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
                return "CollaborationMethodInvokerRemoteProxy for Agent: " + controller.getNamespace() + "." + controller.getName() + ":[" + controller.getIdentity().getID() + "]";

        }

  
        
}
