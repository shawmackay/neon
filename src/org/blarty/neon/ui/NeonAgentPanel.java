/*
 * neon : org.jini.projects.neon.ui
 * 
 * 
 * NeonAgentPanel.java
 * Created on 08-Aug-2005
 * 
 * NeonAgentPanel
 *
 */

package org.jini.projects.neon.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.jini.projects.neon.service.AgentService;
import org.jini.projects.neon.service.ServiceAgentImpl;
import org.jini.projects.neon.service.TransferAgent;
import org.jini.projects.neon.service.admin.AgentAdmin;
import org.jini.projects.neon.service.admin.AgentDescription;
import org.jini.projects.neon.service.admin.DomainDescription;
import org.jini.projects.neon.ui.support.AgentDescriptionPanel;
import org.jini.projects.neon.ui.support.AgentTreeModel;
import org.jini.projects.neon.ui.support.StraightLabel;
import org.jini.projects.neon.ui.support.AgentTreeModel.NamedMap;

/**
 * @author calum
 */
public class NeonAgentPanel extends JPanel {
	private DomainDescription[] descs;
	private AgentService svc;
	private String domainName;
	private JDesktopPane desktop;
	private JPopupMenu popup;
	private JTree tree;
	private AgentTreeModel theModel;
	private JScrollPane treeScroller;

