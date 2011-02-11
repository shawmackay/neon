
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
* DelegateCatalog.java
*
* Created Fri Mar 18 11:44:25 GMT 2005
*/

/**
*
* @author  calum
*
*/

public class DelegateCatalog {
	private java.util.List<Delegate> delegates;
	
	/**
	* getDelegates
	* @return java.util.List
	*/
	public java.util.List getDelegates(){
		return delegates;
	}
	/**
	* setDelegates
	* @param delegates
	*/
	public void setDelegates(java.util.List delegates){
		this.delegates=delegates;
	}

	public void addDelegate(Delegate delegate){
		delegates.add(delegate);
	}
	/**
	* DelegateCatalog
	* @param delegates
	*/
	public DelegateCatalog(java.util.List delegates){
		this.delegates=delegates;
	}
	public DelegateCatalog(){
		this.delegates = new java.util.ArrayList<Delegate>();	
	}
	
}
