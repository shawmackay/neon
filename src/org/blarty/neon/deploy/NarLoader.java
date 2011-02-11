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

package org.jini.projects.neon.deploy;

/*
 * NarLoader.java
 * 
 * Created Wed Mar 16 10:37:12 GMT 2005
 */

// import com.sun.jini.start.ClassLoaderUtil;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 
 * @author calum
 * 
 */

public class NarLoader {
    private Random r = new Random(System.currentTimeMillis());

    private File narDirectory = null;

    public NarLoader() {
        narDirectory = new File("nar");
        if (!narDirectory.exists()) {
            narDirectory.mkdir();
        }

    }

    /**
     * Deploys a Nar into a specified thread
     */
    public URL[] deployNar(URL narLocation) {
        ArrayList<URL> fileURLList = new ArrayList<URL>();

        String loc = downloadJarFile(narLocation);
        File expandedDir = new File(loc + "_dir");
        try {
            expandJarFile(loc);
            File classesBase = new File(loc + "_dir" + File.separator);
            
            File[] listing = expandedDir.listFiles();
            for (int i = 0; i < listing.length; i++) {
                try {
                    if (listing[i].getAbsolutePath().endsWith(".pack")) {
                        fileURLList.add(new URL("pack: " + listing[i].toURL()));
                        // fileURLList.add(new URL("jar:" +
                        // listing[i].toURL().toString()+"!/"));
                    } 
                } catch (Exception ex) {
                    System.err.println("Caught Exception: " + ex.getClass().getName() + "; Msg: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            System.err.println("Caught Exception: " + ex.getClass().getName() + "; Msg: " + ex.getMessage());
            ex.printStackTrace();
        }
        //System.out.println(loc);

        File expandedLibDir = new File(expandedDir, "lib");

        try {
            fileURLList.add(expandedDir.toURL());
        } catch (MalformedURLException e) {
            // TODO Handle MalformedURLException
            e.printStackTrace();
        }
        if (expandedLibDir.exists()) {
            File[] listing = expandedLibDir.listFiles();
            for (int i = 0; i < listing.length; i++) {
                try {
                    if (listing[i].getAbsolutePath().endsWith(".pack")) {
                        fileURLList.add(new URL("pack: " + listing[i].toURL()));
                        // fileURLList.add(new URL("jar:" +
                        // listing[i].toURL().toString()+"!/"));
                    } else
                        fileURLList.add(listing[i].toURL());
                } catch (Exception ex) {
                    System.err.println("Caught Exception: " + ex.getClass().getName() + "; Msg: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        } else {
            System.err.println("Expanded Lib Directory: " + expandedLibDir.getAbsolutePath() + " does not exist");
        }
        URL[] urls = (URL[]) fileURLList.toArray(new URL[] {});
        return urls;
    }

    private void expandJarFile(String fileName) throws IOException {
        /*
         * We expand the Jar file, only if the filesize
         */
        File dlDirectory = new File(fileName + "_dir");
        //System.out.println("Creating Directory " + dlDirectory.getAbsolutePath());
        if (!dlDirectory.exists()) {
            dlDirectory.mkdir();
        }
        JarFile jf = new JarFile(fileName);
        Enumeration entries = jf.entries();
        while (entries.hasMoreElements()) {
            JarEntry je = (JarEntry) entries.nextElement();
            String name = je.getName();
            //System.out.println("Creating " + name);
            String dirname;
            if (name.indexOf("/") == -1)
                dirname = ".";
            else
                dirname = new String(name.substring(0, name.lastIndexOf("/")));
            if (je.isDirectory()) {
                File dir = new File(dlDirectory, name);
                if (!dir.exists())
                    dir.mkdirs();
            } else {
                File dir = new File(dlDirectory, dirname);
                if (!dir.exists())
                    dir.mkdirs();

                File outputfile = new File(dlDirectory, name);
                copyStream(jf.getInputStream(je), new FileOutputStream(outputfile));
            }
        }

    }

    private String downloadJarFile(URL location) {
        try {
            URL earl = new URL("jar:" + location.toExternalForm() + "!/NAR-INF/nar.version");
            Object o = null;
            try {
                o = earl.getContent();
            } catch (Exception ex) {

            }
            String version = "";
            if (o != null) {
                BufferedReader bis = new BufferedReader(new InputStreamReader((InputStream) o));
                String line = bis.readLine();
                String[] parts = line.split(":");
                if (parts[0].trim().equals("version"))
                    version = parts[1].trim();
            }
            String mungedLocation = location.toExternalForm();
            mungedLocation = mungedLocation.replaceAll("/", "_");
            mungedLocation = mungedLocation.replaceAll(":", "_");
            String radnFileName = mungedLocation;
            if(!version.equals(""))
                radnFileName = radnFileName+"@" + version;
                
            File f = new File(narDirectory, radnFileName);
            URL u = location;

            BufferedInputStream is = new BufferedInputStream(u.openStream());
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
            copyStream(is, bos);
            return f.getAbsolutePath();
        } catch (Exception ex) {
            System.err.println("Caught Exception: " + ex.getClass().getName() + "; Msg: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    private void copyStream(InputStream in, OutputStream out) throws IOException {
        int numread = 0;
        byte[] buff = new byte[1024];
        while (numread != -1) {
            numread = in.read(buff, 0, 1024);
            if (numread != -1) {
                out.write(buff, 0, numread);
            }
        }
        out.flush();
        out.close();
        out.close();
    }
}
