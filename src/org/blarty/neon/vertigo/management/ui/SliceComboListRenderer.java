package org.jini.projects.neon.vertigo.management.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;

public class SliceComboListRenderer extends DefaultListCellRenderer {
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		// TODO Auto-generated method stub
		ImageIcon icon = null;
		setText((String) value);
		String val = (String) value;
		if (val.equals("Attractor"))

			icon = new ImageIcon(getClass().getResource("images/attractor.png"));
		if (val.equals("Repeller"))
			icon = new ImageIcon(getClass().getResource("images/repeller.png"));
		if (val.equals("Factory"))
			icon = new ImageIcon(getClass().getResource("images/factory.png"));
		if (val.equals("Redundant"))
			icon = new ImageIcon(getClass().getResource("images/redundant.png"));
		if (val.equals("Virtual"))
			icon = new ImageIcon(getClass().getResource("images/virtual.png"));
		if (val.equals("Default"))
			icon = new ImageIcon(getClass().getResource("images/default.png"));
		if (val.equals("Application"))
			icon = new ImageIcon(getClass().getResource("images/application.png"));
		setIcon(icon);
		setForeground(Color.BLACK);
		if (isSelected)

			setBackground(new Color(159, 218, 255));
		else
			setBackground(list.getBackground());
		return this;
	}
}
