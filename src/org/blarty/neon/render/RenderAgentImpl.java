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
 * RenderAgentImpl.java
 * Created on 22-Nov-2004
 *
 * RenderAgentImpl
 *
 */
package org.jini.projects.neon.render;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.jini.id.Uuid;

import org.jini.glyph.Exportable;
import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.DelegatedAgent;
import org.jini.projects.neon.agents.LocalAgent;
import org.jini.projects.neon.annotations.Render;
import org.jini.projects.neon.dynproxy.CollaborationProxy;
import org.jini.projects.neon.dynproxy.NeonProxy;
import org.jini.projects.neon.dynproxy.PojoFacadeProxy;
import org.jini.projects.neon.host.AgentRegistry;
import org.jini.projects.neon.host.DomainRegistry;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.neon.host.PrivilegedAgentContext;
import org.jini.projects.neon.render.engines.RenderEngine;

/**
 * @author calum
 */
@Exportable
public class RenderAgentImpl extends AbstractAgent implements RenderAgent, LocalAgent {

	static Properties MIMETYPES = null;

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3762257439419873081L;

	private Map<Uuid, PresentableAgent> nameMap;

	public Map<Uuid, URL> presentationMap;

	public Map<String, URL> agentNameMap;

	public Map<String, URL> includers;

	public RenderAgentImpl() {
		this.namespace = "neon";
		this.name = "Renderer";
	}

	private class PresTuple {
		String name;

		Uuid ID;

		public PresTuple(String name, Uuid ID) {
			this.name = name;
			this.ID = ID;
		}
	}

	public RenderResponse render(String agentName, String action, String engineFormat, Map parameters, String outputFormat)
			throws RemoteException {
		// TODO Complete method stub for render
		Object o = null;
		String trueAction = action;
		try {
			// URL presentation = (URL)agentNameMap.get(agentName);
			o = context.getAgent(agentName);
		} catch (NoSuchAgentException e) {
			// TODO Handle NoSuchAgentException
			System.err.println(e.getMessage());
			e.printStackTrace();
			try {
				if (agentName.endsWith("."))
					o = context.getAgent(agentName + action);
				else
					o = context.getAgent(agentName + "." + action);
				trueAction = "index";
			} catch (Exception ex) {
				System.err.println(ex.getMessage());
				ex.printStackTrace();
				return null;
			}

		}
		Class[] intfs = o.getClass().getInterfaces();
		RenderResponse response = new RenderResponse();
		PresentableAgent pagent = (PresentableAgent) o;
		invokeAction(pagent, trueAction, parameters, response);
		EngineInstruction instruct = pagent.getPresentableFormat(engineFormat, trueAction, parameters, true);
		response.setContent(doRender(instruct.getData(), instruct.getEngineFormat(), instruct.getTemplate(), outputFormat));
		// return doRender(pagent.getPresentableFormat(engineFormat, trueAction,
		// parameters, true), engineFormat, pagent.getTemplate("html",
		// trueAction),outputFormat);
		return response;
	}

	/*
	 * @see org.jini.projects.neon.agents.Agent#init()
	 */
	public boolean init() {
		// TODO Complete method stub for init
		this.namespace = "neon";
		this.name = "Renderer";
		presentationMap = new HashMap<Uuid, URL>();
		agentNameMap = new HashMap<String, URL>();
		nameMap = new HashMap<Uuid, PresentableAgent>();
		includers = new TreeMap<String, URL>();
		includers.put("tags", getClass().getResource("tags.html"));
		return true;
	}

	/*
	 * @see
	 * org.jini.projects.neon.web.Renderer#registerPresentation(org.jini.projects
	 * .neon.web.PresentableAgent, java.lang.String)
	 */
	public void registerPresentation(PresentableAgent agent, URL presenter) throws RemoteException {
		try {
			// TODO Complete method stub for registerPresentation

			Agent ag = (Agent) agent;
			presentationMap.put(ag.getIdentity().getID(), presenter);
			agentNameMap.put(ag.getNamespace() + "." + ag.getNamespace(), presenter);
			nameMap.put(ag.getIdentity().getID(), agent);
			getAgentLogger().fine("Presentation REGISTERED (" + presenter + ")");
		} catch (Exception ex) {
			System.out.println("RenderAgent Encountered an exception");
			ex.printStackTrace();
		}
	}

	public RenderResponse render(Uuid agentID, String action, String engineFormat, Map parameters, String outputFormat)
			throws RemoteException {
		URL presentation = (URL) presentationMap.get(agentID);
		PrivilegedAgentContext pc = (PrivilegedAgentContext) getAgentContext();
		RenderResponse response = new RenderResponse();
		try {
			PresentableAgent agent = (PresentableAgent) pc.getAgentHost().getRegistry().getAgent(agentID);

			invokeAction(agent, action, parameters, response);
			EngineInstruction instruct = agent.getPresentableFormat(engineFormat, action, parameters, true);
			response.setContent(doRender(instruct.getData(), instruct.getEngineFormat(), instruct.getTemplate(), outputFormat));
		} catch (NoSuchAgentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setContent("BAD PAGE");
		}
		return response;
	}

