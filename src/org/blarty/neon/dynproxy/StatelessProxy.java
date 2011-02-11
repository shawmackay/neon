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
 * StatelessProxy.java
 * Created on 16-Jul-2004
 *StatelessProxy
 */

package org.jini.projects.neon.dynproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.logging.Logger;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.host.AgentDomain;
import org.jini.projects.neon.host.ManagedDomain;
import org.jini.projects.neon.util.GenericsMethodViewer;
import org.jini.projects.neon.util.MethodViewer;
import org.jini.projects.zenith.messaging.channels.ReceiverChannel;
import org.jini.projects.zenith.messaging.messages.InvocationMessage;
import org.jini.projects.zenith.messaging.messages.Message;
import org.jini.projects.zenith.messaging.messages.MessageHeader;
import org.jini.projects.zenith.messaging.messages.ExceptionMessage;
import org.jini.projects.zenith.messaging.system.MessagingListener;
import org.jini.projects.zenith.messaging.system.MessagingManager;


/**
 * Routes messages to the agent group message router, rather than to a particular instance of that group
 * @author calum
 */
public class StatelessProxy implements InvocationHandler{

	private AgentDomain d;
	private Object theAgent;
	Object o = new String("LOCK");
	public boolean thrownException = false;
	private Logger l =
        Logger.getLogger("org.jini.projects.neon.dynproxy");

	public StatelessProxy(Agent agent) {
		
        theAgent = agent;
	}

	/*
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
	 *          java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(theAgent, args);
	}


    private Throwable extractAppropriateException(Throwable parent, Throwable e, Class[] exceptionTypes) {
		if (e instanceof InvocationTargetException)
			return extractAppropriateException(e, e.getCause(), exceptionTypes);
		if (e instanceof UndeclaredThrowableException)
			return extractAppropriateException(e, e.getCause(), exceptionTypes);
		for (int i = 0; i < exceptionTypes.length; i++) {
			System.out.println("\t\tChecking exception: " + exceptionTypes[i].getName());
			if (exceptionTypes[i].isInstance(e)){
				System.out.println("More app. Exception is : " + e.getClass().getName());
				return e;
			}
		}
		return null;
	}

	
}
