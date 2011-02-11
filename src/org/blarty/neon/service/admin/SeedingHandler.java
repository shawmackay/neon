package org.jini.projects.neon.service.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.ui.AgentListItem;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

public class SeedingHandler extends DefaultHandler{

	List<SeedingAgentDetails> agentDetailList = new ArrayList<SeedingAgentDetails>();

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		super.startElement(uri, localName, name, attributes);
		if(name.equals("agent")){
			SeedingAgentDetails seedAgent = new SeedingAgentDetails();
			seedAgent.setClassName(attributes.getValue("classname"));
			seedAgent.setConfigurl(attributes.getValue("configurl"));
			seedAgent.setConstraintsurl(attributes.getValue("constraintsurl"));
			seedAgent.setDomain(attributes.getValue("domain"));
			seedAgent.setCodebase(attributes.getValue("codebase"));
			seedAgent.setAtServer(Boolean.parseBoolean(attributes.getValue("atserver")));
			agentDetailList.add(seedAgent);
			
		}
			
	}

	

	public List<SeedingAgentDetails> getAgentDetailList() {
		return agentDetailList;
	}

	public SeedingHandler(){
		handleStream(this.getClass().getResourceAsStream("/seeding/sampleSeed.xml"));
	}
	
	public SeedingHandler(InputStream is){
		handleStream(is);
	}
	
	public SeedingHandler(File f){
		try {
			handleStream(new FileInputStream(f));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void handleStream(InputStream is){
		SAXParserFactory parserFact = SAXParserFactory.newInstance();
		try {
			SAXParser parser = parserFact.newSAXParser();
			parser.parse(is, this);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SeedingHandler app = new SeedingHandler();
		for(SeedingAgentDetails detail: app.getAgentDetailList())
			System.out.println(detail);
	}
}
