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

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.host.AsyncAgentEnvironment;
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

public class CollaborationProxy implements InvocationHandler,MessagingListener {
        AsyncAgentEnvironment ctx;

        AgentIdentity linkedAgent;

        Agent controller;

        Object returnObject;

        Logger l = Logger.getLogger("org.jini.projects.neon.dynproxy");

        boolean notified = false;

        public boolean thrownException = false;

        /**
         * 
         */
        public CollaborationProxy(AsyncAgentEnvironment ctx, AgentIdentity linkedAgent, Agent controller) {
                super();
                // URGENT Complete constructor stub for CollaborationProxy
                this.ctx = ctx;
                this.linkedAgent = linkedAgent;
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

                notified = false;
                returnObject = null;
                if(controller !=null)
                l.finest("Calling proxy " + controller.getNamespace() + "." + controller.getName() + "[" + controller.getIdentity() + "]");
                MessageHeader header = new MessageHeader();
                MessagingManager.getManager().registerOnChannel("reply" + controller.getIdentity().getID().toString(), this);
                if (controller instanceof TransactionalResource) {
                        l.finest("...trying to Propagate Transaction");
                        TransactionalResource res = (TransactionalResource) controller;
                        if (res.getTransaction() != null) {
                                String txnIdentifier = res.getTransaction();
                                l.finest("transaction Propagation Confirmed CP = " + txnIdentifier);
                                header.setArbitraryField("txnID", txnIdentifier);

                        }
                }

                header.setReplyAddress("reply" + controller.getIdentity().getID().toString());
                header.setDestinationAddress(linkedAgent.getID().toString());
                // We don't use guaranteed messaging for method invocations.
                header.setGuaranteed(false);
                l.finest("Agent " + controller.getIdentity() + " sending mesg: " + header.getRequestID() + "; Destination: " + linkedAgent.getID());
                InvocationMessage imsg = new InvocationMessage(header, method.getName(), args, GenericsMethodViewer.getMethodShortGenericString(method));
                ctx.sendAsyncMessage(linkedAgent.getID(), imsg);
                l.finest("Message sent");
                synchronized (this) {
                        try {
                                int waitamount = 0;
                                while (!notified) {
                                        wait(100);
                                        waitamount += 100;
                                        if (waitamount % 5000 == 0)
                                                if (waitamount % 30000 == 0)
                                                        l.severe("Waited  " + (waitamount / 1000) + " secs for return for " + GenericsMethodViewer.getMethodShortGenericString(method) + " from Agent " + controller.getIdentity() + "  @ Agent " + linkedAgent.getID().toString() + "; Msg: " + imsg.getHeader().getRequestID() + ".....checking audit log");
                                                else
                                                        l.warning("Waited  " + (waitamount / 1000) + " secs for return for " + GenericsMethodViewer.getMethodShortGenericString(method) + " from Agent " + controller.getIdentity() + "  @ Agent " + linkedAgent.getID().toString() + "; Msg: " + imsg.getHeader().getRequestID() + ".....checking audit log");

                                        MessageAudit audit = MessageAudit.getMessageAuditer();
                                        if (audit.hasResponseFor(imsg.getHeader().getRequestID())) {
                                                System.out.println("Auditer has response registered for message!");
                                                Message response = audit.getResponseFor(imsg.getHeader().getRequestID());
                                                if (response != null) {
                                                        messageReceived(response);
                                                } else
                                                        System.out.println("But why is it null");
                                        }

                                }
                                notified = false;
                        } catch (Exception ex) {
                                ex.printStackTrace();
                        }
                }

                l.finest("Notified returning");
                if (thrownException) {
                        l.finest("Extracting more appropriate exception");
                        Throwable t = extractAppropriateException(null, (Throwable) returnObject, method.getExceptionTypes());

                        if (t == null) {

                                // System.out.println("Throwing exception: " +
                                // returnObject.getClass().getName());
                                throw (Throwable) returnObject;
                        } else {
                                // System.out.println("Throwing appropriate
                                // exception");
                                throw t;
                        }
                }

                return returnObject;

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
                return "CollaborationProxy for Agent: " + controller.getNamespace() + "." + controller.getName() + ":[" + controller.getIdentity().getID() + "]";

        }

        /*
         * @see org.jini.projects.zenith.messaging.system.MessagingListener#messageReceived(org.jini.projects.zenith.messaging.messages.Message)
         */
        public void messageReceived(Message m) {
                if (m instanceof ExceptionMessage) {
                        returnObject = m.getMessageContent();

                        thrownException = true;
                } else {
                        Object o = m.getMessageContent();
                        returnObject = o;

                        this.thrownException = false;
                }
                notified = true;
                Thread.yield();
                synchronized (this) {
                        try {
                                notify();
                        } catch (Exception ex) {
                                ex.printStackTrace();
                        }
                }
        }
}
