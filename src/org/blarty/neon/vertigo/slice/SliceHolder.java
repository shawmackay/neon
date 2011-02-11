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
 * neon : org.jini.projects.neon.vertigo.slice
 * SliceHolder.java
 * Created on 09-May-2005
 *SliceHolder
 */
package org.jini.projects.neon.vertigo.slice;

import java.io.Serializable;

/**
 * @author calum
 */
public class SliceHolder implements Serializable{
	private Slice slice;

	public Slice getSlice() {
		return slice;
	}
	

	public void setSlice(Slice slice) {
		this.slice = slice;
	}


	public SliceHolder(Slice slice) {
		super();
		// TODO Complete constructor stub for SliceHolder
		this.slice = slice;
	}
	
	
}
