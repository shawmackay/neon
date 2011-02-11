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
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.jini.id.Uuid;

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
 * Performs mapping between a synchronous agent interface to an asynchronous Agent interface.<br>
 * The only object that implements the asynchronous interface is this Handlers' proxy. The invocation handler
 * maintains a map of <code>AsyncHandler</code>s and the message ID's. When a message arrives on the reply channel
 * , the handler calls the appropriate AsyncHandler instance, by checking the message correlation ID. 
 * @author Calum
 *
 */
public class CollaborationAsyncMapProxy implements InvocationHandler, MessagingListener {
    AsyncAgentEnvironment ctx;

    AgentIdentity linkedAgent;

    Agent controller;

    Object returnObject;

    Logger l = Logger.getLogger("org.jini.projects.neon.dynproxy");

    Map<Uuid, ReplyDispatcher> correlationHandler = new HashMap<Uuid, ReplyDispatcher>();

    /**
     * 
     */
    public CollaborationAsyncMapProxy(AsyncAgentEnvironment ctx, AgentIdentity linkedAgent, Agent controller) {
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
     *           java.lang.reflect.Method, java.lang.Object[])
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // TODO Complete method stub for invoke
        l.info("Calling proxy " + controller.getNamespace() + "." + controller.getName() + "[" + controller.getIdentity() + "]");
        l.info("Calling method " + method.toGenericString());
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

        // Now we mess around with the AsyncHandler and change the method to be
        // actually called
        // Removing the last class and arg - because they point to the
        // AsyncHandler and are nothing to do with

        Class[] cl = method.getParameterTypes();
        String methString = GenericsMethodViewer.getMethodShortGenericString(method);
        // Cut out the reference to the trailing AsyncHandler, that we want to
        // keep here.
        methString = methString.replace(",org.jini.projects.neon.host.AsyncHandler)", ")");

        Object[] newArgs = new Object[args.length - 1];
        for (int i = 0; i < newArgs.length; i++)
            newArgs[i] = args[i];
        Uuid correlationID = header.getRequestID();
        AsyncHandler handler = (AsyncHandler) args[args.length - 1];
        l.finest("Remapped method: " + methString);
        Message messageSent = new InvocationMessage(header, method.getName(), newArgs, methString);
        ctx.sendAsyncMessage(linkedAgent.getID(), messageSent);
      l.finest("Registering AsyncHandler with: " + correlationID);
        correlationHandler.put(correlationID, new ReplyDispatcher(handler, messageSent, method));
        l.finest("Message sent");
        return null;
    }
    
    

    /*
     * @see org.jini.projects.zenith.messaging.system.MessagingListener#messageReceived(org.jini.projects.zenith.messaging.messages.Message)
     */
    public void messageReceived(Message m) {
        // TODO Complete method stub for messageReceived
        Object o = m.getMessageContent();

        l.finest("Reply Message received for Handler registered on: " + m.getHeader().getCorrelationID());
        Uuid id = m.getHeader().getCorrelationID();
        l.finest("CorrelationID: " + id.toString());
        Object testContains = correlationHandler.get(id);
        if (testContains!=null) {
            ReplyDispatcher dispatch = correlationHandler.get(id);
            dispatch.setReceivedMessage(m);
            if (o instanceof Throwable)
                dispatch.setException((Throwable) o);
            else {
                dispatch.setReturningObject(o);
            }
            Thread t = new Thread(dispatch);
            t.start();
        } else {
           l.severe("Proxy cannot find corresponding AsyncHandler");
        }
    }

    class ReplyDispatcher implements Runnable {
        private AsyncHandler handler;

        private Object returnObject;

        private Throwable thrown;

        private Message messageSent;

        private Message received;
        
        private Method method;

        public ReplyDispatcher(AsyncHandler handler, Message sent, Method method) {
            this.handler = handler;
            messageSent = sent;
            this.method = method;
        }

        public void setReturningObject(Object o) {
            returnObject = o;
        }

        public void setException(Throwable ex) {
            this.thrown = ex;
        }

        public void setReceivedMessage(Message received) {
            this.received = received;
        }

        public void run() {
            // TODO Auto-generated method stub
            if (thrown != null)
                handler.replied(thrown, method, true);
            else
                handler.replied(returnObject, method, false);
            synchronized (this) {
                correlationHandler.remove(messageSent.getHeader().getRequestID());

            }
        }
    }
}
