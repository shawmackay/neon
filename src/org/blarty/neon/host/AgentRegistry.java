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

import java.lang.reflect.Field;
import java.rmi.RemoteException;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;

import net.jini.core.entry.Entry;
import net.jini.core.event.RemoteEventListener;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.AgentState;
import org.jini.projects.neon.agents.Meta;
import org.jini.projects.neon.agents.SensorAgent;
import org.jini.projects.neon.agents.sensors.SensorFilter;
import org.jini.projects.neon.agents.util.meta.Registration;
import org.jini.projects.neon.annotations.Broadcast;
import org.jini.projects.neon.dynproxy.AgentProxy;
import org.jini.projects.neon.host.events.RegistrationEvent;
import org.jini.projects.neon.host.events.RegistrationListener;
import org.jini.projects.neon.host.transactions.NonTransactionalResource;
import org.jini.projects.neon.host.transactions.TransactionAgent;
import org.jini.projects.neon.host.transactions.TransactionBlackBox;
import org.jini.projects.neon.host.transactions.TransactionalResource;
import org.jini.projects.neon.service.AgentCallback;
import org.jini.projects.zenith.messaging.channels.PublishingChannel;
import org.jini.projects.zenith.messaging.channels.ReceiverChannel;

/**
 * Contains details of all the deployed agents, including group references and
 * callbacks. Also maintains a list of sensors that an agent has registered
 * interest in Primarily, this class is used to link up other areas of the
 * system and maintain the list of agents currently running. Also ensures that
 * agent information is cleared up. Some agents may, through their code, be
 * collaborative yet do not necessarily advertise using the MessageBus. Also
 * agents may be 'imported' into other agents at runtime - see Multiplexed
 * agents. Unlike the message bus, the agent registry does not handle
 * inter-domain processing If an agent does not exist in a registry, but exists
 * in a connected registry via the <i>startup.xml </i> file, and would be routed
 * to through <i>context.sendMessage(...) </i> it will throw a
 * NoSuchAgentException, because AgentRegistry only looks at it's agents.
 */
public class AgentRegistry {

	private static Map<String, Semaphore> lockMap = new HashMap<String, Semaphore>();

	private ArrayList pendingList = new ArrayList();

	private HashMap<String, RegistrationSet> registrations = new HashMap<String, RegistrationSet>();

	private boolean wait = false;

	private List<RegistrationListener> listeners = new ArrayList<RegistrationListener>();

	private Map agentListing = new HashMap();
	Random agentChooser = new Random(System.currentTimeMillis());
	private Map agentGroupings = new HashMap();

	private Map callbackRegistrations = new HashMap();

	private Logger l = Logger.getLogger("org.jini.projects.neon.host.AgentRegistry");

	private Map sensorRegistrations = new HashMap();

	private String domainName;

	private ReceiverChannel receive;

	// AcquireAgentThread agentThread = new AcquireAgentThread();

	private PublishingChannel sender;

	
	
	public AgentRegistry(String name) {
		domainName = name;
		lockMap.put(name, new Semaphore(1));
		l.fine("Created Agent Registry and lock for " + name);
	}

	/**
	 * Registers a source agent on a member of an agent category, with the
	 * supplied listener
	 * 
	 * @param source
	 *            the requesting agent
	 * @param agentName
	 *            the name of the category that the requesting agent wnats to
	 *            register against
	 * @param listen
	 *            the listener used when the sensor is triggered
	 * @return
	 */
	public boolean registerSensor(Agent source, String agentName, SensorFilter listen) {
		if (agentGroupings.containsKey(agentName)) {
			List group = (List) agentGroupings.get(agentName);
			return findSensorList(source, listen, group);
		}
		l.warning("Agent Group: " + agentName + " not found");
		return false;
	}

	private boolean findSensorList(Agent source, SensorFilter listen, List group) {
		for (Iterator iter = group.iterator(); iter.hasNext();) {
			Object obj = (iter.next());
			AgentIdentity agid = (AgentIdentity) obj;
			Agent ag = (Agent) this.agentListing.get(agid);
			if (ag instanceof SensorAgent) {
				if (!ag.getAgentState().equals(AgentState.LOCKED)) {
					SensorAgent sa = (SensorAgent) ag;
					addToSensorList(source, sa);
					l.finest("Sensor added");
					try {
						return sa.addListener(source.getIdentity(), listen);
					} catch (RemoteException e) {
						e.printStackTrace();

						return false;
					}
				} else {
					l.finest("All agents are currently locked");
				}
			} else {
				l.finest("Agent does not enable sensors");
			}

		}
		return false;
	}

