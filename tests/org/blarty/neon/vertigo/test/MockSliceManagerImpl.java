package org.jini.projects.neon.vertigo.test;

import net.jini.config.Configuration;
import net.jini.core.lookup.ServiceID;

import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.vertigo.management.SliceManagerImpl;

/** 
 * Overrides key methods so that Unit testing does not require a full Neon instance to be running
 * 
 * @author calum
 *
 */
public class MockSliceManagerImpl extends SliceManagerImpl {
	
	private ServiceID hostingID;
	
	public MockSliceManagerImpl(Configuration config, ServiceID hostingID){
		super(config);
		this.hostingID = hostingID;
	}
	
  @Override
public ServiceID getHostingServiceID(AgentIdentity ag) {
	// TODO Auto-generated method stub
	return hostingID;
}
}
