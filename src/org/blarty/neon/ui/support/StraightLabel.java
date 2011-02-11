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
import javax.swing.border.EmptyBorder;

/**
 * @author calum
 */
public class StraightLabel extends JLabel {

	private boolean extended = false;
	/*
	 * @see java.awt.Component#paint(java.awt.Graphics)
	 */

	private Color borderColor = Color.RED;
	private Color gradientBaseColor = Color.GRAY;
	private Color internalGridColor = Color.YELLOW;

	private Color gradientToColor = Color.BLACK;
	private Color borderToColor = Color.GRAY;
	private Color overlayColor = new Color(255, 255, 255, 150);
	private Color overlayToColor = new Color(0, 0, 0, 0);

	private boolean repeat = false;

	private boolean useShadow = false;

	public StraightLabel(String text, Color border, Color borderTo, Color gradientBase, Color gradientTo, Color overlay, Color overlayTo, Color textColor, Font font) {
		this();
		setText(text);
		// TODO Complete constructor stub for StraightLabel
		borderColor = border;
		borderToColor = borderTo;
		gradientBaseColor = gradientBase;
		gradientToColor = gradientTo;
		overlayColor = overlay;
		overlayToColor = overlayTo;
		setForeground(textColor);
		setFont(font);
	}

	public StraightLabel(String text, Color border, Color borderTo, Color gradientBase, Color gradientTo, Color overlay, Color overlayTo, Color textColor, Font font, boolean repeat) {
		this();
		setText(text);
		// TODO Complete constructor stub for StraightLabel
		borderColor = border;
		borderToColor = borderTo;
		gradientBaseColor = gradientBase;
		gradientToColor = gradientTo;
		overlayColor = overlay;
		overlayToColor = overlayTo;
		setForeground(textColor);
		setFont(font);
		this.repeat = repeat;
	}

	public void setUseShadow(boolean useShadow){
		this.useShadow = useShadow;
	}
	public StraightLabel() {
		// setFont(new Font("Dialog", Font.BOLD, 24));
		// setMinimumSize(new Dimension(24,
		// getFontMetrics(getFont()).getHeight() + 16));
		// setPreferredSize(new Dimension(24,
		// getFontMetrics(getFont()).getHeight() + 16));
		this.setBorder(new EmptyBorder(6, 0, 6, 12));
	}

	public StraightLabel(String text) {
		this();
		setText(text);
		System.out.println(getText());
	}

	public StraightLabel(String text, Color border, Color baseColor) {
		this(text);

		gradientBaseColor = baseColor;
		borderColor = border;
	}

	public StraightLabel(String text, Color border, Color baseColor, Font font) {
		this(text, border, baseColor);
		setFont(font);
	}

	public StraightLabel(String text, Color border, Color baseColor, Font font, Color textColor) {
		this(text, border, baseColor);
		setFont(font);
		setForeground(textColor);
	}

	public void paint(Graphics g) {
		// TODO Complete method stub for paint
		this.getBounds();
		Graphics2D g2 = (Graphics2D) g;
		g.setColor(Color.WHITE);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		GradientPaint p;
		if (repeat)
			p = new GradientPaint(0, 0, this.gradientBaseColor, 0, (this.getHeight() / 10 * 6), gradientToColor, true);
		else
			p = new GradientPaint(0, 0, this.gradientBaseColor, 0, (this.getHeight()), gradientToColor, false);
		GradientPaint p3;
		if (repeat)
			p3 = new GradientPaint(0, 0, this.borderColor, 0, (this.getHeight() / 10 * 4), borderToColor, true);
		else
			p3 = new GradientPaint(0, 0, this.borderColor, 0, (this.getHeight()), borderToColor, false);
		g2.setPaint(p);

		g2.fillRect(0, 0, getWidth(), getHeight());
		GradientPaint p2 = new GradientPaint(0, 0, overlayColor, this.getWidth(), this.getHeight(), overlayToColor);
		g2.setPaint(p2);
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setPaint(p3);
		g.drawRect(1, 1, getWidth() - 2, getHeight() - 2);
		if (useShadow) {
			g2.setColor(new Color(64,64,64,64));

			g.drawString(getText(), 8, this.getFontMetrics(getFont()).getHeight() + 4);
		}
		g2.setColor(getForeground());
		g.drawString(getText(), 6, this.getFontMetrics(getFont()).getHeight() + 2);
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