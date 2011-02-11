/*
 * neon : org.jini.projects.neon.render.engines
 * 
 * 
 * RenderEngine.java
 * Created on 03-Aug-2005
 * 
 * RenderEngine
 *
 */
package org.jini.projects.neon.render.engines;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.jini.projects.neon.collaboration.Collaborative;

/**
 * Class that allows data pulled from an agent to be rendered against an agent-supplied template
 * @author calum
 */
public interface RenderEngine extends Collaborative,Remote {
	/**
	 * Obtain an instance of data processed against a given template
	 * @param template the template to be used by the engine
	 * @param data the data to be processed obtained from the agent
	 * @return Engine specific output (i.e XML, plain text or PDF file)
	 * @throws RemoteException
	 */
	public Object render(Object template, Object data) throws RemoteException;
}
