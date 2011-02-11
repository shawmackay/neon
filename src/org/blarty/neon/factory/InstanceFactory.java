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
 * neon : neon.factory
 * InstanceFactory.java Created on 12-Feb-2004
 *InstanceFactory
 */
package org.jini.projects.neon.factory;
import java.util.HashMap;
import java.util.Map;

import org.jini.projects.neon.agents.Agent;
/**
 * @author calum
 */
public class InstanceFactory {
	Map instanceList = new HashMap();
	/**
	 *  
	 */
	public void register(Agent agent) {
		Class cl = agent.getClass();
		buildFromClass(agent.getName(), cl);
	}
	public void register(String agentname, Class clazz) {
		buildFromClass(agentname, clazz);
	}
	private void buildFromClass(String agentName, Class cl) {
        System.out.println("Putting ");
		instanceList.put(agentName, cl);
	}
	public void register(String agentName, String className) {
		Class cl = null;
		try {
			cl = Class.forName(className);
		} catch (ClassNotFoundException e) {	   
			e.printStackTrace();
		}
		if (cl != null)
			buildFromClass(agentName, cl);
	}
	public Agent createAgent(String name) {
		Object instance = null;
        System.out.println(name);
		try {
			instance = ((Class) instanceList.get(name)).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		if (instance != null)
			return (Agent) instance;
		else
			return null;
	}
}