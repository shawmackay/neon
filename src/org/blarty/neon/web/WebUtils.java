package org.jini.projects.neon.web;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.betwixt.io.BeanReader;
import org.apache.commons.betwixt.io.BeanWriter;
import org.apache.commons.betwixt.io.read.BeanCreationChain;
import org.apache.commons.betwixt.strategy.DecapitalizeNameMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.jini.projects.neon.render.BadFormatException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.commons.logging.LogFactory;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

public class WebUtils {
	public static String convertObjectToXML(String rootName, Object o) throws BadFormatException {
		try {
			StringWriter sWriter = new StringWriter();
			BeanWriter bWriter = new BeanWriter(sWriter);
			bWriter.getXMLIntrospector().getConfiguration().setAttributesForPrimitives(false);
			bWriter.enablePrettyPrint();
			bWriter.getBindingConfiguration().setMapIDs(false);

			// set a custom name mapper for attributes
			bWriter.getXMLIntrospector().getConfiguration().setAttributeNameMapper(new DecapitalizeNameMapper());
			// set a custom name mapper for elements
			bWriter.getXMLIntrospector().getConfiguration().setElementNameMapper(new DecapitalizeNameMapper());

			bWriter.enablePrettyPrint();
			bWriter.write(rootName, o);
			bWriter.flush();
			bWriter.close();
			return sWriter.toString();
		} catch (IOException e) {
			throw new BadFormatException(e);
		} catch (SAXException e) {
			throw new BadFormatException(e);
		} catch (IntrospectionException e) {
			throw new BadFormatException(e);
		}
	}

	public static Object convertXMLToObject(String xml, String rootName, Class buildClass) {

		try {
			BeanReader reader = new BeanReader();
			reader.registerBeanClass(rootName, buildClass);
			return reader.parse(new StringReader(xml));
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public static String combineDocuments(String rootName, XmlCombinerLocation[] items) {
		ArrayList<XmlCombinerLocation> listedItems = new ArrayList<XmlCombinerLocation>();
		for (XmlCombinerLocation item : items) {
			listedItems.add(item);
		}
		return combineDocuments(rootName, listedItems);
	}

	public static String combineDocuments(String rootName, List<XmlCombinerLocation> items) {
		String output = "";
		try {
			InternalURIResolver resolve = new InternalURIResolver();
			StringBuffer buffer = new StringBuffer();
			buffer.append("<root name='" + rootName + "'>");

			for (XmlCombinerLocation combiloc : items) {

				if (combiloc.isNodeNameAsParent()) {
					buffer.append("<subroot name='" + combiloc.getNodeName() + "'>");
					buffer.append("<xi:include xmlns:xi='http://www.w3.org/2001/XInclude' href='" + combiloc.getNodeName()
							+ "'/>");
					buffer.append("</subroot>");
				} else
					buffer.append("<xi:include xmlns:xi='http://www.w3.org/2001/XInclude' href='" + combiloc.getNodeName()
							+ "'/>");

				resolve.addURI(combiloc.getNodeName(), new StreamSource(new StringReader(combiloc.getXml())));
			}
			buffer.append("</root>");
			TransformerFactory tfact = TransformerFactory.newInstance();
			Transformer identityTransform = tfact.newTransformer(new StreamSource(WebUtils.class
					.getResourceAsStream("include.xsl")));

			tfact.setURIResolver(resolve);
			StringWriter outputBuffer = new StringWriter();
			identityTransform.setURIResolver(resolve);
			identityTransform.transform(new StreamSource(new StringReader(buffer.toString())), new StreamResult(outputBuffer));
			return outputBuffer.toString();
		} catch (FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	public static class InternalURIResolver implements URIResolver {

		private HashMap<String, Source> knownURIs = new HashMap<String, Source>();

		public InternalURIResolver() {

		}

		public void addURI(String URI, Source theSource) {
			knownURIs.put(URI, theSource);
		}

		public Source resolve(String href, String base) throws TransformerException {
			// TODO Auto-generated method stub
			System.out.println("Locating: " + href + "(Base: " + base + ")");
			return knownURIs.get(href);
		}

	}

	public static Document createDocument(String source) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = fact.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(source)));
		return doc;
	}

	public static void main(String[] args) {
		XmlCombinerLocation loc = new XmlCombinerLocation("hello", "<a><b><c value='10'/></b><b></b></a>");
		XmlCombinerLocation loc2 = new XmlCombinerLocation("there", "<blarty><b><c value='10'/></b><b></b></blarty>");

		ArrayList<XmlCombinerLocation> items = new ArrayList<XmlCombinerLocation>();
		items.add(loc);
		items.add(loc2);
		System.out.println(combineDocuments("mydoc", items));
	}
}
