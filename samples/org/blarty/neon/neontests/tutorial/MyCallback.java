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
 * neon : org.jini.projects.neon.org.jini.projects.neon.neontests.tutorial
 * MyCallback.java
 * Created on 28-Oct-2003
 */
package org.jini.projects.neon.neontests.tutorial;

import java.rmi.RemoteException;
import java.util.Date;

import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;

/**
 * @author calum
 */
public class MyCallback implements RemoteEventListener {

    /**
     * 
     */
    public MyCallback() {
        super();

    }

    /* (non-Javadoc)
     * @see net.jini.core.event.RemoteEventListener#notify(net.jini.core.event.RemoteEvent)
     */

    public static void main(String[] args) {
    }
    /* (non-Javadoc)
     * @see net.jini.core.event.RemoteEventListener#notify(net.jini.core.event.RemoteEvent)
     */
    public void notify(RemoteEvent theEvent) throws UnknownEventException, RemoteException {
        System.out.println("Callback fired @ " + new Date());
        System.out.println("\tID  : " + theEvent.getID());
        System.out.println("\tSeq : " + theEvent.getSequenceNumber());
        System.out.println("\tSrc : " + theEvent.getSource().toString());
    }

}
