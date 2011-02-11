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



package org.jini.projects.neon.agents.sensors;

/*
* FileSensorFilter.java
*
* Created Mon Feb 28 11:09:40 GMT 2005
*/

/**
*
* @author  calum
*
*/

public class FileSensorFilter implements SensorFilter{
	private String regex;
	private String directory;
	public FileSensorFilter(String directory, String regex){
		this.regex = regex;
		this.directory = directory;
	}

	

	/**
	* Always accept as files will have been filtered already by Themis
	* @param aObject
	* @return boolean
	*/
	public boolean accept(Object  aObject){
		return true;
	}

	/**
	* getRegex
	* @return String
	*/
	public String getRegex(){
		return regex;
	}


	/**
	* getDirectory
	* @return String
	*/
	public String getDirectory(){
		return directory;
	}


}
