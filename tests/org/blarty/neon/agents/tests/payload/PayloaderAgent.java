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

package org.jini.projects.neon.agents.tests.payload;

/*
* PayloaderAgent.java
*
* Created Fri Mar 18 10:17:47 GMT 2005
*/

import java.util.ArrayList;

import net.jini.id.Uuid;

import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.PayloadAgent;
import org.jini.projects.neon.host.PrivilegedAgentContext;

/**
*
* @author  calum
*
*/

public class PayloaderAgent extends AbstractAgent implements PayloadAgent {
	
	ArrayList<Uuid> deployedAgents;
	
	/**
	* init
	* @return boolean
	*/
	public boolean init(){
		return true;
	}
	
	/**
	* start
	*/
	public void start(){
		
	}
	
	/**
	* stop
	*/
	public void stop(){
		super.stop();
	}
	
	/**
	* complete
	*/
	public void complete(){
		super.complete();
	}
	
	/**
	* deployGroup
	*/
	public void deployGroup(){
		PrivilegedAgentContext ctx = (PrivilegedAgentContext) this.getAgentContext();
		deployedAgents = new ArrayList<Uuid>();
		for(int i=0;i<5;i++){
			SimpleLocalAgent local = new SimpleLocalAgent();
			deployedAgents.add(local.getIdentity().getID());
			ctx.deployAgent(local);
		}
		getAgentLogger().info("Agents deployed");
		
	}
	
	/**
	* retireGroup
	*/
	public void retireGroup(){
		
	}
	
	/**
	* stopGroup
	*/
	public void stopGroup(){
		for(Uuid id : deployedAgents){			
			
		}
	}
	
	/**
	* completeGroup
	*/
	public void completeGroup(){
	}
	
	/**
	* getDeployedIDs
	* @return AgentIdentity []
	*/
	public AgentIdentity [] getDeployedIDs(){
		return null;
	}
	
}
