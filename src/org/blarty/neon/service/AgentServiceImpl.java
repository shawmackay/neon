package org.jini.projects.neon.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.rmi.server.RMIClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.jini.admin.Administrable;
import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.core.constraint.RemoteMethodControl;
import net.jini.core.entry.Entry;
import net.jini.core.entry.UnusableEntryException;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lookup.ServiceID;
import net.jini.core.transaction.CannotJoinException;
import net.jini.core.transaction.TransactionException;
import net.jini.core.transaction.UnknownTransactionException;
import net.jini.core.transaction.server.CrashCountException;
import net.jini.core.transaction.server.ServerTransaction;
import net.jini.export.Exporter;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.http.HttpServerEndpoint;
import net.jini.space.JavaSpace;

import org.jini.glyph.chalice.DefaultExporterManager;
import org.jini.glyph.chalice.ExporterManager;
import org.jini.glyph.chalice.ProxyPair;
import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentConstraints;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.ReferenceableConstraints;
import org.jini.projects.neon.agents.util.DomainEntry;
import org.jini.projects.neon.agents.util.EncryptedDomainEntry;
import org.jini.projects.neon.agents.util.meta.DomainInvocation;
import org.jini.projects.neon.agents.util.meta.TransactionID;
import org.jini.projects.neon.collaboration.Collaborative;
import org.jini.projects.neon.dynproxy.AgentProxyInfo;
import org.jini.projects.neon.dynproxy.ExportableAgentFactory;
import org.jini.projects.neon.dynproxy.MethodInvocation;
import org.jini.projects.neon.dynproxy.StatelessAgentFactory;
import org.jini.projects.neon.host.AgentDomain;
import org.jini.projects.neon.host.AgentDomainImpl;
import org.jini.projects.neon.host.DomainRegistry;
import org.jini.projects.neon.host.ManagedDomain;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.neon.host.transactions.NonTransactionalResource;
import org.jini.projects.neon.host.transactions.RemoteTransactionalResource;
import org.jini.projects.neon.host.transactions.TransactionAccessor;
import org.jini.projects.neon.host.transactions.TransactionAgent;
import org.jini.projects.neon.host.transactions.TransactionalResource;
import org.jini.projects.neon.kernel.Kernel;
import org.jini.projects.neon.service.admin.AgentAdminImpl;
import org.jini.projects.neon.service.admin.AgentServiceAdmin;
import org.jini.projects.neon.service.admin.constrainable.AgentAdminProxy;
import org.jini.projects.neon.service.start.DelegateCatalog;
import org.jini.projects.neon.service.start.DomainConfig;
import org.jini.projects.neon.service.start.StartupConfig;
import org.jini.projects.neon.service.start.StartupHandler;
import org.jini.projects.neon.util.MethodViewer;
import org.jini.projects.neon.util.encryption.EncryptionUtils;
import org.jini.projects.zenith.messaging.channels.ReceiverChannel;
import org.jini.projects.zenith.messaging.messages.Message;
import org.jini.projects.zenith.messaging.system.ChannelException;
import org.jini.projects.zenith.messaging.system.MessagingListener;
import org.jini.projects.zenith.messaging.system.MessagingManager;
import org.jini.projects.zenith.messaging.system.MessagingService;
import org.xml.sax.SAXException;

/**
 * The Jini Service Implementation for Neon. This holds the Administration
 * object for Neon, and proxies the Kernel and DomainRegistry classes, so that
 * agents may deployed via the service interface. <br>
 * For constraints, it checks that the domain exists in the constraints,
 * afterwards it delegates to the specified doamin to check the other
 * constraints.
 */
public class AgentServiceImpl implements AgentBackendService, Administrable {
	transient Logger svcLog = Logger.getLogger("org.jini.projects.neon.service");

	// private transient AgentDomain host;
	private transient Kernel kernel;

	private transient Exporter exp;

	private transient AgentAdminImpl adminOb;

	private AgentServiceAdmin remoteAdmin;

	private ServiceID sid;

	private MessagingService svc;

	private JavaSpace loaderSpace;

