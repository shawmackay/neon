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
 * neon : org.jini.projects.neon.agents.listeners
 * CollaborationListener.java
 * Created on 24-Feb-2004
 *CollaborationListener
 */

package org.jini.projects.neon.agents.listeners;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.id.UuidFactory;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.sensors.SensorListener;
import org.jini.projects.neon.collaboration.Advertiser;
import org.jini.projects.neon.collaboration.CollabAdvert;
import org.jini.projects.neon.host.ManagedDomain;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.neon.host.PrivilegedAgentContext;
import org.jini.projects.neon.host.transactions.NonTransactionalResource;
import org.jini.projects.neon.host.transactions.TransactionAccessor;
import org.jini.projects.neon.host.transactions.TransactionAgent;
import org.jini.projects.neon.host.transactions.TransactionalResource;
import org.jini.projects.neon.util.GenericsMethodViewer;
import org.jini.projects.neon.util.MethodViewer;
import org.jini.projects.zenith.messaging.channels.connectors.PublishingQConnector;
import org.jini.projects.zenith.messaging.messages.EventMessage;
import org.jini.projects.zenith.messaging.messages.ExceptionMessage;
import org.jini.projects.zenith.messaging.messages.InvocationMessage;
import org.jini.projects.zenith.messaging.messages.KVPair;
import org.jini.projects.zenith.messaging.messages.Message;
import org.jini.projects.zenith.messaging.messages.MessageHeader;
import org.jini.projects.zenith.messaging.messages.ObjectMessage;
import org.jini.projects.zenith.messaging.messages.StringMessage;
import org.jini.projects.zenith.messaging.system.ChannelException;
import org.jini.projects.zenith.messaging.system.MessagingListener;
import org.jini.projects.zenith.messaging.system.MessagingManager;
import org.jini.projects.zenith.messaging.system.store.StoreSystem;

/**
 * Main Messaging endpoint for agents, for invoking methods on an agent
 * instance.<br>
 * An implementation of the <code>ServiceActivator</code> messaging pattern.
 * 
 * @author calum
 */
public class CollaborationListener implements MessagingListener {

    transient Semaphore agentSemaphore = new Semaphore(1);

    Advertiser cag;

    Map<String, Integer> invokeLog = new TreeMap<String, Integer>();

    CollabAdvert adv;

    Logger log = Logger.getLogger("org.jini.projects.neon.agents.listeners");

    public CollaborationListener(Advertiser cag) {
        this.cag = cag;
        this.adv = cag.advertise();
    }

