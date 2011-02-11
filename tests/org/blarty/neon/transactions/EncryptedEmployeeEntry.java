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


package org.jini.projects.neon.transactions;

/*
* EmployeeEntry.java
*
* Created Mon Mar 21 11:52:02 GMT 2005
*/

import net.jini.entry.AbstractEntry;
import org.jini.projects.neon.util.encryption.CryptoHolder;
import org.jini.projects.neon.util.encryption.EncryptedHolder;

/**
*
* @author  calum
*
*/

public class EncryptedEmployeeEntry extends AbstractEntry implements CryptoHolder{
	public Integer employeeReference;
	public EncryptedHolder encryptedVersion;
	
	//Entry fields must be public non-primitive types
	
	public EncryptedEmployeeEntry(){
		//required public no-args constructor
	}

	public EncryptedHolder getEncryptedObject() {
		// TODO Auto-generated method stub
		return encryptedVersion;
	}

	public void setEncryptedObject(EncryptedHolder encrypted) {
	this.encryptedVersion = encrypted;
	}

  
	
	

}
