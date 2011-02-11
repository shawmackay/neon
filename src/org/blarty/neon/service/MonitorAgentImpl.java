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
 * neon : org.jini.projects.neon.service
 *
 *
 * MonitorAgentImpl.java
 * Created on 15-Jul-2004
 *
 * MonitorAgentImpl
 *
 */

package org.jini.projects.neon.service;

import java.io.File;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.jini.id.Uuid;
import net.jini.id.UuidFactory;

import org.jini.glyph.Exportable;
import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.LocalAgent;
import org.jini.projects.neon.annotations.ServiceBinding;
import org.jini.projects.neon.collaboration.Collaborative;
import org.jini.projects.neon.dynproxy.AgentProxyInfo;
import org.jini.projects.neon.host.AgentRegistry;
import org.jini.projects.neon.host.DomainRegistry;
import org.jini.projects.neon.host.ManagedDomain;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.neon.host.PrivilegedAgentContext;
import org.jini.projects.neon.host.transactions.TransactionalResource;
import org.jini.projects.neon.render.PresentableAgent;

import org.jini.projects.neon.vertigo.management.SliceAgent;
import org.jini.projects.neon.vertigo.slice.Slice;
import org.jini.projects.zenith.messaging.channels.ReceiverChannel;
import org.jini.projects.zenith.messaging.messages.Message;
import org.jini.projects.zenith.messaging.messages.MessageHeader;
import org.jini.projects.zenith.messaging.messages.StringMessage;
import org.jini.projects.zenith.messaging.system.ChannelException;
import org.jini.projects.zenith.messaging.system.MessagingListener;
import org.jini.projects.zenith.messaging.system.MessagingManager;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author calum
 */

@Exportable
@ServiceBinding(type="WS")
public class MonitorAgentImpl extends AbstractAgent implements MonitorAgent,LocalAgent {
    private Object methInvokeLog;
    
    public String getAgentLogInformation(String agentID) {
        System.out.println("Getting Agent Log Info");
        return "<log/>";
        //
        //                Uuid agentUuid = UuidFactory.create(agentID);
        //                Collection c = DomainRegistry.getDomainRegistry().getDomains();
        //                Iterator iter = c.iterator();
        //                try {
        //                        while (iter.hasNext()) {
        //                                ManagedDomain md = (ManagedDomain) iter.next();
        //                                // try {
        //                                Agent a = md.getRegistry().getAgent(agentUuid);
        //                                if (a != null) {
        //                                        ReceiverChannel channel = MessagingManager.getManager().getTemporaryChannel();
        //                                        try {
        //                                                channel.setReceivingListener(new MessagingListener() {
        //                                                        public void messageReceived(Message m) {
        //                                                                // TODO
        //                                                                // Auto-generated
        //                                                                // method stub
        //                                                                msgRecd(m);
        //                                                        }
        //                                                });
        //                                        } catch (ChannelException e) {
        //                                                // TODO Auto-generated catch
        //                                                // block
        //                                                e.printStackTrace();
        //                                        }
        //                                        MessageHeader header = new MessageHeader();
        //                                        header.setReplyAddress(channel.getName());
        //                                        md.getBus().sendDirectedMessage(agentUuid, new StringMessage(header, "methodstats"));
        //                                        synchronized (this) {
        //                                                try {
        //                                                        wait(0);
        //                                                } catch (InterruptedException e) {
        //                                                        // TODO Auto-generated
        //                                                        // catch block
        //                                                        e.printStackTrace();
        //                                                }
        //                                        }
        //                                        System.out.println("Notified!");
        //
        //                                }
        //                        }
        //                } catch (NoSuchAgentException e) {
        //
        //                }
        //                try {
        //                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //                        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        //                        Document doc = docBuilder.newDocument();
        //                        Element monitorElem = doc.createElement("log");
        //                        doc.appendChild(monitorElem);
        //
        //                        Map<String, Integer> methodLog = (Map<String, Integer>) methInvokeLog;
        //                        for (Map.Entry entr : methodLog.entrySet()) {
        //                                Element invokeElement = doc.createElement("invoke");
        //                                monitorElem.appendChild(invokeElement);
        //
        //                                addElement(invokeElement, "methodname", entr.getKey(), doc);
        //                                addElement(invokeElement, "numinvoked", entr.getValue(), doc);
        //                        }
        //                        Source xmlsource = new DOMSource(doc);
        //                        Source xsloutsource = new StreamSource(new File("conf/methodLog.xsl"));
        //                        StringWriter writer = new StringWriter();
        //                        StreamResult xmlResult = new StreamResult(writer);
        //                        TransformerFactory factory = TransformerFactory.newInstance();
        //                        Transformer trans = factory.newTransformer(xsloutsource);
        //                        trans.transform(xmlsource, xmlResult);
        //
        //                        return writer.getBuffer().toString();
        //                } catch (DOMException e) {
        //                        // TODO Auto-generated catch block
        //                        e.printStackTrace();
        //                } catch (TransformerConfigurationException e) {
        //                        // TODO Auto-generated catch block
        //                        e.printStackTrace();
        //                } catch (ParserConfigurationException e) {
        //                        // TODO Auto-generated catch block
        //                        e.printStackTrace();
        //                } catch (TransformerFactoryConfigurationError e) {
        //                        // TODO Auto-generated catch block
        //                        e.printStackTrace();
        //                } catch (TransformerException e) {
        //                        // TODO Auto-generated catch block
        //                        e.printStackTrace();
        //                }
        //                return "null";
        
    }
    