	private void addToSensorList(Agent source, SensorAgent sa) {
		if (sensorRegistrations.containsKey(source.getIdentity())) {
			List l = (List) sensorRegistrations.get(source.getIdentity());
			l.add(sa);
		} else {
			ArrayList a = new ArrayList();
			a.add(sa);
			sensorRegistrations.put(source.getIdentity(), a);
		}
	}

	/**
	 * Remove all sensor registrations for this agent
	 * 
	 * @param source
	 * @return
	 */
	public boolean deregisterSensor(Agent source) {
		if (sensorRegistrations.containsKey(source.getIdentity())) {
			List sensorList = (List) sensorRegistrations.get(source.getIdentity());
			for (int i = 0; i < sensorList.size(); i++) {
				SensorAgent sa = (SensorAgent) sensorList.get(i);
				try {
					sa.removeListener(source.getIdentity());
				} catch (RemoteException e) {
					e.printStackTrace();
					return false;
				}

			}
		} else {
			l.finer("No Sensors registered for this agent");
		}
		return true;
	}

	/**
	 * Place an agent in the agent registry, with it's callback. <br>
	 * This is called once an agent has been wrappered in it's dynamic proxy,
	 * and put into it's executing domain.
	 * 
	 * @param a
	 *            the agent to register
	 * @param callback
	 *            an associated client callback
	 */
	public void registerAgent(Agent a, RemoteEventListener callback) {
		// Insert into the one-one ID table
		l.finest("Registering the agent");
		agentListing.put(a.getIdentity(), a);
		Broadcast caster = a.getClass().getAnnotation(Broadcast.class);
		if (caster != null) {
			if (!registrations.containsKey(caster.name()))
				registrations.put(caster.name(), new RegistrationSet((caster.scope().equalsIgnoreCase("remote"))));
		}

		addRegistrations(a);
		// Ensure that the agent is placed in the group of it's peers - as long
		// as they reside in the same namespace
		String agName = a.getNamespace() + "." + a.getName();
		callbackRegistrations.put(a.getIdentity(), callback);
		if (callback == null)
			l.finest("Registered a " + a.getName() + " agent without a callback into the " + domainName + " domain as " + agName);

		if (agentGroupings.containsKey(agName)) {
			List group = (List) agentGroupings.get(agName);

			group.add(a.getIdentity());
		} else {
			List group = new CopyOnWriteArrayList();
			group.add(a.getIdentity());
			agentGroupings.put(agName, group);

		}
		fireAllListeners(new RegistrationEvent(a.getIdentity(), RegistrationEvent.DEREGISTERED, domainName));
	}

	private void addRegistrations(Agent a) {
		Meta meta = a.getMetaAttributes();
		List<Registration> data = (List<Registration>) meta.getMetaOfType(Registration.class);
		for(Registration reg : data){
			String registerWith = reg.registerWith;
			RegistrationSet regSet = registrations.get(registerWith);
			if(regSet==null){
				l.severe("Requesting registration with '" + registerWith + "' failed");
			} else
				regSet.put(a.getIdentity().getExtendedString(), a.getIdentity());
		}
	}
	
	public RegistrationSet getRegistrations(Agent me){
		Broadcast cast = me.getClass().getAnnotation(Broadcast.class);
		return registrations.get(cast.name());
	}

	/**
	 * Returns all agents in the registry
	 * 
	 * @return the contents of the registry, without callbacks
	 */
	public Agent[] getAllAgents() {
		Collection s = agentListing.values();
		Object[] data = s.toArray();
		Agent[] agents = new Agent[data.length];
		for (int i = 0; i < data.length; i++)
			agents[i] = (Agent) data[i];
		return agents;
	}

	public int getAgentNumber() {
		Collection s = agentListing.values();
		return s.size();
	}

