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
 * neon : org.jini.projects.neon.callbacks.messages
 * ProducerIntf.java
 * Created on 24-Jul-2003
 */
package org.jini.projects.neon.callbacks.messages.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.jini.core.event.EventRegistration;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.LeaseDeniedException;

/**
 * @author calum
 */
public interface ProducerIntf extends Remote{
	public EventRegistration register(long duration, RemoteEventListener l) throws LeaseDeniedException, RemoteException;
}
