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
 * SimpleProxyFactory.java
 * Created on 23-Sep-2003
 */
package org.jini.projects.neon.dynproxy.tests;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;



/**
 * @author calum
 */
public class SimpleProxyFactory {

	/**
	 * 
	 */
	public SimpleProxyFactory() {
		super();
		// TODO Complete constructor stub for SimpleProxyFactory
	}
	public static SimpleProxyInterface create(SimpleProxyInterface receiver) {
		return (SimpleProxyInterface) Proxy.newProxyInstance(SimpleProxyFactory.class.getClassLoader(), new Class[] { SimpleProxyInterface.class }, new SimpleDynamicProxy(receiver));
	}
	/**
	 * @param args
	 */
	public static void main(String args[]){
		SimpleProxyInterface intf = SimpleProxyFactory.create(new SimpleProxiedClass());
		if (intf instanceof Proxy){
		
			System.out.println("This is a simple dynamic proxy");
			InvocationHandler h = Proxy.getInvocationHandler(intf);
			SimpleDynamicProxy p = (SimpleDynamicProxy) h;
			p.hello();
		}		else{
			System.out.println("Superclass: " + intf.getClass().getSuperclass().getName());
			System.out.println("Interfaces.....:");
			Class[] intfs = intf.getClass().getInterfaces();
			for(int i=0;i<intfs.length;i++)
				System.out.println("Interface: " + intfs[i].getClass().getSuperclass().getName());
		}
		
		System.out.println("Returning: " + intf.sayHello("Calum"));
	}
}