    public void msgRecd(Message m) {
        methInvokeLog = m.getMessageContent();
        synchronized (this) {
            notify();
        }
    }
    
    public String getAgentInformation(String agentID) {
        Uuid agentUuid = UuidFactory.create(agentID);
        Collection c = DomainRegistry.getDomainRegistry().getDomains();
        Iterator iter = c.iterator();
        while (iter.hasNext()) {
            ManagedDomain md = (ManagedDomain) iter.next();
            try {
                Agent a = null;
                if (agentUuid.equals(getIdentity().getID()))
                    a = this;
                else
                    a = md.getRegistry().getAgent(agentUuid);
                if (a != null) {
                    ArrayList arr = new ArrayList();
                    getInterfaces(a.getClass(), arr);
                    Class[] intf = (Class[]) arr.toArray(new Class[] {});
                    //We don;t need the agent to be exported
                    AgentProxyInfo ag = new AgentProxyInfo(a.getIdentity(), a.getNamespace() + "." + a.getName(), intf,null,md.getDomainName());
                    Document doc = buildAgentXML(a, ag);
                    
                    // Source xslsource =new
                    // StreamSource(new
                    // StringReader(getStyleSheet()));
                    Source xmlsource = new DOMSource(doc);
                    Source xsloutsource = new StreamSource(new File("conf/agentinfo.xsl"));
                    StringWriter writer = new StringWriter();
                    StreamResult xmlResult = new StreamResult(writer);
                    TransformerFactory factory = TransformerFactory.newInstance();
                    Transformer trans = factory.newTransformer(xsloutsource);
                    trans.transform(xmlsource, xmlResult);
                    
                    return writer.getBuffer().toString();
                }
            } catch (DOMException e) {
                // TODO Handle DOMException
                e.printStackTrace();
            } catch (FactoryConfigurationError e) {
                // TODO Handle FactoryConfigurationError
                e.printStackTrace();
                
            } catch (TransformerConfigurationException e) {
                // TODO Handle TransformerConfigurationException
                e.printStackTrace();
            } catch (TransformerException e) {
                // TODO Handle TransformerException
                e.printStackTrace();
            } catch (NoSuchAgentException e) {
                // URGENT Handle NoSuchAgentException
                e.printStackTrace();
            }
            
        }
        return null;
    }
    
