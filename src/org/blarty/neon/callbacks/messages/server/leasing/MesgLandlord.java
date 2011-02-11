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
 * neon : org.jini.projects.neon.callbacks.messages.server
 * MesgLandlord.java
 * Created on 24-Jul-2003
 */
package org.jini.projects.neon.callbacks.messages.server.leasing;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.core.lease.LeaseDeniedException;
import net.jini.core.lease.LeaseMapException;
import net.jini.core.lease.UnknownLeaseException;
import net.jini.export.Exporter;
import net.jini.id.ReferentUuid;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.http.HttpServerEndpoint;

import org.jini.projects.neon.callbacks.messages.server.LeasedMesgRegistration;

import com.sun.jini.constants.TimeConstants;
import com.sun.jini.landlord.FixedLeasePeriodPolicy;
import com.sun.jini.landlord.Landlord;
import com.sun.jini.landlord.LeaseFactory;
import com.sun.jini.landlord.LeasePeriodPolicy;
import com.sun.jini.landlord.LeasedResource;

/**
 * @author calum
 */
public class MesgLandlord implements ReferentUuid, Landlord, Remote {
	protected Map leases = Collections.synchronizedMap(new HashMap());
	private Logger l = Logger.getLogger("org.jini.projects.neon.callbacks.messages.server.leasing");
	Uuid myId = null;
	public static boolean DEBUG = true;
	protected LeaseFactory myFactory;
	protected LeasePeriodPolicy myGrantPolicy = new FixedLeasePeriodPolicy(1 * TimeConstants.MINUTES, 30 * TimeConstants.SECONDS);
	private Reaper reaper = new Reaper();
	private static int LeaseCount = 0;
	/**
	 * 
	 */
	public MesgLandlord() {
		Logger.getAnonymousLogger().fine("MessageLandlord Started");
		myId = UuidFactory.generate();
		Exporter exp = new BasicJeriExporter(HttpServerEndpoint.getInstance(0), new BasicILFactory());
		try {
			myFactory = new LeaseFactory((Landlord) exp.export(this), myId);
		} catch (ExportException e) {

			e.printStackTrace();
		}
		Logger.getAnonymousLogger().fine("MessageLandlord Initialised");

		reaper.start();
	}

	/* (non-Javadoc)
	 * @see com.sun.jini.landlord.Landlord#renew(net.jini.id.Uuid, long)
	 */
	public long renew(Uuid cookie, long duration) throws LeaseDeniedException, UnknownLeaseException, RemoteException {
		LeasedResource resource;
		LeasePeriodPolicy policy;

		if (DEBUG)
			System.out.println("Renewing a lease");

		if (null != (resource = (LeasedResource) leases.get(cookie)))
			policy = myGrantPolicy;
		else
			throw new UnknownLeaseException("Unknown Lease");

		synchronized (resource) {
			if (resource.getExpiration() <= System.currentTimeMillis()) {
				// Lease has expired, don't renew
				throw new UnknownLeaseException("Lease expired");
			}

			// No one can expire the lease, so it is safe to update.
			final LeasePeriodPolicy.Result r = policy.renew(resource, duration);

			resource.setExpiration(r.expiration);

			return r.duration;
		}
	}

	public Lease newLease(RemoteEventListener resource, long duration) throws LeaseDeniedException {
		long now = System.currentTimeMillis();
		Integer cookie;
		synchronized (MesgLandlord.class) {
			cookie = new Integer(LeaseCount);
			LeaseCount++;
		}
		Uuid regCookie = UuidFactory.generate();
		LeasedMesgRegistration reg = new LeasedMesgRegistration(resource, regCookie);
		LeasePeriodPolicy.Result r = myGrantPolicy.grant(reg, duration);
		reg.setExpiration(r.expiration);
		leases.put(regCookie, reg);
		System.out.println("Creating new Lease from Policy now");
		return myFactory.newLease(regCookie, reg.getExpiration());
		//return myGrantPolicy.grant(reg, duration);			
	}

