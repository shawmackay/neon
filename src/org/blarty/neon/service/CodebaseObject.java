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


package org.jini.projects.neon.service;


import java.rmi.server.RMIClassLoader;

import net.jini.io.MarshalledInstance;

public class CodebaseObject implements java.io.Serializable{
	private MarshalledInstance marshalled;
	private String codebaseAnnotation;
	public CodebaseObject(Object o) throws java.io.IOException{
		codebaseAnnotation = RMIClassLoader.getClassAnnotation(o.getClass());
		marshalled = new MarshalledInstance(o);
	}
	 
	public MarshalledInstance getMarshalled(){
		return marshalled;
	}
	
	public void setMarshalled(MarshalledInstance marshalled){
		this.marshalled=marshalled;
	}
	
	public String getCodebaseAnnotation(){
		return codebaseAnnotation;
	} 
	
	public void setCodebaseAnnotation(String codebaseAnnotation){
		this.codebaseAnnotation=codebaseAnnotation;
	}
	
}