	class AgentTreeRenderer extends DefaultTreeCellRenderer {
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			// TODO Complete method stub for getTreeCellRendererComponent
			JLabel c = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			if (sel)
				c.setBackground(new Color(159, 218, 255));
			else
				c.setBackground(tree.getBackground());
			c.setOpaque(true);
			return c;
		}
	}

	public NeonAgentPanel(String domain, DomainDescription[] domainDescs, AgentService asvc, JDesktopPane frame) {
		this.descs = domainDescs;
		this.svc = asvc;
		this.domainName = domain;
		this.desktop = frame;
		this.setLayout(new GridBagLayout());
		try {
			theModel = new AgentTreeModel((AgentAdmin) asvc.getAdmin());
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		tree = new JTree(theModel);
		tree.setBackground(Color.WHITE);
		tree.setForeground(Color.BLACK);

		tree.setCellRenderer(new AgentTreeRenderer());
		GridBagConstraints gc;
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				// TODO Complete method stub for valueChanged
				if (e.isAddedPath()) {

				}
			}
		});

		popup = new JPopupMenu();
		JMenuItem agentInfo = new JMenuItem("Info");
		agentInfo.setFont(agentInfo.getFont().deriveFont(Font.BOLD));
		JMenuItem agentMove = new JMenuItem("Change Domain");
		agentMove.setFont(agentInfo.getFont().deriveFont(Font.PLAIN));
		JMenuItem agentTransfer = new JMenuItem("Move Host");
		agentTransfer.setFont(agentInfo.getFont().deriveFont(Font.PLAIN));
		JMenuItem agentDelete = new JMenuItem("Kill");
		agentDelete.setFont(agentInfo.getFont().deriveFont(Font.PLAIN));
		JMenuItem agentSleep = new JMenuItem("Hibernate");
		agentSleep.setFont(agentInfo.getFont().deriveFont(Font.PLAIN));
		popup.add(agentInfo);
		popup.add(agentMove);
		popup.add(agentTransfer);
		popup.add(agentDelete);
		popup.add(agentSleep);

		agentInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				handleAgentInfo();
			}
		});

		agentMove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				handleChangeDomain();
				
				
			}
		});

		agentTransfer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				handleTransferAgent();
				theModel.updateDescriptions();
			}
		});

		agentDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				handleKillAgent();
				theModel.updateDescriptions();
			}
		});

		agentSleep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				handleHibernateAgent();
			}
		});
		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mousePressed(e);
				int selRow = tree.getRowForLocation(e.getX(), e.getY());
				if (selRow < 0)
					return;

				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				tree.setSelectionPath(selPath);

				// TODO: do task with the selected path

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mousePressed(e);
				TreePath p = tree.getSelectionPath();

				if (p != null && p.getLastPathComponent() instanceof AgentDescription)
					if (SwingUtilities.isRightMouseButton(e)) {
						popup.show(tree, e.getX(), e.getY());
					}

			}
		});

		gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = 2;
		gc.fill = GridBagConstraints.HORIZONTAL;
		JLabel iconLabel = new JLabel(new ImageIcon(getClass().getResource("images/neonemboss_small_red.jpg")));
		iconLabel.setBackground(Color.BLACK);
		iconLabel.setOpaque(true);
		iconLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
		add(iconLabel, gc);
		gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 1;
		gc.gridwidth = 2;
		gc.fill = GridBagConstraints.HORIZONTAL;
		StraightLabel nameLabel = new StraightLabel("Agents in " + domainName, new Color(169, 218, 225), new Color(54, 54, 90), new Color(159, 218, 255), new Color(91, 126, 175), new Color(200, 200, 255, 100), new Color(0, 0, 0, 0), Color.WHITE,
				new Font("Dialog", 0, 24));
		nameLabel.setUseShadow(true);
		// nameLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
		add(nameLabel, gc);
		setBorder(BorderFactory.createEtchedBorder());
		gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = GridBagConstraints.RELATIVE;
		gc.gridwidth = 1;
		gc.fill = GridBagConstraints.BOTH;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		treeScroller = new JScrollPane(tree);
		add(treeScroller, gc);

		// for (int i = 0; i < descs.length; i++) {
		// JPanel agPanel = new JPanel();
		// agPanel.setLayout(new GridBagLayout());
		// GridBagConstraints gc;
		//
		// gc = new GridBagConstraints();
		// gc.gridx = 0;
		// gc.gridy = 0;
		// gc.gridwidth = 2;
		// JLabel nameLabel = new JLabel(descs[i].getName());
		// nameLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
		// agPanel.add(nameLabel, gc);
		// agPanel.setBorder(BorderFactory.createEtchedBorder());
		// gc = new GridBagConstraints();
		// gc.gridx = 0;
		// gc.gridy = GridBagConstraints.RELATIVE;
		// gc.gridwidth = 1;
		// gc.fill = GridBagConstraints.BOTH;
		// add(agPanel, gc);
		// }
	}

	protected void handleHibernateAgent() {
		// TODO Auto-generated method stub

	}

	protected void handleKillAgent() {
		JOptionPane.showConfirmDialog(null, "<html>Do you really want to kill this agent and<br>destroy any data that it contains?</html>", "Confirm Agent Removal", JOptionPane.YES_NO_OPTION);

	}

	protected void handleTransferAgent() {
		// TODO Auto-generated method stub

	}

	protected void handleChangeDomain() {
	 String[] domains = new String[descs.length-1];
	NamedMap domainMap= (NamedMap) tree.getSelectionPath().getPathComponent(1);
	String domainName = domainMap.toString(); 
	System.out.println("Domain Name: " + domainName);
	int j=0;
	 for(int i=0;i<descs.length;i++)
		 if(!descs[i].getName().equals(domainName))
			 domains[j++] = descs[i].getName();
	 
	String newDomain  = (String) JOptionPane.showInputDialog(null, "Choose New Domain for Agent", "Move Agent", JOptionPane.QUESTION_MESSAGE,null,domains , domains[0]);
		try {
			TransferAgent trans = (TransferAgent) svc.getStatelessAgent("neon.Transfer", domainName.toString());
			
			AgentDescription desc = (AgentDescription) tree.getSelectionPath().getLastPathComponent();
			
			trans.moveDomain(desc.getIdentity(), newDomain);
 theModel.updateDescriptions(tree.getSelectionPath());
 SwingUtilities.invokeLater(new Runnable(){
	 public void run() {
		// TODO Auto-generated method stub
		
		 synchronize();
	}
 });
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	  public void synchronize() 
	  { 
	    Enumeration<TreePath> expandedPaths = tree. getExpandedDescendants(tree.getPathForRow(0)); 
	    TreePath[] selectedPaths = tree.getSelectionPaths(); 
	    theModel.synchronize(); 
	    tree.updateUI();
	    while (expandedPaths.hasMoreElements()) 
	      tree.expandPath(expandedPaths.nextElement()); 
	    tree.setSelectionPaths(selectedPaths); 
	  } 

	

	private void handleAgentInfo() {
		TreePath p = tree.getSelectionPath();
		if (p != null && p.getLastPathComponent() instanceof AgentDescription) {
			Object[] o = p.getPath();
			AgentDescription desc = (AgentDescription) p.getLastPathComponent();
			StringBuffer namespace = new StringBuffer();
			int i = 1;
			String domainToLookIn = (String) o[1].toString();
			for (i = 2; i < o.length - 2; i++)
				namespace.append(o[i].toString() + ".");
			namespace.append(o[i].toString());
			try {
				//Object agent = new ServiceAgentImpl();//
				
				System.out.println("Getting namespace: " + namespace.toString() + " from Domain: " + domainToLookIn );
				Object agent =  svc.getStatelessAgent(namespace.toString(),domainToLookIn);

				JInternalFrame jif = new JInternalFrame("Agent: " + namespace.toString() + ": " + desc.getIdentity(), true, true, true, true);
				jif.getContentPane().setLayout(new BorderLayout());
				jif.getContentPane().add(new AgentDescriptionPanel(desc, agent), BorderLayout.CENTER);
				jif.pack();
				jif.setSize(400, 600);
				jif.setVisible(true);

				desktop.add(jif);
				jif.moveToFront();
			} catch (Exception e1) {
				// TODO Handle RemoteException
				e1.printStackTrace();
			}
		}
	}
}
