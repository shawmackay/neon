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
package org.jini.projects.neon.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.net.ServerSocket;
import java.net.URL;
import java.rmi.MarshalledObject;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.activation.ActivationException;
import java.rmi.activation.ActivationID;
import java.rmi.activation.ActivationSystem;
import java.rmi.server.ExportException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.jini.activation.ActivationExporter;
import net.jini.activation.ActivationGroup;
import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.export.Exporter;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.InvocationLayerFactory;
import net.jini.jeri.ServerEndpoint;
import net.jini.jeri.http.HttpServerEndpoint;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.lookup.JoinManager;
import net.jini.lookup.ServiceIDListener;
import net.jini.lookup.entry.Name;
import net.jini.lookup.entry.ServiceInfo;
import net.jini.lookup.entry.UIDescriptor;
import net.jini.lookup.ui.factory.JComponentFactory;
import net.jini.security.BasicProxyPreparer;
import net.jini.security.ProxyPreparer;
import net.jini.security.TrustVerifier;
import net.jini.space.JavaSpace;

import org.jini.glyph.chalice.DefaultExporterManager;
import org.jini.glyph.di.DIFactory;
import org.jini.projects.eros.ErosLogger;
import org.jini.projects.eros.ErosService;
import org.jini.projects.neon.dynproxy.CollaborationFactory;
import org.jini.projects.neon.dynproxy.ExportableAgentFactory;
import org.jini.projects.neon.dynproxy.StatelessAgentFactory;
import org.jini.projects.neon.host.AgentDomainImpl;
import org.jini.projects.neon.host.ManagedDomain;
import org.jini.projects.neon.kernel.Kernel;
import org.jini.projects.neon.service.admin.AgentAdminImpl;
import org.jini.projects.neon.service.constrainable.AgentServiceProxy;
import org.jini.projects.neon.service.constrainable.CombinedServiceProxy;
import org.jini.projects.neon.service.start.DomainConfig;
import org.jini.projects.neon.service.start.StartupConfig;
import org.jini.projects.neon.service.start.StartupHandler;
import org.jini.projects.neon.ui.AdminConsole;
import org.jini.projects.neon.ui.ConsoleUIFact;
import org.jini.projects.neon.ui.ProgressFrame;
import org.jini.projects.neon.util.NeonSharableClassLoader;
import org.jini.projects.neon.util.ServicePlugin;
import org.jini.projects.neon.util.ServiceTemplatePlugin;
import org.jini.projects.neon.util.encryption.EncryptionUtils;
import org.jini.projects.neon.web.IndependentPluggableResource;
import org.jini.projects.neon.web.NeonLink;
import org.jini.projects.zenith.messaging.channels.InvalidMessageChannel;
import org.jini.projects.zenith.messaging.endpoints.StoringEndpoint;
import org.jini.projects.zenith.messaging.system.MessagingManager;
import org.jini.projects.zenith.messaging.system.MessagingService;
import org.jini.projects.zenith.messaging.system.MessagingServiceImpl;
import org.jini.projects.zenith.messaging.system.MessagingServiceType;
import org.jini.projects.zenith.messaging.system.constrainable.MessagingServiceProxy;
import org.jini.projects.zenith.router.RouterService;
import org.xml.sax.SAXException;

import com.sun.jini.config.Config;
import com.sun.jini.start.LifeCycle;
import com.sun.jini.tool.ClassServer;

/**
 * Bootstrap main class for Neon. <br>
 * <ol>
 * <li>Reads in startup configuration.</li>
 * <li>Sets approriate Log levels for both Neon and apollo</li>
 * <li>Runs under a specific security context, if specified</li>
 * <li>Builds any remote objects required during Join Management</li>
 * <li>During Discovery, obtains a proxy to the RouterService</li>
 * <li>Initialises all domains with their startup agents</li>
 * </ol>
 */
public class ServiceStartup implements Serializable, ServiceIDListener, DiscoveryListener, ServiceStartupIntf {

