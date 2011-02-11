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
package org.jini.projects.neon.host;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;
import net.jini.core.entry.Entry;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.transaction.TransactionException;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;
import net.jini.space.JavaSpace;

import org.jini.glyph.Injection;
import org.jini.glyph.di.DIFactory;
import org.jini.glyph.di.Injector;
import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentConstraints;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.AgentState;
import org.jini.projects.neon.agents.Crypto;
import org.jini.projects.neon.agents.Meta;
import org.jini.projects.neon.agents.ReferenceableConstraints;
import org.jini.projects.neon.agents.RemoteAgentState;
import org.jini.projects.neon.agents.listeners.CollaborationListener;
import org.jini.projects.neon.agents.sensors.SensorFilter;
import org.jini.projects.neon.agents.util.ConstraintsUtil;
import org.jini.projects.neon.agents.util.DomainEntry;
import org.jini.projects.neon.agents.util.EncryptedDomainEntry;
import org.jini.projects.neon.annotations.ServiceBinding;
import org.jini.projects.neon.collaboration.Response;
import org.jini.projects.neon.collaboration.SimpleResponse;
import org.jini.projects.neon.collaboration.SubscriberResponses;
import org.jini.projects.neon.dynproxy.AgentProxyFactory;
import org.jini.projects.neon.dynproxy.AgentProxyInfo;
import org.jini.projects.neon.dynproxy.CollaborationDirectProxy;
import org.jini.projects.neon.dynproxy.CollaborationFactory;
import org.jini.projects.neon.dynproxy.CollaborationMethodInvokerRemoteProxy;
import org.jini.projects.neon.dynproxy.CollaborationProxy;
import org.jini.projects.neon.dynproxy.CollaborationRemoteProxy;
import org.jini.projects.neon.dynproxy.FacadeProxy;
import org.jini.projects.neon.dynproxy.PojoAgentProxyFactory;
import org.jini.projects.neon.dynproxy.PojoFacadeProxy;
import org.jini.projects.neon.export.CXFExporter;
import org.jini.projects.neon.export.JiniServiceExporter;
import org.jini.projects.neon.host.transactions.NonTransactionalResource;
import org.jini.projects.neon.host.transactions.TransactionAgent;
import org.jini.projects.neon.host.transactions.TransactionBlackBox;
import org.jini.projects.neon.host.transactions.TransactionalResource;
import org.jini.projects.neon.kernel.Kernel;
import org.jini.projects.neon.recovery.CheckpointAgent;
import org.jini.projects.neon.service.AgentService;
import org.jini.projects.neon.service.ServiceAgent;
import org.jini.projects.neon.service.start.AgentCatalog;
import org.jini.projects.neon.service.start.AgentDef;
import org.jini.projects.neon.service.start.AgentSet;
import org.jini.projects.neon.service.start.DelegateCatalog;
import org.jini.projects.neon.service.start.DomainConfig;
import org.jini.projects.neon.util.encryption.EncryptionUtils;
import org.jini.projects.neon.vertigo.management.AgentAction;
import org.jini.projects.neon.vertigo.management.SliceManager;
import org.jini.projects.neon.vertigo.management.SliceManagerImpl;
import org.jini.projects.neon.vertigo.management.VetoResult;
import org.jini.projects.neon.vertigo.slice.Slice;
import org.jini.projects.zenith.bus.Bus;
import org.jini.projects.zenith.bus.BusManager;
import org.jini.projects.zenith.messaging.channels.MessageChannel;
import org.jini.projects.zenith.messaging.channels.PointToPointChannel;
import org.jini.projects.zenith.messaging.channels.PublishSubscribeChannel;
import org.jini.projects.zenith.messaging.channels.ReceiverChannel;
import org.jini.projects.zenith.messaging.endpoints.MessageDispatcher;
import org.jini.projects.zenith.messaging.messages.Message;
import org.jini.projects.zenith.messaging.messages.MessageHeader;
import org.jini.projects.zenith.messaging.system.ChannelException;
import org.jini.projects.zenith.messaging.system.MessagingManager;

import com.sun.jini.constants.TimeConstants;

/**
 * An implementation of a partition in a host.<br>
 * Partitions are representative of a network-wide domain, and from the agent's
 * point of view the partitions and the domain it is part of are inseperable<br>
 * Going by the outline functions of an agent host presented in <i>An
 * introduction to agents </i>: <br>
 * <ul>
 * <li>An agent host must allow multiple agents to co-exist and execute
 * simultaneously</li>
 * <li>Must allow agents to communicate with each other and the agent host
 * </li>
 * <li>Must be able to negotiate the exchange of agents</li>
 * <li>Must be able to freeze an agent and transfer it to another host</li>
 * <li>Must be able to thaw an agent transferred from another host and allow it
 * to resume execution</li>
 * <li>Must prevent agents from directly interfering with each other</li>
 * </ul>
 * <p/>To this end
 */
public class AgentDomainImpl implements PrivilegedAsyncAgentEnvironment, ManagedDomain {

	private SecurityOptions securityopts;
	
	private class RegistryAccessAction implements PrivilegedExceptionAction {

		String name;
		AgentRegistry reg;

		public RegistryAccessAction(AgentRegistry a, String name) {
			hostLog.finest("RegistryAccessAction initiated");
			this.reg = a;
			this.name = name;
		}

		public Object run() throws NoSuchAgentException {
			hostLog.finest("RegistryAction called");
			return reg.getCallbacks(name);
		}
	}

	private class RegistryAgentIDCallback implements PrivilegedExceptionAction {

		AgentIdentity ID;
		AgentRegistry reg;

		public RegistryAgentIDCallback(AgentRegistry a, AgentIdentity ID) {
			this.reg = a;
			this.ID = ID;
		}

		public Object run() throws NoSuchAgentException {
			return reg.getCallback(ID);
		}
	}

	private class UnsubscribeAgentAction implements PrivilegedExceptionAction {

		Agent ag;

		public UnsubscribeAgentAction(Agent ag) {
			this.ag = ag;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.security.PrivilegedExceptionAction#run()
		 */
		public Object run() throws Exception {
			// TODO Complete method stub for run
			hostLog.fine("Unsubscribing agent " + ag.getIdentity() + ")");
			registry.deregisterAgent(ag);
			ag.unadvertise();
			System.out.println("UNSUBSCRIBE NEEDS FIXING");
			// bus.unsubscribe(ag.getIdentity().getID());
			return null;
		}
	}

	ServiceID agentServiceID;

	// The message bus used to send messages between agents and
	// host services
	Bus bus;
	BusManager busManager;
	List currentworkers = new ArrayList();
	HashMap dispatchers;
	String domainName;
	Logger hostLog = Logger.getLogger("org.jini.projects.neon.host.DomainImpl");
	DomainConfig initData;
	Map mesgLocks;
	MessagingManager mgr;
	AgentRegistry registry;
	String spaceName = null;

	// Each Domain has it's own transaction box
	TransactionBlackBox tbb;

	// And Slice Manager
	SliceManager sliceMgr;
	boolean direct = false;
	private Configuration config;

	public AgentDomainImpl(String domainName, ServiceID id, DomainConfig data, Configuration config) {
		setupMode(config);

		initDomain(domainName, id, data);
	}