    static StoreSystem sys;
    static {
        try {
            sys = new StoreSystem(System.getProperty("org.jini.projects.zenith.messaging.system.store.dir"));
        } catch (IOException e) {

            System.out.println("Storage Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*
     * @see apollo.messaging.system.MessagingListener#messageReceived(apollo.messaging.messages.Message)
     */
    public void messageReceived(Message msg) {
        // TODO Complete method stub for messageReceived
           
//        try {
//            agentSemaphore.acquire();
//        } catch (InterruptedException e2) {
//            // TODO Handle InterruptedException
//            e2.printStackTrace();
//        }

        Object content;
        if (msg == null)
            log.warning("Message is null for " + cag.getSubscriberIdentity());
        MessageHeader header = msg.getHeader();
        log.finest("Reply address for Msg:" + header.getRequestID() + " is " + header.getReplyAddress() + ";Type: " + msg.getClass().getName());
        // URGENT Make sure this field is documented
        content = msg;
        String er_enabled = null;
        if (header.getArbitraryField("externalRoutingEnabled") != null)
            er_enabled = (String) header.getArbitraryField("externalRoutingEnabled");
        Object returningObject = null;
        PublishingQConnector replyChannel = null;
        PublishingQConnector invalidChannel = null;
        Exception throwingException = null;
        boolean sent = false;
        if (content instanceof InvocationMessage) {
            try {
                try {
                    if (msg.getHeader().getReplyAddress() != null) {
                        replyChannel = MessagingManager.getManager().getPublishingConnector(msg.getHeader().getReplyAddress());
                    } else
                            log.finer("The reply address for Msg: " + header.getRequestID() + " is null");
                    invalidChannel = MessagingManager.getManager().getPublishingConnector("invalidChannel");
                } catch (ChannelException e1) {
                    // URGENT Handle ChannelException
                    e1.printStackTrace();
                }
                handleManagement(msg);
                InvocationMessage cm = (InvocationMessage) content;
                if (invokeLog.containsKey(cm.getMethodSignature())) {
                    int i = invokeLog.get(cm.getMethodSignature());
                    invokeLog.put(cm.getMethodSignature(), i + 1);
                } else
                    invokeLog.put(cm.getMethodSignature(), 1);
                System.out.println("Invoking now: " + cm.getMethodSignature() + " (" + header.getRequestID() +")");
                returningObject = findAndInvoke(cm);
                System.out.println("Finished Invoking: " + cm.getMethodSignature());
                // sys.remove(msg);
                // if (replyChannel != null) {
                // Create Header, build up correlation information
                // from original request & send
                // sendReply(header, returningObject, replyChannel);
                // sent = true;
                // returningObject=null;
                // }

            } catch (Exception e) {
                // e.printStackTrace();
               
                throwingException = e;
            }

        }
        // We send messages with a key value pair object as control messages
        // for instance transactions
        if (content instanceof ObjectMessage) {
            ObjectMessage om = (ObjectMessage) content;

            if (om.getMessageContent() instanceof KVPair) {
                try {
                  
                    KVPair messagepair = (KVPair) om.getMessageContent();
                    String tid = (String) messagepair.getValue();
                    if (cag instanceof TransactionalResource) {
                       
                        log.finest("Setting transaction ID as part of request: " + header.getRequestID());
                        if (tid != null)
                            ((TransactionalResource) cag).setTransaction(tid);
                        else
                            ((TransactionalResource) cag).setTransaction(null);
                    } else
                        log.finest("Not a transactional resource");
                } catch (TransactionException e) {
                    // URGENT Handle TransactionException
                    e.printStackTrace();
                }
            }
        }
        if (content instanceof EventMessage) {
            if (cag instanceof org.jini.projects.neon.agents.sensors.SensorListener) {
                EventMessage evmsg = (EventMessage) content;
                try {
					((SensorListener) cag).sensorTriggered(evmsg.getEventName(), evmsg.getMessageContent());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            } else
                log.warning("Agent does not support events");

        }
        if (content instanceof StringMessage) {
            String scontent = (String) ((StringMessage) content).getMessageContent();
            
            try {
                if (msg.getHeader().getReplyAddress() != null) {
                    replyChannel = MessagingManager.getManager().getPublishingConnector(msg.getHeader().getReplyAddress());
                } else
                    log.finer("The reply address for Msg: " + header.getRequestID() + " is null");
                invalidChannel = MessagingManager.getManager().getPublishingConnector("invalidChannel");
            } catch (ChannelException e1) {
                // URGENT Handle ChannelException
                e1.printStackTrace();
            }

            if (scontent.equals("methodstats"))
                returningObject = invokeLog;
        }
        // Publish to invalid Message Channel
        // And send back an exception reponse
        if (replyChannel != null) {
            MessageHeader replyHeader = new MessageHeader();
            replyHeader.setCorrelationID(header.getRequestID());
            replyHeader.setGuaranteed(false);
            if (throwingException != null) {

                replyChannel.sendMessage(new ExceptionMessage(replyHeader, throwingException, msg));
                log.finest("Sent Reply: Reply ID:" + replyHeader.getRequestID() + " in response to " + header.getRequestID());
                // returningObject=null;
            } else {

                replyChannel.sendMessage(new ObjectMessage(replyHeader, returningObject));
                log.finest("Sent Reply: Reply ID:" + replyHeader.getRequestID() + " in response to " + header.getRequestID() + " on channel " + replyChannel.toString());
                // returningObject=null;
            }
        }
        if (invalidChannel != null && replyChannel == null) {
            MessageHeader invalidHeader = new MessageHeader();
            invalidHeader.setCorrelationID(header.getRequestID());
            invalidChannel.sendMessage(new ExceptionMessage(invalidHeader, throwingException, msg));

        }
       // agentSemaphore.release();
        // System.out.println("Semaphore released while processing Msg: " +
        // msg.getHeader().getRequestID() + "; CAG:" +
        // cag.getSubscriberIdentity());
    }

    /**
     * @param header
     * @param returningObject
     * @param replyChannel
     */
    private void sendReply(MessageHeader header, Object returningObject, PublishingQConnector replyChannel) {
        MessageHeader replyHeader = new MessageHeader();
        replyHeader.setCorrelationID(header.getRequestID());
        replyChannel.sendMessage(new ObjectMessage(replyHeader, returningObject));
        log.finest("Sent Reply: Reply ID:" + replyHeader.getRequestID() + " in response to " + header.getRequestID());
    }

    /**
     * @param returningObject
     * @param cm
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    private Object findAndInvoke(InvocationMessage cm) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object returningObject;
        Method methodToCall = null;
        try {
            log.finest("Trying to invoke a method for Msg: " + cm.getHeader().getRequestID());
            Class[] paramsclasses;

            List methods = adv.getCollaborativeMethods();
            List l = adv.getCollaborativeMethods();
            for (Iterator iter = l.iterator(); iter.hasNext();) {
                Method meth = (Method) iter.next();
                log.finest("Method: " + meth.getName());
            }

            List possMethodsToCall;
            // If we don't have a method signature to match against, we
            // need to deduces which method should be called
            if (cm.getMethodSignature() == null) {
                possMethodsToCall = new ArrayList();
                if (cm.getParameters() != null) {
                    paramsclasses = new Class[cm.getParameters().length];
                } else {
                    paramsclasses = new Class[0];
                }
                for (int j = 0; j < paramsclasses.length; j++) {
                    paramsclasses[j] = cm.getParameters()[j].getClass();
                }

                for (Iterator iter = methods.iterator(); iter.hasNext() && methodToCall == null;) {
                    Method m = (Method) iter.next();
                    if (m.getName().equalsIgnoreCase(cm.getMethodName()) && m.getParameterTypes().length == paramsclasses.length) {
                        possMethodsToCall.add(m);
                    }
                }
                // Only one possible method that matches the name => call it
                if (possMethodsToCall.size() == 1)
                    methodToCall = (Method) possMethodsToCall.get(0);
                for (Iterator iter = possMethodsToCall.iterator(); iter.hasNext();) {
                    Method m = (Method) iter.next();
                    // We have an exact class match =>call it
                    if (Arrays.equals(m.getParameterTypes(), paramsclasses))
                        methodToCall = m;
                }
            } else {

                /*
                 * Signature matching - fastest way to match the method.
                 */

                possMethodsToCall = methods;
                if (possMethodsToCall.size() > 0 && methodToCall == null) {
                    // log.info("\tMultiple methods - same name, same parameter
                    // numbers, different param classes......Checking");
                    Method assumptiveMethod = null;
                    for (int loop = 0; loop < possMethodsToCall.size(); loop++) {
                        Method m = (Method) possMethodsToCall.get(loop);
                        String sig = cm.getMethodSignature();
                       
                        if (GenericsMethodViewer.getMethodShortGenericString(m).equals(sig)) {
                            assumptiveMethod = m;
                            break;
                        }
                    }
                    if (assumptiveMethod != null)
                        methodToCall = assumptiveMethod;
                    else
                        throw new NoSuchMethodException("Method " + cm.getMethodSignature() + " cannot be found on this object");
                }
            }
            if (methodToCall != null) {
                log.finest("Invoking: " + methodToCall.toString());
                returningObject = methodToCall.invoke(cag, cm.getParameters());

                cag.clearCurrentMessage();
                // return ob;
            } else {
                cag.clearCurrentMessage();
                throw new NoSuchMethodException("A Matching method does not exist\n\t" + cm.getMethodSignature());
            }
            return returningObject;
        } catch (InvocationTargetException ex) {
            cag.clearCurrentMessage();
            log.finer("Caught Exception: " + ex.getClass().getName()+ "; Msg: " + ex.getMessage() + "AG: (" +cag.getClass().getName() + "#" + cm.getMethodName() + ")");

            throw ex;
        }

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
                // System.out.println("More app. Exception is : " +
                // e.getClass().getName());
                return e;
            }
        }
        return null;
    }

    /**
     * @param msg
     * @throws TransactionException
     */
    private void handleManagement(Message msg) throws TransactionException {
        if (cag.getCurrentMessage() != null)
            log.warning("\t\tAlready handling another message!!!!");
        cag.setCurrentMessage(msg);
        log.finest("Checking for transaction context");
        // msg.getHeader().showArbitraryFields();
        // System.out.println("Handling Txn Management for Msg: " +
        // msg.getHeader().getRequestID());
        if (msg.getHeader().getArbitraryField("txnID") != null) {
            if (!(cag instanceof NonTransactionalResource)) {
                log.finest("Call is running under a transactional context - setting my (" + cag.getName() + ") context.....");
                Class[] intfs = cag.getClass().getInterfaces();
                if (msg.getHeader().getArbitraryField("actualTxn") != null) {
                    log.finest("Attaching Txn Management for Msg: " + msg.getHeader().getRequestID());

                    ((TransactionalResource) cag).attachTransaction((String) msg.getHeader().getArbitraryField("txnID"), (Transaction) msg.getHeader().getArbitraryField("actualTxn"));
                    log.finest("Txn Management Attached for Msg: " + msg.getHeader().getRequestID());
                } else {
                    log.finest("Setting Txn Management for Msg: " + msg.getHeader().getRequestID());
                    ((TransactionalResource) cag).setTransaction((String) msg.getHeader().getArbitraryField("txnID"));
                }
                if (cag instanceof TransactionAccessor) {
                    log.finest("Obtaining Domain for Txn Management for Msg: " + msg.getHeader().getRequestID());
                    String[] parts = ((String) msg.getHeader().getArbitraryField("txnID")).split(":");

                    ManagedDomain dom = ((ManagedDomain) ((Agent) cag).getAgentContext());
                    try {
                        TransactionAgent txnag = (TransactionAgent) ((PrivilegedAgentContext)dom).getAgent(new AgentIdentity(parts[0]));

                        log.finest("Getting Transaction from BlackBox for Msg: " + msg.getHeader().getRequestID());
                        String managerAndTxnRef = (String) msg.getHeader().getArbitraryField("txnID");
                        String[] txparts = managerAndTxnRef.split(":");
                        // URGENT: Add method into this class to reflect getting
                        // an agent via ID, but allow for cross-host objects
                       // TransactionAgent ag = (TransactionAgent) dom.getRegistry().getAgent(UuidFactory.create(txparts[0]));

                        try {
							Transaction txn = txnag.getTransactionFor(UuidFactory.create(parts[1]));
							log.finest("Setting Accessible Dist. Txn for Msg: " + msg.getHeader().getRequestID());
							((TransactionAccessor) cag).setGlobalTransaction(txn);
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                    } catch (NoSuchAgentException e) {
                        // TODO Handle NoSuchAgentException
                        e.printStackTrace();
                    }
                }
            } else
                log.finest("Not propagating transaction to NonTransactionalResource (" + cag.getName() + ")");
        }
        log.finest("Transaction propagation complete for Msg: " + msg.getHeader().getRequestID());
    }
}
