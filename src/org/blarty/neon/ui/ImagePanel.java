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


package org.jini.projects.neon.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author calum
 *Callisto
 */
public class ImagePanel extends JPanel {

	int imx = 0;
	int imy = 0;
	double scale = 1.0;
	int anchor = NONE;
	ImageIcon ic = null;
	boolean allOpaque = true;
	public ImagePanel() {
		super();
	}

	public static final int NONE = 0;
	public static final int CENTER = 1;
	public static final int NORTH = 2;
	public static final int NORTHEAST = 3;
	public static final int EAST = 4;
	public static final int SOUTHEAST = 5;
	public static final int SOUTH = 6;
	public static final int SOUTHWEST = 7;
	public static final int WEST = 8;
	public static final int NORTHWEST = 9;

	public ImagePanel(ImageIcon ic) {
		super();
		this.ic = ic;
	}

	public ImagePanel(ImageIcon ic, int x, int y, double scale) {
		super();
		imx = x;
		imy = y;
		this.scale = scale;

		this.ic = ic;
	}

	public ImagePanel(ImageIcon ic, int x, int y, double scale, int anchor) {
		super();
		imx = x;
		imy = y;
		this.scale = scale;
		this.anchor = anchor;
		this.ic = ic;
	}

	/**
	 * @see java.awt.Component#print(Graphics)
	 */
	public void paint(Graphics g) {
		double aspectx = (double) getWidth() / (double) ic.getIconWidth();
		double aspecty = (double) getHeight() / (double) ic.getIconHeight();
		aspectx *= scale;
		aspecty *= scale;
		Graphics2D g2 = (Graphics2D) g;
		int dx = imx;
		int dy = imy;

		int dwid = 0;
		int dhigh = 0;
		if (aspectx > aspecty) {
			dwid = (int) (ic.getIconWidth() * aspecty);
			dhigh = (int) (ic.getIconHeight() * aspecty);
		} else {
			dwid = (int) (ic.getIconWidth() * aspectx);
			dhigh = (int) (ic.getIconHeight() * aspectx);

		}

		switch (anchor) {
			case CENTER :
				dx = this.getWidth() / 2 - dwid/2 + imx;
				dy = this.getHeight() / 2 - dhigh/2 + imy;
				break;
			case WEST :
				dx = imx;
				dy = this.getHeight() / 2 - dhigh/2+ imy;
				break;
			case EAST :
				dx = this.getWidth() - dwid - imx;
				dy = this.getHeight() / 2 -dhigh/2 - imy;
				break;
			case NORTH :
				dx = this.getWidth()/2 - dwid/2 + imx;
				dy = imy;
				break;
			case SOUTH :
				dx = this.getWidth() / 2 - dwid/2 + imx;
				dy = this.getHeight() -dhigh - imy;
				break;
			case NORTHEAST :
				dx = getWidth() - dwid - imx;
				dy = imy;
				break;
			case SOUTHEAST :
				dx = this.getWidth() - dwid - imx;
				dy = this.getHeight() - dhigh - imy;
				break;
			case SOUTHWEST :
				dx = imx;
				dy = this.getHeight() - dhigh - imy;
				break;
		}

	
				g2.drawImage(ic.getImage(), dx, dy, dwid, dhigh, null);
		if (this.anchor!=ImagePanel.NONE){	
		GradientPaint pnt = new GradientPaint(0.0f, 0.0f, Color.WHITE, (float) getWidth(), (float) getHeight(), new Color(255, 255, 255, 1), true);

		g2.setPaint(pnt);
		g2.fillRect(0, 0, getWidth(), getHeight());
		}
		setOpaque(false);
		super.paint(g);
	}
	public static void main(String[] args) {
		JFrame frame = new JFrame("Test");
		frame.addWindowListener(new WindowAdapter() { /**
										 * @see java.awt.event.WindowAdapter#windowClosing(WindowEvent)
										 */
			public void windowClosing(WindowEvent e) {
				System.exit(0);

			}

			/**
			 * @see java.awt.event.WindowListener#windowClosed(WindowEvent)
			 */
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.getContentPane().setLayout(new BorderLayout());
		ImagePanel pan = new ImagePanel(new ImageIcon("/home/calum/images/content2.jpg"), 0, 0, 0.5, ImagePanel.NONE);
		pan.setLayout(new BorderLayout());
		JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayout(2, 2));
		pan.setAllComponentsOpaque(false);
		panel2.setOpaque(false);
		pan.add(panel2, BorderLayout.CENTER);
		panel2.add(new JLabel("Name"));
		JTextField thru = new JTextField("");
		
		thru.setOpaque(false);
		JTextField thru2 = new JTextField("");
		thru2.setOpaque(false);
		panel2.add(thru);
		panel2.add(new JLabel("Email Address"));
		panel2.add(thru2);
		frame.getContentPane().add(pan, BorderLayout.CENTER);
		frame.setSize(600, 600);
		frame.show();
	}

	public void setAllComponentsOpaque(boolean yesno) {
		allOpaque = yesno;
	}

	private void changeOpacity(Component comp) {
		if (comp instanceof JComponent) {
			if(!allOpaque)
			((JComponent) comp).setOpaque(allOpaque);
		}
	}

	/**
	 * @see java.awt.Container#add(Component, int)
	 */
	public Component add(Component comp, int index) {
		changeOpacity(comp);
		return super.add(comp, index);
	}

	/**
	 * @see java.awt.Container#add(Component, Object, int)
	 */
	public void add(Component comp, Object constraints, int index) {
		changeOpacity(comp);
		super.add(comp, constraints, index);
	}

	/**
	 * @see java.awt.Container#add(Component, Object)
	 */
	public void add(Component comp, Object constraints) {
		changeOpacity(comp);
		super.add(comp, constraints);
	}

	/**
	 * @see java.awt.Container#add(Component)
	 */
	public Component add(Component comp) {
		changeOpacity(comp);
		return super.add(comp);
	}

	/**
	 * @see java.awt.Container#add(String, Component)
	 */
	public Component add(String name, Component comp) {
		changeOpacity(comp);
		return super.add(name, comp);
	}

}