	private void setupMode(Configuration config) {
		this.config = config;
		String mode;
		try {
			mode = (String) config.getEntry("org.jini.projects.neon", "mode", String.class, "direct");
			spaceName = (String) config.getEntry("org.jini.projects.neon", "storeDomainDataSpace", String.class);
			direct = mode.equals("direct");
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/*
	 * @see org.jini.projects.neon.host.ManagedDomain#addMessageLock(net.jini.id.Uuid,
	 *      net.jini.id.Uuid)
	 */
	public void addMessageLock(Uuid transmitter, Uuid receiver, String name) {
		// TODO Complete method stub for addMessageLock
		if (mesgLocks.containsKey(transmitter)) {
			Map m = (Map) mesgLocks.get(transmitter);
			m.put(name, receiver);
		} else {
			Map m = new HashMap();
			mesgLocks.put(transmitter, m);
			m.put(name, receiver);
		}
	}

	public synchronized void advertise(Agent cag) {
		// Create a subscription on a publish subscribe channel

		// Skip this if we're running directmode

		if (!direct) {
			System.out.printf("Creating channels for %s.%s\n", cag.getNamespace(), cag.getName());
			if (mgr.getChannel(domainName + "|" + cag.getNamespace() + "." + cag.getName()) == null) {
				try {
					mgr.createChannel(domainName + "|" + cag.getNamespace() + "." + cag.getName());
					mgr.createChannel("control" + domainName + "|" + cag.getNamespace() + "." + cag.getName());
					ReceiverChannel input = mgr.getReceivingChannel(domainName + "|" + cag.getNamespace() + "." + cag.getName());
					ReceiverChannel control = mgr.getReceivingChannel("control" + domainName + "|" + cag.getNamespace() + "." + cag.getName());

					MessageDispatcher dispatch = new MessageDispatcher(input, control);
					dispatchers.put(domainName + "|" + cag.getNamespace() + "." + cag.getName(), dispatch);
				} catch (ChannelException e1) {
					// URGENT Handle ChannelException
					e1.printStackTrace();
				}
			}
			try {
				// Register on the PS channel fro this agent name
				CollaborationListener messageHook = new CollaborationListener(cag);
				// mgr.registerOnChannel(cag.getNamespace() + "." +
				// cag.getName(),
				// messageHook);
				// Create a new invocation channel for accepting request
				// messages
				mgr.addChannel(new PointToPointChannel(cag.getIdentity().getID().toString()));
				mgr.registerOnChannel(cag.getIdentity().getID().toString(), messageHook);
				MessageDispatcher dispatcher = (MessageDispatcher) dispatchers.get(domainName + "|" + cag.getNamespace() + "." + cag.getName());
				if (dispatcher != null) {
					dispatcher.addDispatcher(new AgentDispatcher(cag));
				} else {
					this.hostLog.severe("******** Dispatcher [" + domainName + "|" + cag.getNamespace() + "." + cag.getName() + " is null ********");
				}
				// Create the channel that will act as the reply address
				// for
				// invocations
				mgr.addChannel(new PointToPointChannel("reply" + cag.getIdentity().getID().toString()));
				// Create a control channel for setting things like
				// transactions
			} catch (ChannelException e) {
				// URGENT Handle ChannelException
				e.printStackTrace();
			}
		}
	}

	public Object attachAgent(String name, Agent controller) throws NoSuchAgentException {
		// TODO Complete method stub for attachAgent
		return obtainAgent(name, controller, AgentState.ATTACHED, true);
	}

	public Object attachAgent(String name, Entry[] matchTo, Agent controller) throws NoSuchAgentException {
		// TODO Complete method stub for attachAgent
		return obtainAgentWithMeta(name, matchTo, controller, AgentState.ATTACHED, true);
	}

	public Object attachAgent(String name, Entry[] matchTo, int timeout, Agent controller) throws NoSuchAgentException {
		// TODO Complete method stub for attachAgent
		int timecounter = 0;
		Object o = obtainAgentWithMeta(name, matchTo, controller, AgentState.ATTACHED, false);
		while (o == null && timecounter <= timeout) {
			synchronized (this) {
				try {
					wait(50);
				} catch (InterruptedException e) {
					// TODO Handle InterruptedException
					e.printStackTrace();
				}
			}
			if (timeout != 0) {
				timecounter += 50;
			}
			o = obtainAgentWithMeta(name, matchTo, controller, AgentState.ATTACHED, false);
			if (o != null && timeout == 0) {
				break;
			}
		}
		if (o != null) {
			return o;
		} else {
			throw new NoSuchAgentException("A matching agent [" + name + "] could not be found on the network and timeout expired");
		}
	}

	public Object attachAgent(String name, int timeout, Agent controller) throws NoSuchAgentException {
		// TODO Complete method stub for attachAgent
		int timecounter = 0;
		Object o = obtainAgent(name, controller, AgentState.ATTACHED, false);
		while (o == null && timecounter <= timeout) {
			synchronized (this) {
				try {
					wait(50);
				} catch (InterruptedException e) {
					// TODO Handle InterruptedException
					e.printStackTrace();
				}
			}
			if (timeout != 0) {
				timecounter += 50;
			}
			o = obtainAgent(name, controller, AgentState.ATTACHED, false);

		}
		if (o != null) {
			return o;
		} else {
			throw new NoSuchAgentException("A matching agent [" + name + "] could not be found on the network and timeout expired");
		}
	}

	/**
	 * Before an agent is accepted into the system, the agent provides a set of
	 * constraints/capabilities that it requires. An agent host must provide a
	 * querying mechanism to ensure that it can execute the agent properly.
	 * 
	 * @param constraints
	 *            The set of constraints and capabilities that the agent runs
	 *            under
	 * @return
	 */
	public boolean canAccept(AgentConstraints constraints) {
		if (constraints == null) {
			return true;
		} else {
			return ConstraintsUtil.evaluateConstraints(constraints);
		}
	}

	/**
	 * Before an agent is accepted into the system, the agent provides a set of
	 * constraints/capabilities that it requires. An agent host must provide a
	 * querying mechanism to ensure that it can execute the agent properly.
	 * 
	 * @param constraints
	 *            The set of constraints and capabilities that the agent runs
	 *            under
	 * @return
	 */
	public boolean canAccept(String constraintsLocation) {
		if (constraintsLocation == null || constraintsLocation == "") {
			return true;
		}
		System.out.println("Checking constraints @ " + constraintsLocation);
		try {
			ReferenceableConstraints constraints = new ReferenceableConstraints(new URL(constraintsLocation));
			return ConstraintsUtil.evaluateConstraints(constraints);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * @see org.jini.projects.neon.host.ManagedDomain#clearMessageLock(net.jini.id.Uuid)
	 */
	public void clearMessageLock(Uuid transmitter) {
		// TODO Complete method stub for clearMessageLock
		clearMessageLock(transmitter, 0);
	}

	public void clearMessageLock(Uuid transmitter, int level) {
		// TODO Complete method stub for clearMessageLock
		if (mesgLocks.containsKey(transmitter)) {
			Map m = (Map) mesgLocks.get(transmitter);
			mesgLocks.remove(m);
			for (Iterator iter = m.entrySet().iterator(); iter.hasNext();) {
				Map.Entry entry = (Map.Entry) iter.next();
				// for (int i = 0; i < level; i++)
				// System.out.print(" ");
				// System.out.println("Clearing Lock: " +
				// entry.getKey() + "["
				// + entry.getValue() + "]");
				clearMessageLock((Uuid) entry.getValue(), level + 1);
			}
		}
	}

	/**
	 * Wraps the agent code within a harness, in this case a Thread Harness,
	 * adds the agent to the list of current workers, then asks the harness to
	 * start the agent.
	 * 
	 * @param agt
	 */
	public void deployAgent(Agent agt) {
		deployAgent(agt, null);
	}

	public void deployAgent(Agent agt, RemoteEventListener c) {
		Slice s = sliceMgr.getSliceFor(agt.getIdentity());
		if (s != null) {
			VetoResult veto = s.vetoes(AgentAction.DEPLOY, agt.getIdentity(), sliceMgr, null, getAgentServiceID());
			if (veto == VetoResult.NO) {
				hostLog.severe("SliceManager has vetoed against deployment of this agent");
			}
			if (veto == VetoResult.WARNING) {
				hostLog.severe("SliceManager has warned against deployment of this agent");
			}
		}
		// agt.setAgentContext(this);
		Agent a = AgentProxyFactory.create(agt, initData.getDelegateCatalog());
		
		if(agt instanceof Crypto){
			((Crypto)agt).useCrypto(this.initData.getSecurityLevel()>2);
		}
		doInjection(a);

		ServiceBinding binding = (ServiceBinding) agt.getClass().getAnnotation(ServiceBinding.class);

		if (binding != null) {
			doBinding(binding, agt);
		}
		
		hostLog.finest("Deploying a " + a.getName() + "[" + a.getIdentity() + "]: State" + a.getAgentState() + " agent ( " + a.getSecondaryState() + ")");
		a.setAgentContext(new AgentContextAdapter(a, this, this));
		String agentfqn = agt.getNamespace() + "." + agt.getName();
		advertise(a);
		if (a.getSecondaryState() != null) {
			a.setAgentState(a.getSecondaryState());
		}
		if (a.getAgentState() == null) {
			a.setAgentState(AgentState.AVAILABLE);
		}
		registry.registerAgent(a, c);

		AgentHarness harness = getDeploymentHarness(agt, a);
		harness.go();
		hostLog.finest("Deployed a " + a.getName() + " agent ( " + a.getAgentState() + ") ");
		/*
		 * try { if (!(a instanceof LocalAgent)) { CheckpointAgent cp =
		 * (CheckpointAgent) StatelessAgentFactory.create(this,
		 * "neon.CheckPoint"); if(cp==null) throw new NoSuchAgentException("No
		 * Agent"); cp.clearAgentCheckpoints(a, AgentState.TRANSFER); } } catch
		 * (NoSuchAgentException e) { hostLog.info("Unable to clear any
		 * checkpoint information"); }
		 */
	}

	private void doBinding(ServiceBinding binding, Agent a) {
		// TODO Auto-generated method stub
		hostLog.info("Doing Service Binding");
		try {
			String localHost = InetAddress.getLocalHost().getHostName();
			if (binding.type().equalsIgnoreCase("WS")) {

				String address = "http://" + localHost + ":" + (Integer) this.config.getEntry("org.jini.projects.neon", "webServicePort", int.class);
				CXFExporter exporter = new CXFExporter();
				exporter.exportAgent(a, address);
			}
			if (binding.type().equalsIgnoreCase("Jini")) {

				JiniServiceExporter exporter = new JiniServiceExporter();
				exporter.exportAgent(a);
			}
		} catch (ConfigurationException ex) {
			Logger.getLogger(AgentDomainImpl.class.getName()).log(Level.SEVERE, null, ex);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deployObject(Object o, String configurationLocation, String constraintsLocation) throws MalformedURLException {
		Agent a = PojoAgentProxyFactory.create(o);
		URL configLoc = null;
		if (configurationLocation != null && !configurationLocation.equals("")) {
			if (configurationLocation.startsWith("class:")) {
				configLoc = o.getClass().getResource(configurationLocation.substring(6));
			} else {
				configLoc = new URL(configurationLocation);
			}
		}
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
		a.setConfigurationLocation(configLoc);
		a.setConstraints(constraints);
		deployAgent(a);
	}

	public boolean detachAgent(Object reference) {
		// TODO Complete method stub for detachAgent

		// Will reference always implement agent => Might need to change
		// this to
		// Collaborative or Object?
		if (reference == null) {
			hostLog.severe("Why reference is null?");
		}
		InvocationHandler handler = Proxy.getInvocationHandler(reference);
		AgentIdentity id;
		if (direct) {
			if (handler instanceof CollaborationDirectProxy) {
				CollaborationDirectProxy cdp = (CollaborationDirectProxy) handler;

				id = cdp.getIdentity();
			} else {
				hostLog.info("Casting to CollaborationRemoteProxy");
				CollaborationMethodInvokerRemoteProxy crp = (CollaborationMethodInvokerRemoteProxy) handler;
				hostLog.info("Casting to RemoteAgentState");
				RemoteAgentState ras = (RemoteAgentState) reference;
				try {
					ras.setRemoteAgentState(AgentState.AVAILABLE);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			}
		} else {
			CollaborationProxy cbp = (CollaborationProxy) handler;
			id = cbp.getIdentity();
		}
		try {
			hostLog.info("Detaching Agent " + id.toString());
			if (reference instanceof RemoteAgentState) {
				hostLog.info("Remote Agent State recognised");
				RemoteAgentState ras = (RemoteAgentState) reference;
				try {
					System.out.println("Setting (Remote) to Available");
					ras.setRemoteAgentState(AgentState.AVAILABLE);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Unlocked Agent");
			} else {
				Agent a = registry.getAgent(id);
				hostLog.info("Agent referenced is null? " + (a == null));
				if (!(a instanceof NonTransactionalResource)) {
					if (((TransactionalResource) a).inTransaction()) {
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return false;
					}
				}
				System.out.println("Setting to Available");
				a.setAgentState(AgentState.AVAILABLE);

				hostLog.finest(a.getNamespace() + "." + a.getName() + ";ID :" + a.getIdentity() + ":  has been detached");
			}
			return true;
		} catch (NoSuchAgentException e) {
			// TODO Handle NoSuchAgentException
			e.printStackTrace();
		}
		return false;
	}

	private void doInjection(Agent worker) {
		InvocationHandler handler = Proxy.getInvocationHandler(worker);
		FacadeProxy f = (FacadeProxy) handler;
		Agent receiver = f.getReceiver();
		Object injectee = null;
		if (receiver instanceof Proxy) {
			InvocationHandler h = Proxy.getInvocationHandler(receiver);
			if (h instanceof PojoFacadeProxy) {
				PojoFacadeProxy facade = (PojoFacadeProxy) h;
				injectee = facade.getPOJO();
			}
		} else {
			injectee = receiver;
		}
		//System.out.println("Null checks: Injectee : " + (injectee==null) + "; hostLog: " + (hostLog==null));
		hostLog.finest("Checking for Injection on " + injectee.getClass().getName());
		Injection annotation = injectee.getClass().getAnnotation(Injection.class);
		if (annotation != null) {
			try {
				hostLog.finer("Injecting " + injectee.getClass().getName());

				Injector injector = DIFactory.getInjector(worker.getConfigurationLocation());
				injector.inject(injectee);
			} catch (ConfigurationException e) {
				// TODO Auto-generated catch block
				hostLog.severe("Injection failed");
				e.printStackTrace();

			}
		}
	}

	public Object getAgent(String name, Agent controller) throws NoSuchAgentException {
		return obtainAgent(name, controller, null, true);

	}

	public Object getAgent(String name, Entry[] matchTo, Agent controller) throws NoSuchAgentException {
		return obtainAgentWithMeta(name, matchTo, controller, null, true);
	}

	public Object getAgent(String name, Entry[] matchTo, int timeout, Agent controller) throws NoSuchAgentException {
		// TODO Complete method stub for getAgen
		int timecounter = 0;
		Object o = obtainAgentWithMeta(name, matchTo, controller, null, false);
		while (o == null || timecounter <= timeout) {
			synchronized (this) {
				try {
					wait(50);
				} catch (InterruptedException e) {
					// TODO Handle InterruptedException
					e.printStackTrace();
				}
			}
			if (timeout != 0) {
				timecounter += 50;
			}
			o = obtainAgentWithMeta(name, matchTo, controller, null, false);
			if (o != null && timeout == 0) {
				break;
			}
		}
		if (o != null) {
			return o;
		} else {
			throw new NoSuchAgentException("A matching agent [" + name + "] could not be found on the network and timeout expired");
		}
	}

	public Object getAgent(String name, int timeout, Agent controller) throws NoSuchAgentException {
		int timecounter = 0;
		Object o = obtainAgent(name, controller, null, false);
		while (o == null || timecounter <= timeout) {
			// System.out.println("Checking again for " + name);
			synchronized (this) {
				try {
					wait(1000);
				} catch (InterruptedException e) {
					// TODO Handle InterruptedException
					e.printStackTrace();
				}
			}
			if (timeout != 0) {
				timecounter += 50;
			}
			o = obtainAgent(name, controller, null, false);
			if (o != null && timeout == 0) {
				break;
			}
		}
		if (o != null) {
			return o;
		} else {
			throw new NoSuchAgentException("A matching agent [" + name + "] could not be found on the network and timeout expired");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jini.projects.neon.host.PrivilegedAgentContext#getAgentHost()
	 */
	public ManagedDomain getAgentHost() {
		// TODO Complete method stub for getAgentHost
		return this;
	}

	public ServiceID getAgentServiceID() {
		return agentServiceID;
	}

	public Object getAsyncAgent(String name, Agent controller) {
		// TODO Complete method stub for getAgent
		Agent ag = null;
		String namespace = controller.getNamespace();
		String agentToFind;
		hostLog.finer("Looking for " + name + " agent to attach to");
		if (name.indexOf('.') != -1) {
			agentToFind = name;
		} else {
			agentToFind = controller.getNamespace() + "." + name;
		}
		try {
			ag = registry.getAgent(agentToFind);
		} catch (NoSuchAgentException e) {
			// URGENT Handle NoSuchAgentException
			// e.printStackTrace();
		}
		if (ag != null) {
			return CollaborationFactory.createAsync(this, ag, controller);
		}
		hostLog.finer("Checking AttachedDomains of " + domainName + " for " + agentToFind);
		List l = DomainRegistry.getDomainRegistry().getAllowedDomainsOut(this.domainName);
		if (l != null) {
			for (Iterator iter = l.iterator(); iter.hasNext() && ag == null;) {
				String linkedDomain = (String) iter.next();
				hostLog.finer("Looking in " + linkedDomain + " domain.");
				PrivilegedAsyncAgentEnvironment agdom = (PrivilegedAsyncAgentEnvironment) DomainRegistry.getDomainRegistry().getDomain(linkedDomain);
				Object o = agdom.getAsyncAgent(name, controller);
				if (o != null) {
					return o;
				}
			}
		}
		if (ag != null) {
			return CollaborationFactory.createAsync(this, ag, controller);
		}
		hostLog.info("Checking other services");
		ServiceItem[] agSvcs = Kernel.getAllServices(AgentService.class);
		for (int i = 0; i < agSvcs.length; i++) {
			if (agSvcs[i].serviceID != agentServiceID) {
				hostLog.finer("Checking " + agSvcs[i].serviceID);
				AgentService svc = (AgentService) agSvcs[i].service;
				AgentProxyInfo info = null;
				try {
					info = svc.getAgentProxyInfo(agentToFind, this.domainName);
				} catch (RemoteException e1) {
					// URGENT Handle RemoteException
					e1.printStackTrace();
				}
				if (info != null) {
					return CollaborationFactory.createAsync(this, info, controller);
				}
			}
		}
		return null;
	}

	public Object getAsyncAgent(String name, AsyncHandler handler, Agent controller) {
		// TODO Complete method stub for getAgent
		Agent ag = null;
		String namespace = controller.getNamespace();
		String agentToFind;
		hostLog.finer("Looking for " + name + " agent to attach to");
		if (name.indexOf('.') != -1) {
			agentToFind = name;
		} else {
			agentToFind = controller.getNamespace() + "." + name;
		}
		try {
			ag = registry.getAgent(agentToFind);
		} catch (NoSuchAgentException e) {
			// URGENT Handle NoSuchAgentException
			// e.printStackTrace();
		}
		if (ag != null) {
			return CollaborationFactory.createQuasiAsync(this, ag, controller, handler);
		}
		hostLog.finer("Checking AttachedDomains of " + domainName + " for " + agentToFind);
		List l = DomainRegistry.getDomainRegistry().getAllowedDomainsOut(this.domainName);
		if (l != null) {
			for (Iterator iter = l.iterator(); iter.hasNext() && ag == null;) {
				String linkedDomain = (String) iter.next();
				hostLog.finer("Looking in " + linkedDomain + " domain.");
				PrivilegedAsyncAgentEnvironment agdom = (PrivilegedAsyncAgentEnvironment) DomainRegistry.getDomainRegistry().getDomain(linkedDomain);
				Object o = agdom.getAsyncAgent(name, handler, controller);
				if (o != null) {
					return o;
				}
			}
		}
		if (ag != null) {
			return CollaborationFactory.createQuasiAsync(this, ag, controller, handler);
		}
		hostLog.info("Checking other services");
		ServiceItem[] agSvcs = Kernel.getAllServices(AgentService.class);
		for (int i = 0; i < agSvcs.length; i++) {
			if (agSvcs[i].serviceID != agentServiceID) {
				hostLog.finer("Checking agentService: " + agSvcs[i].serviceID);
				AgentService svc = (AgentService) agSvcs[i].service;
				AgentProxyInfo info = null;
				try {
					info = svc.getAgentProxyInfo(agentToFind, this.domainName);
				} catch (RemoteException e1) {
					// URGENT Handle RemoteException
					e1.printStackTrace();
				}
				if (info != null) {
					return CollaborationFactory.createQuasiAsync(this, info, controller, handler);
				}
			}
		}
		return null;
	}

	public Object getAsyncAgent(String name, Entry[] matchTo, Agent controller) {
		// TODO Complete method stub for getAgent
		Agent ag = null;
		String namespace = controller.getNamespace();
		String agentToFind;
		hostLog.finer("Looking for " + name + " agent to attach to");
		if (name.indexOf('.') != -1) {
			agentToFind = name;
		} else {
			agentToFind = controller.getNamespace() + "." + name;
		}
		try {
			ag = registry.getAgent(agentToFind, matchTo);
		} catch (NoSuchAgentException e) {
			// URGENT Handle NoSuchAgentException
			// e.printStackTrace();
		}
		if (ag != null) {
			return CollaborationFactory.createAsync(this, ag, controller);
		}
		hostLog.finer("Checking AttachedDomains of " + domainName + " for " + agentToFind);
		List l = DomainRegistry.getDomainRegistry().getAllowedDomainsOut(this.domainName);
		if (l != null) {
			for (Iterator iter = l.iterator(); iter.hasNext() && ag == null;) {
				String linkedDomain = (String) iter.next();
				hostLog.finer("Looking in " + linkedDomain + " domain.");
				PrivilegedAsyncAgentEnvironment agdom = (PrivilegedAsyncAgentEnvironment) DomainRegistry.getDomainRegistry().getDomain(linkedDomain);
				Object o = agdom.getAsyncAgent(name, matchTo, controller);
				if (o != null) {
					return o;
				}
			}
		}
		if (ag != null) {
			return CollaborationFactory.createAsync(this, ag, controller);
		}
		hostLog.info("Checking other services");
		ServiceItem[] agSvcs = Kernel.getAllServices(AgentService.class);
		for (int i = 0; i < agSvcs.length; i++) {
			if (agSvcs[i].serviceID != agentServiceID) {
				hostLog.finer("Checking " + agSvcs[i].serviceID);
				AgentService svc = (AgentService) agSvcs[i].service;
				AgentProxyInfo info = null;
				try {
					info = svc.getAgentProxyInfo(agentToFind, matchTo, this.domainName);
				} catch (RemoteException e1) {
					// URGENT Handle RemoteException
					e1.printStackTrace();
				}
				if (info != null) {
					return CollaborationFactory.createAsync(this, info, controller);
				}
			}
		}
		return null;
	}

	public Object getAsyncAgent(String name, Entry[] matchTo, AsyncHandler handler, Agent controller) {
		// TODO Complete method stub for getAgent
		Agent ag = null;
		String namespace = controller.getNamespace();
		String agentToFind;
		hostLog.finer("Looking for " + name + " agent to attach to");
		if (name.indexOf('.') != -1) {
			agentToFind = name;
		} else {
			agentToFind = controller.getNamespace() + "." + name;
		}
		try {
			ag = registry.getAgent(agentToFind, matchTo);
		} catch (NoSuchAgentException e) {
			// URGENT Handle NoSuchAgentException
			// e.printStackTrace();
		}
		if (ag != null) {
			return CollaborationFactory.createQuasiAsync(this, ag, controller, handler);
		}
		hostLog.finer("Checking AttachedDomains of " + domainName + " for " + agentToFind);
		List l = DomainRegistry.getDomainRegistry().getAllowedDomainsOut(this.domainName);
		if (l != null) {
			for (Iterator iter = l.iterator(); iter.hasNext() && ag == null;) {
				String linkedDomain = (String) iter.next();
				hostLog.finer("Looking in " + linkedDomain + " domain.");
				PrivilegedAsyncAgentEnvironment agdom = (PrivilegedAsyncAgentEnvironment) DomainRegistry.getDomainRegistry().getDomain(linkedDomain);
				Object o = agdom.getAsyncAgent(name, matchTo, handler, controller);
				if (o != null) {
					return o;
				}
			}
		}
		if (ag != null) {
			return CollaborationFactory.createQuasiAsync(this, ag, controller, handler);
		}
		hostLog.info("Checking other services");
		ServiceItem[] agSvcs = Kernel.getAllServices(AgentService.class);
		for (int i = 0; i < agSvcs.length; i++) {
			if (agSvcs[i].serviceID != agentServiceID) {
				hostLog.finer("Checking " + agSvcs[i].serviceID);
				AgentService svc = (AgentService) agSvcs[i].service;
				AgentProxyInfo info = null;
				try {
					info = svc.getAgentProxyInfo(agentToFind, matchTo, this.domainName);
				} catch (RemoteException e1) {
					// URGENT Handle RemoteException
					e1.printStackTrace();
				}
				if (info != null) {
					return CollaborationFactory.createQuasiAsync(this, info, controller, handler);
				}
			}
		}
		return null;
	}

	public Bus getBus() {
		return bus;
	}

	/**
	 * @return
	 */
	public BusManager getBusManager() {
		return busManager;
	}

	public RemoteEventListener getCallback(AgentIdentity ID) throws NoSuchAgentException {
		try {
			return (RemoteEventListener) AccessController.doPrivileged(new RegistryAgentIDCallback(registry, ID), AccessController.getContext());
		} catch (PrivilegedActionException e) {
			if (e.getException() instanceof NoSuchAgentException) {
				throw (NoSuchAgentException) e.getException();
			} else {
				System.out.println("Err: " + e.getMessage());
				e.printStackTrace();
			}
		}
		return null;
	}

	public RemoteEventListener[] getCallbacks(String agentName) throws NoSuchAgentException {
		try {
			return (RemoteEventListener[]) AccessController.doPrivileged(new RegistryAccessAction(registry, agentName), AccessController.getContext());
		} catch (PrivilegedActionException e) {
			if (e.getException() instanceof NoSuchAgentException) {
				throw (NoSuchAgentException) e.getException();
			} else {
				System.out.println("Err: " + e.getMessage());
				e.printStackTrace();
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jini.projects.neon.host.AgentContext#getContextLogger(java.lang.String)
	 */
	public Logger getContextLogger(String agentName) {
		return hostLog;
	}

	public InetAddress getCurrentHost() {
		try {
			return InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			System.out.println("Err: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	private AgentHarness getDeploymentHarness(Agent innerAgent, Agent proxiedAgent) {
		boolean runThread = false;
		runThread = (proxiedAgent instanceof Runnable);

		AgentHarness harness = null;
		if (runThread) {
			harness = new ThreadHarness(proxiedAgent);
		} else {
			harness = new NonThreadedHarness(proxiedAgent);
		}
		return harness;
	}

	public String getDomainName() {
		return domainName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jini.projects.neon.host.PrivilegedAgentContext#getHostServiceID()
	 */
	public Uuid getHostServiceID() {
		// TODO Complete met
		return UuidFactory.create(agentServiceID.getMostSignificantBits(), agentServiceID.getLeastSignificantBits());
	}

	/**
	 * @return
	 */
	public AgentRegistry getRegistry() {
		return registry;
	}

	/*
	 * @see org.jini.projects.neon.host.AgentDomain#getTemporaryChannel()
	 */
	public ReceiverChannel getTemporaryChannel() {
		return mgr.getTemporaryChannel();
	}

	/*
	 * @see org.jini.projects.neon.host.ManagedDomain#getTxnBlackBox()
	 */
	public TransactionBlackBox getTxnBlackBox() {
		// TODO Complete method stub for getTxnBlackBox
		return tbb;
	}

	private Response handleResponse(Object resp) {
		// System.out.println("Handling a response");
		Response returnResponse = null;
		if (resp instanceof SubscriberResponses) {
			SubscriberResponses subsresp = (SubscriberResponses) resp;
			if (subsresp.size() == 1) {
				SimpleResponse sresp = new SimpleResponse();
				sresp.setResponseObject(subsresp.get(0));
				returnResponse = sresp;
			}
		} else {
			SimpleResponse sresp = new SimpleResponse();
			sresp.setResponseObject(resp);
			returnResponse = sresp;
		}
		return returnResponse;
	}

	private void initDomain(String domainName, ServiceID id, DomainConfig data) {
		initData = data;
		securityopts = new SecurityOptions(initData);
		hostLog.info("Initialising Domain partition- " + domainName);
		this.domainName = domainName;
		// registry = new AgentRegistry(domainName);
		mgr = MessagingManager.getManager();
		this.agentServiceID = id;
		mesgLocks = new HashMap();
		dispatchers = new HashMap();
		try {
			sliceMgr = new SliceManagerImpl(ConfigurationProvider.getInstance(new String[] { "conf/slicemgr.config" }));
			final int savePointWaitingPeriod = ((Integer) config.getEntry("org.jini.projects.neon", "savepointPeriod", Integer.class, new Integer(300)));
			if (savePointWaitingPeriod != -1) {
				Runnable r = new Runnable() {

					public void run() {
						// TODO Auto-generated method stub
						for (;;) {
							if (registry != null) {
								storeDomainAgentState();
							}
							try {
								Thread.sleep(savePointWaitingPeriod * TimeConstants.SECONDS);
							} catch (InterruptedException iex) {
								iex.printStackTrace();
							}
						}
					}
				};
				Thread savepointThread = new Thread(r);
				savepointThread.start();
			}
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jini.projects.neon.host.ManagedDomain#initialise()
	 */
	public void initialise() {
		// URGENT Complete method stub for initialise
		// String linkBuses = data.getAllowCallsTo();
		// if (!domainName.toLowerCase().equals("global"))
		// bus.addLink(linkBuses,
		// DomainRegistry.getDomainRegistry().getDomain("global").getBus());
		// if (linkBuses != null) {
		//
		// String[] links = linkBuses.split(" ");
		// for (int i = 0; i < links.length; i++)
		// bus.addLink(linkBuses,
		// DomainRegistry.getDomainRegistry().getDomain(links[i]).getBus());
		// }
		try {
			AgentCatalog agents = initData.getAgentCatalog();
			if (agents != null) {
				AgentSet preSet = agents.getPreSet();
				int wait = agents.getWaitTime();
				AgentSet postSet = agents.getPostSet();
				for (Iterator iter = preSet.getAgents().iterator(); iter.hasNext();) {
					AgentDef def = (AgentDef) iter.next();
					hostLog.fine("Deploying " + def.getNumber() + " " + def.getClassname() + " agents into " + this.domainName + " domain.");
					if (def.getConstraints() == null || canAccept(def.getConstraints())) {
						for (int i = 0; i < def.getNumber(); i++) {
							try {
								Object ob = Class.forName(def.getClassname()).newInstance();
								Agent ag = null;
								if (!(ob instanceof Agent)) {
									hostLog.info("Deploying POJO.....creating agent wrapper");
									ag = PojoAgentProxyFactory.create(ob);
								} else {
									ag = (Agent) ob;
								}
								hostLog.fine("\tDeployed: " + ag.getName() + ": " + ag.getIdentity().getID());
								try {
									if (def.getConfigurationURL() != null) {
										ag.setConfigurationLocation(new java.net.URL(def.getConfigurationURL()));
									}
									if (def.getConstraints() != null) {
										ag.setConstraints(new ReferenceableConstraints(new URL(def.getConstraints())));
									}

								} catch (Exception ex) {
									System.err.println("Caught Exception: " + ex.getClass().getName() + "; Msg: " + ex.getMessage());
									ex.printStackTrace();
								}
								try {
									deployAgent(ag);

									if (def.getWaitAfterInit() > 0) {
										Thread.sleep(def.getWaitAfterInit() * 1000);
									}
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							} catch (InstantiationException ex) {
								System.err.println("Cannot find class: " + def.getClassname());
								ex.printStackTrace();
							}
						}
					}
				}
				try {
					hostLog.fine("Waiting for " + wait + " seconds for agents to be initialised");
					Thread.sleep(wait * 1000);
				} catch (InterruptedException e) {
				}
				for (Iterator iter = postSet.getAgents().iterator(); iter.hasNext();) {
					AgentDef def = (AgentDef) iter.next();
					hostLog.fine("Deploying " + def.getNumber() + " " + def.getClassname() + " agents into " + this.domainName + " domain.");
					for (int i = 0; i < def.getNumber(); i++) {
						Agent ag = (Agent) Class.forName(def.getClassname()).newInstance();
						try {
							ag.setConfigurationLocation(def.getConfigurationURL() != null ? new java.net.URL(def.getConfigurationURL()) : null);
						} catch (Exception ex) {
							System.err.println("Caught Exception: " + ex.getClass().getName() + "; Msg: " + ex.getMessage());
							ex.printStackTrace();
						}
						deployAgent(ag);
					}
				}
			}
		} catch (InstantiationException e) {
			// URGENT Handle InstantiationException
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// URGENT Handle IllegalAccessException
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// URGENT Handle ClassNotFoundException
			e.printStackTrace();
		}
		hostLog.info(domainName + " domain partition initialised");
	}

	public Uuid lockReferences(String name, Uuid transmitter) {
		if (mesgLocks.containsKey(transmitter)) {
			Map m = (Map) mesgLocks.get(transmitter);
			Object obj = m.get(name);
			if (obj != null) {
				return (Uuid) obj;
			}
		}
		return null;
	}

	private Object obtainAgentFromIdentity(AgentIdentity id, Agent controller, AgentState setState, boolean throwExIfNoneFound) throws NoSuchAgentException {
		// TODO Complete method stub for getAgent
		Agent ag = null;
		try {
			if (controller != null && setState != null) {
				// System.out.println("GETTING PRE-LOCKED
				// AGENT");
				// ag = registry.getAgent(agentToFind, setState);
			} else {
				ag = registry.getAgent(id);
			}
		} catch (NoSuchAgentException e) {
			// URGENT Handle NoSuchAgentException
			// e.printStackTrace();
		}
		if (ag != null) {
			return CollaborationFactory.create(this, ag, controller);
		}
		// hostLog.finer("Checking AttachedDomains of " + domainName + "
		// for " +
		// agentToFind);
		List l = DomainRegistry.getDomainRegistry().getAllowedDomainsOut(this.domainName);
		if (l != null) {
			for (Iterator iter = l.iterator(); iter.hasNext() && ag == null;) {
				String linkedDomain = (String) iter.next();
				hostLog.finer("Looking in " + linkedDomain + " domain.");
				PrivilegedAsyncAgentEnvironment agdom = (PrivilegedAsyncAgentEnvironment) DomainRegistry.getDomainRegistry().getDomain(linkedDomain);
				return agdom.getAgent(id, controller);
			}
		}
		if (ag != null) {
			return CollaborationFactory.create(this, ag, controller);
		}
		hostLog.info("Checking other services");
		ServiceItem[] agSvcs = Kernel.getAllServices(AgentService.class);
		for (int i = 0; i < agSvcs.length; i++) {
			if (!agSvcs[i].serviceID.equals(agentServiceID)) {
				hostLog.finer("Checking agentService " + agSvcs[i].serviceID + " for " + id + "; my sid:" + agentServiceID);
				AgentService svc = (AgentService) agSvcs[i].service;
				AgentProxyInfo info = null;
				try {
					info = svc.getAgentProxyInfo(id, this.domainName);
				} catch (RemoteException e1) {

					e1.printStackTrace();
				}
				if (info != null) {
					return CollaborationFactory.create(this, info, controller);
				}
			}
		}
		if (throwExIfNoneFound) {
			throw new NoSuchAgentException("A matching agent [" + id + "] could not be found on the network");
		} else {
			return null;
		}
	}

	/**
	 * @param name
	 * @param controller
	 * @throws NoSuchAgentException
	 */
	private Object obtainAgent(String name, Agent controller, AgentState setState, boolean throwExIfNoneFound) throws NoSuchAgentException {
		// TODO Complete method stub for getAgent
		Agent ag = null;
		String namespace = "";
		String agentToFind = name;
		if (controller != null) {
			namespace = controller.getNamespace();
			if (name.indexOf('.') == -1) {
				agentToFind = controller.getNamespace() + "." + name;
			}
		}
		// hostLog.finer("Looking for " + name + " agent to attach to");

		if (controller != null) {
			hostLog.finer("Looking for " + agentToFind + " agent to attach to from:  " + controller.getNamespace() + "." + controller.getName());
		} else {
			hostLog.finer("Looking for " + agentToFind + " agent to attach to from external non-agent source");
		}
		try {
			if (controller != null && setState != null) {
				// System.out.println("GETTING PRE-LOCKED
				// AGENT");
				ag = registry.getAgent(agentToFind, setState);
			} else {
				ag = registry.getAgent(agentToFind);
			}
		} catch (NoSuchAgentException e) {
			// URGENT Handle NoSuchAgentException
			// e.printStackTrace();
		}
		if (ag != null) {
			return CollaborationFactory.create(this, ag, controller);
		}
		// hostLog.finer("Checking AttachedDomains of " + domainName + "
		// for " +
		// agentToFind);
		List l = DomainRegistry.getDomainRegistry().getAllowedDomainsOut(this.domainName);
		if (l != null) {
			for (Iterator iter = l.iterator(); iter.hasNext() && ag == null;) {
				String linkedDomain = (String) iter.next();
				hostLog.finer("Looking in " + linkedDomain + " domain.");
				PrivilegedAsyncAgentEnvironment agdom = (PrivilegedAsyncAgentEnvironment) DomainRegistry.getDomainRegistry().getDomain(linkedDomain);
				return agdom.getAgent(name, controller);
			}
		}
		if (ag != null) {
			return CollaborationFactory.create(this, ag, controller);
		}
		hostLog.info("Checking other services");
		ServiceItem[] agSvcs = Kernel.getAllServices(AgentService.class);
		for (int i = 0; i < agSvcs.length; i++) {
			if (!agSvcs[i].serviceID.equals(agentServiceID)) {
				hostLog.finer("Checking agentService " + agSvcs[i].serviceID + " for " + name + "; my sid:" + agentServiceID);
				AgentService svc = (AgentService) agSvcs[i].service;
				AgentProxyInfo info = null;
				try {
					info = svc.getAgentProxyInfo(agentToFind, this.domainName);
				} catch (RemoteException e1) {

					e1.printStackTrace();
				}
				if (info != null) {
					return CollaborationFactory.create(this, info, controller);
				}
			}
		}

		// ServiceItem[] agSvcs = Kernel.getAllServices(RouterService.class);
		// for (int i = 0; i < agSvcs.length; i++) {
		// if (!agSvcs[i].serviceID.equals(agentServiceID)) {
		// hostLog.finer("Checking Router Service " + agSvcs[i].serviceID + "
		// for " + name + "; my sid:" + agentServiceID);
		// RouterService svc = (RouterService) agSvcs[i].service;
		// AgentProxyInfo info = null;
		// try {
		// info = svc.getAgentProxyInfo(agentToFind, this.domainName);
		// } catch (RemoteException e1) {
		//
		// e1.printStackTrace();
		// }
		// if (info != null)
		// return CollaborationFactory.create(this, info, controller);
		// }
		// }

		if (throwExIfNoneFound) {
			throw new NoSuchAgentException("A matching agent [" + name + "] could not be found on the network");
		} else {
			return null;
		}
	}

	/**
	 * @param name
	 * @param matchTo
	 * @param controller
	 * @throws NoSuchAgentException
	 */
	private Object obtainAgentWithMeta(String name, Entry[] matchTo, Agent controller, AgentState setState, boolean throwExIfNoneFound) throws NoSuchAgentException {
		// TODO Complete method stub for getAgent
		Agent ag = null;
		String namespace = "";
		String agentToFind = name;
		if (controller != null) {
			namespace = controller.getNamespace();
			if (name.indexOf('.') == -1) {
				agentToFind = controller.getNamespace() + "." + name;
			}
		}
		try {
			if (controller != null && setState != null) {
				ag = registry.getAgent(agentToFind, setState, matchTo);
			} else {
				ag = registry.getAgent(agentToFind, matchTo);
			}
		} catch (NoSuchAgentException e) {
			// URGENT Handle NoSuchAgentException
			// e.printStackTrace();
		}
		if (ag != null) {
			return CollaborationFactory.create(this, ag, controller);
		}
		// hostLog.finer("Checking AttachedDomains of " + domainName + "
		// for " +
		// agentToFind);
		List l = DomainRegistry.getDomainRegistry().getAllowedDomainsOut(this.domainName);
		if (l != null) {
			for (Iterator iter = l.iterator(); iter.hasNext() && ag == null;) {
				String linkedDomain = (String) iter.next();
				hostLog.finer("Looking in " + linkedDomain + " domain.");
				PrivilegedAsyncAgentEnvironment agdom = (PrivilegedAsyncAgentEnvironment) DomainRegistry.getDomainRegistry().getDomain(linkedDomain);
				return agdom.getAgent(name, matchTo, controller);
			}
		}
		if (ag != null) {
			return CollaborationFactory.create(this, ag, controller);
		}
		hostLog.info("Checking other services");
		ServiceItem[] agSvcs = Kernel.getAllServices(AgentService.class);
		for (int i = 0; i < agSvcs.length; i++) {
			if (agSvcs[i].serviceID != agentServiceID) {
				if (!agSvcs[i].serviceID.equals(this.agentServiceID)) {
					System.out.println("Checking agentService " + agSvcs[i].serviceID + " for " + name + " with attributes");
					AgentService svc = (AgentService) agSvcs[i].service;
					AgentProxyInfo info = null;
					try {
						info = svc.getAgentProxyInfo(agentToFind, matchTo, this.domainName);
					} catch (RemoteException e1) {
						// URGENT Handle RemoteException
						e1.printStackTrace();
					}
					if (info != null) {
						return CollaborationFactory.create(this, info, controller);
					}
				}
			}
		}
		if (throwExIfNoneFound) {
			throw new NoSuchAgentException("A matching agent [" + name + "] could not be found on the network");
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jini.projects.neon.host.AgentContext#registerSensor(org.jini.projects.neon.agents.Agent,
	 *      java.lang.String,
	 *      org.jini.projects.neon.agents.sensors.SensorListener)
	 */
	public boolean registerSensor(Agent ag, String name, SensorFilter l) {
		// TODO Complete method stub for registerSensor
		return registry.registerSensor(ag, name, l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jini.projects.neon.host.AgentContext#detach(null)
	 */
	public boolean removeAgent(Agent ag) {
		if (direct) {
			registry.deregisterAgent(ag);
			return true;
		} else {

			try {
				if (mgr.removeChannel(ag.getIdentity().getID().toString())) {
					MessageDispatcher dispatcher = (MessageDispatcher) dispatchers.get(domainName + "|" + ag.getNamespace() + "." + ag.getName());
					if (dispatcher != null) {
						dispatcher.removeDispatcher(ag.getIdentity().getID());
					} else {
						hostLog.finest("******** Dispatcher [" + domainName + "|" + ag.getNamespace() + "." + ag.getName() + "] is null ********");
					}
					registry.deregisterAgent(ag);
					return true;
				} else {
					return false;
				}
			} catch (ChannelException e) {
				// URGENT Handle ChannelException
				e.printStackTrace();
			}
		}
		return false;
	}

	public void returnTemporaryChannel(MessageChannel tmpChannel) {
		mgr.returnTemporaryChannel(tmpChannel);
	}

	/**
	 * Routes a message to the an instance of a named agent, if it exists, in
	 * some instances an agent may be created specifically to handle this
	 * message
	 * 
	 * @param agentName
	 * @param message
	 */
	public void sendAsyncMessage(String agentName, Message message) throws NoSuchAgentException {
		// URGENT sendMessage now deals with implicit namespacing - this
		// should
		// be done by the AgentContextAdapter
		MessageHeader header = message.getHeader();
		if (header.getArbitraryField("txnID") != null) {
			if (header.getArbitraryField("actualTxn") == null) {
				// Add in the jini transaction into the message
				// header
				// System.out.println("Attaching real
				// Transaction Object");
				String managerAndTxnRef = (String) header.getArbitraryField("txnID");
				String[] parts = managerAndTxnRef.split(":");
				// URGENT: Add method into this class to reflect
				// getting an
				// agent via ID, but allow for cross-host
				// objects
				TransactionAgent ag = (TransactionAgent) getAgent(new AgentIdentity(parts[0]), null);

				try {
					header.setArbitraryField("actualTxn", ag.getTransactionFor(UuidFactory.create(parts[1])));
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		String agentToSendTo;
		agentToSendTo = agentName;
		if (mgr.containsChannel(agentName)) {
			try {
				if (mgr.getChannel(agentName) instanceof PublishSubscribeChannel) {
					System.out.println("PUB SUB being called");
				}
				mgr.getPublishingConnector(agentName).sendMessage(message);
			} catch (ChannelException e) {
				// URGENT Handle ChannelException
				e.printStackTrace();
			}
		} else {
			throw new NoSuchAgentException("Agent Type not found in host, " + agentName);
		}
	}

	public void sendAsyncMessage(Uuid id, Message message) throws NoSuchAgentException {
		Response returnResponse = null;
		MessageHeader header = message.getHeader();
		if (header.getArbitraryField("txnID") != null) {
			if (header.getArbitraryField("actualTxn") == null) {
				// Add in the jini transaction into the message
				// header
				String managerAndTxnRef = (String) header.getArbitraryField("txnID");
				String[] parts = managerAndTxnRef.split(":");
				// URGENT: Add method into this class to reflect
				// getting an
				// agent via ID, but allow for cross-host
				// objects
				TransactionAgent ag = (TransactionAgent) registry.getAgent(new AgentIdentity(parts[0]).getID());

				try {
					header.setArbitraryField("actualTxn", ag.getTransactionFor(UuidFactory.create(parts[1])));
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		String agentName = id.toString();
		if (mgr.containsChannel(agentName)) {
			try {
				if (mgr.getChannel(agentName) instanceof PublishSubscribeChannel) {
					System.out.println("PUB SUB being called");
				}
				mgr.getPublishingConnector(agentName).sendMessage(message);
			} catch (ChannelException e) {
				// URGENT Handle ChannelException
				e.printStackTrace();
			}
		} else {
			throw new NoSuchAgentException("Agent Type not found in host, " + agentName);
		}
		// throw new NoSuchAgentException("Unique agent not found in
		// host");
	}

	/**
	 * @param bus
	 */
	public void setBus(Bus bus) {
		this.bus = bus;
	}

	/**
	 * @param manager
	 */
	public void setBusManager(BusManager manager) {
		busManager = manager;
	}

	/**
	 * @param registry
	 */
	public void setRegistry(AgentRegistry registry) {
		this.hostLog.finest("Setting Registry field");
		this.registry = registry;
		registerMBeanServer();
	}

	public void setTxnBlackBox(TransactionBlackBox tbb) {
		this.tbb = tbb;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jini.projects.neon.host.PrivilegedAgentContext#unsubscriberAgent(org.jini.projects.neon.agents.AgentIdentity)
	 */
	public void unsubscribeAgent(Agent ID) throws NoSuchAgentException, SecurityException {
		// TODO Complete method stub for unsubscriberAgent
		try {
			AccessController.doPrivileged(new UnsubscribeAgentAction(ID), AccessController.getContext());
		} catch (PrivilegedActionException e) {
			if (e.getException() instanceof NoSuchAgentException) {
				throw (NoSuchAgentException) e.getException();
			} else {
				System.out.println("Err: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void storeDomainAgentState() {
		System.out.println("Starting Savepoint process");
		try {
			// busManager.deregisterAll();
		} catch (NullPointerException npe) {
		}
		try {

			Object o = registry.getAgent("neon.CheckPoint");
			if (o != null) {
				CheckpointAgent cp = (CheckpointAgent) o;
				Meta checkBootstrapped = new Meta();
				checkBootstrapped.addAttribute(new BootstrappedAgent(true));
				Agent[] allAgents = registry.getAllAgents();
				int numstored = 0;
				List l = new ArrayList();
				for (int i = 0; i < allAgents.length; i++) {
					Agent ag = allAgents[i];
					System.out.print("\r" + getDomainName() + ": Checking....." + ag.getName());
					// System.out.print("....Stopping Agent ");
					// ag.stop();
					// System.out.print("\r" + getDomainName() + ":
					// Stopped....." +
					// ag.getName());

					// if (initData.isPersistOnShutdown()) {

					if (!ag.getMetaAttributes().matches(checkBootstrapped)) {
						System.out.print("....Storing Agent ");
						ag.setAgentState(AgentState.SAVEPOINT);
						l.add(ag);
						numstored++;
					} else {
						System.out.println("Skipping " + ag.getName());
					}

					// }
				}
				try {
					cp.checkpointAgentSet(l, false);
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println("\nPersisted " + numstored + " agents.");

				ServiceAgent svcAg = (ServiceAgent) registry.getAgent("neon.Services");
				JavaSpace space = (JavaSpace) svcAg.getSingleNamedService(spaceName, JavaSpace.class);

				DelegateCatalog catalog = initData.getDelegateCatalog();

				List delList = catalog.getDelegates();
				List receiveFrom = initData.getReceiveCallsFromDomains();
				List sendTo = initData.getSendCallsToDomains();
				try {
					DomainEntry domEntry = new DomainEntry(domainName, agentServiceID.toString(), delList, receiveFrom, sendTo, initData.getAllowedClasses(), initData.getAllowedNamespaces(), initData.getSecurityLevel(), initData.isEncryptDomainInfo(),
							initData.isEncryptAgentStorage(), initData.isEncryptOtherData(), initData.isFailIfEncryptionAvailable());
					if (initData.isEncryptDomainInfo()) {
						EncryptedDomainEntry encDomEntry = new EncryptedDomainEntry();
						try {
							EncryptionUtils.initialiseEncryptedVersion(domEntry, encDomEntry);
							space.write(encDomEntry, null, Lease.FOREVER);
						} catch (Exception e) {
							hostLog.warning("Encryption of Domain Information failed: - Falling back");
							space.write(domEntry, null, Lease.FOREVER);
						}

					} else
						space.write(domEntry, null, Lease.FOREVER);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TransactionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (NoSuchAgentException e) {
			// TODO Handle NoSuchAgentException
			e.printStackTrace();
		}
	}

	public void shutdownDomain() {
		System.out.println("Starting Deregister process");
		try {
			busManager.deregisterAll();
		} catch (NullPointerException npe) {
		}
		try {
			CheckpointAgent cp = (CheckpointAgent) registry.getAgent("neon.CheckPoint");
			Meta checkBootstrapped = new Meta();
			checkBootstrapped.addAttribute(new BootstrappedAgent(true));
			Agent[] allAgents = registry.getAllAgents();
			int numstored = 0;
			List l = new ArrayList();
			for (int i = 0; i < allAgents.length; i++) {
				Agent ag = allAgents[i];
				System.out.print("\r" + getDomainName() + ": Checking....." + ag.getName());
				System.out.print("....Stopping Agent ");
				ag.stop();
				System.out.print("\r" + getDomainName() + ": Stopped....." + ag.getName());

				// if (initData.isPersistOnShutdown()) {

				if (!ag.getMetaAttributes().matches(checkBootstrapped)) {
					System.out.print("....Storing Agent ");
					ag.setAgentState(AgentState.DUMPED);
					l.add(ag);
					numstored++;
				} else {
					System.out.println("Skipping " + ag.getName());
				}

				// }
			}
			try {
				cp.checkpointAgentSet(l, true);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("\nPersisted " + numstored + " agents.");
			System.out.println("Domain: " + domainName + " de-registered");
			ServiceAgent svcAg = (ServiceAgent) registry.getAgent("neon.Services");
			JavaSpace space = (JavaSpace) svcAg.getSingleNamedService(spaceName, JavaSpace.class);

			DelegateCatalog catalog = initData.getDelegateCatalog();

			List delList = catalog.getDelegates();
			List receiveFrom = initData.getReceiveCallsFromDomains();
			List sendTo = initData.getSendCallsToDomains();
			try {
				DomainEntry domEntry = new DomainEntry(domainName, agentServiceID.toString(), delList, receiveFrom, sendTo, initData.getAllowedClasses(), initData.getAllowedNamespaces(), initData.getSecurityLevel(), initData.isEncryptDomainInfo(),
						initData.isEncryptAgentStorage(), initData.isEncryptOtherData(), initData.isFailIfEncryptionAvailable());
				if (initData.isEncryptDomainInfo()) {
					EncryptedDomainEntry encDomEntry = new EncryptedDomainEntry();
					try {
						EncryptionUtils.initialiseEncryptedVersion(domEntry, encDomEntry);
						space.write(encDomEntry, null, Lease.FOREVER);
					} catch (Exception e) {
						hostLog.warning("Encryption of Domain Information failed: - Falling back");
						space.write(domEntry, null, Lease.FOREVER);
					}

				} else
					space.write(domEntry, null, Lease.FOREVER);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransactionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchAgentException e) {
			// TODO Handle NoSuchAgentException
			e.printStackTrace();
		}
	}

	public void registerMBeanServer() {
		// MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		//		 
		// try {
		// ObjectName name = new
		// ObjectName("org.jini.projects.neon.host.jmx:type=Registry,domain=" +
		// this.getDomainName());
		//
		// Registry mbean = new Registry(this.registry);
		//			  
		// mbs.registerMBean(mbean, name);
		// MBeanInfo info = mbs.getMBeanInfo(name);
		// System.out.println("Description: "+info.getDescription());
		// System.out.println(info.getClassName());
		// MBeanOperationInfo[] ops = info.getOperations();
		// for (int i=0;i<ops.length;i++){
		// System.out.println(ops[i].getName());
		// }
		// MBeanAttributeInfo[] attrs = info.getAttributes();
		// for(MBeanAttributeInfo attr: attrs){
		// System.out.println(attr.getName());
		// }
		// System.out.println("Registerd MBean");
		// } catch (MalformedObjectNameException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (InstanceAlreadyExistsException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (MBeanRegistrationException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (NotCompliantMBeanException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (NullPointerException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (InstanceNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IntrospectionException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (ReflectionException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

	public Object getAgent(AgentIdentity id, Agent controller) throws NoSuchAgentException {
		return obtainAgentFromIdentity(id, controller, null, true);
	}

	public SecurityOptions getSecurityOptions() throws SecurityException {
		// TODO Auto-generated method stub
		return securityopts;
	}
}
