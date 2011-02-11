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



package org.jini.projects.neon.agents;

/*
* PayloadAgent.java
*
* Created Fri Mar 18 10:13:14 GMT 2005
*/

/**
*
* A payload Agent is used to deploy and track multiple agents as a single unit 
* @author  calum
*
*/

public interface PayloadAgent{
	public void deployGroup();
	
	public void retireGroup();
	
	public void stopGroup();
	
	public void completeGroup();
	
	public AgentIdentity[] getDeployedIDs();
}