	/**
	 * Removes an agent from the registry, and removes it's callback. Does not
	 * remove any sensors
	 * 
	 * @param a
	 *            the agent to be deregistered
	 * @see AgentRegistry#deregisterSensor(Agent)
	 */
	public void deregisterAgent(Agent a) {

		if (agentListing.containsKey(a.getIdentity())) {
			agentListing.remove(a.getIdentity());
			List group = (List) agentGroupings.get(a.getNamespace() + "." + a.getName());
			if(group!=null)
			group.remove(a.getIdentity());
			l.finer("Removed: " + a.getIdentity());

			callbackRegistrations.remove(a.getIdentity());
			fireAllListeners(new RegistrationEvent(a.getIdentity(), RegistrationEvent.DEREGISTERED, domainName));
		} else
			System.out.println("Could not find the agent in order to deregister it");
	}

	/**
	 * Get the agent with the given identity, if it exists in <em>this</em>
	 * registry - does not check state
	 * 
	 * @param ID
	 *            the ID for the agent
	 * @return the agent
	 * @throws NoSuchAgentException
	 *             if the agent does not exist in the registry
	 */
	public Agent getAgent(AgentIdentity ID) throws NoSuchAgentException {
		return (Agent) agentListing.get(ID);
	}

	public boolean contains(AgentIdentity ID) {
		return agentListing.containsKey(ID);
	}

	/**
	 * Get the next available agent for this category, that matches the meta
	 * entries - does not mark the agent as busy
	 * 
	 * @param name
	 * @param meta
	 * @return
	 * @throws NoSuchAgentException
	 */
	public synchronized Agent getAgent(String name, Entry[] meta) throws NoSuchAgentException {
		return getAgent(name, null, meta);
	}

