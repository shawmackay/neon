
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
 * neon : org.jini.projects.neon.dynproxy.invoker
 * InvokerDelegat.java
 * Created on 11-Nov-2003
 *InvokerDelegat
 */
package org.jini.projects.neon.dynproxy.invoker;

import java.lang.reflect.Method;

import org.jini.projects.neon.host.ManagedDomain;


/**
 * Handles neon method interception
 * @see org.jini.projects.neon.dynproxy.FacadeProxy 
 * @author calum
 */
public interface InvokerDelegate {
    public Object invokeDelegate(Method m, Object source, Object originalObject ,Object receiver, Object[] args) throws Throwable;
    public void setDomain(ManagedDomain domain);
}
