/*
 * neon : org.jini.projects.neon.ui.support
 * 
 * 
 * SkewLabel.java
 * Created on 08-Aug-2005
 * 
 * SkewLabel
 *
 */

package org.jini.projects.neon.ui.support;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

import javax.swing.JLabel;

/**
 * @author calum
 */
public class SkewLabel extends JLabel {

	private boolean extended = false;
	/*
	 * @see java.awt.Component#paint(java.awt.Graphics)
	 */

	private Color borderColor = Color.RED;
	private Color gradientBaseColor = Color.GRAY;
	private Color internalGridColor = Color.YELLOW;

	public SkewLabel() {
		setFont(new Font("Dialog", Font.BOLD, 24));
		setMinimumSize(new Dimension(24, getFontMetrics(getFont()).getHeight() + 16));
		setPreferredSize(new Dimension(24, getFontMetrics(getFont()).getHeight() + 16));
		
	}

	public void paint(Graphics g) {
		// TODO Complete method stub for paint
		this.getBounds();
		Graphics2D g2 = (Graphics2D) g;
		g.setColor(Color.WHITE);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		GradientPaint p = new GradientPaint(0, 0, this.gradientBaseColor, 0, (this.getHeight() / 10 * 6), new Color(0, 0, 0, 255), true);
		GradientPaint p3 = new GradientPaint(0, 0, this.borderColor, 0, (this.getHeight() / 10 * 4), new Color(64, 64, 64, 255), true);
		g2.setPaint(p);
		Polygon poly = new Polygon();
		poly.addPoint(20, 3);
		poly.addPoint(getWidth() - 3, 3);
		poly.addPoint(getWidth() - 20, getHeight() - 6);
		poly.addPoint(4, getHeight() - 6);
		g2.fillPolygon(poly);
		GradientPaint p2 = new GradientPaint(0, 0, new Color(255, 255, 255, 150), this.getWidth(), this.getHeight(), new Color(0, 0, 0, 0));
		g2.setPaint(p2);
		g2.fillPolygon(poly);
		g2.setPaint(p3);
		g.drawLine(20, 3, getWidth() - 5, 3);
		g.drawLine(getWidth() - 5, 3, getWidth() - 22, getHeight() - 6);
		g.drawLine(getWidth() - 22, getHeight() - 6, 4, getHeight() - 6);
		g.drawLine(4, getHeight() - 6, 20, 3);

		g2.setColor(getForeground().darkGray);
		g.drawString(getText(), 26, 32);
		g2.setColor(getForeground());
		g.drawString(getText(), 24, 30);
	}

	public void setBorderColor(Color col) {
		this.borderColor = col;
	}

	public void setGradientBaseColor(Color col) {
		this.gradientBaseColor = col;
	}

	public void setInternalGridColor(Color col) {
		this.internalGridColor = col;
	}
}