	private String spaceName = "Blitz JavaSpace";
	private static ErosLogger eLogger;
	private static boolean registerShutDownHook = false;
	private static ErosService eros;

	/** Creates a new service ID. */
	protected ServiceID createServiceID() {
		try {
			File persistenceDirectory = new File((String) config.getEntry("org.jini.projects.neon", "persistenceDirectory", String.class));
			File logdir = new File("log");
			if (!logdir.exists()) {
				logdir.mkdir();
			}
			File f = new File("log/serviceid");
			if (f.exists()) {
				ServiceID sid = null;
				try {
					ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));
					sid = (ServiceID) ois.readObject();
					;
					ois.close();
				} catch (FileNotFoundException e) {
					// TODO Handle FileNotFoundException
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Handle IOException
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Handle ClassNotFoundException
					e.printStackTrace();
				}
				startupLog.fine("ServiceID recovered: " + sid);
				return sid;
			} else {
				Uuid uuid = UuidFactory.generate();
				ServiceID sid = new ServiceID(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
				try {
					ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
					oos.writeObject(sid);
					oos.flush();
					oos.close();
				} catch (FileNotFoundException e) {
					// TODO Handle FileNotFoundException
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Handle IOException
					e.printStackTrace();
				}
				System.out.println("ServiceID generated and persisted: " + sid);
				return sid;
			}
		} catch (Exception ex) {
			System.err.println("Exception while storing ServiceID");
			ex.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) throws Exception {

		if (System.getProperty("org.jini.projects.neon.ui") != null) {
			ProgressFrame app = new ProgressFrame("Neon Console");

			app.setSize(600, 600);
			app.setVisible(true);
		}
		ServiceStartup.registerShutDownHook = true;
		System.setProperty("java.rmi.server.RMIClassLoaderSpi", "org.jini.projects.neon.deploy.NeonPreferredClassProvider");

		new ServiceStartup(args, null);
	}

	private ActivationID activationID;
	private String apolloDir;
	private transient Configuration config;
	boolean erosFound = false;
	private transient Exporter exporter;
	private ServiceID id;
	private boolean initialised = false;
	private transient StoringEndpoint invalidEndpoint;
	private transient JoinManager jm;
	private transient LookupDiscoveryManager ldm = null;
	private transient MessagingServiceImpl msgSvc;
	String name = "Neon";
	private transient AgentBackendService proxy;
	private transient RouterService router;
	transient URL startCfg;
	private transient Logger startupLog = Logger.getLogger("org.jini.projects.neon");
	private ArrayList stringlist = new ArrayList();
	private transient AgentServiceImpl svc;
	private transient IndependentPluggableResource jetty;
	private transient IndependentPluggableResource ravenous;
	private ServiceID msgSvcID;
	private MessagingService exportedMsgSvc;

	public ServiceStartup() {
		String protocols = System.getProperty("java.protocol.handler.pkgs");
		String packProtocol = "org.jini.users.cshawmackay.url";
		if (protocols != null) {
			if (protocols.indexOf("org.jini.users.cshawmackay.url") == -1) {
				protocols = protocols + "|" + packProtocol;
				System.setProperty("java.protocol.handler.pkgs", protocols);
			}
		} else {
			System.setProperty("java.protocol.handler.pkgs", packProtocol);
		}
	}

	public ServiceStartup(ActivationID aid, MarshalledObject data) throws IOException, ActivationException, ConfigurationException, ClassNotFoundException {
		this();
		Object obj = data.get();
		init((String[]) obj, aid);
	}

	public ServiceStartup(String[] args, LifeCycle lifeCycle) throws RemoteException {
		this();
		registerShutDownHook = true;
		// System.out.println("ServiceStartup: lifecycle");
		if (registerShutDownHook) {
			System.out.println("Registering a shutdown hook");
			Runtime.getRuntime().addShutdownHook(new Thread(new Terminate()));
		}
		init(args, null);
	}

