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
 * neon : org.jini.projects.neon.web.sample
 *
 *
 * SimpleWebAgent.java
 * Created on 14-Mar-2005
 *
 * SimpleWebAgent
 *
 */

package org.jini.projects.neon.web.sample;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.LocalAgent;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.neon.render.BadFormatException;
import org.jini.projects.neon.render.EngineInstruction;
import org.jini.projects.neon.render.PresentableAgent;
import org.jini.projects.neon.render.RenderAgent;
import org.jini.projects.neon.web.WebUtils;

/**
 * @author calum
 */
public class SimpleWebAgent extends AbstractAgent implements PresentableAgent,LocalAgent,SimpleWeb {
        /**
         */

        public SimpleWebAgent() {
                this.namespace = "neon";
                this.name = "SimpleWeb";
        }

        private static final long serialVersionUID = 3544671767701632819L;

        Map map = new HashMap();

      
        public boolean init() {
                getAgentLogger().info("Loading SimpleWebAgent");
                try {
                        RenderAgent render = (RenderAgent) context.getAgent("Renderer");
                        java.net.URL earl = getStyleSheet();
                        if (earl == null)
                                getAgentLogger().info("URL is null");
                        try {
							render.registerPresentation(this, getStyleSheet());
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        map.put("A", "one");
                        map.put("B", "two");
                        map.put("C", "threee");

                        return true;
                } catch (NoSuchAgentException e) {
                        // TODO Handle NoSuchAgentException
                        e.printStackTrace();
                }

                return false;
        }

        public URL getTemplate(String type, String action) {
                // TODO Complete method stub for getTemplate
                if (type.equals("html") || type.equals("xml"))
                        return getStyleSheet();
                return null;
        }

        public EngineInstruction getPresentableFormat(String type, String action, Map params, boolean getTemplate) {

                SimpleWebDocument doc = new SimpleWebDocument();
                for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
                        Map.Entry entr = (Map.Entry) iter.next();
                        doc.addEntry((String) entr.getKey(), (String) entr.getValue());
                }
                for (Object o : params.entrySet()) {
                        Map.Entry entr = (Map.Entry) o;
                        if (entr.getValue() instanceof String)
                                doc.addParameter((String) entr.getKey(), (String) entr.getValue());
                        else
                                doc.addParameter((String) entr.getKey(), (String[]) entr.getValue());
                }
                
                try {
                        String ret =  WebUtils.convertObjectToXML("doc", doc);
                    
                        return new EngineInstruction("xsl",getTemplate("xml",action),ret);
                } catch (BadFormatException e) {
                        // TODO Auto-generated catch block
                       e.printStackTrace();
                }
                return null;
        }

        private java.net.URL getStyleSheet() {
                java.net.URL earl = this.getClass().getResource("rendering.xsl");
                if (earl == null)
                        System.err.println("URL for getStyleSheet is null");
                return earl;
        }

        public void update(Map options) {
                // TODO Auto-generated method stub
               
                String[] names = (String[]) options.get("name");
                String[] values = (String[]) options.get("value");

                for (int i = 0; i < names.length; i++) {

                        map.put(names[i], values[i]);
                }
        }
}
