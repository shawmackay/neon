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
 * neon : org.jini.projects.neon.web
 * NeonLink.java
 * Created on 31-Aug-2004
 *NeonLink
 */
package org.jini.projects.neon.web;

import org.jini.projects.neon.service.AgentBackendService;

/**
 * @author calum
 */
public class NeonLink {
	private static NeonLink link;
	
	private NeonLink(){
	
	}
	
	static AgentBackendService svc;
	
	static {
		link = new NeonLink();
	}
	
	public static void setBackend(AgentBackendService neon){
		svc = neon;
	}
	
	public static String getTestMessage(){
		return "Successful Test";
	}
	public static NeonLink getLink(){
		return link;
	}
	public AgentBackendService getNeon(){
		return svc;
	}
}
