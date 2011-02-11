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
 * neon : org.jini.projects.neon.web
 * EmbeddedTomcat.java
 * Created on 15-Jul-2004
 *EmbeddedTomcat
 */

package org.jini.projects.neon.web;

import java.io.File;
import java.util.logging.Logger;

import net.jini.config.Configuration;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * @author calum
 */
public class EmbeddedJetty
                implements
                IndependentPluggableResource {
        private String path = null;


        Logger log = Logger.getLogger("org.jini.projects.neon.web");

        boolean runEmbeddedVersion = false;

        private org.mortbay.jetty.Server server;

        public EmbeddedJetty(Configuration configuration) throws UnsupportedOperationException {

                try {
//                        String tomcatBase = (String) configuration.getEntry("org.jini.projects.neon", "tomcatBase", String.class, null);
//                        log.fine("Tomcat Base path is: " + tomcatBase);
//                        setPath(tomcatBase);
//                        System.setProperty("catalina.home", getPath());
//                        System.setProperty("catalina.base", getPath());
//
//                        runCatalina();
//                        
                    String warlocation = (String) configuration.getEntry("org.jini.projects.neon", "warLocation", String.class, "lib-war/neonadmin.war");
                    String renderContext= (String) configuration.getEntry("org.jini.projects.neon", "renderContext", String.class, "lib-war/neonadmin.war");
                    
                    File warFile = new File(warlocation);
                    log.fine("Loading war from : " + warlocation);
                    server = new Server();
                    org.mortbay.jetty.nio.SelectChannelConnector connector = new SelectChannelConnector();
                    int portNum = ((Integer) configuration.getEntry("org.jini.projects.neon", "httpdPort", int.class, Integer.valueOf(8080))).intValue();
                    connector.setPort(portNum);
                    server.setConnectors (new Connector[]{connector});
                    WebAppContext wah = new WebAppContext();
                    wah.setContextPath("/neon");
                    wah.setWar(warFile.toURL().toExternalForm());
                    wah.setAttribute("renderContext", renderContext);
                  
                    server.addHandler(wah);
                    server.setStopAtShutdown(true);
                } catch (Exception e) {
                        // TODO Handle ConfigurationException
                        e.printStackTrace();
                }

        }

        
        public void start() throws Exception {
            log.fine("Jetty Server Starting");
            server.start();
            log.fine("Jetty Server Started");
        }

        public void stop() throws Exception {

        }

        public void initialise(Configuration config) {
                // TODO Auto-generated method stub
                
        }

}
