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
 * neon : org.jini.projects.neon.callbacks.messages.server
 * LeasedMesgRegistrartio.java
 * Created on 25-Jul-2003
 */
package org.jini.projects.neon.callbacks.messages.server;

import java.rmi.RemoteException;

import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.id.Uuid;

import com.sun.jini.landlord.LeasedResource;

/**
 * @author calum
 */
public class LeasedMesgRegistration implements RemoteEventListener, LeasedResource {
	
	RemoteEventListener rel;
	Uuid leaseKey;
	protected long expirationTime = 0L;
	/**
	 * 
	 */
	public LeasedMesgRegistration(RemoteEventListener listen,Uuid LeaseKey) {
		super();
		rel = listen;
		leaseKey = LeaseKey;
	}

	/* (non-Javadoc)
	 * @see net.jini.core.event.RemoteEventListener#notify(net.jini.core.event.RemoteEvent)
	 */
	public void notify(RemoteEvent theEvent) throws UnknownEventException, RemoteException {
		// TODO Complete method stub for notify
		rel.notify(theEvent);
	}

	/* (non-Javadoc)
	 * @see com.sun.jini.landlord.LeasedResource#setExpiration(long)
	 */
	public void setExpiration(long newExpiration) {
		this.expirationTime = newExpiration;
		
	}

	/* (non-Javadoc)
	 * @see com.sun.jini.landlord.LeasedResource#getExpiration()
	 */
	public long getExpiration() {
		// TODO Complete method stub for getExpiration
		return expirationTime;
	}

	/* (non-Javadoc)
	 * @see com.sun.jini.landlord.LeasedResource#getCookie()
	 */
	public Uuid getCookie() {
		// TODO Complete method stub for getCookie
		return leaseKey;
	}

}
