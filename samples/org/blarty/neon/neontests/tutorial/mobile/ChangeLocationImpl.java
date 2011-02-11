/*
 * neon : org.jini.projects.neon.neontests.tutorial.mobile
 * 
 * 
 * ChangeLocationImpl.java
 * Created on 03-Aug-2005
 * 
 * ChangeLocationImpl
 *
 */
package org.jini.projects.neon.neontests.tutorial.mobile;

import org.jini.projects.neon.agents.AbstractAgent;

import net.jini.core.lookup.ServiceID;

/**
 * @author calum
 */
public class ChangeLocationImpl extends AbstractAgent implements ChangeLocation{

	public ChangeLocationImpl(){
		this.name = "ChangeLocation";
		this.namespace="neon.samples.mobile";
	}
	
	@Override
	public boolean init() {
		// TODO Complete method stub for init
		return true;
	}



	public void doChanegLocTo(ServiceID id) {
		// TODO Complete method stub for doChanegLocTo
		getAgentLogger().info("Changing Location to: " + id);
	}

	public void doChangeLoc() {
		// TODO Complete method stub for doChangeLoc
		getAgentLogger().info("Changing Location to random service");
	}

}
