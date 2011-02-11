package org.jini.projects.neon.vertigo.management.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class SliceTreeRenderer extends DefaultTreeCellRenderer {

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		// TODO Auto-generated method stub
		ImageIcon icon = null;
		Object val = ((DefaultMutableTreeNode) value).getUserObject();
		if (val instanceof SliceData) {
			SliceData data = (SliceData) val;
			if (data.getType() == SliceData.APPLICATION)
				icon = new ImageIcon(getClass().getResource("images/application.png"));
			if (data.getType() == SliceData.ATTRACTOR)
				icon = new ImageIcon(getClass().getResource("images/attractor.png"));
			if (data.getType() == SliceData.REPELLER)
				icon = new ImageIcon(getClass().getResource("images/repeller.png"));
			if (data.getType() == SliceData.FACTORY)
				icon = new ImageIcon(getClass().getResource("images/factory.png"));
			if (data.getType() == SliceData.REDUNDANT)
				icon = new ImageIcon(getClass().getResource("images/redundant.png"));
			if (data.getType() == SliceData.VIRTUAL)
				icon = new ImageIcon(getClass().getResource("images/virtual.png"));
			if (data.getType() == SliceData.DEFAULT)
				icon = new ImageIcon(getClass().getResource("images/default.png"));
		
			setIcon(icon);
			setText(data.getName());
			if(sel){
				setOpaque(true);
				setForeground(Color.BLUE);
			} else{
				setOpaque(true);
			setForeground(Color.BLACK);
			}
			
			return this;
		} else {
			

			return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		}
	}

}
