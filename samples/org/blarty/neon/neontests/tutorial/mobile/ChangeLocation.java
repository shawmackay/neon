/*
 * neon : org.jini.projects.neon.neontests.tutorial.mobile
 * 
 * 
 * ChangeLocation.java
 * Created on 03-Aug-2005
 * 
 * ChangeLocation
 *
 */
package org.jini.projects.neon.neontests.tutorial.mobile;

import org.jini.projects.neon.collaboration.Collaborative;

import net.jini.core.lookup.ServiceID;

/**
 * @author calum
 */
public interface ChangeLocation extends Collaborative{
	public void doChangeLoc();
	public void doChanegLocTo(ServiceID id);
}
