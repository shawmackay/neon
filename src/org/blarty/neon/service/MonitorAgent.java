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
 * neon : org.jini.projects.neon.service
 * MonitorAgent.java
 * Created on 16-Jul-2004
 *MonitorAgent
 */

package org.jini.projects.neon.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.jini.glyph.Exportable;

import org.jini.projects.neon.collaboration.Collaborative;

/**
 * @author calum
 */

public interface MonitorAgent extends Collaborative, Remote {
	public abstract String getInformation(String category) throws RemoteException;

	public String getAgentInformation(String agentID) throws RemoteException;

	public String getAgentLogInformation(String agentID) throws RemoteException;
}