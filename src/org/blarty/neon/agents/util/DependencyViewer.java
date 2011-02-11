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
 * neon : org.jini.projects.neon.agents.util
 * DependencyViewer.java
 * Created on 22-Oct-2003
 */
package org.jini.projects.neon.agents.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author calum
 */
public class DependencyViewer {

    /**
     * 
     */
    public DependencyViewer() {
        super();
        // URGENT Complete constructor stub for DependencyViewer
    }

    public static void main(String[] args) {
        File f = new File(args[0]);
        Map m = dependentAgents(f);
        Iterator iter = m.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry ent = (Map.Entry) iter.next();
            List l = (List) ent.getValue();
            System.out.println("Agent: " + ent.getKey());
            for (Iterator listiter = l.iterator(); listiter.hasNext();)
                System.out.println("\t" + listiter.next());
        }
    }
    public static Map dependentAgents(File f) {
        HashMap deps = new HashMap();
        try {
            BufferedReader rdr = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String line = null;
            do {
                line = rdr.readLine();
                Pattern p = Pattern.compile("context\\)?.sendMessage\\(\\\"[A-Za-z0-9]*\\\"[{punct}|{blank}|,]*.*\\\"");
                if (line != null) {
                    Matcher m = p.matcher(line);
                    if (m.find()) {
                        String dat = m.group().trim();

                        String[] items = dat.split("context\\)?.sendMessage\\(|\\\"|(,( )?)|new (Collaboration)?Message\\(");
                        String agentName = null;
                        String messageName = null;
                        for (int i = 0; i < items.length; i++) {
                            if (!items[i].equals("")) {

                                if (agentName != null && messageName == null)
                                    messageName = items[i];
                                if (agentName == null)
                                    agentName = items[i];
                            }
                        }
                        if (deps.containsKey(agentName)) {
                            List l = (List) deps.get(agentName);
                            if(!l.contains(messageName))
                                l.add(messageName);
                        } else {
                            List l = new ArrayList();
                            l.add(messageName);
                            deps.put(agentName, l);
                        }
                       // System.out.println(line);
                    }
                }
            } while (line != null);
        } catch (FileNotFoundException e) {
            // URGENT Handle FileNotFoundException
            e.printStackTrace();
        } catch (IOException e) {
            // URGENT Handle IOException
            e.printStackTrace();
        }
        return deps;
    }

}
