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

package org.jini.projects.neon.vertigo.management;

/*
* SIDEntry.java
*
* Created Thu Jun 02 11:04:53 BST 2005
*/

import net.jini.core.lookup.ServiceID;

import net.jini.entry.AbstractEntry;

/**
*
* @author  calum
*
*/

public class SIDEntry extends AbstractEntry{
	
	public net.jini.core.lookup.ServiceID sid;
	
	//Entry fields must be public non-primitive types
	
	public SIDEntry(){
		//required public no-args constructor
	}
	
	/**
	* SIDEntry
	* @param sid
	*/
	public SIDEntry(ServiceID sid){
		this.sid=sid;
	}

}
