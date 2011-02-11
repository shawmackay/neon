/*
 * neon : org.jini.projects.neon.neontests.tutorial.mobile
 * 
 * 
 * MobilityDialog.java
 * Created on 03-Aug-2005
 * 
 * MobilityDialog
 *
 */
package org.jini.projects.neon.neontests.tutorial.mobile;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.jini.projects.neon.host.AgentContext;
import org.jini.projects.neon.host.NoSuchAgentException;


/**
 * @author calum
 */
public class MobilityDialog extends JDialog {

	private AgentContext context;
	
	public MobilityDialog(AgentContext context) throws HeadlessException {
		super();
		this.context = context;
		init();
		// TODO Complete constructor stub for MobilityDialog		
	}
	
	public void init(){
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(new javax.swing.JLabel("Click Button to move other Agent"), BorderLayout.NORTH);
		JButton jb = new JButton("Click to move called agent");
		jb.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				// TODO Complete method stub for actionPerformed
				try {
					ChangeLocation loc = (ChangeLocation) context.getAgent("neon.samples.mobile.ChangeLocation");
					loc.doChangeLoc();
				} catch (NoSuchAgentException e1) {
					// TODO Handle NoSuchAgentException
					e1.printStackTrace();
				}
			}
		});
		mainPanel.add(jb);
		getContentPane().add(mainPanel);
	}
 
}
