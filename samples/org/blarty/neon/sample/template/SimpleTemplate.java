package org.jini.projects.neon.sample.template;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.Map;

import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.render.EngineInstruction;
import org.jini.projects.neon.render.PresentableAgent;

public class SimpleTemplate extends AbstractAgent implements PresentableAgent{

	public SimpleTemplate(){
		this.namespace="example.render";
		this.name="GlyphRender";
		System.out.println("Created Glyph Test");
	}
	
	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		return true;
	}

	public EngineInstruction getPresentableFormat(String type, String action, Map params, boolean getTemplate) throws RemoteException {
		// TODO Auto-generated method stub
		return new EngineInstruction("glyph",getTemplate("glyph", action), params);
	}

	public URL getTemplate(String type, String action) throws RemoteException {
		// TODO Auto-generated method stub
		return getClass().getResource("TemplateExample.tmpl");
	}

}
