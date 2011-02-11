/*
 * neon : org.jini.projects.neon.ui
 * 
 * 
 * NeonServicePanel.java
 * Created on 08-Aug-2005
 * 
 * NeonServicePanel
 *
 */
package org.jini.projects.neon.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.jini.projects.neon.service.AgentService;
import org.jini.projects.neon.service.admin.AgentDescription;
import org.jini.projects.neon.service.admin.AgentServiceAdmin;
import org.jini.projects.neon.service.admin.DomainDescription;
import org.jini.projects.neon.ui.support.AgentTreeModel;
import org.jini.projects.neon.ui.support.SkewLabel;
import org.jini.projects.neon.ui.support.StraightLabel;

/**
 * @author calum
 */
public class NeonServicePanel extends JPanel{
	private JDesktopPane desktop;
	
	public static void main(String[] args){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		JFrame testFrame = new JFrame();
		testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		testFrame.setLayout(new BorderLayout());
		testFrame.setSize(300,300);
		JDesktopPane jdf = new JDesktopPane();
		JDialog jdlog = new JDialog();
		jdlog.setSize(800,800);
		jdlog.getContentPane().add(jdf);
		jdlog.setVisible(true);
		try {
			testFrame.add(new NeonServicePanel(new MockAgentService(), jdf), BorderLayout.CENTER);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		testFrame.setVisible(true);
	}
	
	public NeonServicePanel(AgentService svc, JDesktopPane frame)throws RemoteException{
		init();
		this.desktop = frame;
		System.out.println("NeonServicePanel: svcs==null" + (svc==null));
		showDomains(svc);
	}
	
	public void init(){
		setLayout(new GridLayout());
		
	}
	
	public void showDomains(final AgentService svc) throws RemoteException{
		 AgentServiceAdmin adm = (AgentServiceAdmin) svc.getAdmin();
		 setLayout(new BorderLayout());
		 final DomainDescription[] domains = adm.getDomains();
		 JPanel jp = new JPanel();
		 JPanel domainPanel = new JPanel();
		 jp.setLayout(new BorderLayout());
		 domainPanel.setLayout(new GridBagLayout());
		 StraightLabel lbl = new StraightLabel("Partitions",new Color(169,218,225), new Color(54, 54, 90), new Color(159, 218, 255), new Color(91,126, 175), new Color(200,200 , 255, 100), new Color(0, 0, 0, 0), Color.WHITE, new Font("Dialog", 0, 24));
		lbl.setUseShadow(true);
		 GridBagConstraints lblGC = new GridBagConstraints();
		 lblGC.gridwidth = GridBagConstraints.REMAINDER;
		 lblGC.fill = GridBagConstraints.HORIZONTAL;
		 lblGC.ipady=24;
		 jp.add(lbl, BorderLayout.NORTH);
		
		 for(int i=0;i<domains.length;i++){
			 JButton domLabel = new JButton();
			 domLabel.setText(domains[i].getName());
			 //domLabel.setGradientBaseColor(Color.GREEN);
			 final AgentDescription[] agentDesc = domains[i].getAgents();
			 
			 final String name = domains[i].getName();
			 domLabel.addActionListener(new ActionListener(){
				 public void actionPerformed(ActionEvent e) {
					// TODO Complete method stub for actionPerformed
					JInternalFrame agentFrame = new JInternalFrame(name,true,true,true,true);
					agentFrame.setLayout(new BorderLayout());
					agentFrame.add(new NeonAgentPanel(name,domains,svc,desktop), BorderLayout.CENTER);
					agentFrame.pack();
					agentFrame.setVisible(true);
					
					desktop.add(agentFrame);
					agentFrame.moveToFront();
				}
			 });
			 GridBagConstraints gc = new GridBagConstraints();
			 gc.gridx=0;
			 gc.gridy=GridBagConstraints.RELATIVE;
			 gc.ipadx=6;
			
			 gc.fill = GridBagConstraints.HORIZONTAL;
			 gc.insets= new Insets(3,6,3,6);
			 gc.weightx=1.0;
			 domainPanel.add(domLabel, gc);
		 }
		 JLabel neonLabel = new JLabel(new ImageIcon(this.getClass().getResource("images/neonemboss_small_green.jpg")));
		 neonLabel.setBackground(Color.BLACK);
		 neonLabel.setOpaque(true);
		 add(neonLabel, BorderLayout.NORTH);
		 jp.add(domainPanel, BorderLayout.CENTER);
		 add(jp,BorderLayout.CENTER);
	}
}
