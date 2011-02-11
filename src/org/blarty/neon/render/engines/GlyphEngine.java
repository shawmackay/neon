package org.jini.projects.neon.render.engines;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.Map;

import org.jini.glyph.ContentTemplate;
import org.jini.glyph.ContentTemplateException;
import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.LocalAgent;
import org.jini.projects.neon.host.transactions.NonTransactionalResource;

/**
 * Uses the available Glyph ContentTemplate merging code to produce a rendered file
 * @author calum
 *
 */
public class GlyphEngine extends AbstractAgent implements RenderEngine,LocalAgent, NonTransactionalResource{
	public GlyphEngine(){
		this.name = "glyphEngine";
		this.namespace="neon.render.engines";
	}
	
	public Object render(Object template, Object data) throws RemoteException {
		// TODO Auto-generated method stub
		try {
			ContentTemplate t = new ContentTemplate((URL) template);
			Map datavalues = (Map) data;
			return t.getContent(datavalues);
		} catch (ContentTemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
@Override
public boolean init() {
	// TODO Auto-generated method stub
	return true;
}
}
