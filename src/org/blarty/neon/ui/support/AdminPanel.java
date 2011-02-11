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
 * neon : org.jini.projects.neon.ui.support
 * AdminPanel.java
 * Created on 19-Jul-2004
 *AdminPanel
 */
package org.jini.projects.neon.ui.support;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EtchedBorder;


/**
 * @author calum
 */
public class AdminPanel extends JPanel{
	
	
	public static void main(String[] args){
	 JFrame fr = new JFrame("New Frame");
	 fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 fr.setSize(600,400);
	 AdminPanel ap = new AdminPanel();
	 
	 
	 fr.getContentPane().add(ap);	
	 
	ap.add(new JButton("Hello"), BorderLayout.WEST);
	ap.add(new JButton("There"), BorderLayout.WEST);
	ap.add(new JButton("Test"), BorderLayout.WEST);
	ap.add(new JButton("Add"), BorderLayout.WEST);
	 fr.setVisible(true);
	}
	JLabel sysLabel = new javax.swing.JLabel("sadasd");
	JPanel im = new JPanel();
	JPanel btnPanel ;
	public AdminPanel() {
		im.setBackground(Color.WHITE);
		 im.setSize(new Dimension(200,400));
		setLayout(new BorderLayout());
		btnPanel = new JPanel();
		add(sysLabel, BorderLayout.NORTH);
		add(im, BorderLayout.CENTER);
		add(btnPanel, BorderLayout.EAST);
		}
	
	/* @see java.awt.Container#add(java.awt.Component)
	 */
	public Component add(Component comp) {
		// TODO Complete method stub for add
		
		return super.add(comp);
	}
	/* @see java.awt.Container#add(java.awt.Component, int)
	 */
	public Component add(final Component comp, int index) {
		// TODO Complete method stub for add
		if(comp instanceof JButton){
			((JButton) comp).addActionListener(new ActionListener(){
				/* @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				public void actionPerformed(ActionEvent e) {
					// TODO Complete method stub for actionPerformed
					
					
					im.removeAll();
					JLabel l = new JLabel();
					l.setFont(new Font("Dialog", Font.BOLD, 16));
					l.setText(((JButton)comp).getText());
					l.setForeground(Color.WHITE);
					l.setOpaque(true);
					im.setLayout(new BorderLayout());
					im.add(l);
				}
			});
		}
		return btnPanel.add(comp, index);
	}
	/* @see java.awt.Container#add(java.awt.Component, java.lang.Object, int)
	 */
	public void add(Component comp, Object constraints, int index) {
		// TODO Complete method stub for add
		super.add(comp, constraints, index);
	}
	/* @see java.awt.Container#add(java.awt.Component, java.lang.Object)
	 */
	public void add(final Component comp, Object constraints) {
//		 TODO Complete method stub for add
		if(comp instanceof JButton){
			((JButton) comp).addActionListener(new ActionListener(){
				/* @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				public void actionPerformed(ActionEvent e) {
					// TODO Complete method stub for actionPerformed
					
					
					im.removeAll();
					SkewLabel l = new SkewLabel();
					l.setFont(new Font("Dialog", Font.BOLD, 24));
                                        l.setMinimumSize(new Dimension(24,l.getFontMetrics(l.getFont()).getHeight()+16));
                                        l.setPreferredSize(new Dimension(24,l.getFontMetrics(l.getFont()).getHeight()+16));
					l.setText(((JButton)comp).getText());
                                        l.setGradientBaseColor(Color.RED);
                                        l.setBorderColor(Color.LIGHT_GRAY);
					l.setForeground(Color.WHITE);
					l.setOpaque(false);
					im.setLayout(new BorderLayout());
					im.add(l, BorderLayout.NORTH);
					JLabel spacer = new JLabel("   ");
					im.add(spacer, BorderLayout.WEST);
					JTable table = new JTable(6,2);
					table.setOpaque(false);
					im.add(table, BorderLayout.CENTER);
					im.updateUI();
				}
			});
			btnPanel.add(comp, constraints);
		}
		else
			super.add(comp, constraints);
		
	}
	
	
	
        
        
	/* @see java.awt.Container#add(java.lang.String, java.awt.Component)
	 */
	public Component add(String name, Component comp) {
		// TODO Complete method stub for add
		return super.add(name, comp);
	}
}