    /**
     * @param a
     * @param ag
     */
    private Document buildAgentXML(Agent a, AgentProxyInfo ag) {
        // TODO Complete method stub for buildAgentXML
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element agentElem = doc.createElement("agent");
            doc.appendChild(agentElem);
            addElement(agentElem, "name", a.getName(), doc);
            addElement(agentElem, "namespace", a.getNamespace(), doc);
            addElement(agentElem, "id", a.getIdentity().toString(), doc);
            addElement(agentElem, "extid", a.getIdentity().getExtendedString(), doc);
            addElement(agentElem, "primary", a.getAgentState().toString(), doc);
            
            if (a.getSecondaryState() != null)
                addElement(agentElem, "second", a.getSecondaryState().toString(), doc);
            else
                addElement(agentElem, "second", "<none>", doc);
            if (a instanceof TransactionalResource)
                addElement(agentElem, "txid", ((TransactionalResource) a).getTransaction(), doc);
            else
                addElement(agentElem, "txid", "<none>", doc);
            SliceAgent slicer = (SliceAgent) context.getAgent("vertigo.Slice");
            Slice s = slicer.getSliceFor(a.getIdentity());
            if (s != null) {
                System.out.println("Adding slice reference");
                addElement(agentElem, "slice", s.getName(), doc);
            }
            Element facetsElement = doc.createElement("facets");
            agentElem.appendChild(facetsElement);
            Class[] cl = ag.getCollaborativeInterfaces();
            for (int i = 0; i < cl.length; i++) {
                Element facetElement = doc.createElement("facet");
                facetElement.setAttribute("name", cl[i].getName());
                facetsElement.appendChild(facetElement);
                Method[] ops = cl[i].getMethods();
                for (int j = 0; j < ops.length; j++) {
                    Element operationElement = doc.createElement("operation");
                    facetElement.appendChild(operationElement);
                    Method m = ops[j];
                    addElement(operationElement, "name", m.getName(), doc);
                    addElement(operationElement, "return", m.getReturnType().getName(), doc);
                    addElement(operationElement, "modifiers", Modifier.toString(m.getModifiers()), doc);
                    Element parameterElement = doc.createElement("params");
                    operationElement.appendChild(parameterElement);
                    Class paramsCl[] = m.getParameterTypes();
                    for (int k = 0; k < paramsCl.length; k++)
                        addElement(parameterElement, "cname", paramsCl[k].getName(), doc);
                }
            }
            return doc;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    private void getInterfaces(Class cl, ArrayList arr) {
        // System.out.println("Checking interfaces...." + cl.getName());
        Class[] classes = cl.getInterfaces();
        // Class[] classes = cl.getClasses();
        for (int i = 0; i < classes.length; i++) {
            Class subclass = classes[i];
            // System.out.println("Class: " + subclass.getName());
            if (subclass.isInterface()) {
                getInterfaces(subclass, arr);
            }
            if (subclass.equals(Collaborative.class)) {
                if (!cl.getName().startsWith("$Proxy")) {
                    arr.add(cl);
                }
            }
        }
    }
    
    public MonitorAgentImpl() {
        this.namespace = "neon";
        this.name = "Monitor";
    }
    
        /*
         * @see org.jini.projects.neon.agents.Agent#init()
         */
    public boolean init() {
        // TODO Complete method stub for init
        return true;
    }
    
    public String getInformation(String category) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element monitorElem = doc.createElement("monitor");
            doc.appendChild(monitorElem);
            if (category.equalsIgnoreCase("host")) {
                addElement(monitorElem, "host", InetAddress.getLocalHost().getHostName(), doc);
                Element javaelem = doc.createElement("java");
                monitorElem.appendChild(javaelem);
                addElement(javaelem, "vm.version", System.getProperty("java.vm.version"), doc);
                addElement(javaelem, "vm.vendor", System.getProperty("java.vm.vendor"), doc);
                addElement(javaelem, "vm.name", System.getProperty("java.vm.name"), doc);
                
                Element memel = doc.createElement("memory");
                monitorElem.appendChild(memel);
                addElement(memel, "memfree", String.valueOf(Runtime.getRuntime().freeMemory()), doc);
                addElement(memel, "totalmem", String.valueOf(Runtime.getRuntime().totalMemory()), doc);
                Element osel = doc.createElement("os");
                monitorElem.appendChild(osel);
                addElement(osel, "hostingos", System.getProperty("os.name"), doc);
                addElement(osel, "osversion", System.getProperty("os.version"), doc);
                addElement(osel, "osarch", System.getProperty("os.arch"), doc);
                addElement(osel, "userdir", System.getProperty("user.dir"), doc);
                PrivilegedAgentContext agCon = ((PrivilegedAgentContext) getAgentContext());
                Collection c = DomainRegistry.getDomainRegistry().getDomains();
                Iterator iter = c.iterator();
                while (iter.hasNext()) {
                    ManagedDomain d = (ManagedDomain) iter.next();
                    getDomainAgents(doc, monitorElem, d);
                    
                }
                
                Element treeElem = doc.createElement("tree");
                monitorElem.appendChild(treeElem);
                iter = c.iterator();
                while (iter.hasNext()) {
                    ManagedDomain d = (ManagedDomain) iter.next();
                    buildAgentXMLNodeTree(doc, treeElem, d);
                    
                }
                
                
            }
            if (category.equalsIgnoreCase("domains")) {
                PrivilegedAgentContext agCon = ((PrivilegedAgentContext) getAgentContext());
                Collection c = DomainRegistry.getDomainRegistry().getDomains();
                Iterator iter = c.iterator();
                Element domainsElement = doc.createElement("domains");
                monitorElem.appendChild(domainsElement);
                while (iter.hasNext()) {
                    ManagedDomain d = (ManagedDomain) iter.next();
                    Element domainElement = doc.createElement("domainDetails");
                    
                    domainsElement.appendChild(domainElement);
                    String domName = d.getDomainName();
                    domainElement.setAttribute("name", domName);
                    AgentRegistry agReg = d.getRegistry();
                    addElement(domainElement, "numberAgents", String.valueOf(agReg.getAgentNumber()), doc);
                    DomainRegistry reg = DomainRegistry.getDomainRegistry();
                    List<String> callsOutTo = reg.getAllowedDomainsOut(domName);
                    List<String> callsInFrom = reg.getAllowedDomainsIn(domName);
                    Element outElement = doc.createElement("allowout");
                    domainElement.appendChild(outElement);
                    for (String s : callsOutTo)
                        addElement(outElement, "aclname", s, doc);
                    Element inElement = doc.createElement("allowin");
                    domainElement.appendChild(inElement);
                    for (String s : callsInFrom)
                        addElement(inElement, "aclname", s, doc);
                }
            }
            
            // Source xslsource =new StreamSource(new
            // StringReader(getStyleSheet()));
            Source xmlsource = new DOMSource(doc);
            Source xsloutsource = new StreamSource(new File("conf/monitor.xsl"));
           // Source xsloutsource = new StreamSource();
            StringWriter writer = new StringWriter();
            StreamResult xmlResult = new StreamResult(writer);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer trans = factory.newTransformer(xsloutsource); 
            trans.transform(xmlsource, xmlResult);
           // System.out.println("\n" + writer.getBuffer().toString() +"\n");
            return writer.getBuffer().toString();
        } catch (DOMException e) {
            // TODO Handle DOMException
            e.printStackTrace();
        } catch (UnknownHostException e) {
            // TODO Handle UnknownHostException
            e.printStackTrace();
        } catch (FactoryConfigurationError e) {
            // TODO Handle FactoryConfigurationError
            e.printStackTrace();
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
        return "";
    }
    
