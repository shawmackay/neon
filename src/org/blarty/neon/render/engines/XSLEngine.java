/*
 * neon : org.jini.projects.neon.render.engines
 * 
 * 
 * XSLEngine.java
 * Created on 03-Aug-2005
 * 
 * XSLEngine
 *
 */
package org.jini.projects.neon.render.engines;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jini.glyph.Exportable;
import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.LocalAgent;
import org.jini.projects.neon.host.transactions.NonTransactionalResource;

/**
 * @author calum
 */
@Exportable
public class XSLEngine extends AbstractAgent implements RenderEngine,LocalAgent, NonTransactionalResource{

	public XSLEngine(){
		this.name = "xslEngine";
		this.namespace="neon.render.engines";
	}
	
	public Object render(Object template, Object data) throws RemoteException{
		// TODO Complete method stub for render
		try {			
			Source xmlsource = new StreamSource(new StringReader((String) data));
			Source xsltsource = new StreamSource(((URL)template).openStream(), ((URL) template).toExternalForm());         
			StringWriter writer = new StringWriter();
			StreamResult xmlResult = new StreamResult(writer);
			TransformerFactory factory = TransformerFactory.newInstance();
			factory.setURIResolver(new VfsUriResolver());
			Transformer trans = factory.newTransformer(xsltsource);
		
			trans.transform(xmlsource, xmlResult);
            
            //Scan for includer tags            
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
		}catch (java.io.IOException e) {
			// TODO Handle TransformerException
			e.printStackTrace();
		}
		return "Bad page";
	}

	@Override
	public boolean init() {
		// TODO Complete method stub for init
		return  true;
	}

	
}