	private Configuration config;

	private boolean direct;

	private AgentBackendService exportedSelf;

	public AgentServiceImpl(MessagingService svc, ServiceID sid, JavaSpace loaderSpace, Configuration config) {
		this.config = config;
		svcLog.setLevel(svcLog.getParent().getLevel());
		if (svc == null)
			svcLog.severe("Messaging Service reference is null!");
		this.svc = svc;
		this.sid = sid;
		DomainRegistry.getDomainRegistry().setSid(sid);
		DomainRegistry.getDomainRegistry().setServiceConfig(config);
		this.loaderSpace = loaderSpace;

		setupMode(config);
	}

	private void setupMode(Configuration config) {
		this.config = config;
		String mode;
		try {
			mode = (String) config.getEntry("org.jini.projects.neon", "mode", String.class, "direct");
			direct = mode.equals("direct");
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setRemoteVersion(AgentBackendService exportedSelf) {
		this.exportedSelf = exportedSelf;
	}

	public void setAgentHost(AgentDomain host) {
		// this.host = host;
	}

	public void setMessagingServiceRef(MessagingService svc) {
		this.svc = svc;
	}

	public void setServiceKernel(Kernel k) {
		this.kernel = k;
	}

	public boolean canAccept(AgentConstraints constraints) throws RemoteException {
		svcLog.log(Level.FINER, "Checking Agent Constraints");
		ManagedDomain domain;
		if (constraints.getConstraints().getDomain() != null) {
			domain = kernel.getDomain(constraints.getConstraints().getDomain());
			if (domain != null)
				return domain.canAccept(constraints);
			else {
				svcLog.info("Specified Domain does not exist on this host");
				return false;
			}
		} else {
			domain = kernel.getDomain("global");
			return domain.canAccept(constraints);
		}
	}

	public void deployAgent(AgentConstraints constraints, CodebaseObject agent) throws RemoteException {
		svcLog.log(Level.FINER, "Deploying Agent without callback");
		RemoteEventListener callback = null;
		Agent a = null;

		try {
			net.jini.io.MarshalledInstance instance = agent.getMarshalled();
			a = (Agent) instance.get(false);
			System.out.println("Marshalled Agent CL: " + RMIClassLoader.getClassAnnotation(a.getClass()));

			if (constraints == null)
				deployToDomain(a, callback);
			else {
				if (canAccept(constraints))
					deployToDomain(a, callback);
				else
					throw new RemoteException("Agent cannot be deployed - constraints not met");
			}
		} catch (IOException e) {
			// URGENT Handle IOException
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// URGENT Handle ClassNotFoundException
			e.printStackTrace();
		}
	}

	private void deployToDomain(Agent a, RemoteEventListener callback) {

		AgentConstraints ac = a.getConstraints();
		if (ac != null) {
			String dom = a.getConstraints().getConstraints().getDomain();
			if (dom != null) {
				ManagedDomain domain = kernel.getDomain(dom);
				if (domain != null) {
					System.out.println("Deploying agent [" + a.getNamespace() + "." + a.getName() + "] to " + dom + " domain");
					deployToDomain(a, callback, domain);
				}
			} else {
				System.out.println("Deploying agent [" + a.getNamespace() + "." + a.getName() + "] to global domain");
				deployToDomain(a, callback, kernel.getDomain("global"));
			}
		} else {
			System.out.println("Deploying agent [" + a.getNamespace() + "." + a.getName() + "] to global domain");
			deployToDomain(a, callback, kernel.getDomain("global"));
		}
	}

	private void deployToDomain(Agent a, RemoteEventListener callback, ManagedDomain domain) {
		domain.deployAgent(a, callback);
	}

	private void deployToDomain(Agent a, RemoteEventListener callback, String domainName) {
		kernel.getDomain(domainName).deployAgent(a, callback);
	}

	private void deployToDomain(Agent a, String domainToUse, RemoteEventListener callback) {

		AgentConstraints ac = a.getConstraints();

		String dom = domainToUse;
		if (dom != null) {
			ManagedDomain domain = kernel.getDomain(dom);
			if (domain != null) {
				System.out.println("Deploying agent [" + a.getNamespace() + "." + a.getName() + "] to " + dom + " domain");
				domain.deployAgent(a);
			}
		} else {
			System.out.println("Deploying agent [" + a.getNamespace() + "." + a.getName() + "] to global domain");
			kernel.getDomain("global").deployAgent(a, callback);
		}

	}

	private void deployToDomain(Object o, String constraintsLocation, String configurationLocation) throws MalformedURLException {
		ReferenceableConstraints constraints = null;
		if (constraintsLocation != null && !constraintsLocation.equals("")) {
			if (constraintsLocation.startsWith("class:")) {
				URL constLoc = o.getClass().getResource(configurationLocation.substring(6));
				constraints = new ReferenceableConstraints(constLoc);
			} else {
				URL constLoc = new URL(configurationLocation);
				constraints = new ReferenceableConstraints(constLoc);
			}
		}
		if (constraints != null) {
			String dom = constraints.getConstraints().getDomain();
			if (dom != null) {
				ManagedDomain domain = kernel.getDomain(dom);
				if (domain != null) {

					domain.deployObject(o, configurationLocation, constraintsLocation);
				}
			} else {

				kernel.getDomain("global").deployObject(o, configurationLocation, constraintsLocation);
			}
		} else {

			kernel.getDomain("global").deployObject(o, configurationLocation, constraintsLocation);
		}
	}

	private void deployToDomain(Object o, String constraintsLocation, String domainToUse, String configurationLocation)
			throws MalformedURLException {
		ReferenceableConstraints constraints = null;
		if (constraintsLocation != null && !constraintsLocation.equals("")) {
			if (constraintsLocation.startsWith("class:")) {
				URL constLoc = o.getClass().getResource(configurationLocation.substring(6));
				constraints = new ReferenceableConstraints(constLoc);
			} else {
				URL constLoc = new URL(configurationLocation);
				constraints = new ReferenceableConstraints(constLoc);
			}
		}
		String dom = domainToUse;
		if (dom != null) {
			ManagedDomain domain = kernel.getDomain(dom);
			if (domain != null) {

				domain.deployObject(o, configurationLocation, constraintsLocation);
			}
		} else {

			kernel.getDomain("global").deployObject(o, configurationLocation, constraintsLocation);
		}
	}

	public void deployAgent(AgentConstraints constraints, CodebaseObject agent, RemoteEventListener callback)
			throws RemoteException {
		svcLog.log(Level.FINER, "Deploying Agent with callback");
		Agent a = null;
		try {

			AgentConstraints ac = constraints;
			if (ac == null) {
				a = (Agent) agent.getMarshalled().get(false);
				deployToDomain(a, callback);
			} else {
				if (canAccept(constraints)) {
					a = (Agent) agent.getMarshalled().get(false);
					deployToDomain(a, callback);
				} else
					throw new RemoteException("Agent cannot be deployed - constraints not met");
			}
		} catch (IOException e) {
			// URGENT Handle IOException
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// URGENT Handle ClassNotFoundException
			e.printStackTrace();
		}
	}

	public Object sendMessage(String agentName, String domain, Message message) throws NoSuchAgentException, RemoteException {

		ExternalMessageHandler listen = new ExternalMessageHandler(agentName, domain, message);

		listen.execute();
		svcLog.finest("Notified returning");
		Object returnObject = listen.getReturnObject();
		if (returnObject instanceof Throwable) {
			((Throwable) returnObject).printStackTrace();
			throw new RemoteException("Error during Message dispatch", (Throwable) returnObject);
		}
		return returnObject;

	}

	private class ExternalMessageHandler implements MessagingListener {
		private Object returnObject;

		private String agentName;

		private String domain;

		private Message message;

		/**
		 * @param agentName
		 * @param domain
		 * @param message
		 */
		public ExternalMessageHandler(String agentName, String domain, Message message) {
			super();
			this.agentName = agentName;
			this.domain = domain;
			this.message = message;
		}

		public void execute() {
			AgentDomain dom = kernel.getDomain(domain);
			ReceiverChannel rec = dom.getTemporaryChannel();
			message.getHeader().setReplyAddress(rec.getName());

			try {
				MessagingManager.getManager().registerOnChannel(rec.getName(), this);
				System.out.println("Sending message to " + agentName);
				dom.sendAsyncMessage(agentName, message);
				svcLog.finest("Message sent");
				synchronized (this) {
					try {
						wait();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			} catch (ChannelException e) {
				// URGENT Handle ChannelException
				e.printStackTrace();
			} catch (NoSuchAgentException e) {
				// URGENT Handle NoSuchAgentException
				e.printStackTrace();
			}
			dom.returnTemporaryChannel(rec);
		}

		/*
		 * @seeorg.jini.projects.zenith.messaging.system.MessagingListener#
		 * messageReceived(org.jini.projects.zenith.messaging.messages.Message)
		 */
		public void messageReceived(Message m) {
			// TODO Complete method stub for messageReceived
			// TODO Complete method stub for messageReceived
			Object o = m.getMessageContent();
			returnObject = o;
			svcLog.finest("Reply Message received");
			synchronized (this) {
				try {
					notify();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			System.out.println("Notified");
		}

		/**
		 * @return Returns the returnObject.
		 */
		public Object getReturnObject() {
			return this.returnObject;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jini.admin.Administrable#getAdmin()
	 */
	public Object getAdmin() throws RemoteException {
		// TODO Complete method stub for getAdmin
		try {
			if (remoteAdmin == null) {
				exp = null;
				exp = new BasicJeriExporter(HttpServerEndpoint.getInstance(0), new BasicILFactory());
				if (adminOb instanceof RemoteMethodControl)
					svcLog.fine("The service Implementation is secure");
				else
					svcLog.fine("The service Implementation is not secure");
				Remote proxy = (AgentServiceAdmin) exp.export(adminOb);
				if (proxy instanceof RemoteMethodControl)
					svcLog.fine("The wrappered proxy is secure");
				else
					svcLog.fine("The wrappered proxy is not secure");
				remoteAdmin = AgentAdminProxy.create((AgentServiceAdmin) proxy, UuidFactory.generate());
			}
			return remoteAdmin;
		} catch (ExportException e) {
			// TODO Handle ExportException
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param impl
	 */
	public void setAdminOb(AgentAdminImpl impl) {
		adminOb = impl;
	}

	/**
	 * @param exporter
	 */
	public void setExp(Exporter exporter) {
		exp = exporter;
	}

	/*
	 * @see
	 * org.jini.projects.neon.service.AgentBackendService#getAgent(java.lang
	 * .String, java.lang.String)
	 */
	public AgentProxyInfo getAgentProxyInfo(String name, String domain) throws RemoteException {
		this.svcLog.info("Looking for " + name + "." + domain);
		try {
			Agent a = this.kernel.getDomain(domain).getRegistry().getAgent(name);
			Class[] intf;
			// if (direct && a instanceof Remote) {
			System.out.println("Getting remote agent");
			if (a != null) {
				ArrayList arr = new ArrayList();
				getInterfaces(a.getClass(), arr);
				if (a instanceof NonTransactionalResource)
					arr.add(NonTransactionalResource.class);
				if (a instanceof TransactionalResource)
					arr.add(TransactionalResource.class);
				intf = (Class[]) arr.toArray(new Class[] {});
				return new AgentProxyInfo(a.getIdentity(), a.getNamespace() + "." + a.getName(), intf, exportedSelf, domain);
				// }

				// System.out.println("Returning class Information only");
				// ArrayList arr = new ArrayList();
				// getInterfaces(a.getClass(), arr);
				// Class[] intf = (Class[]) arr.toArray(new Class[] {});
				// return new AgentProxyInfo(a.getIdentity(), a.getNamespace() +
				// "." + a.getName(), intf, null);
			}
			if (a == null)
				return null;

		} catch (NoSuchAgentException e) {
			// URGENT Handle NoSuchAgentException
			e.printStackTrace();
		}
		return null;
	}

	public AgentProxyInfo getAgentProxyInfo(AgentIdentity id, String domain) throws RemoteException {
		this.svcLog.info("Looking for " + id + "." + domain);
		try {
			Agent a = this.kernel.getDomain(domain).getRegistry().getAgent(id);
			if (a != null) {
				System.out.println("Found a matching agent");
				if (!(a instanceof Remote)) {
					System.out.println("....but the agent cannot be exported");
				}
			}
			if (direct) {
				System.out.println("Getting remote agent");
				ExporterManager mgr = DefaultExporterManager.getManager();
				ProxyPair proxyPair = mgr.exportProxyAndPair((Remote) a, "Standard", UuidFactory.generate());
				Object smartProxy = proxyPair.getSmartProxy();
				ArrayList arr = new ArrayList();

				getInterfaces(a.getClass(), arr);
				if (a instanceof NonTransactionalResource)
					arr.add(NonTransactionalResource.class);
				if (a instanceof TransactionalResource)
					arr.add(RemoteTransactionalResource.class);

				Class[] intf = (Class[]) arr.toArray(new Class[] {});
				return new AgentProxyInfo(a.getIdentity(), a.getNamespace() + "." + a.getName(), intf, exportedSelf, domain);
			}
			if (a != null) {
				System.out.println("Returning class Information only");
				ArrayList arr = new ArrayList();
				getInterfaces(a.getClass(), arr);
				Class[] intf = (Class[]) arr.toArray(new Class[] {});
				return new AgentProxyInfo(a.getIdentity(), a.getNamespace() + "." + a.getName(), intf, null, domain);
			}
			if (a == null)
				return null;

		} catch (NoSuchAgentException e) {
			// URGENT Handle NoSuchAgentException
			e.printStackTrace();
		}
		return null;
	}

	public AgentProxyInfo getAgentProxyInfo(String name, Entry[] meta, String domain) throws RemoteException {
		this.svcLog.info("Looking (with Metadata) for " + name + "." + domain);
		try {
			Agent a = this.kernel.getDomain(domain).getRegistry().getAgent(name, meta);
			if (direct) {
				System.out.println("Getting remote agent");
				System.out.println("Exporting");
				ProxyPair proxyPair = DefaultExporterManager.getManager().exportProxyAndPair((Remote) a, "standard",
						UuidFactory.generate());
				Object smartProxy = proxyPair.getSmartProxy();
				ArrayList arr = new ArrayList();
				getInterfaces(a.getClass(), arr);
				if (a instanceof NonTransactionalResource)
					arr.add(NonTransactionalResource.class);
				if (a instanceof TransactionalResource)
					arr.add(TransactionalResource.class);
				Class[] intf = (Class[]) arr.toArray(new Class[] {});
				return new AgentProxyInfo(a.getIdentity(), a.getNamespace() + "." + a.getName(), intf, exportedSelf, domain);
			}
			if (a != null) {
				System.out.println("Returning class Information only");
				ArrayList arr = new ArrayList();
				getInterfaces(a.getClass(), arr);
				Class[] intf = (Class[]) arr.toArray(new Class[] {});
				return new AgentProxyInfo(a.getIdentity(), a.getNamespace() + "." + a.getName(), intf, null, domain);
			}
			if (a == null)
				return null;

		} catch (NoSuchAgentException e) {
			// URGENT Handle NoSuchAgentException
			e.printStackTrace();
		}
		return null;
	}

	private void getInterfaces(Class cl, ArrayList arr) {
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
					arr.add(cl);
				}
			}
		}

	}

	/*
	 * @see
	 * org.jini.projects.neon.service.AgentBackendService#getStatelessAgent(
	 * java.lang.String, java.lang.String)
	 */
	public Object getStatelessAgent(String name, String domain) throws RemoteException {

		svcLog.finest("Getting Stateless agent [" + name + "] from domain [" + domain + "]");

		return StatelessAgentFactory.create(kernel.getDomain(domain), name);
	}

	public Object getExportableAgent(String name, String domain) throws RemoteException {

		System.out.println("Getting Exported agent [" + name + "] from domain [" + domain + "]");
		return ExportableAgentFactory.create(kernel.getDomain(domain), name, svc);
	}

	public void deploySeeding(URL seedFromLocation) throws RemoteException {
		// TODO Complete method stub for deploySlice
		svcLog.finest("Attempting to deploy seeding file");
		StartupConfig startCfg = parseStartupFile(seedFromLocation);
		if (startCfg.getDomainConfigs() != null)
			for (int i = 0; i < startCfg.getDomainConfigs().size(); i++) {
				DomainConfig d = (DomainConfig) startCfg.getDomainConfigs().get(i);
				ManagedDomain agdom = new AgentDomainImpl(d.getName(), sid, d, config);

				kernel.addDomain(agdom, d.getReceiveCallsFromDomains(), d.getSendCallsToDomains());

			}

		// Slice slice = s.getSlice();
	}

	public void reloadSeeding() {
		try {
			extractUnencryptedDomainInformation();
			String ksLoc = (String) config.getEntry("org.jini.projects.neon", "keystoreLocation", String.class, null);
			if (ksLoc != null) {
				try {
					extractEncryptedDomainInformation();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnusableEntryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransactionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void extractUnencryptedDomainInformation() throws UnusableEntryException, TransactionException, InterruptedException,
			RemoteException {
		System.out.println("Loader Space is Null? " + (loaderSpace==null));
		DomainEntry domEnt = (DomainEntry) loaderSpace.takeIfExists(new DomainEntry("Global", sid.toString(), null, null, null),
				null, 5000);

		while (domEnt != null) {
			Uuid sid = UuidFactory.create(domEnt.referentServiceID);
			if (kernel.getDomain(domEnt.name) == null) {
				svcLog.info("Creating new domain from stored definitions");
				DomainConfig domConfig = new DomainConfig(domEnt.name);
				domConfig.setAllowedClasses(domEnt.allowedClasses);
				domConfig.setAllowedNamespaces(domEnt.allowedNamespaces);
				domConfig.setEncryptAgentStorage(domEnt.encryptAgentStorage);
				domConfig.setEncryptDomainInfo(domEnt.encryptDomainInfo);
				domConfig.setEncryptOtherData(domEnt.encryptOtherData);
				domConfig.setFailIfEncryptionAvailable(domEnt.failIfEncryptionAvailable);
				DelegateCatalog catalog = new DelegateCatalog(domEnt.delegateList);
				domConfig.setDelegateCatalog(catalog);
				domConfig.setReceiveCallsFromDomains(domEnt.receiveCallsFrom);
				domConfig.setSendCallsToDomains(domEnt.sendCallsTo);
				domConfig.setSecurityLevel(domEnt.securityLevel);
				ManagedDomain dom = new AgentDomainImpl(domEnt.name, new ServiceID(sid.getMostSignificantBits(), sid
						.getLeastSignificantBits()), domConfig, config);
				kernel.addDomain(dom, domEnt.receiveCallsFrom, domEnt.sendCallsTo);
			} else {
				svcLog.info("Should use pre-existing running domain");
			}
			domEnt = (DomainEntry) loaderSpace.takeIfExists(new DomainEntry(null, sid.toString(), null, null, null), null, 5000);
		}
	}

	private void extractEncryptedDomainInformation() throws Exception {
		DomainEntry search = new DomainEntry(null, sid.toString(), null, null, null);
		EncryptedDomainEntry encSearch = new EncryptedDomainEntry();
		EncryptionUtils.initialiseEncryptedVersion(search, false, encSearch);
		
		EncryptedDomainEntry domEnt = (EncryptedDomainEntry) loaderSpace.takeIfExists(encSearch, null, 5000);

		while (domEnt != null) {
			DomainEntry unEncEntry = (DomainEntry) EncryptionUtils.decryptObjectWithDefaultKey(domEnt.getEncryptedObject()
					.getEncryptedBytes());
			Uuid sid = UuidFactory.create(unEncEntry.referentServiceID);
			if (kernel.getDomain(unEncEntry.name) == null) {
				svcLog.info("Creating new domain from stored definitions");
				DomainConfig domConfig = new DomainConfig(unEncEntry.name);
				domConfig.setAllowedClasses(unEncEntry.allowedClasses);
				domConfig.setAllowedNamespaces(unEncEntry.allowedNamespaces);
				domConfig.setEncryptAgentStorage(unEncEntry.encryptAgentStorage);
				domConfig.setEncryptDomainInfo(unEncEntry.encryptDomainInfo);
				domConfig.setEncryptOtherData(unEncEntry.encryptOtherData);
				domConfig.setFailIfEncryptionAvailable(unEncEntry.failIfEncryptionAvailable);
				DelegateCatalog catalog = new DelegateCatalog(unEncEntry.delegateList);
				domConfig.setDelegateCatalog(catalog);
				domConfig.setReceiveCallsFromDomains(unEncEntry.receiveCallsFrom);
				domConfig.setSendCallsToDomains(unEncEntry.sendCallsTo);
				domConfig.setSecurityLevel(unEncEntry.securityLevel);
				System.out.println(domConfig);
				ManagedDomain dom = new AgentDomainImpl(unEncEntry.name, new ServiceID(sid.getMostSignificantBits(), sid
						.getLeastSignificantBits()), domConfig, config);
				kernel.addDomain(dom, unEncEntry.receiveCallsFrom, unEncEntry.sendCallsTo);
			} else {
				svcLog.info("Should use pre-existing running domain");
			}
			domEnt = (EncryptedDomainEntry) loaderSpace.takeIfExists(encSearch, null, 5000);
		}
	}

	public StartupConfig parseStartupFile(URL seedLocation) {
		// System.out.println("Parsing Startup file");
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser parser = spf.newSAXParser();
			StartupHandler loader = new StartupHandler();

			parser.parse(seedLocation.openStream(), loader, seedLocation.toExternalForm());
			DomainRegistry.getDomainRegistry().setDefaultDelegateCatalog(loader.getDelegateCatalog());
			StartupConfig sysCon = loader.getStartupConfig();
			return sysCon;
		} catch (FileNotFoundException e) {
			// URGENT Handle FileNotFoundException
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			// URGENT Handle FactoryConfigurationError
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// URGENT Handle ParserConfigurationException
			e.printStackTrace();
		} catch (SAXException e) {
			// URGENT Handle SAXException
			e.printStackTrace();
		} catch (IOException e) {
			// URGENT Handle IOException
			e.printStackTrace();
		}

		return null;
	}

	public void deployPOJOAgent(Object pojo, String constraintsLocation, String configurationLocation)
			throws MalformedURLException, RemoteException {
		// TODO Auto-generated method stub
		deployToDomain(pojo, constraintsLocation, configurationLocation);
	}

	public void createAgent(String classname, String constraintsurl, String configurl, String domain) throws RemoteException {
		// TODO Auto-generated method stub
		try {
			Class cl = Class.forName(classname);
			Agent a = (Agent) cl.newInstance();
			if (constraintsurl != null)
				a.setConstraints(new ReferenceableConstraints(new URL(constraintsurl)));
			a.setConfigurationLocation(new URL(configurl));
			ManagedDomain dom;
			if (domain != null)
				dom = DomainRegistry.getDomainRegistry().getDomain(domain);
			else
				dom = DomainRegistry.getDomainRegistry().getDomain("global");
			dom.deployAgent(a);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
			throw new RemoteException("Error in deployment", e);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RemoteException("Error in deployment", e);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RemoteException("Error in deployment", e);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RemoteException("Error in deployment", e);
		}
	}

	public void deployAgent(AgentConstraints constraints, CodebaseObject agent, String domain, RemoteEventListener callback)
			throws RemoteException {
		// TODO Auto-generated method stub
		Agent a = null;
		try {

			AgentConstraints ac = constraints;
			if (ac == null) {
				a = (Agent) agent.getMarshalled().get(false);
				deployToDomain(a, callback, domain);
			} else {
				if (canAccept(constraints)) {
					a = (Agent) agent.getMarshalled().get(false);
					deployToDomain(a, callback, domain);
				} else
					throw new RemoteException("Agent cannot be deployed - constraints not met");
			}
		} catch (IOException e) {
			// URGENT Handle IOException
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// URGENT Handle ClassNotFoundException
			e.printStackTrace();
		}

	}

	public void deployAgent(AgentConstraints constraints, CodebaseObject agent, String domain) throws RemoteException {
		// TODO Auto-generated method stub
		Agent a = null;
		System.out.println("Deploying an agent in to the " + domain + " domain");
		try {

			AgentConstraints ac = constraints;
			if (ac == null) {
				a = (Agent) agent.getMarshalled().get(false);
				deployToDomain(a, (RemoteEventListener) null, domain);
			} else {
				if (canAccept(constraints)) {
					a = (Agent) agent.getMarshalled().get(false);
					deployToDomain(a, (RemoteEventListener) null, domain);
				} else
					throw new RemoteException("Agent cannot be deployed - constraints not met");
			}
		} catch (IOException e) {
			// URGENT Handle IOException
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// URGENT Handle ClassNotFoundException
			e.printStackTrace();
		}

	}

	public void deployPOJOAgent(Object pojo, String constraintsLocation, String configurationLocation, String domainName)
			throws MalformedURLException, RemoteException {
		// TODO Auto-generated method stub
		ReferenceableConstraints constraints = null;
		if (constraintsLocation != null && !constraintsLocation.equals("")) {
			if (constraintsLocation.startsWith("class:")) {
				URL constLoc = pojo.getClass().getResource(configurationLocation.substring(6));
				constraints = new ReferenceableConstraints(constLoc);
			} else {
				URL constLoc = new URL(configurationLocation);
				constraints = new ReferenceableConstraints(constLoc);
			}
		}
		String dom = domainName;
		if (dom != null) {
			ManagedDomain domain = kernel.getDomain(dom);
			if (domain != null) {

				domain.deployObject(pojo, configurationLocation, constraintsLocation);
			}
		} else {

			kernel.getDomain("global").deployObject(pojo, configurationLocation, constraintsLocation);
		}
	}

	
	public Object invoke(AgentIdentity identity, MethodInvocation invocation) throws RemoteException {
		// TODO Auto-generated method stub
		try {
			List<DomainInvocation> domainList = invocation.getInvocationMetaData().getMetaOfType(DomainInvocation.class);
			List<TransactionID> txnList = invocation.getInvocationMetaData().getMetaOfType(TransactionID.class);
			ManagedDomain dom = kernel.getDomain(domainList.get(0).domain);
			Agent ag = dom.getRegistry().getAgent(identity);

			if (txnList.size() > 0) {
				if (!(ag instanceof NonTransactionalResource)) {

					try {
						TransactionAgent txnAg = (TransactionAgent) dom.getRegistry().getAgent("neon.Transaction");
						System.out.println("Propagated Transaction: " + txnList.get(0));
						Uuid localID = txnAg.attachTransaction((ServerTransaction) txnList.get(0).theTransaction, UuidFactory.create( txnList.get(0).transactionID));

						txnAg.enlistInTransaction((TransactionalResource) ag, localID);
					} catch (UnknownTransactionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CannotJoinException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CrashCountException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (TransactionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				if(ag instanceof TransactionAccessor)
					((TransactionAccessor) ag).setGlobalTransaction(txnList.get(0).theTransaction);
			}
			Method[] methods = ag.getClass().getDeclaredMethods();
			
			Method methodToCall = null;
			for (Method m : methods) {
				if (MethodViewer.getMethodShortString(m).equals(invocation.getMethodsignature())) {
					System.out.println("Found method to Call!");
					methodToCall = m;
					break;
				}
			}

			if (methodToCall != null)
				return methodToCall.invoke(ag, invocation.getParams());
			else {
				svcLog.severe("Not calling any matching method!!!! " + invocation.getMethodsignature());
			}
				return null;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchAgentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
}
