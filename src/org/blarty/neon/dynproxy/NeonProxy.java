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
 * NeonProxy.java
 * Created on 23-Sep-2003
 */
package org.jini.projects.neon.dynproxy;

import java.lang.reflect.InvocationHandler;

import org.jini.projects.neon.agents.Agent;


/**
 * Base interface that all Neon InvocationHandlers should implement, rather than implementing InvocationHandler directly.
 * This provides a way to get at the encapsulated agent such that when agents are transferred, they can be wrappered at their destination
 * according to that configuration. 
 * @author calum
 */
public interface NeonProxy extends InvocationHandler{
    /**
     * Obtains the wrappered Agent
     * @return the initial agent that the proxy delegates to
     */
	public Agent getReceiver();
}
