/*
 * neon : org.jini.projects.neon.ui
 * 
 * 
 * TestHarness.java
 * Created on 08-Aug-2005
 * 
 * TestHarness
 *
 */

package org.jini.projects.neon.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jini.projects.neon.service.AgentService;

import com.sun.jini.resource.Service;

import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceMatches;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;
import net.jini.lookup.ServiceDiscoveryManager;

/**
 * @author calum
 */
public class TestHarness implements DiscoveryListener {

	public static void main(String[] args) {
		System.setProperty("swing.aatext", "true");
		System.setSecurityManager(new RMISecurityManager());
		new TestHarness();
	}

	LookupDiscoveryManager ldm;
	private JDesktopPane desktop;

	public TestHarness() {
		try {
			ldm = new LookupDiscoveryManager(new String[]{"uk.co.cwa.jini2"}, null, this);
			try {
				synchronized (this) {
					wait(0);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			ldm.terminate();
			System.exit(0);
		} catch (IOException e) {
			// TODO Handle IOException
			e.printStackTrace();
		}
	}

	public void discarded(DiscoveryEvent e) {
		// TODO Complete method stub for discarded

	}

	public void discovered(DiscoveryEvent e) {
		// TODO Complete method stub for discovered
		ServiceRegistrar[] regs = e.getRegistrars();
		try {
			ServiceMatches matches = regs[0].lookup(new ServiceTemplate(null, new Class[]{AgentService.class}, null), 10);
			JDialog jf = new JDialog();
			jf.getContentPane().setLayout(new BorderLayout());
			desktop = new JDesktopPane();
			desktop.setBackground(Color.BLACK);
			jf.getContentPane().add(desktop, BorderLayout.CENTER);
			jf.setSize(500, 500);

			if (matches != null) {

				JPanel svcPanel = new JPanel();
				svcPanel.setLayout(new GridBagLayout());
				JInternalFrame ifr = new JInternalFrame("Services", true, false, true, true);
				ifr.getContentPane().setLayout(new BorderLayout());
				ifr.setVisible(true);
				if (matches != null)
					for (int i = 0; i < matches.items.length; i++) {
						AgentService svc = (AgentService) matches.items[i].service;
						ServiceID sid = matches.items[i].serviceID;
						if (svc != null) {
							GridBagConstraints gc = new GridBagConstraints();
							gc.gridy = GridBagConstraints.RELATIVE;
							gc.fill = GridBagConstraints.HORIZONTAL;
							gc.insets = new Insets(3, 6, 3, 6);
							gc.gridx = 0;
							JButton button = new JButton(sid.toString());
							button.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									// TODO Complete method stub for
									// actionPerformed
									String sid = ((JButton)e.getSource()).getText();
									Uuid uid = UuidFactory.create(sid);
									System.out.println(uid);
									ServiceID SID = new ServiceID(uid.getMostSignificantBits(),uid.getLeastSignificantBits());
									System.out.println(SID);
									try {
										AgentService svc = (AgentService)ldm.getRegistrars()[0].lookup(new ServiceTemplate(SID,null,null));
										JInternalFrame domFrame = new JInternalFrame("Domains in node: " + sid,true,true,true,true);
										desktop.add(domFrame);
										domFrame.setVisible(true);
										domFrame.add(new NeonServicePanel(svc, desktop));
										domFrame.pack();
										
									} catch (RemoteException e1) {
										// TODO Handle RemoteException
										e1.printStackTrace();
									}
								}
							});
							svcPanel.add(button, gc);

						}
					}
				ifr.getContentPane().add(svcPanel);

				ifr.setSize(200, 200);
				desktop.add(ifr);

				ifr.pack();
				ifr.setVisible(true);
				jf.setModal(true);
				jf.setVisible(true);
			}
		} catch (HeadlessException e1) {
			// TODO Handle HeadlessException
			e1.printStackTrace();
		} catch (RemoteException e1) {
			// TODO Handle RemoteException
			e1.printStackTrace();
		}
		synchronized (this) {
			notify();
		}
	}
}
