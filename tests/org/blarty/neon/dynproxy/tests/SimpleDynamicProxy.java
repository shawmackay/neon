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
 * neon : org.jini.projects.neon.dynproxy
 * SimpleDynamicProxy.java
 * Created on 23-Sep-2003
 */
package org.jini.projects.neon.dynproxy.tests;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * @author calum
 */
public class SimpleDynamicProxy implements InvocationHandler {
	SimpleProxyInterface receiver;
	/**
	 * 
	 */
	public SimpleDynamicProxy(SimpleProxyInterface receiver) {
		super();
		// TODO Complete constructor stub for SimpleDynamicProxy
		this.receiver = receiver;
	}

	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("Invoking now");
		try {
			System.out.println("Receiving object: "  + proxy.getClass().getName());
			return method.invoke(receiver, args);
		} catch (IllegalArgumentException e) {
			// TODO Handle IllegalArgumentException
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Handle IllegalAccessException
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Handle InvocationTargetException
			e.printStackTrace();
		}
		return null;
	}
	
	public void hello(){
		System.out.println("Hello yourself");
	}

}
