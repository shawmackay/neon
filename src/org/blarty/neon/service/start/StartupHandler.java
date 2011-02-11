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

package org.jini.projects.neon.service.start;

/*
 * StartupHandler.java
 * 
 * Created Fri Mar 18 11:24:31 GMT 2005
 */

import java.util.ArrayList;
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author calum
 * 
 */

public class StartupHandler extends DefaultHandler {

	private DomainConfig currentDomainConfig;
	private AgentCatalog currentCatalog;
	private DelegateCatalog currentDelegates = new DelegateCatalog();
	AgentSet preSet;
	AgentSet postSet;
	private ArrayList<DomainConfig> domains = new ArrayList<DomainConfig>();
	private boolean inService;
	private boolean inDomain;
	private boolean inpre;
	private boolean inpost;
	private String element;

	public StartupConfig getStartupConfig() {

		return new StartupConfig(domains);
	}

	private StringBuffer data;

	public StartupHandler() {

	}
	
	public DelegateCatalog getDelegateCatalog(){
		return currentDelegates;
	}

	public void characters(char[] characters, int start, int begin) throws SAXException {
		data.append(new String(characters, start, begin));
	}

	/**
	 * endDocument
	 * 
	 * @throws org.xml.sax.SAXException
	 */
	public void endDocument() throws SAXException {
		super.endDocument();
	}

	public void endElement(String aString0, String aString1, String elname) throws SAXException {

		if (elname.equals("domains"))
			inDomain = false;
		if (elname.equals("pre"))
			inpre = false;
		if (elname.equals("post"))
			inpost = false;
		if (elname.equals("domain")) {
			domains.add(currentDomainConfig);
		}
		data.delete(0, data.length());

	}

	/**
	 * startDocument
	 * 
	 * @throws org.xml.sax.SAXException
	 */
	public void startDocument() throws SAXException {
		super.startDocument();
	}

	/**
	 * startElement
	 * 
	 * @param aString0
	 * @param aString1
	 * @param aString2
	 * @param aAttributes
	 * @throws org.xml.sax.SAXException
	 */
	public void startElement(String aString0, String aString1, String elname, Attributes aAttributes) throws SAXException {
		data = new StringBuffer();
		// System.out.println("Entering element: " + elname);
		element = elname;
		if (elname.equals("domains")) {
			inDomain = true;
			return;
		}
		if (elname.equals("domain")) {
			currentDomainConfig = new DomainConfig(aAttributes.getValue("name"));

			preSet = new AgentSet();
			postSet = new AgentSet();

			currentCatalog = new AgentCatalog();
			currentDomainConfig.setAgentCatalog(currentCatalog);
			
			currentDomainConfig.setDelegateCatalog(currentDelegates);
			currentCatalog.setPostSet(postSet);
			currentCatalog.setPreSet(preSet);
			String allowedClasses = aAttributes.getValue("allowedClasses");
			if (allowedClasses != null && !allowedClasses.equals("")) {
				ArrayList arr = new ArrayList();
				String[] individualclasses = allowedClasses.split(",");
				for (String s : individualclasses)
					arr.add(s);
				currentDomainConfig.setAllowedClasses(arr);
			} else
				currentDomainConfig.setAllowedClasses(new ArrayList());

			String allowedNS = aAttributes.getValue("allowedNamespaces");
			if (allowedNS != null && !allowedNS.equals("")) {
				ArrayList arr = new ArrayList();
				String[] individualNS = allowedClasses.split(",");
				for (String s : individualNS)
					arr.add(s);
				currentDomainConfig.setAllowedNamespaces(arr);
			} else
				currentDomainConfig.setAllowedNamespaces(new ArrayList());

			String secLevel = aAttributes.getValue("securityLevel");
			int iSecLevel = 0;
			if (secLevel != null && !secLevel.equals("")) {
				try {
					iSecLevel = Integer.parseInt(secLevel);
				} catch (NumberFormatException e) {
				}
			}
			//			
			// private boolean encryptDomainInfo;
			// private boolean encryptAgentStorage;
			// private boolean encryptOtherData;
			// private boolean failIfEncryptionAvailable;

			boolean encryptDomainInfo = Boolean.parseBoolean(aAttributes.getValue("encryptDomainInfo"));
			boolean encryptAgentStorage = Boolean.parseBoolean(aAttributes.getValue("encryptAgentStorage"));
			boolean encryptOtherData = Boolean.parseBoolean(aAttributes.getValue("encryptOtherData"));
			boolean failIfEncryptionAvailable = Boolean.parseBoolean(aAttributes.getValue("failIfEncryptionAvailable"));

			if (checkSecurityLevel(iSecLevel, encryptDomainInfo, encryptAgentStorage, encryptOtherData, failIfEncryptionAvailable)) {
				currentDomainConfig.setSecurityLevel(iSecLevel);
				currentDomainConfig.setEncryptDomainInfo(encryptDomainInfo);
				currentDomainConfig.setEncryptAgentStorage(encryptAgentStorage);
				currentDomainConfig.setEncryptOtherData(encryptOtherData);
				currentDomainConfig.setFailIfEncryptionAvailable(failIfEncryptionAvailable);
			} else {
				Logger.getAnonymousLogger().severe("Security Information does not match supplied levels - defaulting to level 0");
				currentDomainConfig.setSecurityLevel(0);
			}
		}
		if (elname.equals("pre"))
			inpre = true;
		if (elname.equals("post"))
			inpost = true;
		if (elname.equals("agent")) {
			AgentDef def = new AgentDef(aAttributes.getValue("classname"), Integer.parseInt(aAttributes.getValue("number")), aAttributes.getValue("configuration"), aAttributes.getValue("waitafterinit"), aAttributes.getValue("constraints"));
			if (inpre)
				preSet.addAgent(def);
			if (inpost)
				postSet.addAgent(def);
		}
		if (elname.equals("wait")) {
			currentCatalog.setWaitTime(Integer.parseInt(aAttributes.getValue("time")));
		}
		if (elname.equals("delegate")) {
			String type = aAttributes.getValue("type");
			String iclass = aAttributes.getValue("interfaceclass");
			String dclass = aAttributes.getValue("delegateclass");
			currentDelegates.addDelegate(new Delegate(type, iclass, dclass));
		}
		// System.out.println("Checked: "+elname);
		super.startElement(aString0, aString1, elname, aAttributes);
	}

