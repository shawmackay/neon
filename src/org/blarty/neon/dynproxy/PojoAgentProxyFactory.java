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
 * PojoAgentProxyFactory.java Created on 23-Sep-2003
 */
package org.jini.projects.neon.dynproxy;

import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Logger;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.DelegatedAgent;
import org.jini.projects.neon.collaboration.ProducerInterest;
import org.jini.projects.neon.dynproxy.invoker.InvokerDelegate;
import org.jini.projects.neon.host.ManagedDomain;
import org.jini.projects.neon.host.transactions.NonTransactionalResource;
import org.jini.projects.neon.host.transactions.TransactionalResource;
import org.jini.projects.neon.service.start.Delegate;
import org.jini.projects.neon.service.start.DelegateCatalog;

/**
 * Wrappers an agent in a dynamic proxy accroding to rules set out by the system
 * configuration or partition
 * 
 * @author calum
 */
public class PojoAgentProxyFactory {

        private static Logger proxyLogger = Logger.getLogger("org.jini.projects.neon.proxy");

        /**
         * 
         */

        /**
         * Given an agent, this factory method looks at each class in the agents
         * hierarchy adding all interfaces to the proxy definition
         * 
         * @param receiver
         * @return
         */
        public static Agent create(Object receiver) {
                LinkedHashSet intfsToProxy = new LinkedHashSet();
                // System.out.println("Agent Class: " +
                // receiver.getClass().getName());
                // System.out.println("Superclass: " +
                // receiver.getClass().getSuperclass().getName());

                // System.out.println("Interfaces.....:");
                PojoFacadeProxy delegater = new PojoFacadeProxy(new DelegatedAgent(receiver), receiver);
                addIntfsToList(intfsToProxy, receiver.getClass());
                intfsToProxy.add(Agent.class);

                // Don't need to add delegates in here - we'll wrapper
                // pojofacadeproxy
                // inside FacadeProxy,
                // which will add the delegates for us

                Class[] interfaces = new Class[intfsToProxy.size()];
                int i = 0;
                for (Iterator iter = intfsToProxy.iterator(); iter.hasNext(); i++)
                        interfaces[i] = (Class) iter.next();
                try {
                        Agent ag = (Agent) Proxy.newProxyInstance(receiver.getClass().getClassLoader(), interfaces, delegater);
                      
                        return ag;
                } catch (ClassCastException e) {
                        // TODO Handle IllegalArgumentException
                        e.printStackTrace();
                }
                return null;
        }

        private static void addIntfsToList(LinkedHashSet list, Class cl) {
                if (cl.getSuperclass() != null)
                        addIntfsToList(list, cl.getSuperclass());
                Class[] clintfs = cl.getInterfaces();
                for (int i = 0; i < clintfs.length; i++) {
                        addIntfsToList(list, clintfs[i]);

                }
                // 0 System.out.println("Returned");
                Class[] intfs = cl.getInterfaces();
                for (int i = 0; i < intfs.length; i++) {
                       // System.out.println("\t Interface: " + intfs[i].getName() + " of " + cl.getName());
                        if (!list.contains(intfs[i]))
                                list.add(intfs[i]);

                }
        }
}
