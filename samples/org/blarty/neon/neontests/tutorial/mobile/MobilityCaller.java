/*
 * neon : org.jini.projects.neon.neontests.tutorial.mobile
 * 
 * 
 * MobilityCaller.java
 * Created on 03-Aug-2005
 * 
 * MobilityCaller
 *
 */
package org.jini.projects.neon.neontests.tutorial.mobile;

import org.jini.projects.neon.agents.AbstractAgent;

/**
 * @author calum
 */
public class MobilityCaller extends AbstractAgent implements Runnable{

	@Override
	public boolean init() {
		// TODO Complete method stub for init
		return true;
	}

	
	public void run() {
		// TODO Complete method stub for start
		MobilityDialog dialog = new MobilityDialog(getAgentContext());
		dialog.pack();
		dialog.setVisible(true);
	}

}
