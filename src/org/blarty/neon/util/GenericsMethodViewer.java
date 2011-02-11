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

package org.jini.projects.neon.util;

import java.lang.reflect.Method;
/**
 * Provides the 'short' generic method signature<br>
 * The JLS specifies that a method must differ in the parameters it takes, differing by return type alone is not enough
 * This utility allows you to generate the method signatures required for <code>InvocationMessage</code>s, when
 * invoking agent methods using low-level Zenith calls. Requires J2SE5
 * @author Calum
 *
 */
public class GenericsMethodViewer {
    public static void main(String[] args) throws Exception{
        String classname =args[0];
        Class cl = Class.forName(args[0]);
        System.out.println("Methods described in Class: " + cl.getName());
        Method[] meths = cl.getMethods();
        for (int i=0;i<meths.length;i++){
            Method m = meths[i];
            System.out.println("\n("+m.getName()+"): " +m.toGenericString());
            System.out.println(getMethodShortGenericString(m));
        }
    }
  
  
    
    /**
     * Used to get a method signature. Because Java will treat methods that
     * have the same parameter list and names, but different return types as
     * an error, we can use just the name and parameter signature, as a
     * way of mathcing methods together.  
     * @param m the method to extract the short signature from
     * @return a String in the format %lt;method_name>(%lt;parameter list>)
     */
   public static String getMethodShortGenericString(Method m){
       String[] parts = m.toGenericString().split(m.getDeclaringClass().getName() + "." + m.getName());
       StringBuffer rebuild = new StringBuffer();
       String modifiers = parts[0];
       String parameters = parts[1];
       rebuild.append(m.getName());
       rebuild.append(parameters);
       return rebuild.toString();   
    }
}