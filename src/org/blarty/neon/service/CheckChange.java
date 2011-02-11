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


import java.io.File;
/*
* CheckChange.java
*
* Created Tue Jan 11 14:51:09 GMT 2005
*/

/**
*
* @author  calum
*
*/

public class CheckChange{
	public static void main(String[] args){
		System.out.println("User.dir is " + System.getProperty("user.dir"));	
		File f= new File("simple");
		System.out.println("File path is " + f.getAbsolutePath());
	}
	
}