    private void buildAgentXMLNodeTree(Document doc, Element monitorElem, ManagedDomain d){
        Agent[] agents = d.getRegistry().getAllAgents();
        Map rootMap = new NamedMap("root");
        
        for (int i = 0; i < agents.length; i++) {
            Map contextMap = rootMap;
            String name = agents[i].getNamespace() + "."+ agents[i].getName();
            String[] parts = name.split("\\.");
            ArrayList arr = new ArrayList();
            for(String part:parts)
                arr.add(part);
            Map m = findPath(arr, contextMap);
            m.put(agents[i].getIdentity(), agents[i]);
        }
        System.out.println("\n\n" + rootMap + "\n\n");
            Element treeElem = doc.createElement("agent-tree");
            monitorElem.appendChild(treeElem);
            treeElem.setAttribute("domain", d.getDomainName());
            generateAgentNodeTreeSection(doc,treeElem, rootMap);
        
    }
    
    public Map findPart(String part, Map context){
        System.out.println("Looking for: " + part + " in " + context);
        return (Map) context.get(part);
    }
    
    public Map findPath(ArrayList parts,Map treecontext){
        if(parts.size()>0){
            String searchpart = (String) parts.get(0);
            Object o =treecontext.get(searchpart);
            if(o==null){            
                treecontext.put(searchpart, new NamedMap(searchpart));
                System.out.println("Adding: " + searchpart);
            }
            parts.remove(0);
            return findPath(parts,(NamedMap) treecontext.get(searchpart));
        }
        else 
            return treecontext;
    }
    