	/*
	 * public static void main(String[] args) { try { SAXParserFactory spf =
	 * SAXParserFactory.newInstance(); SAXParser parser = spf.newSAXParser();
	 * StartupHandler loader = new StartupHandler(); FileInputStream fis = new
	 * FileInputStream("conf/New.xml"); parser.parse(fis, loader,
	 * "conf/New.xml"); ServiceConfig sysCon = loader.getServiceConfig(); List<DomainConfig>
	 * domains = loader.getDomainConfigs(); for(DomainConfig config : domains){
	 * System.out.println(config.getName()); AgentCatalog catalog =
	 * config.getAgentCatalog(); AgentSet pre = catalog.getPreSet(); AgentSet
	 * post = catalog.getPostSet(); for(AgentDef def : pre.getAgents()){
	 * System.out.println("\t"+def.getClassname() + ": " + def.getNumber()); } } }
	 * catch (FileNotFoundException e) { // URGENT Handle FileNotFoundException
	 * e.printStackTrace(); } catch (FactoryConfigurationError e) { // URGENT
	 * Handle FactoryConfigurationError e.printStackTrace(); } catch
	 * (ParserConfigurationException e) { // URGENT Handle
	 * ParserConfigurationException e.printStackTrace(); } catch (SAXException
	 * e) { // URGENT Handle SAXException e.printStackTrace(); } catch
	 * (IOException e) { // URGENT Handle IOException e.printStackTrace(); } }
	 */

	private boolean checkSecurityLevel(int secLevel, boolean encryptDomainInfo, boolean encryptAgentStorage, boolean encryptOtherData, boolean failIfEncryptionAvailable) {
		// TODO Auto-generated method stub
		if (secLevel < 3) {
			if (!encryptDomainInfo && !encryptAgentStorage && !encryptOtherData && !failIfEncryptionAvailable)
				return true;
			else
				return false;
		}
		if (secLevel == 3) {
			if (encryptDomainInfo && !encryptAgentStorage && !encryptOtherData && !failIfEncryptionAvailable)
				return true;
			else
				return false;
		}
		if (secLevel == 4) {
			if (encryptDomainInfo && encryptAgentStorage && !encryptOtherData && !failIfEncryptionAvailable)
				return true;
			else
				return false;
		}
		if (secLevel == 5) {
			if (encryptDomainInfo && encryptAgentStorage && encryptOtherData && failIfEncryptionAvailable)
				return true;
			else
				return false;
		}
		if (secLevel == 6)
			return true;
		return false;
	}

}
