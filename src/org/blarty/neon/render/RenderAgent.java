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
 *
 *
 * Renderer.java
 * Created on 22-Nov-2004
 *
 * Renderer
 *
 */
package org.jini.projects.neon.render;

import java.io.InputStream;
import java.net.URL;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import net.jini.id.Uuid;

import org.jini.glyph.Exportable;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.collaboration.Collaborative;
import org.jini.projects.neon.render.PresentableAgent;

/**
 * @author calum
 */

public interface RenderAgent extends Collaborative, Remote{
    public void registerInclude(String name, URL includePage) throws RemoteException;
    
    public void registerPresentation(PresentableAgent agent, URL presenter) throws RemoteException;
    
    public RenderResponse render(PresentableAgent agent, String action,String engineFormat, Map parameters, String outputFormat) throws BadFormatException, RemoteException;
    
    public RenderResponse render(String agentName,String action, String engineFormat, Map parameters, String outputFormat) throws BadFormatException, RemoteException;
    
    public InputStream getAgentResource(String agentName, String resource) throws RemoteException;
    
    public String getAgentResourceMimeType( String resource) throws RemoteException;
    
    public RenderResponse render(Uuid agentID,String action, String engineFormat, Map parameters, String outputFormat) throws BadFormatException, RemoteException;
    public RenderResponse render(AgentIdentity agentID,String action, String engineFormat, Map parameters, String outputFormat) throws BadFormatException, RemoteException;
}
