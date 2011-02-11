package org.jini.projects.neon.render.engines;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;

import net.sf.saxon.StandardURIResolver;
import net.sf.saxon.trans.XPathException;

public class VfsUriResolver extends StandardURIResolver {
	public Source resolve(String href, String base) {
		System.out.println("The href is: " + href +  "; base is: " + base);
	if(base.startsWith("jar:"))
		href=(base.substring(0,base.lastIndexOf("/")+1)) + href;
		try {
			return super.resolve(href, base);
		} catch (XPathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
