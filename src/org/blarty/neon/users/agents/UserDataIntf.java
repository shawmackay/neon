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


package org.jini.projects.neon.users.agents;


import java.util.Map;
import java.util.Set;

import net.jini.id.Uuid;

import org.jini.projects.neon.collaboration.Collaborative;
import org.jini.projects.neon.users.util.AuthToken;
import org.jini.projects.neon.users.util.UserInfo;

/**
 * @author calum
 */
public interface UserDataIntf extends Collaborative{
	public boolean addUser(String username, char[] password, Map attributes);
	public boolean deleteUser(String username);
	public boolean modifyUser(String username, char[] existingPassword, char[] newPassword, Map attributes, int syncoption);
	public boolean containsUser(String username);
	public Set getUserNames();
	public AuthToken authenticateUser(String username, char[] providedPassword);
	public Map getAttributes(String username);
	public UserInfo getUserInformation(String username);
	public Uuid getIDForUser(String username);
}