	public boolean match(Entry[] meta, Entry[] check) {
		Entry[] agentMeta = check;
		boolean assignableCheck = false;
		if (meta != null)
			if (meta.length <= agentMeta.length) {
				for (int checkloop = 0; checkloop < meta.length; checkloop++) {
					for (int innerloop = 0; innerloop < agentMeta.length; innerloop++)
						if (meta[checkloop].getClass().isInstance(agentMeta[innerloop])) {
							assignableCheck = true;
							Field[] entryData = meta[checkloop].getClass().getFields();
							try {
								if (!checkFields(entryData, meta[checkloop], agentMeta[innerloop]))
									return false;
							} catch (IllegalAccessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
				}

			}

		return true;
	}

	private boolean checkFields(Field[] fields, Entry template, Entry toMatch) throws IllegalAccessException {
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			Object templateValue = f.get(template);
			if (templateValue == null) {
				continue;
			}
			Object matchValue = f.get(toMatch);
			if (matchValue != null) {
				if (templateValue.getClass().equals(matchValue.getClass())) {
					if (templateValue.equals(matchValue)) {
						return true;
					} else
						return false;
				} else
					return false;
			} else
				return false;
		}
		return true;
	}

	/**
	 * Get the next available agent for this category - does not mark the agent
	 * as busy
	 * 
	 * @param name
	 * @return
	 * @throws NoSuchAgentException
	 */
	public synchronized Agent getAgent(String name) throws NoSuchAgentException {
		return getAgent(name, (AgentState) null);
	}

	public boolean contains(String agentname){
		return agentGroupings.containsKey(agentname);
	}
	
	public synchronized Agent getAgent(String name, AgentState setState) throws NoSuchAgentException {
		return getAgent(name, setState, null);
	}

	public synchronized Agent getAgent(String name, AgentState setState, Entry[] metaMatches) throws NoSuchAgentException {
		// System.out.println("Acquiring semaphore lock");
		try {
			lockMap.get(domainName).acquire();
		} catch (InterruptedException e) {
			// TODO Handle InterruptedException
			e.printStackTrace();
		}
		// System.out.println("Looking for " + name + " in Agent Groups");
		// agentGroupings.keySet());
		if (agentGroupings.containsKey(name)) {
			List group = (List) agentGroupings.get(name);
			int size = group.size();
			boolean donecycle = false;
			int startpos = agentChooser.nextInt(size);
			int currentpos = startpos;
			int lookedAt = 0;
			while (!donecycle) {
				Object obj = group.get(currentpos++);
				if (currentpos == size)
					currentpos = 0;
				lookedAt++;
				if (lookedAt == size)
					donecycle = true;
				AgentIdentity agid = (AgentIdentity) obj;
				// synchronized (pendingList) {
				if (!pendingList.contains(agid)) {
					pendingList.add(agid);
					Agent ag = (Agent) agentListing.get(agid);
					synchronized (ag) {
						if (metaMatches != null) {
							if (ag.getAgentState().equals(AgentState.AVAILABLE)) {
								// if (metaMatches != null) {
								Agent o = getMetaMatchingAgent(ag, metaMatches, setState);
								if (o != null) {
									pendingList.remove(agid);
									return o;
								}
							}
						} else {
							Agent o = getMatchingAgent(setState, ag);
							if (o != null) {
								pendingList.remove(agid);
								return o;
							}
						}
					}
					pendingList.remove(agid);
				} else
					System.out.println("\t****PENDING LIST Contains ID - skipping");
				// }
			}
		}
		lockMap.get(domainName).release();

		return null;
	}

	/**
	 * @param setState
	 * @param ag
	 * @return
	 */
	private Agent getMatchingAgent(AgentState setState, Agent ag) {
		if (!(ag instanceof NonTransactionalResource)) {
			if (!((TransactionalResource) ag).inTransaction()) {

				// ag.setAgentState(AgentState.PENDING);
				if (setState != null)
					ag.setAgentState(setState);
				lockMap.get(domainName).release();
				return ag;
			}
		} else {
			// ag.setAgentState(AgentState.PENDING);
			if (setState != null)
				ag.setAgentState(setState);
			lockMap.get(domainName).release();
			return ag;
		}
		return null;
	}

	public Agent getMetaMatchingAgent(Agent ag, Entry[] metaMatches, AgentState setState) {
		try {
			if (match(metaMatches, ag.getMetaAttributes().toEntries())) {
				if (!(ag instanceof NonTransactionalResource)) {
					if (((TransactionalResource) ag).getTransaction() == null) {
						// ag.setAgentState(AgentState.PENDING);
						if (setState != null)
							ag.setAgentState(setState);
						lockMap.get(domainName).release();
						return ag;
					} else {
						String txnRef = ((TransactionalResource) ag).getTransaction();
						String[] parts = txnRef.split(":");
						AgentIdentity agentRef = new AgentIdentity(parts[0]);
						Uuid txnid = UuidFactory.create(parts[1]);

						TransactionAgent a = (TransactionAgent) getAgent(agentRef.getID());

						// Object o = a.getTransactionFor(txnid);

						if (setState != null)
							ag.setAgentState(setState);
						lockMap.get(domainName).release();
						return ag;
					}
				} else {
					// ag.setAgentState(AgentState.PENDING);
					if (setState != null)
						ag.setAgentState(setState);
					lockMap.get(domainName).release();
					return ag;
				}
			}
		} catch (NoSuchAgentException e) {
			// TODO Handle NoSuchAgentException
			e.printStackTrace();
		}
		return null;
	}

	public String getAgentName(AgentIdentity id) {
		Object o = agentListing.get(id);
		if (o != null) {
			Agent agt = (Agent) o;
			return agt.getNamespace() + "." + agt.getName();
		}
		return null;
	}

	public String getAgentClassName(AgentIdentity id) {
		Object o = agentListing.get(id);
		if (o != null) {
			if (o instanceof AgentProxy) {
				Agent agt = ((AgentProxy) o).getReceiver();
				return agt.getClass().getName();
			}
		}
		return null;
	}

	public synchronized Agent getAgent(Uuid agentID) throws NoSuchAgentException {
		l.finest("Looking for " + agentID + " in Registry");
		for (Iterator iter = agentListing.keySet().iterator(); iter.hasNext();) {
			AgentIdentity ag = (AgentIdentity) iter.next();
			if (ag.getID().equals(agentID))
				return (Agent) agentListing.get(ag);
		}
		return null;
	}

	/*
	 * public Agent getAgent(AgentIdentity agentID) throws NoSuchAgentException {
	 * l.finest("Looking for " + agentID + " in Registry"); for (Iterator iter =
	 * agentListing.keySet().iterator(); iter.hasNext();) { AgentIdentity ag =
	 * (AgentIdentity) iter.next(); if (ag.equals(agentID)) return (Agent)
	 * agentListing.get(ag); } return null; }
	 */

	/**
	 * Gets all the callbacks registered for all the agents under the given
	 * category
	 * 
	 * @param agentName
	 *            the name of the agent category
	 * @return a list of callbacks
	 * @throws NoSuchAgentException
	 *             if no category exists with the supplied name
	 */
	public RemoteEventListener[] getCallbacks(String agentName) throws NoSuchAgentException {
		PrivilegedAgentPermission p = new PrivilegedAgentPermission("listCallbacks");
		AccessController.checkPermission(p);
		RemoteEventListener[] returnCallbacks;
		l.finest("AgentName " + agentName + " contained? " + agentGroupings.containsKey((agentName)));
		if (agentGroupings.containsKey(agentName)) {
			List group = (List) agentGroupings.get(agentName);
			returnCallbacks = new RemoteEventListener[group.size()];
			int loop = 0;
			for (Iterator iter = group.iterator(); iter.hasNext();)
				returnCallbacks[loop++] = (AgentCallback) this.callbackRegistrations.get(iter.next());
			return returnCallbacks;
		}
		throw new NoSuchAgentException("The agent group " + agentName + " does not exist");

	}

	/**
	 * GEt the callback for the agent with the given identity
	 * 
	 * @param ID
	 *            the identity of the agent
	 * @return teh callback for the agent
	 * @throws NoSuchAgentException
	 *             if no agent with that identity exists in this registry
	 */
	public RemoteEventListener getCallback(AgentIdentity ID) throws NoSuchAgentException {
		// System.out.println("Contains? " + ID + "=" +
		// agentListing.containsKey(ID));
		if (callbackRegistrations.containsKey(ID)) {

			RemoteEventListener ac = (RemoteEventListener) callbackRegistrations.get(ID);

			/*
			 * if (ac == null) System.out.println("....but it is null");
			 */
			return ac;
		} else if (!(agentListing.containsKey(ID))) {
			System.out.println("There is no agent listed in the " + domainName + " domain");
			throw new NoSuchAgentException("Agent " + ID.getID() + " does not exist");
		}
		return null;
	}

	public void addRegistrationListener(RegistrationListener listener) {
		this.listeners.add(listener);
	}

	private void fireRegistrationListener(RegistrationEvent event, RegistrationListener listener) {
		listener.notify(event);
	}

	private void fireAllListeners(RegistrationEvent event) {
		for (RegistrationListener lis : listeners)
			fireRegistrationListener(event, lis);
	}

	public Set  getAgentNames(){
		return agentGroupings.keySet();
	}
	
	// ArrayBlockingQueue<GetAgentRequest> getQueue = new
	// ArrayBlockingQueue<GetAgentRequest>(25);

	// public interface AgentRequestListener {
	// public void notifyListener(Agent agent);
	// public Agent getAgent();
	// }
	//
	// public class GetAgentRequest {
	// String agentName;
	// AgentState newState;
	// Entry[] meta;
	// AgentRequestListener agentListener;
	//
	// public String getAgentName() {
	// return agentName;
	// }
	//
	// public Entry[] getMeta() {
	// return meta;
	// }
	//
	// public AgentState getNewState() {
	// return newState;
	// }
	//
	// public GetAgentRequest(String name, Entry[] meta, AgentState state,
	// AgentRequestListener listener) {
	// // TODO Complete constructor stub for GetAgentRequest
	// agentName = name;
	// this.meta = meta;
	// newState = state;
	// this.agentListener = listener;
	// }
	//
	// public AgentRequestListener getAgentListener() {
	// return agentListener;
	// }
	//
	// }
	//
	// public class AcquireAgentThread implements Runnable {
	//
	// public void run() {
	// // TODO Complete method stub for run
	// System.out.println("AgentAcquireThread started");
	// for (;;) {
	// GetAgentRequest req = null;
	// try {
	// req = getQueue.take();
	// } catch (InterruptedException e) {
	// // TODO Handle InterruptedException
	// e.printStackTrace();
	// }
	// System.out.println("Obtained something from queue");
	// if (req != null) {
	// String name = req.getAgentName();
	// Entry[] metaMatches = req.getMeta();
	// AgentState setState = req.getNewState();
	//                                      
	// }
	// }
	// }
	// }

}
