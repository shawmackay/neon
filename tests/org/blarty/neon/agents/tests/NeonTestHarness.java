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
 * neon : org.jini.projects.neon.agents.tests
 * NeonTestHarness.java
 * Created on 20-Jan-2004
 *NeonTestHarness
 */
package org.jini.projects.neon.agents.tests;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import org.jini.projects.neon.collaboration.CollabAdvert;
import org.jini.projects.neon.recovery.CheckpointAgentImpl;

/**
 * @author calum
 */
public class NeonTestHarness {

	/**
	 * 
	 */
	public NeonTestHarness() {
		super();
		// URGENT Complete constructor stub for NeonTestHarness
        System.out.println("Testing");
        CollabAdvert adv = new CollabAdvert(new CheckpointAgentImpl());
        List l = adv.getCollaborativeMethods();
        for(Iterator iter = l.iterator(); iter.hasNext();){
            Method meth = (Method) iter.next();
            System.out.println("Method: " + meth.getName());
        }
        System.out.println("Test Finished");
	}
    
    public static void main (String[] args){
      new NeonTestHarness();   
    }

}