	/*
	 * @see
	 * org.jini.projects.neon.web.Renderer#render(org.jini.projects.neon.web
	 * .PresentableAgent)
	 */
	public RenderResponse render(PresentableAgent agent, String action, String engineFormat, Map parameters, String outputFormat)
			throws RemoteException {
		// TODO Complete method stub for render
		Uuid referentUuid = null;
		if (agent == null)
			getAgentLogger().warning("Requesting to render an agent that  is null");
		else {
			Class[] cl = agent.getClass().getInterfaces();
			for (int i = 0; i < cl.length; i++) {
				Class c = cl[i];
			}
		}
		if (agent instanceof CollaborationProxy) {
			getAgentLogger().finest("Getting ID from CollaborationProxy");
			referentUuid = ((CollaborationProxy) agent).getIdentity().getID();
		}
		if (agent instanceof Agent) {
			getAgentLogger().finest("Getting ID from Agent");
			referentUuid = ((Agent) agent).getIdentity().getID();
		}
		// URL presentation = (URL)presentationMap.get(referentUuid);
		RenderResponse response = new RenderResponse();
		invokeAction(agent, action, parameters, response);
		EngineInstruction instruct = agent.getPresentableFormat(engineFormat, action, parameters, true);
		response.setContent(doRender(instruct.getData(), instruct.getEngineFormat(), instruct.getTemplate(), outputFormat));
		return response;
	}

