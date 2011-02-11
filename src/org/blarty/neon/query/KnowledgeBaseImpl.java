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
 * neon : org.jini.projects.neon.query
 * KnowledgeBaseImpl.java
 * Created on 20-Aug-2003
 */
package org.jini.projects.neon.query;

import java.io.File;
import java.io.FileInputStream;

//import org.apache.xindice.client.xmldb.services.CollectionManager;
//import org.apache.xindice.util.XindiceException;
//import org.apache.xindice.xml.dom.DOMParser;
//import org.xmldb.api.DatabaseManager;
//import org.xmldb.api.base.Collection;
//import org.xmldb.api.base.Database;
//import org.xmldb.api.base.Resource;
//import org.xmldb.api.base.ResourceIterator;
//import org.xmldb.api.base.ResourceSet;
//import org.xmldb.api.base.XMLDBException;
//import org.xmldb.api.modules.XMLResource;
//import org.xmldb.api.modules.XPathQueryService;
//import org.xmldb.api.modules.XUpdateQueryService;

/**
 * Represents a connection from the query engine to the knowledge base
 * @author calum
 */
public class KnowledgeBaseImpl {
//	private Collection col;
//	private Database database;
//	private XPathQueryService service;
//	private CollectionManager collectionManager;
//	private XUpdateQueryService updateService;

	/**
	 *
	 */
	public KnowledgeBaseImpl() {
		super();
		// TODO Complete constructor stub for KnowledgeBaseImpl
	}

	public void initialise() throws  Exception {
//		String driver = "org.apache.xindice.client.xmldb.DatabaseImpl";
//        //System.out.println("KBase Initilialising");
//		Class c = Class.forName(driver);
//		database = (Database) c.newInstance();
//        //System.out.println("Registering DB ");
//		DatabaseManager.registerDatabase(database);
//
//		setCurrentCollection("/db/addressbook");
        //System.out.println("Set Base Collection....Initialised");
		//col = DatabaseManager.getCollection("xmldb:xindice://e0052sts3s.countrywide-assured.co.uk:4080/db/addressbook");

	}

	public void addCollection(String collectionName){
//		String collectionConfig = "<collection compressed=\"true\" name=\"" + collectionName + "\">" + "   <filer class=\"org.apache.xindice.core.filer.BTreeFiler\" gzip=\"true\"/>" + "</collection>";
//
//		try {
//			try {
//				collectionManager.createCollection(collectionName, DOMParser.toDocument(collectionConfig));
//			} catch (XMLDBException e) {
//				System.out.println("Err: " + e.getMessage());
//			}
//		} catch (XindiceException e) {
//
//			System.out.println("Err: " + e.getMessage());
//			e.printStackTrace();
//		}
//
//		System.out.println("Collection " + collectionName + " created.");
	}

	public void setCurrentCollection(String baseName, String collectionName, boolean createIfNotExist) {

//		col = DatabaseManager.getCollection("xmldb:xindice://e0052sts3s.countrywide-assured.co.uk:4080" + baseName + "/" + collectionName);
//
//		if (col == null && createIfNotExist) {
//			System.out.println("Creating new collection");
//			col = DatabaseManager.getCollection("xmldb:xindice://e0052sts3s.countrywide-assured.co.uk:4080" + baseName);
//			collectionManager = (CollectionManager) col.getService("CollectionManager", "1.0");
//			col = collectionManager.createCollection(collectionName);
//
//		}
//		service = (XPathQueryService) col.getService("XPathQueryService", "1.0");
//		collectionManager = (CollectionManager) col.getService("CollectionManager", "1.0");
//		updateService = (XUpdateQueryService) col.getService("XUpdateQueryService", "1.0");
	}

	public void setCurrentCollection(String collectionName) {
//
//		col = DatabaseManager.getCollection("xmldb:xindice://e0052sts3s.countrywide-assured.co.uk:4080" + collectionName);
//
//		service = (XPathQueryService) col.getService("XPathQueryService", "1.0");
//		collectionManager = (CollectionManager) col.getService("CollectionManager", "1.0");
//		updateService = (XUpdateQueryService) col.getService("XUpdateQueryService", "1.0");
	}

	public void executeQuery(String xpquery) throws Exception {
//		col = null;
//		try {
//
//			String xpath = xpquery;
//
//			ResourceSet resultSet = service.query(xpath);
//			ResourceIterator results = resultSet.getIterator();
//			while (results.hasMoreResources()) {
//				Resource res = results.nextResource();
//				System.out.println((String) res.getContent());
//			}
//		} catch (XMLDBException e) {
//			System.err.println("XML:DB Exception occured " + e.errorCode);
//		}
	}

	public void close() {
//		if (col != null) {
//			try {
//				col.close();
//			} catch (XMLDBException e) {
//
//				System.out.println("Err: " + e.getMessage());
//				e.printStackTrace();
//			}
//		}
	}

	public static void main(String[] args) throws Exception {
		KnowledgeBaseImpl app = new KnowledgeBaseImpl();
		app.initialise();
		app.executeQuery("//person[fname='John']");
		app.executeQuery("//person[lname='Cletus']");
		//app.updateMatch();
		app.setCurrentCollection("/db");
		app.addCollection("APIDriver");
		app.setCurrentCollection("/db/APIDriver");
		app.storeToCollection("simple", app.readFileFromDisk("kb/simple.xml"));
		app.storeToCollection("antbuild", app.readFileFromDisk("src/build.xml"));
		app.retrieveDocument("simple");

		app.deleteDocument("37671fc150619c20000000f724172bbf");
		app.deleteDocument("37671fc150619c20000000f724195f0b");
		app.deleteDocument("37671fc150619c20000000f7241a06a2");
		app.deleteDocument("37671fc150619c20000000f7241d14e8");
		System.out.println("\n\n");
		app.executeQuery("//javac");
		app.setCurrentCollection("/db/addressbook");

		app.close();
		System.out.println("Connection Closed");

	}

	public void storeToCollection(String name, String data) {
//		XMLResource document = (XMLResource) col.createResource(name, "XMLResource");
//
//		document.setContent(data);
//		col.storeResource(document);
	}

	public String readFileFromDisk(String fileName) throws Exception {
		File file = new File(fileName);
		FileInputStream insr;
		insr = new FileInputStream(file);

		byte[] fileBuffer = new byte[(int) file.length()];

		insr.read(fileBuffer);
		insr.close();

		return new String(fileBuffer);
	}

	public void retrieveDocument(String documentName) {
//		XMLResource document = (XMLResource) col.getResource(documentName);
//		if (document != null) {
//			System.out.println("Document " + documentName);
//			System.out.println(document.getContent());
//		} else {
//			System.out.println("Document " + documentName + " not found");
//		}
	}

	public void updateMatch()  {
//		String xupdate =
//			"<xu:modifications version=\"1.0\""
//				+ "      xmlns:xu=\"http://www.xmldb.org/xupdate\">"
//				+ "   <xu:remove select=\"/person/phone[@type = 'home']\"/>"
//				+ "   <xu:update select=\"/person/phone[@type = 'work']\">"
//				+ "       480-300-3003"
//				+ "   </xu:update>"
//				+ "</xu:modifications>";
//		System.out.println("xupdateStr: \n" + xupdate);
//
//		updateService.update(xupdate);
	}

	public void deleteDocument(String documentName) {
//		Resource document = col.getResource(documentName);
//		if (document != null) {
//			col.removeResource(document);
//			System.out.println("Document " + documentName + " removed");
//		}
	}

}
