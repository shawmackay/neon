/*
 * neon : org.jini.projects.neon.ui.support
 * 
 * 
 * AgentDescriptionPanel.java
 * Created on 11-Aug-2005
 * 
 * AgentDescriptionPanel
 *
 */

package org.jini.projects.neon.ui.support;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.rmi.server.RMIClassLoader;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import net.jini.id.UuidFactory;

import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.agents.AgentState;
import org.jini.projects.neon.dynproxy.ExportedAgentProxy;
import org.jini.projects.neon.service.ServiceAgentImpl;
import org.jini.projects.neon.service.admin.AgentDescription;

/**
 * @author calum
 */
public class AgentDescriptionPanel extends JPanel {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Complete method stub for main
		System.setProperty("swing.aatext", "true");
		JFrame jf = new JFrame();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.getContentPane().setLayout(new BorderLayout());
		AgentDescription desc = new AgentDescription("neon.Services", new AgentIdentity(), null, AgentState.AVAILABLE, AgentState.TRANSFER);
		ServiceAgentImpl svc = new ServiceAgentImpl();
		jf.getContentPane().add(new AgentDescriptionPanel(desc, svc), BorderLayout.CENTER);
		jf.setSize(600, 800);
		jf.setVisible(true);
		jf.pack();
	}

	AgentDescription desc;
	Object proxy;

	public AgentDescriptionPanel(AgentDescription desc, Object proxy) {
		this.desc = desc;
		this.proxy = proxy;
       // System.out.println("Proxy class annotation: "+ RMIClassLoader.getClassAnnotation(proxy.getClass()));
		init();
	}

	private void init() {
		setLayout(new GridBagLayout());
		GridBagConstraints gc;
		JLabel nameLabel = new JLabel("Name");
		JLabel nameData = new JLabel(desc.getName().substring(desc.getName().lastIndexOf(".") + 1));
		JLabel nameSpace = new JLabel("Namespace");
		JLabel nameSpaceData = new JLabel(desc.getName().substring(0, desc.getName().lastIndexOf(".")));
		JLabel idLabel = new JLabel("Identity");
		JLabel idData = new JLabel(desc.getIdentity().toString());
		JLabel extidLabel = new JLabel("Ext. Identity");
		JLabel extidData = new JLabel(desc.getIdentity().getExtendedString());
		JLabel stateLabel = new JLabel("Primary State");
		JLabel stateData = new JLabel(desc.getState().toString());

		JLabel secstateLabel = new JLabel("Secondary State");
		JLabel secstateData;
		if (desc.getSecondaryState() != null)
			secstateData = new JLabel(desc.getSecondaryState().toString());
		else
			secstateData = new JLabel("none");

		nameLabel.setFont(new Font("Dialog", Font.BOLD, 11));
		nameData.setFont(new Font("Dialog", 0, 11));
		nameSpace.setFont(new Font("Dialog", Font.BOLD, 11));
		nameSpaceData.setFont(new Font("Dialog", 0, 11));
		idLabel.setFont(new Font("Dialog", Font.BOLD, 11));
		idData.setFont(new Font("Dialog", 0, 11));
		extidLabel.setFont(new Font("Dialog", Font.BOLD, 11));
		extidData.setFont(new Font("Dialog", 0, 11));
		stateLabel.setFont(new Font("Dialog", Font.BOLD, 11));
		stateData.setFont(new Font("Dialog", 0, 11));

		secstateLabel.setFont(new Font("Dialog", Font.BOLD, 11));
		secstateData.setFont(new Font("Dialog", 0, 11));

		nameLabel.setForeground(Color.WHITE);
		nameData.setForeground(Color.WHITE);
		idLabel.setForeground(Color.WHITE);
		idData.setForeground(Color.WHITE);
		stateLabel.setForeground(Color.WHITE);
		stateData.setForeground(Color.WHITE);
		nameSpace.setForeground(Color.WHITE);
		nameSpaceData.setForeground(Color.WHITE);
		extidLabel.setForeground(Color.WHITE);
		extidData.setForeground(Color.WHITE);
		secstateLabel.setForeground(Color.WHITE);
		secstateData.setForeground(Color.WHITE);

		setBackground(new Color(64, 64, 64));
		JPanel mainPanel = new JPanel();
		JPanel infoPanel = new JPanel();
		infoPanel.setBackground(new Color(91,126, 175));
		infoPanel.setLayout(new GridBagLayout());
		infoPanel.setBorder(BorderFactory.createEtchedBorder());
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBackground(Color.WHITE);
		gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1.0;
		gc.weighty =1.0;
		gc.fill = GridBagConstraints.BOTH;
		gc.insets = new Insets(12, 12, 12, 12);
		add(mainPanel, gc);

		gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(6, 12, 3, 6);
		infoPanel.add(nameLabel, gc);
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.gridy = 0;
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(6, 6, 3, 12);
		gc.weightx = 1.0;
		infoPanel.add(nameData, gc);

		gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 1;
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(0, 12, 3, 6);
		infoPanel.add(nameSpace, gc);
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.gridy = 1;
		gc.insets = new Insets(0, 6, 3, 12);
		gc.weightx = 1.0;
		gc.anchor = GridBagConstraints.WEST;
		infoPanel.add(nameSpaceData, gc);

		gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 2;
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(0, 12, 3, 6);

		infoPanel.add(idLabel, gc);
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.gridy = 2;
		gc.insets = new Insets(0, 6, 6, 12);
		gc.weightx = 1.0;
		gc.anchor = GridBagConstraints.WEST;
		infoPanel.add(idData, gc);

		gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 3;
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(6, 12, 3, 6);
		infoPanel.add(extidLabel, gc);
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.gridy = 3;
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(6, 6, 3, 12);
		gc.weightx = 1.0;
		infoPanel.add(extidData, gc);

		gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 4;
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(0, 12, 3, 6);
		infoPanel.add(stateLabel, gc);
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.gridy = 4;
		gc.insets = new Insets(0, 6, 3, 12);
		gc.weightx = 1.0;
		gc.anchor = GridBagConstraints.WEST;
		infoPanel.add(stateData, gc);

		gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 5;
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(0, 12, 3, 6);

		infoPanel.add(secstateLabel, gc);
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.gridy = 5;
		gc.insets = new Insets(0, 6, 6, 12);
		gc.weightx = 1.0;
		gc.anchor = GridBagConstraints.WEST;
		infoPanel.add(secstateData, gc);

		gc = new GridBagConstraints();
		gc.gridx = 0;

		gc.gridwidth = 1;
		gc.gridy = 0;
		gc.insets = new Insets(6, 6, 6, 12);
		gc.fill = GridBagConstraints.BOTH;
		gc.weightx = 1.0;
		gc.weighty = 0.0;
		mainPanel.add(infoPanel, gc);

		JPanel classPanel = new JPanel();
		gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridwidth = 1;
		gc.gridy = 1;
		gc.insets = new Insets(6, 6, 6, 12);
		gc.fill = GridBagConstraints.BOTH;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		mainPanel.add(new JScrollPane(classPanel), gc);
		classPanel.setBackground(Color.WHITE);
		classPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		classPanel.setLayout(new GridBagLayout());
		Class[] intfs = proxy.getClass().getInterfaces();
		for (int i = 0; i < intfs.length; i++) {
			gc = new GridBagConstraints();
			gc.gridx = 0;
			gc.gridwidth = 2;
			gc.fill = GridBagConstraints.HORIZONTAL;
			gc.weightx = 1.0;
			// gc.weighty=1.0;
			//gc.ipady = 100;
			gc.insets = new Insets(12, 12, 12, 12);
			JPanel jp = buildMethodTable(intfs[i]);
			jp.setBorder(BorderFactory.createEtchedBorder());
			gc.gridy = GridBagConstraints.RELATIVE;
			classPanel.add(jp, gc);
		}
	}

	private JPanel buildMethodTable(Class cl) {
		JPanel methPanel = new JPanel();
		methPanel.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.BOTH;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1.0;
		StraightLabel classNameLabel = new StraightLabel(cl.getName(), new Color(169,218,225), new Color(54, 54, 90), new Color(159, 218, 255), new Color(91,126, 175), new Color(200,200 , 255, 100), new Color(0, 0, 0, 0), Color.WHITE, new Font("Dialog", Font.BOLD, 16));
		classNameLabel.setUseShadow(true);
		methPanel.add(classNameLabel,gc);
		JPanel methodPanel = new JPanel();
		methodPanel.setLayout(new GridBagLayout());
		Method[] meths = cl.getMethods();
		
		addMethodHeader("Name",0,methodPanel);
		addMethodHeader("Returning",1,methodPanel);
		addMethodHeader("Modifiers",2,methodPanel);
		addMethodHeader("Parameters",3,methodPanel);

		Color gradientBase = new Color(255, 255, 255);
		Color gradientTo =new Color(255, 255, 255,40);
		Color border = new Color(43, 103, 175, 0);
		Color borderTo = new Color(255,255, 255, 255);
		Color overlay = new Color(43, 103, 175, 25);
		Color overlayTo  =new Color(62, 99, 143, 40);
		Color textCol = Color.BLACK;
		Font labelFont = new Font("Dialog", 0, 10);
		int ypos = 2;
		for (int i = 0; i < meths.length; i++) {
			Method m = meths[i];
			gc = new GridBagConstraints();
			gc.fill = GridBagConstraints.BOTH;
			gc.gridx = 0;
			gc.gridy = ypos;
			gc.weightx = 1.0;
			gc.anchor= GridBagConstraints.CENTER;
			StraightLabel l = new StraightLabel(m.getName(), border, borderTo, gradientBase, gradientTo, overlay, overlayTo, textCol, labelFont);
			methodPanel.add(l, gc);
			
			
			gc = new GridBagConstraints();
			gc.fill = GridBagConstraints.BOTH;
			gc.gridx = 1;
			gc.gridy = ypos;
			gc.weightx = 1.0;
			gc.anchor= GridBagConstraints.CENTER;
			l = new StraightLabel(m.getReturnType().getName(), border, borderTo, gradientBase, gradientTo, overlay, overlayTo, textCol, labelFont);
			methodPanel.add(l, gc);
			
			gc = new GridBagConstraints();
			gc.fill = GridBagConstraints.BOTH;
			gc.gridx = 2;
			gc.gridy = ypos;
			gc.weightx = 1.0;
			gc.anchor= GridBagConstraints.CENTER;
			l = new StraightLabel(Modifier.toString(m.getModifiers()),border, borderTo, gradientBase, gradientTo, overlay, overlayTo, textCol, labelFont);
			methodPanel.add(l, gc);
			
			gc = new GridBagConstraints();
			gc.fill = GridBagConstraints.HORIZONTAL;
			
			gc.gridx = 3;
			gc.gridy = ypos;
			gc.weightx = 1.0;
			Class[] params = m.getParameterTypes();
			JPanel paramPanel = new JPanel();
			paramPanel.setLayout(new GridLayout(0,1));
			for (int j = 0; j < params.length; j++) {
				l = new StraightLabel(params[j].getName(),border, borderTo, gradientBase, gradientTo, overlay, overlayTo, textCol, labelFont);
				paramPanel.add(l);
			}
			
			methodPanel.add(paramPanel, gc);
			ypos++;
		}
		gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.gridx = 0;
		gc.gridy = 1;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		methPanel.add(methodPanel, gc);
		return methPanel;
	}

	/**
	 * @param methodPanel
	 */
	private void addMethodHeader(String label, int xpos,JPanel methodPanel) {
		GridBagConstraints gc;
		StraightLabel l;
		gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.gridx = xpos;
		gc.gridy = 1;
		gc.anchor= GridBagConstraints.CENTER;
		gc.weightx = 1.0;			
		l = new StraightLabel(label, new Color(169,218,225), new Color(54, 54, 90), new Color(159, 218, 255), new Color(91,126, 175), new Color(200,200 , 255, 100), new Color(0, 0, 0, 0), Color.WHITE, new Font("Dialog", Font.BOLD, 12));
		methodPanel.add(l, gc);
	}

}
