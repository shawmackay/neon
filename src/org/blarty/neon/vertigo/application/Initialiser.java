
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
 * neon : org.jini.projects.neon.vertigo.application
 * Initialiser.java
 * Created on 09-May-2005
 *Initialiser
 */
package org.jini.projects.neon.vertigo.application;

import java.io.Serializable;

import org.jini.projects.neon.vertigo.slice.Slice;

/**
 * @author calum
 */
public interface Initialiser extends Serializable{
	public boolean init(Slice app);
}
