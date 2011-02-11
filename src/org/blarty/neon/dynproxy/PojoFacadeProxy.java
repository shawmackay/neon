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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.DelegatedAgent;
import org.jini.projects.neon.collaboration.Advertiser;
import org.jini.projects.neon.dynproxy.invoker.InvokerDelegate;
import org.jini.projects.neon.host.transactions.TransactionAgent;
import org.jini.projects.zenith.endpoints.Producer;

/**
 * The main dynamic proxy invocation handler used by Neon. When an agent is
 * deployed, it is wrapped inside a proxy class. This class allows for method
 * delegation and interception.
 * 
 * @author calum
 */
public class PojoFacadeProxy implements NeonProxy {

    Object receiver;

    DelegatedAgent agentwrapper;

    Map featureMap = new HashMap();

    Logger log = Logger.getLogger("org.jini.projects.neon.dynproxy");

    // TransactionHolder holder;
    Producer currentProducer;

    int numinvokes = 0;

    /**
     * Create a PojoFacadeProxy instance, for a particular agent
     */
    public PojoFacadeProxy(DelegatedAgent delegate, Object receiver) {
        super();
        // featureMap.put("org.jini.projects.neon.collaboration.ProducerInterest",
        // new ProducerHandler());
        if (receiver == null)
            System.out.print(" ");
        this.receiver = receiver;
        this.agentwrapper = delegate;
        // holder = new TransactionHolder();
    }
    
    public Object getPOJO(){
            return receiver;
    }

    /*
     * 
     * @see org.jini.projects.neon.dynproxy.NeonProxy#getReceiver()
     */
    public Agent getReceiver() {

        return agentwrapper;
    }

    /*
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
     *      java.lang.reflect.Method, java.lang.Object[])
     */

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // System.out.println(method.getDeclaringClass().getName());
        if (isNeonAgentMethod(method)) {                     
            Object o = method.invoke(agentwrapper, args);  
           Method pojoMethod = isAlsoPOJOMethod(method);
           if(pojoMethod!=null)
                   pojoMethod.invoke(receiver, args);
            return o;
        } else {
            return method.invoke(receiver, args);
        }
        // return null;
    }
    
    public Method isAlsoPOJOMethod(Method method){
           
          
            try {
                return receiver.getClass().getMethod(method.getName(), method.getParameterTypes());
        } catch (SecurityException e) {
                // TODO Auto-generated catch block
                log.log(Level.WARNING,"Security Error when checking availability of method",e);
                
        } catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                //If the method doesn't exist return null;
               return null;
        }
            return null;
    }

    public boolean isNeonAgentMethod(Method method) {
     
        if (method.getDeclaringClass().equals(Agent.class))
            return true;
        if (method.getDeclaringClass().equals(Advertiser.class))
            return true;
        if (method.getDeclaringClass().equals(Producer.class))
            return true;
        return false;
    }
}
