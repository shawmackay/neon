package org.jini.projects.neon.vertigo.management.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.jini.id.Uuid;
import net.jini.id.UuidFactory;

import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.vertigo.application.AgentTypeReference;
import org.jini.projects.neon.vertigo.management.AgentListObject;
import org.jini.projects.neon.vertigo.slice.Slice;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SliceXMLHandler extends DefaultHandler {
	private Slice generatedSlice;

	private String inElement;

	private boolean inSubSlices;

	private boolean inAttachedAgents;

	private List attachedAgents = new ArrayList();

	private List attachedTypeAgents = new ArrayList();

	private Map subSlices = new HashMap();

	private Map<String, Object> elements = new HashMap<String, Object>();

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub

		inElement = qName;

		if (qName.equals("slicedetails")) {
			System.out.println("Loading attributes");
			moveElements("name", attributes);
			moveElements("s_desc", attributes);
			moveElements("l_desc", attributes);
			moveElements("sliceid", attributes);
			moveElements("parentid", attributes);
			moveElements("type", attributes);
		}
		if (qName.equals("sliceref")) {
			System.out.println("Adding SubSlice:");
			subSlices.put(attributes.getValue("name"), attributes.getValue("id"));
		}
		
		if (qName.equals("agentref")) {
			System.out.println("Adding agenttype");
			AgentIdentity id  = new AgentIdentity(attributes.getValue("id"));
			attachedAgents.add(new AgentListObject(attributes.getValue("name"), id,attributes.getValue("class"), attributes.getValue("domain")));
		}
		
		if (qName.equals("agenttyperef")) {
			System.out.println("Adding agenttyperef");

			attachedTypeAgents.add(new AgentTypeReference(attributes.getValue("name"), Integer.parseInt(attributes.getValue("number"))));
		}
	}

	public void moveElements(String name, Attributes attr) {
		elements.put(name, attr.getValue(name));
	}

	@Override
	public void startDocument() throws SAXException {
		elements.clear();
		attachedAgents.clear();
		subSlices.clear();
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub

		try {
			System.out.println("TYPE: " + elements.get("type"));
			Class cl = Class.forName((String) elements.get("type"));
			generatedSlice = (Slice) cl.newInstance();
			generatedSlice.setName((String) elements.get("name"));
			generatedSlice.setLongDescription((String) elements.get("l_desc"));
			generatedSlice.setShortDescription((String) elements.get("s_desc"));
			if (elements.get("parentid") != null)
				generatedSlice.setParentSliceID(UuidFactory.create((String) elements.get("parentid")));
			Uuid id = UuidFactory.create((String) elements.get("sliceid"));
			generatedSlice.setSliceID(id);
			for (Iterator iter = subSlices.entrySet().iterator(); iter.hasNext();) {
				Map.Entry entr = (Map.Entry) iter.next();
				generatedSlice.addSlice(UuidFactory.create((String) entr.getValue()), (String) entr.getKey());
			}
			for (Iterator iter = attachedAgents.iterator(); iter.hasNext();) {
				AgentListObject agid = (AgentListObject)iter.next();
				generatedSlice.attachAgent(agid);
			}
			for (Iterator iter = attachedTypeAgents.iterator(); iter.hasNext();) {
				AgentTypeReference ref = (AgentTypeReference) iter.next();
			
				generatedSlice.attachAgentType(ref.getName(), ref.getNumber());
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Slice getGeneratedSlice() {
		return generatedSlice;
	}
}
