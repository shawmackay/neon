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

package org.jini.projects.neon.users.util;

import java.io.Serializable;
import java.util.Map;

import net.jini.id.Uuid;
import net.jini.id.UuidFactory;

/**
 * @author calum
 */
public class UserInfo implements Serializable{
	Uuid internalID;
	char[] password;
	String username;
	Map attributes;
	public static final int SYNC_OVERWRITE=1;
	public static final int SYNC_MERGE=2;
	
	/**
	 * @param password
	 * @param username
	 * @param attributes
	 */
	public UserInfo(String username, char[] password, Map attributes) {
		super();
		this.password = password;
		this.username = username;
		this.attributes = attributes;
	}
	public Map getAttributes() {
		return attributes;
	}
	public void setAttributes(Map attributes, int syncoption) {
		switch(syncoption){
			case SYNC_OVERWRITE:
				this.attributes =attributes;
				break;
			case SYNC_MERGE:
				this.attributes.putAll(attributes);
				break;			
		}		
	}
	
	public char[] getPassword() {
		return password;
	}
	public void setPassword(char[] password) {
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public Uuid getInternalID(){
		if(this.internalID==null) {
			this.internalID = UuidFactory.generate();
		}
		return this.internalID;	
	}
	
	
}
