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
 *  AthenaUIFact.java
 *
 *  Created on 03 October 2001, 12:04
 */

package org.jini.projects.neon.ui;

import java.awt.Dimension;

import javax.swing.JFrame;

import net.jini.core.lookup.ServiceItem;

import org.jini.projects.neon.service.AgentService;

/**
 * @author calum
 */
public class ConsoleUIFact implements net.jini.lookup.ui.factory.JFrameFactory, java.io.Serializable {

	/**
	 * Creates new Neon UI ConsoleFact
	 * 
	 * @since
	 */
	public ConsoleUIFact() {
	}

	/**
	 * Returns a <CODE>JFrame</CODE> containing the Console for this Neon instance.
	 * 
	 *
	 * @return The Neon console
	 * @since
	 */
	public JFrame getJFrame(Object roleObject) {
		ServiceItem svItem = (ServiceItem) roleObject;

		NeonAdminFrame adminFr = new NeonAdminFrame((AgentService) svItem.service);
		adminFr.setSize(new Dimension(800,600));
		return adminFr;

	}

}