    private void generateAgentNodeTreeSection(Document doc,
            Element treeElem, Map contextMap) {
        for(Iterator iter = contextMap.entrySet().iterator();iter.hasNext();){
            Map.Entry entr = (Map.Entry) iter.next();
            if(entr.getValue() instanceof NamedMap){
                NamedMap nmap = (NamedMap) entr.getValue();
                Element nsElem = doc.createElement("namespace");
                System.out.println("Added namespace element: "  +nmap.getName());
                nsElem.setAttribute("name", nmap.getName());
                treeElem.appendChild(nsElem);
                generateAgentNodeTreeSection(doc, nsElem, nmap);
            } else if (entr.getValue() instanceof Agent){
                Agent ag = (Agent) entr.getValue();
                Element agentElem = doc.createElement("agentinformation");
                agentElem.setAttribute("ID", ag.getIdentity().toString());
                agentElem.setAttribute("state", ag.getAgentState().toString());
                treeElem.appendChild(agentElem);
            }
        }
        
    }
    
    /**
     * @param doc
     * @param monitorElem
     * @param agents
     */
    private void getDomainAgents(Document doc, Element monitorElem, ManagedDomain d) {
        Element domainel = doc.createElement("domain");
        domainel.setAttribute("name", d.getDomainName());
        Agent[] agents = d.getRegistry().getAllAgents();
        Element agentTable = doc.createElement("agents");
        monitorElem.appendChild(domainel);
        domainel.appendChild(agentTable);
        for (int i = 0; i < agents.length; i++) {
            Element agentItem = doc.createElement("agent");
            Agent agt = agents[i];
            agentTable.appendChild(agentItem);
            addElement(agentItem, "name", agt.getName(), doc);
            addElement(agentItem, "namespace", agt.getNamespace(), doc);
            addElement(agentItem, "id", agt.getIdentity().toString(), doc);
            addElement(agentItem, "extid", agt.getIdentity().getExtendedString(), doc);
            addElement(agentItem, "state", agt.getAgentState().toString(), doc);
            addElement(agentItem, "render", agt instanceof PresentableAgent, doc);
        }
    }
    
    public static void main(String[] args) {
        MonitorAgentImpl impl = new MonitorAgentImpl();
        ArrayList arr = new ArrayList();
        arr.add("neon");
        arr.add("examples");
        arr.add("services");
        Map m = new HashMap();
        System.out.println(impl.findPath(arr,m));
        arr.add("neon");
        arr.add("slice");
        arr.add("SliceManager");
       System.out.println( impl.findPath(arr,m));
        System.out.println(m);
    }
    
    private void addElement(Element parent, String elementName, Object elementValue, Document doc) {
        Element elem = doc.createElement(elementName);
        if (elementValue != null)
            elem.appendChild(doc.createTextNode(elementValue.toString()));
        else
            elem.appendChild(doc.createTextNode("null"));
        parent.appendChild(elem);
    }
    
    private String getStyleSheet() {
        // A simple identity stylesheet renders a DOM tree ut as an XML
        // string
        // which can then be passed back
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<xsl:stylesheet version=\"1.0\" xmlns:xsl=" + "\"http://www.w3.org/1999/XSL/Transform\">" + "<xsl:output method=\"xml\" indent=\"yes\"/>" + "<xsl:template match=\"@*|node()\">" + "<xsl:copy>" + "<xsl:apply-templates select=\"@*|node()\"/>" + "</xsl:copy>" + "</xsl:template>" + "</xsl:stylesheet>";
    }
}
