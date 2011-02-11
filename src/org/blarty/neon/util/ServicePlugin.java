package org.jini.projects.neon.util;

import java.io.IOException;
import java.rmi.RemoteException;

import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.lookup.LookupCache;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.lookup.ServiceItemFilter;

public class ServicePlugin implements org.jini.glyph.di.DIPlugin {

	private LookupDiscoveryManager ldm;

	private ServiceDiscoveryManager sdm;

	private LookupCache cache;

	public ServicePlugin(String[] groups) {
		try {
			ldm = new LookupDiscoveryManager(LookupDiscovery.ALL_GROUPS, null, null);

			sdm = new ServiceDiscoveryManager(ldm, null);
			cache = sdm.createLookupCache(new ServiceTemplate(null, null, null), null, null);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Object findReference(Object params) {
		// TODO Auto-generated method stub
		ServiceTemplate tmp = (ServiceTemplate) params;

		ServiceItemFilter filter = new ServiceItemFilter() {
			public boolean check(ServiceItem item) {
				// TODO Auto-generated method stub
				return false;

			}
		};
		ServiceItem o = sdm.lookup(tmp, null);
		if (o != null)
			return o.service;
		else
			return null;
	}
}
