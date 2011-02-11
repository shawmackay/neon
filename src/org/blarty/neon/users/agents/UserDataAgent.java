/*
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

package org.jini.projects.neon.users.agents;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.LoginException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;

import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.render.EngineInstruction;
import org.jini.projects.neon.render.RenderAgent;
import org.jini.projects.neon.users.util.AuthToken;
import org.jini.projects.neon.users.util.UserInfo;
import org.jini.projects.neon.render.PresentableAgent;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author calum
 */
public class UserDataAgent extends AbstractAgent implements UserDataIntf,PresentableAgent {
        private transient KeyStoreUtility ksHandler;

        private HashMap<String, UserInfo> userdata;

        private HashMap session;

        /**
         * Comment for <code>serialVersionUID</code>
         */
        private static final long serialVersionUID = 3978711713707209785L;

        public UserDataAgent() {
                this.namespace = "neon";
                this.name = "UserData";

        }

        public Uuid getIDForUser(String username) {
                return null;
        }

        public UserInfo getUserInformation(String query) {
                if (query.indexOf("=") != -1) {
                        String[] parts = query.split("=");
                        for (UserInfo ui : userdata.values()) {
                                Map m = ui.getAttributes();
                                if (m.get(parts[0]).equals(parts[1]))
                                        return ui;
                        }
                } else {
                        return userdata.get(query);
                }
                return null;
        }

        public EngineInstruction getPresentableFormat(String type, String action, Map params, boolean getTemplate) {
                // TODO Complete method stub for getPresentableFormat
                try {
                        DocumentBuilderFactory docbf = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = docbf.newDocumentBuilder();
                        Document d = builder.newDocument();
                        Element el = d.createElement("users");

                        d.appendChild(el);
                        for (Iterator iter = userdata.entrySet().iterator(); iter.hasNext();) {
                                Map.Entry entr = (Map.Entry) iter.next();
                                UserInfo info = (UserInfo) entr.getValue();
                                Element userEl = d.createElement("user");
                                userEl.setAttribute("name", (String) entr.getKey());
                                userEl.setAttribute("password", String.valueOf(info.getPassword()));
                                Map m = info.getAttributes();
                                Element attrElHolder = d.createElement("attributes");
                                userEl.appendChild(attrElHolder);
                                for (Iterator attrIter = m.entrySet().iterator(); attrIter.hasNext();) {
                                        Map.Entry attribute = (Map.Entry) attrIter.next();
                                        Element attrEl = d.createElement("attribute");
                                        attrEl.setAttribute("name", (String) attribute.getKey());
                                        attrEl.setAttribute("value", (String) attribute.getValue());
                                        attrElHolder.appendChild(attrEl);
                                }
                                el.appendChild(userEl);
                        }

                        Source xslsource = new StreamSource(new StringReader(getStyleSheet()));
                        Source xmlsource = new DOMSource(d);
                        StringWriter writer = new StringWriter();
                        StreamResult xmlResult = new StreamResult(writer);
                        TransformerFactory factory = TransformerFactory.newInstance();
                        Transformer trans = factory.newTransformer(xslsource);
                        trans.transform(xmlsource, xmlResult);

                        return new EngineInstruction("xsl",getTemplate("xml",action),writer.getBuffer().toString());
                } catch (ParserConfigurationException e) {
                        // TODO Handle ParserConfigurationException
                        e.printStackTrace();
                } catch (TransformerConfigurationException e) {
                        // TODO Handle TransformerConfigurationException
                        e.printStackTrace();
                } catch (TransformerException e) {
                        // TODO Handle TransformerException
                        e.printStackTrace();
                }
                return new EngineInstruction("xsl",getTemplate("xml", action),"bad");
        }

        public boolean addUser(String username, char[] password, Map attributes) {
                this.getAgentLogger().info("Checking user: " + username);
                if (userdata.containsKey(username))
                        return false;
                this.getAgentLogger().info("Adding a user: " + username);

                userdata.put(username, new UserInfo(username, password, attributes));

                return true;
        }

        public boolean containsUser(String username) {
                return userdata.containsKey(username);
        }

        public boolean deleteUser(String username) {
                Object o = userdata.remove(username);
                return (o != null);
        }

        public boolean modifyUser(String username, char[] existingPassword, char[] newPassword, Map attributes, int syncoption) {
                UserInfo info = (UserInfo) userdata.get(username);
                if (info == null)
                        return false;
                if (newPassword != null) {
                        info.setPassword(newPassword);
                }
                if (attributes != null) {
                        info.setAttributes(attributes, syncoption);
                }
                return true;
        }

        /*
         * @see org.jini.projects.neon.agents.Agent#init()
         */
        public boolean init() {

                userdata = new HashMap<String, UserInfo>();
                session = new HashMap();
             
             
                try {
                        try {
                                Configuration config = getAgentConfiguration();
                                String ksLoc = (String) config.getEntry("neon.UserData", "keystoreLocation", String.class);
                                String ksType = (String) config.getEntry("neon.UserData", "keystoreType", String.class);
                                String ksPass = (String) config.getEntry("neon.UserData", "keystorePassword", String.class);
                                ksHandler = new KeyStoreUtility(ksType, ksLoc, ksPass.toCharArray());
                        } catch (ConfigurationException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }
//                        RenderAgent render = (RenderAgent) context.getAgent("Renderer");
//                        render.registerPresentation(this, new File("conf/useractions.xsl").toURL());
//                        getAgentLogger().finer("User Data XSL Registered");
                } catch (Exception ex) {
                        System.err.println("Caught Exception: " + ex.getClass().getName() + "; Msg: " + ex.getMessage());
                        ex.printStackTrace();
                }

                return true;
        }

        public URL getTemplate(String type, String action) {
                if (type.equals("html") || type.equals("xml"))
                        try {
                                return new File("conf/useractions.xsl").toURL();
                        } catch (MalformedURLException e) {
                                // TODO Handle MalformedURLException
                                e.printStackTrace();
                        }
                return null;
        }

        public AuthToken authenticateUser(String username, char[] providedPassword) {
               

                if (ksHandler.authenticateAlias(username, providedPassword)) {
                        System.out.println("Authenticated via KeyStore");
                        AuthToken tok = new AuthToken(UuidFactory.generate());
                        session.put(tok.getAuthToken(), new UserInfo(null,new char[0],null));
                        return tok;
                } else {
                        System.out.println("User " + username + " not authenticated with password " + providedPassword);
                }
               
                return null;
        }

        public Map getAttributes(String username) {
                // if(session.containsKey(userSession.getAuthToken()))
                // return ((UserInfo)
                // session.get(userSession.getAuthToken())).getAttributes();
                // else
                return null;
        }

        public Set getUserNames() {
                this.getAgentLogger().info("getting Usernames");
                for (String name : userdata.keySet())
                        System.out.println("Name: " + name);
                Set s = new HashSet(userdata.keySet());
                System.out.println("Set size: " + s.size());
                return s;
        }

        private String getStyleSheet() {
                // A simple identity stylesheet renders a DOM tree ut as an XML
                // string
                // which can then be passed back
                return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<xsl:stylesheet version=\"1.0\" xmlns:xsl=" + "\"http://www.w3.org/1999/XSL/Transform\">" + "<xsl:output method=\"xml\" indent=\"yes\"/>" + "<xsl:template match=\"@*|node()\">" + "<xsl:copy>" + "<xsl:apply-templates select=\"@*|node()\"/>" + "</xsl:copy>" + "</xsl:template>" + "</xsl:stylesheet>";
        }
}
