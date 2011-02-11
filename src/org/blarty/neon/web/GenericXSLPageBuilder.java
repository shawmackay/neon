
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
 * GenericXSLPageBuilder.java
 * Created on 22-Nov-2004
 *GenericXSLPageBuilder
 */

package org.jini.projects.neon.web;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * @author calum
 */
public class GenericXSLPageBuilder {
	public String generate(String pagename, Map parameters) {
		try {
			String wrapper = "<page/>";
			Source xmlsource = new StreamSource(new StringReader(wrapper));
			Source xsloutsource = new StreamSource(new File(pagename));
			StringWriter writer = new StringWriter();
			StreamResult xmlResult = new StreamResult(writer);
			TransformerFactory factory = TransformerFactory.newInstance();
			
			Transformer trans = factory.newTransformer(xsloutsource);
			for (Iterator iter = parameters.entrySet().iterator(); iter.hasNext();) {
				Map.Entry entry = (Map.Entry) iter.next();
				System.out.println("Adding " + entry.getValue() + "[" + entry.getValue().getClass().getName() + "] as " + entry.getKey());
				trans.setParameter((String) entry.getKey(), entry.getValue());
			}
			trans.transform(xmlsource, xmlResult);

			return writer.getBuffer().toString();
		} catch (TransformerConfigurationException e) {
			// TODO Handle TransformerConfigurationException
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Handle TransformerFactoryConfigurationError
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Handle TransformerException
			e.printStackTrace();
		}
		return "Bad page";
	}
}
