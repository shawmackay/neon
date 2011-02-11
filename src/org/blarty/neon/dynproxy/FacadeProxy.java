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
 * neon : org.jini.projects.neon.dynproxy
 * TransactionalProxy.java Created on 11-Nov-2003
 *TransactionalProxy
 */

package org.jini.projects.neon.dynproxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.dynproxy.invoker.InvokerDelegate;
import org.jini.projects.neon.host.transactions.TransactionAgent;
import org.jini.projects.zenith.endpoints.Producer;

/**
 * The main dynamic proxy invocation handler used by Neon. When an agent is deployed, it is wrapped inside a proxy class.
 * This class allows for method delegation and interception.
 * 
 * @author calum
 */
public class FacadeProxy implements NeonProxy {
        Agent receiver;
        Map featureMap = new HashMap();
        Logger log = Logger.getLogger("org.jini.projects.neon.dynproxy");
        // TransactionHolder holder;
        Producer currentProducer;
        int numinvokes=0;
        /**
         * Create a FacadeProxy instance, for a particular agent
         */
        public FacadeProxy(Agent receiver) {
                super();
                // featureMap.put("org.jini.projects.neon.collaboration.ProducerInterest",
                // new ProducerHandler());
                if (receiver == null)
                        System.out.print(" ");
                this.receiver = receiver;

                // holder = new TransactionHolder();
        }

        /*
         * 
         * @see org.jini.projects.neon.dynproxy.NeonProxy#getReceiver()
         */
        public Agent getReceiver() {

                return receiver;
        }

        /*
         * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
         *         java.lang.reflect.Method, java.lang.Object[])
         */

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                if (method.getName().equals("informOfProducer")) {
                        // log.finest("Producer Set");
                System.out.println("Informed of Producer");
                        currentProducer = (Producer) args[0];
                }
                
                /**
                 * Handle any delegate processing
                 */
                //System.out.printf("Method %s declared in %s\n", method.toString(),method.getDeclaringClass().getName() );
                if (featureMap.containsKey(method.getDeclaringClass().getName())) {
                        //synchronized (proxy) {
                        try {
                                InvokerDelegate invDel = (InvokerDelegate) featureMap.get(method.getDeclaringClass().getName());
                                Object retval = invDel.invokeDelegate(method, currentProducer,receiver, proxy, args);
                                // currentProducer = null;                              
                                return retval;
                        } catch (Exception ex){
                                System.out.println("Facade Proxy caught exception: " + ex.getClass().getName());
                                ex.printStackTrace();
                                throw ex;
                        }
                        //}
                } else {
                        numinvokes++;
                   //     System.out.println("Invoking " + method.getName() + " = (" + method.toGenericString() + ")");
                        Object ob = method.invoke(receiver, args);
                        return ob;
                }

                // return null;
        }

        /**
         * Creates a connection between a particular interface, and a delegate which
         * will process method calls to that interface on behalf of the agent
         * 
         * @param handlingInterface
         *                 name of the interface to handle
         * @param delegate
         *                 the class that will execute processing for the interface on
         *                 behalf of the agent
         */
        public void addInvokerDelegate(String handlingInterface, InvokerDelegate delegate) {
                featureMap.put(handlingInterface, delegate);
        }

        /**
         * Creates a connection between a particular interface, and a delegate which
         * will process method calls to that interface on behalf of the agent
         * 
         * @param handlingInterface
         *                 the interface to handle
         * @param delegate
         *                 the class that will execute processing for the interface on
         *                 behalf of the agent
         */
        public void addInvokerDelegate(Class forInterface, InvokerDelegate delegate) {
                featureMap.put(forInterface.getName(), delegate);
        }
}
