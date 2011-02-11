
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
package org.jini.projects.neon.host;

import java.lang.reflect.Method;

/*
* AsyncHandler.java
*
* Created Tue Apr 12 14:43:59 BST 2005
*/

/**
* Any method call to an asynchronous agent proxy, will have all return values sent to the replied method
* @author  calum
*
*/

public interface AsyncHandler{
	public void replied(Object handback, Method handbackMethod, boolean isException);
}