	private void activateHost(Configuration config, ActivationID id) throws ConfigurationException, ExportException, RemoteException {
		configureZenith();

		configureStdLogging();
		DIFactory.registerPlugin(ServiceTemplatePlugin.class, new ServicePlugin(LookupDiscovery.ALL_GROUPS));
		boolean useDirect = useDirectMode(config);
		ExportableAgentFactory.setDirectProxyMode(useDirect);
		CollaborationFactory.setDirectProxyMode(useDirect);

		this.activationID = id;
		String[] lookups = (String[]) config.getEntry("org.jini.projects.neon", "initialLookupGroups", String[].class, LookupDiscovery.ALL_GROUPS);
		LookupLocator[] locs = (LookupLocator[]) Config.getNonNullEntry(config, "org.jini.projects.neon", "initialLookupLocators", LookupLocator[].class, new LookupLocator[0]);
		try {
			ldm = new LookupDiscoveryManager(lookups, locs, this);
		} catch (IOException e) {
			// TODO Handle IOException
			e.printStackTrace();
		}

	}

	private boolean useDirectMode(Configuration config) {
		this.config = config;
		String mode;
		try {
			mode = (String) config.getEntry("org.jini.projects.neon", "mode", String.class, "direct");
			return mode.equals("direct");
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	private void configureCodebase() throws ConfigurationException {
		String codebase = (String) config.getEntry("org.jini.projects.neon", "codebase", String.class);
		if (codebase == null) {
			startupLog.severe("Codebase is not set in the configuration file...exiting");
			System.exit(1);
		} else {

			System.setProperty("java.rmi.server.codebase", codebase);

		}
	}

	private void configureStdLogging() throws ConfigurationException {
		String logLevel = (String) config.getEntry("org.jini.projects.neon", "consoleLogLevel", String.class, "INFO");
		Logger stdLogger = startupLog;
		stdLogger.setLevel(Level.parse(logLevel));
		Handler ch = new ConsoleHandler();
		ch.setFormatter(new LogFormatter());
		ch.setLevel(Level.parse(logLevel));
		stdLogger.addHandler(ch);
		stdLogger.log(Level.FINER, "Fine message");
		stdLogger.setUseParentHandlers(false);
		Logger apolloLog = Logger.getLogger("org.jini.projects.zenith");
		apolloLog.setLevel(Level.parse(logLevel));
		apolloLog.setUseParentHandlers(false);
		Handler apolloch = new ConsoleHandler();
		apolloch.setFormatter(new LogFormatter());
		apolloch.setLevel(Level.parse(logLevel));
		apolloLog.addHandler(apolloch);
		Handler[] hs = startupLog.getHandlers();
	}

	private void configureJetty(Configuration config) throws ConfigurationException {
		Boolean useTomcat = (Boolean) config.getEntry("org.jini.projects.neon", "useTomcat", Boolean.class, Boolean.FALSE);
		if (useTomcat.booleanValue()) {
			try {
				// See if the tomcat libraries are installed
				// We need to use reflection to stop
				// ClassNotFoundExceptions being thrown
				Class.forName("org.mortbay.jetty.Server");
				Class embedClass = Class.forName("org.jini.projects.neon.web.EmbeddedJetty");
				// System.setProperty("javax.xml.parsers.DocumentBuilderFactory","org.apache.crimson.jaxp.DocumentBuilderFactoryImpl");
				Constructor constr = embedClass.getConstructor(new Class[] { Configuration.class });
				jetty = (IndependentPluggableResource) constr.newInstance(new Object[] { config });
				jetty.start();
			} catch (Exception ex) {
				startupLog.warning("Jetty integration requested in configuration. but Jetty libraries not found, or unable to start......continuing");
				ex.printStackTrace();
			}
		} else {
			startupLog.info("Tomcat integration not enabled");
		}
	}

	private void configureRavenous(Configuration config) throws ConfigurationException {
		Boolean useRavenous = (Boolean) config.getEntry("org.jini.projects.neon", "useRavenous", Boolean.class, Boolean.FALSE);
		if (useRavenous.booleanValue()) {
			try {
				// See if the tomcat libraries are installed
				// We need to use reflection to stop
				// ClassNotFoundExceptions being thrown
				Class.forName("com.solido.ravenous.Ravenous");
				Class embedClass = Class.forName("org.jini.projects.neon.web.EmbeddedRavenous");
				// System.setProperty("javax.xml.parsers.DocumentBuilderFactory","org.apache.crimson.jaxp.DocumentBuilderFactoryImpl");
				Constructor constr = embedClass.getConstructor(new Class[] { SecurityManager.class, String.class });
				String ravConfig = (String) config.getEntry("org.jini.projects.neon", "ravenousConfig", String.class, null);
				ravenous = (IndependentPluggableResource) constr.newInstance(new Object[] { System.getSecurityManager(), ravConfig });
			} catch (Exception ex) {
				startupLog.warning("Ravenous integration requested in configuration. but classes libraries not found......continuing");
				ex.printStackTrace();
			}
		} else {
			startupLog.info("Tomcat integration not enabled");
		}
	}

	private void configureZenith() throws ConfigurationException {
		System.out.println("Loading from ExporterMgrConfig into Default");
		DefaultExporterManager.loadManager("default", (String) config.getEntry("org.jini.projects.neon", "exporterMgrConfig", String.class));
		DefaultExporterManager.loadManager("messaging", (String) config.getEntry("org.jini.projects.neon", "messagingexportMgrConfig", String.class));
		DefaultExporterManager.loadManagersFromDir((String) config.getEntry("org.jini.projects.neon","exporterMgrDirectory",String.class));
		apolloDir = (String) config.getEntry("apollo", "storageDir", String.class, "messages");
		System.setProperty("org.jini.projects.zenith.messaging.system.store.dir", apolloDir);
		//DefaultExporterManager.displayManagers();
	}

	public void discarded(DiscoveryEvent e) {
		startupLog.log(Level.FINE, "Discarded a lookup Service");
	}

	public void discovered(DiscoveryEvent e) {
		try {
			startupLog.log(Level.FINE, "Discovered by a lookup Service");
			ServiceRegistrar[] regs = e.getRegistrars();
			router = null;

			ServiceTemplate erosTemplate = new ServiceTemplate(null, new Class[] { ErosService.class }, null);

			for (int i = 0; i < regs.length; i++) {
				if (!erosFound) {
					ServiceRegistrar reg = regs[i];
					Object o = reg.lookup(erosTemplate);
					if (o != null) {
						erosFound = true;
						initialiseEros((ErosService) o);
					}
				}
			}

			if (!initialised) {
				startupLog.log(Level.INFO, "Initialising Neon");
				id = createServiceID();
				configureJetty(config);
				configureRavenous(config);
				initServices(config, activationID);

				initialiseServiceJoin();
				initialiseHost();
				startupLog.log(Level.INFO, "Neon Initialised");
				initialised = true;
			}
		} catch (Exception e1) {
			// TODO Handle RemoteException
			e1.printStackTrace();
		}
	}

	private void bootInternalClassServer(int portStart, Configuration config) throws ConfigurationException {
		String compname = "org.jini.projects.lamplighter.auto";
		int portstart = ((Integer) config.getEntry(compname, "classServerPort", Integer.class, new Integer(8080))).intValue();
		String dir = (String) config.getEntry(compname, "classServerDir", String.class, "lib-dl");
		Boolean trees = (Boolean) config.getEntry(compname, "classServerTrees", Boolean.class, Boolean.TRUE);
		Boolean verbose = (Boolean) config.getEntry(compname, "classServerVerbose", Boolean.class, Boolean.FALSE);
		ServerSocket s = null;
		try {
			s = new ServerSocket(portStart);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Port " + portStart + " is in use.");
		}

		while (s != null || !s.isBound()) {
			try {
				s = new ServerSocket(++portStart);
			} catch (IOException ioex) {
				System.out.println("Port " + portStart + " is in use.");
			}
		}
		try {
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Using Port " + portStart + " for class server");
		startClassServer(portStart, dir, trees, verbose);
	}

	private void startClassServer(final int port, final String dir, final boolean trees, final boolean verbose) {
		Thread classServerThread = new Thread(new Runnable() {

			public void run() {
				try {
					final ClassServer server = new ClassServer(port, dir, trees, verbose);

					Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

						public void run() {
							server.terminate();
						}

						;
					}));
				}

				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		});
		classServerThread.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jini.projects.neon.service.ServiceStartupIntf#getProxy()
	 */

	public Object getProxy() {
		return proxy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jini.projects.neon.service.ServiceStartupIntf#getProxyVerifier()
	 */
	public TrustVerifier getProxyVerifier() {
		return null;
	}

	/**
	 * Returns the service ID for server.
	 */
	protected ServiceID getServiceID() {
		return createServiceID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jini.projects.neon.service.ServiceStartupIntf#init(java.lang.String[],
	 *      java.rmi.activation.ActivationID)
	 */
	public void init(final String[] args, final ActivationID aid) throws RemoteException {

		Runnable r = new Runnable() {

			public void run() {
				try {
					if (System.getSecurityManager() == null) {
						System.setSecurityManager(new RMISecurityManager());
					}

					// System.out.println("Reading config");
					config = ConfigurationProvider.getInstance(args);
					if (config == null) {
						startupLog.severe("Service Configuration is null");
					}
					configureCodebase();
					String startupfile = (String) config.getEntry("org.jini.projects.neon", "startupfile", String.class, null);
					if (startupfile != null) {
						startCfg = new File(startupfile).toURL();
					} else {
						startCfg = null;
					}
					// String serverUserName = (String)
					// config.getEntry("loki",
					// "serverUserName", String.class, "server");
					String ksLoc = (String) config.getEntry("org.jini.projects.neon", "keystoreLocation", String.class, null);
					if (ksLoc != null) {
						startupLog.info("Encryption enabled for this Host");
						String encUser = (String) config.getEntry("org.jini.projects.neon", "securityUser", String.class, null);
						String encPass = (String) config.getEntry("org.jini.projects.neon", "securityPassword", String.class, null);
						String keystoreType = (String) config.getEntry("org.jini.projects.neon", "keystoreType", String.class, null);
						String keystorePass = (String) config.getEntry("org.jini.projects.neon", "keystorePass", String.class, null);
						EncryptionUtils.initialise(ksLoc, keystorePass.toCharArray(), encUser, encPass.toCharArray(), keystoreType);
					}
					System.gc();
					LoginContext lc = (LoginContext) config.getEntry("loki", "loginContext", LoginContext.class, null);
					if (lc == null) {
						activateHost(config, aid);
					} else {

						lc.login();
						Subject.doAsPrivileged(lc.getSubject(), new PrivilegedExceptionAction() {

							public Object run() throws Exception {
								activateHost(config, aid);
								return null;
							}
						}, null);

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		Thread t = new Thread(r);
		t.start();
		// System.out.println("Exiting init()");
	}

	/**
	 * @param service
	 */
	private void initialiseEros(ErosService service) {
		// TODO Complete method stub for initialiseEros
		boolean returnWithout = false;
		if (returnWithout) {
			System.out.println(" Skipping eros init");
			return;
		}
		try {
			ProxyPreparer preparer = (ProxyPreparer) config.getEntry("org.jini.projects.neon", "proxyPreparer", ProxyPreparer.class, new BasicProxyPreparer());
			eros = (ErosService) preparer.prepareProxy(service);
			eLogger = eros.getLogger();
			eLogger.initialise("Neon");

			Handler logHandler = (Handler) eLogger.getLoggingHandler();

			int Kb = 1024;
			FileHandler fhandler = new FileHandler("log/eros", 500 * Kb, 40);
			fhandler.setFormatter(new LogFormatter());

			String consoleLogLevel = (String) config.getEntry("org.jini.projects.neon", "consoleLogLevel", String.class);
			String fileLogLevel = (String) config.getEntry("org.jini.projects.neon", "fileLogLevel", String.class);

			// startupLog.setLevel(Level.parse(fileLogLevel));
			// Logger exporterLogger =
			// java.util.logging.Logger.getLogger("exportermanager");
			// exporterLogger.setUseParentHandlers(false);
			// exporterLogger.setLevel(Level.parse(consoleLogLevel));

			startupLog.setUseParentHandlers(false);
			Handler[] handlers = startupLog.getHandlers();
			for (int i = 0; i < handlers.length; i++) {
				startupLog.removeHandler(handlers[i]);
			}
			startupLog.setLevel(Level.parse(fileLogLevel));
			fhandler.setLevel(Level.parse(fileLogLevel));
			logHandler.setLevel(Level.parse(consoleLogLevel));
			// exporterLogger.addHandler(logHandler);
			startupLog.addHandler(fhandler);
			startupLog.addHandler(logHandler);
			startupLog.info("Eros Logging enabled");
		} catch (RemoteException e) {
			System.err.println("Exception occured whilst interacting with Eros");
			e.printStackTrace();
		} catch (SecurityException e) {
			System.err.println("Security Exception occured whilst interacting with Eros");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Exception occured whilst interacting with Eros");
			e.printStackTrace();
		}
	}

	private void initialiseHost() {
		try {
			startupLog.info("Initialising host");
			Kernel k = new Kernel(config);
			svc.setServiceKernel(k);
			if (startCfg != null) {
				startupLog.info("Initialising  Host from specified Seeding File before reloading from Space...");
				svc.deploySeeding(startCfg);
			}
			startupLog.info("Now Reloading agents from Space");
			svc.reloadSeeding();

			// svc.setAgentHost(globalDomain);
		} catch (Exception e) {
			startupLog.severe("Agent Host could not be initialised: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void initServices(Configuration config, ActivationID aid) throws ConfigurationException, ExportException, RemoteException {
		try {
			activationID = aid;

			ServerEndpoint endpoint = TcpServerEndpoint.getInstance(0);
			InvocationLayerFactory ilFact = new BasicILFactory();
			Exporter defaultExporter = new BasicJeriExporter(endpoint, ilFact, false, true);
			if (activationID != null) {
				ActivationSystem activationSystem = ActivationGroup.getSystem();
				ProxyPreparer aidpreparer = (ProxyPreparer) Config.getNonNullEntry(config, "org.jini.projects.neon", "activationIdPreparer", ProxyPreparer.class, new BasicProxyPreparer());
				ProxyPreparer actSysPreparer = (ProxyPreparer) Config.getNonNullEntry(config, "org.jini.projects.neon", "activationSystemPreparer", ProxyPreparer.class, new BasicProxyPreparer());
				activationID = (ActivationID) aidpreparer.prepareProxy(activationID);
				activationSystem = (ActivationSystem) actSysPreparer.prepareProxy(activationSystem);

				defaultExporter = new ActivationExporter(activationID, defaultExporter);
			}
			exporter = (Exporter) config.getEntry("org.jini.projects.neon", "serverExporter", Exporter.class, defaultExporter, activationID);
			MessagingManager.createManager("default", config);
			msgSvc = new MessagingServiceImpl();

			Exporter msgSvcExporter = new BasicJeriExporter(HttpServerEndpoint.getInstance(0), new BasicILFactory());
			Remote r = msgSvcExporter.export(msgSvc);
			// Uuid id = UuidFactory.generate();
			Uuid msgSvcUuid = UuidFactory.generate();

			ServiceRegistrar reg = ldm.getRegistrars()[0];
			spaceName = (String) config.getEntry("org.jini.projects.neon", "storeDomainDataSpace", String.class, null);

			JavaSpace space = (JavaSpace) reg.lookup(new ServiceTemplate(null, new Class[] { JavaSpace.class }, new Entry[] { new Name(spaceName) }));
			// msgSvcID = new ServiceID(msgSvcUuid.getMostSignificantBits(),
			// msgSvcUuid.getLeastSignificantBits());
			exportedMsgSvc = MessagingServiceProxy.create((MessagingService) r, UuidFactory.generate());
			MessagingManager.getManager().setMessagingServiceID(id);
			InvalidMessageChannel invalid = new InvalidMessageChannel("invalidChannel");
			MessagingManager.getManager().addChannel(invalid);
			invalidEndpoint = new StoringEndpoint(invalid, new File(new File(apolloDir), "invalid"));

			AgentServiceImpl svcImpl = new AgentServiceImpl(msgSvc, id, space, this.config);
			NeonLink.setBackend(svcImpl);
			svcImpl.setMessagingServiceRef(exportedMsgSvc);
			svc = svcImpl;

			proxy = (AgentBackendService) exporter.export(svc);
			svc.setRemoteVersion(proxy);
		} catch (Exception e) {
			startupLog.severe("Error occured during Join: " + e.getMessage());
			throw new RemoteException("Failed to initialise Neon", e);
		}
	}

	/**
	 * @param attribs
	 * @param svcImpl
	 * @throws RemoteException
	 * @throws ConfigurationException
	 */
	private void initialiseServiceJoin() throws RemoteException, ConfigurationException {

		Entry[] baseattrs;
		baseattrs = setupBaseAttributes();
		Entry[] attribs = (Entry[]) Config.getNonNullEntry(config, "org.jini.projects.neon", "initialLookupAttributes", Entry[].class, baseattrs);
		Name name = new Name("Zenith Messaging Service");

		CombinedService secproxy = CombinedServiceProxy.create(proxy, exportedMsgSvc, UuidFactory.generate());

		try {
			jm = new JoinManager(secproxy, attribs, this.id, ldm, null);
		} catch (ExportException e1) {
			// TODO Handle ExportException
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Handle IOException
			e1.printStackTrace();
		}

		AgentAdminImpl adminOb = new AgentAdminImpl(jm, ldm, activationID);
		svc.setExp(exporter);
		svc.setAdminOb(adminOb);

		startupLog.info("Neon Services preparing to join");
	}

	public void serviceIDNotify(ServiceID serviceID) {
		startupLog.info("Service registered as: " + serviceID.toString());
		initialiseHost();
	}

	private Entry[] setupBaseAttributes() {
		Entry[] baseattrs;
		UIDescriptor consoleDesc = null;
		try {
			consoleDesc = new UIDescriptor(AdminConsole.ROLE, ConsoleUIFact.TOOLKIT, null, new java.rmi.MarshalledObject(new ConsoleUIFact()));
			consoleDesc.attributes = new java.util.HashSet();
			consoleDesc.attributes.add(new net.jini.lookup.ui.attribute.UIFactoryTypes(java.util.Collections.singleton(JComponentFactory.TYPE_NAME)));
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		if (consoleDesc == null) {
			baseattrs = new Entry[] { new Name(name), new ServiceInfo("Neon Agent Platform", "CA", "CA", "0.1a", "prototype", "1"), new NeonServiceType() };
		} else {
			baseattrs = new Entry[] { new Name(name), new ServiceInfo("Neon Agent Platform", "CA", "CA", "0.1a", "prototype", "1"), new NeonServiceType(), consoleDesc };
		}
		return baseattrs;
	}

	class Terminate implements Runnable {

		public void run() {
			System.out.println("Firing ServiceStartup#Terminate shutdown hook");
			if (jetty != null) {
				System.out.println("Shutting Down Tomcat");
				try {
					jetty.stop();
					System.out.println("Tomcat Shutdown");
				} catch (Exception e) {
					// TODO Handle Exception
					e.printStackTrace();
				}
			}
			if (ravenous != null) {
				System.out.println("Shutting down Ravenous");
				try {
					ravenous.stop();
					System.out.println("Ravenous Shutdown");
				} catch (Exception e) {
					// TODO Handle Exception
					e.printStackTrace();
				}
			}
			System.out.println("Deregistering joinmanagers");
			jm.terminate();

			ldm.terminate();
			System.out.println("Services deregistered from LUS");
			System.out.println("Terminating JVM");
			System.out.println("\tNeon Service shutdown hook completed");
		}
	}
}
