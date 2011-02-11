package org.jini.projects.neon.vertigo.management.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public class SliceTreeDesigner extends JPanel {

	
	
	public SliceTreeDesigner(){
		initComponents();
	}
	
	public void initComponents(){	
		
		DefaultMutableTreeNode root = new javax.swing.tree.DefaultMutableTreeNode(new SliceData("App", SliceData.APPLICATION));
		
		javax.swing.tree.DefaultTreeModel model = new javax.swing.tree.DefaultTreeModel(root);
		DefaultMutableTreeNode dataNode = addChildNode(new SliceData("Data", SliceData.REPELLER),root);
		addChildNode(new SliceData("servers", SliceData.APPLICATION), dataNode);
		addChildNode(new SliceData("databases", SliceData.VIRTUAL), dataNode);
		addChildNode(new SliceData("webfront-end", SliceData.ATTRACTOR), root);
		addChildNode(new SliceData("Added", SliceData.FACTORY),addChildNode(new SliceData("backup", SliceData.REDUNDANT), root));
		
		JTree myTree = new JTree(model);
		myTree.setCellRenderer(new SliceTreeRenderer());
		setLayout(new BorderLayout());
		add(myTree, BorderLayout.CENTER);
	}
	
	private DefaultMutableTreeNode addChildNode(SliceData data,DefaultMutableTreeNode parent){
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(data);
		parent.add(node);
		return node;
	}
	
	public static void main(String[] args){
		JFrame jf = new JFrame("Sample Form");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setSize(200,400);
		jf.getContentPane().setLayout(new BorderLayout());
		jf.getContentPane().add(new SliceTreeDesigner(), BorderLayout.CENTER);
		jf.setVisible(true);
	}
}