	/**
	 * @param agent
	 * @param presentation
	 * @return
	 */
	private String doRender(Object info, String engineFormat, URL presentation, String outputFormat) {
		try {
			String realFormat = engineFormat;
			if (engineFormat.equals("xml") || engineFormat.equals("html") || engineFormat.equals("xhtml"))
				realFormat = "xsl";

			RenderEngine engine = (RenderEngine) context.getAgent("neon.render.engines." + realFormat + "Engine");
			return (String) engine.render(presentation, info);
		} catch (NoSuchAgentException e) {
			// TODO Handle NoSuchAgentException
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Bad Page";
	}

	private void doIncludes(StringWriter writer) {

		// TODO Auto-generated method stub
		Pattern p = Pattern.compile("\\$\\[.*\\]");
		Matcher m = p.matcher(writer.getBuffer());
		while (m.find()) {
			System.out.println("Found includer tag: " + m.group());

			String includerTag = m.group().trim().substring(2).trim();
			includerTag = includerTag.substring(0, includerTag.length() - 1);
			String[] operands = includerTag.toLowerCase().split(" ");
			if (operands[0].equals("include")) {
				// writer.getBuffer().replace(m.start(),m.end(),"<b>INCLUDE
				// TAG REPLACED</b>");
				URL u = includers.get(operands[1]);
				try {

					BufferedReader rdr;
					rdr = new BufferedReader(new InputStreamReader(new BufferedInputStream(u.openStream(), 256)));
					byte[] buff = new byte[256];
					String line = rdr.readLine();
					StringBuffer insertBuffer = new StringBuffer();
					while (line != null) {
						insertBuffer.append(line + "\n");
						line = rdr.readLine();
					}
					rdr.close();
					writer.getBuffer().replace(m.start(), m.end(), insertBuffer.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void registerInclude(String name, URL includePage) {
		includers.put(name, includePage);

	}

	private void invokeAction(PresentableAgent agent, String action, Map parameters, RenderResponse response) {
		if (!action.equals("index")) {
			try {
				Method toInvoke = null;
				Class[] clazz = agent.getClass().getInterfaces();
				boolean invokedAnAction = false;
				for (Class cl : clazz) {
					Method[] methods = cl.getMethods();

					for (int i = 0; i < methods.length; i++) {
						Method meth = methods[i];

						Render renderAnnotation = meth.getAnnotation(Render.class);

						
						if (renderAnnotation != null) {
							Method agentMethod = agent.getClass().getMethod(meth.getName(), meth.getParameterTypes());
							if (findActionInvoker(agent, action, parameters, agentMethod, renderAnnotation)) {
								invokedAnAction = true;

								response.setContentType(renderAnnotation.contentType());
								response.setCacheable(renderAnnotation.cacheable());
							}
						}

					}
				}
				// Method m = agent.getClass().getMethod(action, new Class[] {
				// Map.class });

				// if (!invokedAnAction)
				// System.out.println("MNot invoking an action for " + action);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private boolean findActionInvoker(Object agent, String action, Map parameters, Method meth, Render renderAnnotation)
			throws InstantiationException, IllegalAccessException, InvocationTargetException {
		Method toInvoke = null;
		if (renderAnnotation != null) {
			String renderAction = renderAnnotation.action();
			if (renderAction.equals(action)) {
				Class[] populateClass = renderAnnotation.populate();
				Object[] populated = new Object[populateClass.length];
				if (populateClass.length != 0) {
					for (int j = 0; j < populateClass.length; j++) {
						Class c = populateClass[j];
						Object o = c.newInstance();
						populateObject(parameters, o);
						populated[j] = o;
					}
				}
				toInvoke = meth;
				if (toInvoke != null) {
					toInvoke.invoke(agent, populated);
					return true;
				}
			}

		}
		return false;
	}

	private void populateObject(Map options, Object emptyObject) throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		Class c = emptyObject.getClass();
		Method[] methods = c.getMethods();
		for (Method m : methods) {
			if (m.getName().startsWith("set")) {
				String property = m.getName().substring(3);
				Object option = options.get(property.toLowerCase());
				if (option != null) {
					Class optionClass = option.getClass();
					Class[] paramClass = m.getParameterTypes();
					if (option instanceof String[] && ((String[]) option).length == 1)
						option = new String(((String[]) option)[0]);
					if (paramClass.length == 1) {
						if (paramClass[0] == String.class)

							m.invoke(emptyObject, new Object[] { option });
						if (paramClass[0] == String[].class)
							m.invoke(emptyObject, new Object[] { option });
						if (paramClass[0] == int.class)
							m.invoke(emptyObject, new Object[] { Integer.parseInt((String) option) });
						if (paramClass[0] == double.class)
							m.invoke(emptyObject, new Object[] { Double.parseDouble((String) option) });
						if (paramClass[0] == float.class)
							m.invoke(emptyObject, new Object[] { Float.parseFloat((String) option) });
						if (paramClass[0] == long.class)
							m.invoke(emptyObject, new Object[] { Long.parseLong((String) option) });
					}

				}
			} else if (m.getName().startsWith("add")) {
				String property = m.getName().substring(3);
				String[] option = (String[]) options.get(property.toLowerCase());
				if (option != null) {
					Class optionClass = option.getClass();
					Class[] paramClass = m.getParameterTypes();
					if (option instanceof String[] && ((String[]) option).length == 1)
						option = (String[]) option;
					if (paramClass.length == 1) {
						if (paramClass[0] == String.class)
							for (Object o : option)
								m.invoke(emptyObject, new Object[] { o });
						if (paramClass[0] == String[].class)
							m.invoke(emptyObject, new Object[] { option });
						if (paramClass[0] == int.class)
							for (Object o : option)
								m.invoke(emptyObject, new Object[] { Integer.parseInt((String) o) });
						if (paramClass[0] == double.class)
							for (Object o : option)
								m.invoke(emptyObject, new Object[] { Double.parseDouble((String) o) });
						if (paramClass[0] == float.class)
							for (Object o : option)
								m.invoke(emptyObject, new Object[] { Float.parseFloat((String) o) });
						if (paramClass[0] == long.class)
							for (Object o : option)
								m.invoke(emptyObject, new Object[] { Long.parseLong((String) o) });
					}

				}
			}
		}
	}

	public RenderResponse render(AgentIdentity agentID, String action, String engineFormat, Map parameters, String outputFormat)
			throws RemoteException {
		AgentRegistry registry = DomainRegistry.getDomainRegistry().getDomain(context.getDomainName()).getRegistry();
		PresentableAgent agent;
		RenderResponse response = new RenderResponse();
		try {
			agent = (PresentableAgent) registry.getAgent(agentID);
			invokeAction(agent, action, parameters, response);
			EngineInstruction instruct = agent.getPresentableFormat(engineFormat, action, parameters, true);
			response.setContent(doRender(instruct.getData(), instruct.getEngineFormat(), instruct.getTemplate(), outputFormat));
		} catch (NoSuchAgentException e) {
			// TODO Handle NoSuchAgentException
			e.printStackTrace();
		}
		response.setContent("Bad page");
		return response;
	}

	public InputStream getAgentResource(String agentName, String resource) {
		try {
			AgentRegistry registry = DomainRegistry.getDomainRegistry().getDomain(context.getDomainName()).getRegistry();
			Agent agent = registry.getAgent(agentName);
			Object theResourceRef = getActualAgent(agent);
			URL testURL = theResourceRef.getClass().getResource(resource);

			return theResourceRef.getClass().getResourceAsStream(resource);
		} catch (NoSuchAgentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private Object getActualAgent(Agent ag) {
		Object actual = null;
		if (ag instanceof Proxy) {
			InvocationHandler handler = Proxy.getInvocationHandler(ag);
			if (handler instanceof NeonProxy)
				actual = ((NeonProxy) handler).getReceiver();
			if (actual instanceof Proxy) {

				InvocationHandler pojoHandler = Proxy.getInvocationHandler(actual);
				if (pojoHandler instanceof PojoFacadeProxy) {

					actual = ((PojoFacadeProxy) pojoHandler).getReceiver();

				}
			}
		} else
			actual = ag;

		if (actual instanceof DelegatedAgent)
			actual = ((DelegatedAgent) actual).getDelegatedObject();
		return actual;
	}

	public String getAgentResourceMimeType(String resource) {
		if (MIMETYPES == null) {
			MIMETYPES = new Properties();
			try {
				MIMETYPES.load(getClass().getResourceAsStream("mimetypes.properties"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String mimetype = MIMETYPES.getProperty(resource.substring(resource.lastIndexOf('.')));
		if (mimetype == null)
			return MIMETYPES.getProperty(".txt");
		else
			return mimetype;
	}

}
