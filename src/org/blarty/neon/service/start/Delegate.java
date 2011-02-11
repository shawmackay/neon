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

package org.jini.projects.neon.service.start;

import java.io.Serializable;

/*
* Delegate.java
*
* Created Fri Mar 18 11:44:56 GMT 2005
*/

/**
*
* @author  calum
*
*/

public class Delegate implements Serializable{
	private String type;
	private String interfaceclass;
	private String delegateclass;
	/**
	* Delegate
	* @param type name defining what kind of delegate this is
	* @param interfaceclass the interface that the dynamic proxy will implement
	* @param delegateclass the nam of the class that will handle the invocation on behalf of the agent
	*/
	public Delegate(String type, String interfaceclass, String delegateclass){
		this.type=type;
		this.interfaceclass=interfaceclass;
		this.delegateclass =delegateclass;
	}
	/**
	* getType
	* @return String
	*/
	public String getType(){
		return type;
	}
	/**
	* setType
	* @param type
	*/
	public void setType(String type){
		this.type=type;
	}
	/**
	* getInterfaceclass
	* @return String
	*/
	public String getInterfaceclass(){
		return interfaceclass;
	}
	/**
	* setInterfaceclass
	* @param interfaceclass
	*/
	public void setInterfaceclass(String interfaceclass){
		this.interfaceclass=interfaceclass;
	}

	/**
	* getDelegateclass
	* @return String
	*/
	public String getDelegateclass(){
		return delegateclass;
	}
	/**
	* setDelegateclass
	* @param delegateclass
	*/
	public void setDelegateclass(String delegateclass){
		this.delegateclass=delegateclass;
	}

}
