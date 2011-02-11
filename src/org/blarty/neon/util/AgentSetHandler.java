package org.jini.projects.neon.util;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AgentSetHandler extends DefaultHandler {
        private List definitions = new ArrayList();

        private String codebase = null;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                // TODO Auto-generated method stub

                super.startElement(uri, localName, qName, attributes);
                if (qName.equals("agent")) {
                        AgentDeployDefinition def = new AgentDeployDefinition();
                        def.setAgentclass(attributes.getValue("class"));
                        def.setAgentconfig(attributes.getValue("config"));
                        def.setAgentconstraints(attributes.getValue("constraints"));
                        if (attributes.getValue("codebase") == null) {
                                if (codebase == null)
                                        System.out.println("You must supply a codebase for your agents, or supply a default in the agentset element");
                                else
                                        def.setCodebase(codebase);
                        } else {
                                def.setCodebase(attributes.getValue("codebase"));
                        }
                        definitions.add(def);
                }
                if (qName.equals("agentset")) {
                        String defaultcodebase = attributes.getValue("codebase");
                        if (defaultcodebase != null)
                                codebase = defaultcodebase;
                }
        }
        
        public List getDefinitions(){
                return definitions;
        }
}
