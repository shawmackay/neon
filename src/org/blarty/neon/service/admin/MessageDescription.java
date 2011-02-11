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
 * neon : org.jini.projects.neon.service.admin
 * MessageDescription.java
 * Created on 22-Sep-2003
 */
package org.jini.projects.neon.service.admin;

import java.io.Serializable;

/**
 * @author calum
 */
public class MessageDescription implements Serializable {
	
	private String name;
	private String[] parameterTypes;
	private String returnType;
	/**
	 * 
	 */
	public MessageDescription() {
		
	}

	public MessageDescription(String name, String[] parameterTypes, String returnType) {
		this.name = name;
		this.parameterTypes = parameterTypes;
		this.returnType = returnType;			
	}
	
	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public String[] getParameterTypes() {
		return parameterTypes;
	}

	/**
	 * @return
	 */
	public String getReturnType() {
		return returnType;
	}

}
