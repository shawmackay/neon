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
 * neon : org.jini.projects.neon.vertigo.management.store
 * XMLSliceStorage.java
 * Created on 02-Jun-2005
 *XMLSliceStorage
 */

package org.jini.projects.neon.vertigo.management.store;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.jini.id.Uuid;

import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.service.start.StartupConfig;
import org.jini.projects.neon.service.start.StartupHandler;
import org.jini.projects.neon.vertigo.application.AgentTypeReference;
import org.jini.projects.neon.vertigo.application.DefaultApplication;
import org.jini.projects.neon.vertigo.management.AgentListObject;
import org.jini.projects.neon.vertigo.management.SliceManager;
import org.jini.projects.neon.vertigo.slice.Slice;
import org.xml.sax.SAXException;

/**
 * @author calum
 */
public class XMLSliceStorage implements SliceStorage {

	private File directory;

	private SliceManager mgr;

	public XMLSliceStorage(String directory, SliceManager mgr) {
		this.directory = new File(directory);
		if (!this.directory.exists())
			this.directory.mkdirs();
		this.mgr = mgr;
	}

	public void loadSlices() throws IOException {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser parser = spf.newSAXParser();
			SliceXMLHandler loader = new SliceXMLHandler();
			File[] files = directory.listFiles();
			for (File f : files) {
				FileInputStream fis = new FileInputStream(f);
				parser.parse(fis, loader, f.getAbsolutePath());
				Slice s = loader.getGeneratedSlice();
				mgr.deploySlice(s);
			}

		} catch (FileNotFoundException e) {
			// URGENT Handle FileNotFoundException
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			// URGENT Handle FactoryConfigurationError
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// URGENT Handle ParserConfigurationException
			e.printStackTrace();
		} catch (SAXException e) {
			// URGENT Handle SAXException
			e.printStackTrace();
		} catch (IOException e) {
			// URGENT Handle IOException
			e.printStackTrace();
		}
	}

	public void storeSlice(Slice s) throws IOException {
		// TODO Complete method stub for storeSlice
		System.out.println("Storing slice: " + s.getSliceID());
		StringBuffer xmlBuffer = new StringBuffer();
		xmlBuffer.append("<slice>\n");
		Map m = new TreeMap();
		m.put("name", s.getName().trim());
		m.put("s_desc", s.getShortDescription());
		m.put("l_desc", s.getLongDescription());
		m.put("sliceid", s.getSliceID());
		m.put("parentid", s.getParentSliceID());
		m.put("type", s.getClass().getName());
		xmlBuffer.append(addTag("slicedetails", null, m));
		xmlBuffer.append("<subsliceids>");
		m = s.getSubSlices();
		for (Iterator iter = m.entrySet().iterator(); iter.hasNext();) {
			Map.Entry entr = (Map.Entry) iter.next();
			Map map = new HashMap();
			map.put("name", entr.getKey());
			map.put("id", entr.getValue());
			xmlBuffer.append(addTag("sliceref", null, map));
			File subsliceFile = new File(directory, "slice" + entr.getValue().toString() + ".xml");
			if (!subsliceFile.exists()) {
				Slice sub = mgr.getSlice((Uuid) entr.getValue());
				storeSlice(sub);
			}
		}
		xmlBuffer.append("</subsliceids>");
		xmlBuffer.append("<attachedagents>");
		List l = s.getAgentIDs();
		for (Iterator iter = l.iterator(); iter.hasNext();) {
			AgentListObject i = (AgentListObject) iter.next();
			Map map = new HashMap();
			map.put("id", i.getAgentID().getExtendedString());
			map.put("name", i.getAgentFqName());
			map.put("class", i.getAgentClassName());
			map.put("domain", i.getDomain());
			xmlBuffer.append(addTag("agentref", null, map));
		}
		xmlBuffer.append("</attachedagents>");
		xmlBuffer.append("<attachedtypeagents>");
		l = s.getAgentTypeReferences();
		for (Iterator iter = l.iterator(); iter.hasNext();) {
			AgentTypeReference i = (AgentTypeReference) iter.next();
			Map map = new HashMap();
			map.put("name", i.getName());
			map.put("number", i.getNumber());
			xmlBuffer.append(addTag("agenttyperef", null, map));
		}
		xmlBuffer.append("</attachedtypeagents>");
		xmlBuffer.append("</slice>");
		File f = null;
		if (s instanceof DefaultApplication)
			f = new File(directory, "root" + s.getSliceID().toString() + ".xml");
		else
			f = new File(directory, "slice" + s.getSliceID().toString() + ".xml");
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
		bos.write(xmlBuffer.toString().getBytes());
		bos.flush();
		bos.close();

	}

	private String addTag(String name, Object value, Map attribs) {
		StringBuffer b = new StringBuffer();
		b.append("<" + name);
		if (attribs != null) {
			b.append(" ");
			for (Iterator iter = attribs.entrySet().iterator(); iter.hasNext();) {
				Map.Entry entr = (Map.Entry) iter.next();

				System.out.println("Adding: " + entr.getKey() + ": " + entr.getValue());
				if (entr.getValue() != null)
					b.append(entr.getKey() + "=\"" + entr.getValue().toString() + "\" ");
			}
		}
		if (value != null)
			b.append(">\n\t" + value + "</" + name + ">");
		else
			b.append("/>");
		return b.toString();
	}

	public void removeSlice(Slice s) throws IOException {
		// TODO Complete method stub for removeSlice

	}

	public Slice loadSlice(Uuid id) throws IOException {
		// TODO Complete method stub for loadSlice
		return null;
	}

}
