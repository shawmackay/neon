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
 * neon : org.jini.projects.neon.dynproxy
 * StatelessAgentFactory.java
 * Created on 16-Jul-2004
 *StatelessAgentFactory
 */
package org.jini.projects.neon.dynproxy;

import java.lang.reflect.Proxy;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.logging.Logger;

import net.jini.id.UuidFactory;

import org.jini.glyph.chalice.DefaultExporterManager;
import org.jini.glyph.chalice.ProxyPair;
import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.collaboration.Collaborative;
import org.jini.projects.neon.host.ManagedDomain;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.zenith.messaging.channels.connectors.PublishingQConnector;
import org.jini.projects.zenith.messaging.system.ChannelException;
import org.jini.projects.zenith.messaging.system.MessagingService;

/**
 * @author calum
 */
public class ExportableAgentFactory {
	static Logger log = Logger.getLogger("org.jini.projects.neon.dynproxy");

	private static boolean useDirect = true;

	public static void setDirectProxyMode(boolean use) {
		useDirect = use;
	}

	public static Object create(ManagedDomain ctx, String name, MessagingService svc) {
		
		if (!useDirect) {
			return getMessageBasedProxy(ctx, name, svc);
		} else
			try {
				// We need an initial agent reference in order to build the
				// proxy
				System.out.println("Using direct stateless proxy for accessing: " + name);
				Agent a = ctx.getRegistry().getAgent(name);
				if (a instanceof Remote) {
					System.out.println("Getting remote agent");
					System.out.println("Exporting");
					ProxyPair proxyPair = DefaultExporterManager.getManager().exportProxyAndPair((Remote) a, "standard", UuidFactory.generate());
					Object smartProxy = proxyPair.getSmartProxy();
					ArrayList arr = new ArrayList();
					getInterfaces(a.getClass(), arr);
					Class[] intf = (Class[]) arr.toArray(new Class[] {});
					return smartProxy;
				} else {
					System.out.println("Returning Hybrid Proxy");
					return getMessageBasedProxy(ctx, name, svc);
				}
				
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAgentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
	}

	private static Object getMessageBasedProxy(ManagedDomain ctx, String name, MessagingService svc) {
		ExportedAgentProxy p = null;
		if (svc == null)
			log.severe("Messaging Service is null");
		try {
			PublishingQConnector conn = svc.getPublishingConnector(ctx.getDomainName() + "|" + name);
			p = new ExportedAgentProxy(ctx.getDomainName() + "|" + name, svc, svc.getTemporaryChannel(null), conn);
		} catch (RemoteException e) {
			// TODO Handle RemoteException
			e.printStackTrace();
		} catch (ChannelException e) {
			// TODO Handle ChannelException
			e.printStackTrace();
		}

		Class[] collabInterfaces = getAgentProxyInterfaces(ctx, name);

		return Proxy.newProxyInstance(collabInterfaces[0].getClassLoader(), collabInterfaces, p);
	}

	private static Class[] getAgentProxyInterfaces(ManagedDomain ctx, String name) {
		try {
			log.info("Looking for " + name);
			Agent a = ctx.getRegistry().getAgent(name);
			if (a == null)
				return null;
			ArrayList arr = new ArrayList();
			getInterfaces(a.getClass(), arr);
			Class[] intf = (Class[]) arr.toArray(new Class[] {});
			// return new AgentProxyInfo(a.getIdentity(), a.getNamespace() + "."
			// + a.getName(), intf);
			return intf;
		} catch (NoSuchAgentException e) {
			// URGENT Handle NoSuchAgentException
			e.printStackTrace();
		}
		return null;
	}

	private static void getInterfaces(Class cl, ArrayList arr) {
		// System.out.println("Checking interfaces...." + cl.getName());

		Class[] classes = cl.getInterfaces();
		// Class[] classes = cl.getClasses();
		for (int i = 0; i < classes.length; i++) {
			Class subclass = classes[i];
			// System.out.println("Class: " + subclass.getName());
			if (subclass.isInterface()) {
				getInterfaces(subclass, arr);
			}
			if (subclass.equals(Collaborative.class)) {
				if (!cl.getName().startsWith("$Proxy")) {
					if (!arr.contains(cl))
						arr.add(cl);
				}
			}
		}
	}
}