	public void killLease(Uuid obj) throws net.jini.core.lease.UnknownLeaseException {
		if (DEBUG)
			System.out.println("Killing Lease ID: " + obj.toString());
		RemoteEventListener lconn = (RemoteEventListener) leases.get(obj);
		if (lconn == null) {
			throw new UnknownLeaseException("Unknown lease");
		}
		leases.remove(obj);
	}

	/* (non-Javadoc)
	 * @see com.sun.jini.landlord.Landlord#cancel(net.jini.id.Uuid)
	 */
	public void cancel(Uuid cookie) throws UnknownLeaseException {

		if (DEBUG)
			System.out.println("Cancelling a lease");
		killLease(cookie);
	}

	/* (non-Javadoc)
	 * @see com.sun.jini.landlord.Landlord#renewAll(net.jini.id.Uuid[], long[])
	 */
	public RenewResults renewAll(Uuid[] cookies, long[] durations) throws RemoteException {

		boolean somethingDenied = false;

		long[] granted = new long[cookies.length];

		Exception[] denied = new Exception[cookies.length + 1];

		for (int i = 0; i < cookies.length; i++) {
			try {
				granted[i] = renew(cookies[i], durations[i]);
				denied[i] = null;
			} catch (Exception ex) {
				somethingDenied = true;
				granted[i] = -1;
				denied[i] = ex;
			}

		}
		if (!somethingDenied)
			denied = null;
		return new Landlord.RenewResults(granted, denied);
	}

	/* (non-Javadoc)
	 * @see com.sun.jini.landlord.Landlord#cancelAll(net.jini.id.Uuid[])
	 */
	public Map cancelAll(Uuid[] cookies) throws RemoteException {

		Map exMap = null;
		LeaseMapException lmEx = null;
		for (int i = 0; i < cookies.length; i++) {
			try {
				cancel(cookies[i]);
			} catch (Exception e) {
				if (lmEx == null) {
					exMap = new HashMap();
					lmEx = new LeaseMapException(null, exMap);
				}

				exMap.put(myFactory.newLease(cookies[i], 0), e);

			}
		}
		if (lmEx != null)
			return exMap;
		else
			return null;
	}

	/* (non-Javadoc)
	 * @see net.jini.id.ReferentUuid#getReferentUuid()
	 */
	public Uuid getReferentUuid() {
		if (this.myId == null)
			myId = UuidFactory.generate();
		return myId;
	}

	class Reaper extends Thread {
		public void run() {
			for (;;) {

				synchronized (leases) {
					try {
						Vector keystoremove = new Vector();
						Iterator iter = leases.entrySet().iterator();

						int i = 1;
						while (iter.hasNext()) {
							try {
								Map.Entry leaseEntry = (Map.Entry) iter.next();
								Object obj = leaseEntry.getValue();
								//System.out.println("LeaseValue name "+obj.getClass().getName()); 
								LeasedResource lconn = (LeasedResource) obj;
								if ((lconn.getExpiration() + (15 * TimeConstants.SECONDS)) < System.currentTimeMillis()) {
									Uuid leaseKey = (Uuid) leaseEntry.getKey();
									if (DEBUG)
										System.out.println("...expiring a lease with key " + leaseKey);

									keystoremove.add(leaseKey);
								}
							} catch (ConcurrentModificationException cmex) {
								System.out.println("Fail-fast");
							}
						}
						for (int a = 0; a < keystoremove.size(); a++) {
							if (DEBUG)
								System.out.println("Deallocating");
							leases.remove(keystoremove.elementAt(a));
						}
						keystoremove.clear();
					} catch (Exception ex) {
						System.out.println(new java.util.Date() + ": Err: " + ex.getMessage());
						ex.printStackTrace();
					}
				}
				try {
					Thread.sleep(10 * 1000L);
					//Logger.getLogger("Reaper").info("waiting....");

				} catch (Exception ex) {
					System.out.println(new java.util.Date() + ": Err: " + ex.getMessage());
					ex.printStackTrace();
				}

			}
		}
	}

	public Map getRegistered() {
		return leases;
	}

}