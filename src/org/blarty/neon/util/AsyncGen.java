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

/*
 * AsyncGen.java
 *Created Wed Apr 13 11:52:03 BST 2005
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jini.projects.neon.collaboration.Collaborative;

/**
 * Generates an asynchronous version of an interface.<br>
 * Runs with up to two parameters:
 * <ol><li><strong>classname</strong> - name of interface to use as the basis for generating an asynchronous interface</li>
 * <li><strong>outputdir</strong> - (optional) name of base output directory for interface source file to be generated to, if missing
 * or set to <code>out</code> will write interface to stdout</li>
 * </ol>
 * The package for the asynchronous interface is always the same as the package specified in the orignal interface<br>
 *@author calum
 */

public class AsyncGen {
    public static void main(String[] args) {
        if(args.length==0){
            System.out.println("usage: asyncgen classToGenerateFrom [outputDir]");
            System.exit(0);
        }
            
        String classname = args[0];
        if (args.length > 1) {
            String outputDir = args[1];
            new AsyncGen(classname, outputDir);
        } else
            new AsyncGen(classname, null);
    }

    public AsyncGen(String classname, String outputDir) {
        Class cl;
        try {
            cl = Class.forName(classname);
            buildDefinition(cl, outputDir);         
        } catch (Exception ex) {
            System.err.println("AsyncGen cannot handle " + classname + " because " + ex.getClass().getName() + " exception occured\n" + "with message: " + ex.getMessage());
            ex.printStackTrace();
        }

    }

    private void buildDefinition(Class cl, String outputDir) throws IOException{
        ArrayList makeAsyncMeths = new ArrayList();

        getInterfaces(cl, makeAsyncMeths);
        List asyncClasses = getCollaborativeMethods(makeAsyncMeths);
        for (int i = 0; i < asyncClasses.size(); i++) {            
            ClassDefHolder cdh = (ClassDefHolder) asyncClasses.get(i);
            StringBuffer b = this.generateAsyncInterface(cdh.getPackagename(), cdh.getClassname(), cdh.getMeths());
            if (outputDir != null && !(outputDir.equals("out"))) {
                String outputPkgDir = cdh.getPackagename().replace('.', java.io.File.separatorChar);
                
                File outputDirectory = new File(outputDir);
                File outputFileDir = new File(outputDirectory, outputPkgDir);
                if (!outputFileDir.exists()) {
                    outputFileDir.mkdirs();
                    System.out.println(outputFileDir.getAbsolutePath() + " created");
                }
                File javaFile = new File(outputFileDir, "Async" + cdh.getClassname() + ".java");
                BufferedWriter writer = new BufferedWriter(new FileWriter(javaFile));
                writer.write(b.toString());
                writer.flush();
                writer.close();
                System.out.println("Generated " + cdh.getPackagename() + ".Async" + cdh.getClassname() +"(" + b.toString().getBytes().length + " bytes) to " + javaFile.getAbsolutePath());
            } else 
                System.out.println(b.toString());

        }
  
    }

    public List getCollaborativeMethods(ArrayList arr) {
        ArrayList methList = new ArrayList();
        for (Iterator iter = arr.iterator(); iter.hasNext();) {
            Class cl = (Class) iter.next();
            ClassDefHolder cdh = new ClassDefHolder();
            cdh.setClass(cl);
            Method[] methods = cl.getMethods();
            for (int i = 0; i < methods.length; i++)
                if (!methList.contains(methods[i])) {
                    if (methods[i].getDeclaringClass().equals(cl))
                        cdh.getMeths().add(methods[i]);
                }
            methList.add(cdh);

        }
        return methList;
    }

    private void getInterfaces(Class cl, ArrayList arr) {
        Class[] classes = cl.getInterfaces();

        for (int i = 0; i < classes.length; i++) {
            Class subclass = classes[i];
            if (subclass.isInterface()) {
                getInterfaces(subclass, arr);
            }
            if (subclass.equals(Collaborative.class)) {
                if (!arr.contains(cl))
                    arr.add(cl);
            }
        }

        // arr.add(Serializable.class);

    }

    private StringBuffer generateAsyncInterface(String pkg, String intfName, List methods) {
        StringBuffer b = new StringBuffer();
        b.append("package " + pkg + ";\n\n");
        b.append("/*\n * Generated by AsyncGen\n");
        b.append(" * Original interface : " + intfName + "\n");
        b.append(" * Generated @ " + new java.util.Date() + "\n");

        b.append(" */\n\n");
        b.append("public interface Async" + intfName + "{\n");
        for (int i = 0; i < methods.size(); i++) {
            Method m = (Method) methods.get(i);
            b.append("\tpublic void " + m.getName() + "(");
            for (int j = 0; j < m.getParameterTypes().length; j++) {
                Class param = m.getParameterTypes()[j];
                b.append(param.getName() + " a");
                b.append(param.getName().substring(param.getName().lastIndexOf(".") + 1) + ", ");
            }
            b.append(" org.jini.projects.neon.host.AsyncHandler contextHandler);\n\n");
        }
        b.append("}\n");
        return b;
    }

    private class ClassDefHolder {
        private String classname;

        private String packagename;

        private ArrayList<Method> meths = new ArrayList<Method>();;

        public void setClass(Class cl) {
            this.classname = cl.getName().substring(cl.getName().lastIndexOf('.') + 1);
            this.packagename = cl.getPackage().getName();

        }

        public String getClassname() {
            return classname;
        }

        public void setClassname(String classname) {
            this.classname = classname;
        }

        public ArrayList<Method> getMeths() {
            return meths;
        }

        public void setMeths(ArrayList<Method> meths) {
            this.meths = meths;
        }

        public String getPackagename() {
            return packagename;
        }

        public void setPackagename(String packagename) {
            this.packagename = packagename;
        }

    }
}
