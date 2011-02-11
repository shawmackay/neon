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
 * AgentProxy.java Created on 23-Sep-2003
 */
package org.jini.projects.neon.dynproxy;

import java.lang.reflect.Method;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.zenith.endpoints.Producer;


/**
 * Defines a basic dynamic proxy with no augmentations.
 *@author calum
 */
public class AgentProxy implements NeonProxy {
    Agent receiver;
    /**
	 *  
	 */
    public AgentProxy(Agent receiver) {
        super();
        this.receiver = receiver;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
	 *           java.lang.reflect.Method, java.lang.Object[])
	 */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("AgentProxy Invoking method: " + method.getName());
        if (method.getName().equalsIgnoreCase("informOfProducer")) {
            if (args[0] != null)
                System.out.println("Agent called from producer: " + ((Producer) args[0]).getProducerIdentity());
            else
                System.out.println("Agent called from producer: null");
        } else {

            if (method.getName().startsWith("handle"))
                System.out.println("This is a message");
            Object ob = method.invoke(receiver, args);
            //System.out.println("Method invoked");
            return ob;
        }
        return null;
    }

    public Agent getReceiver() {
        return receiver;
    }

}
