package org.jini.projects.neon.vertigo.management.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Rectangle2D;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class SliceComponent
                extends
                JButton {

        private SliceComponent parentSlice;

        private boolean isDragging = false;

        private int mouseClickOffsetX = 0;
        private int mouseClickOffsety = 0;
        
        public static int APPLICATION = 0;
    	public static int REPELLER = 1;
    	public static int ATTRACTOR = 2;
    	public static int FACTORY = 3;
    	public static int REDUNDANT = 4;
    	public static int VIRTUAL = 5;
        
        public SliceComponent(String label) {
                super(label);
                setOpaque(false);
                setBorder(null);
             
                addMouseMotionListener(new MouseMotionAdapter() {
                	
                        public void mouseDragged(final MouseEvent e) {
                                // TODO Auto-generated method stub
                                SwingUtilities.invokeLater(new Runnable() {

                                        public void run() {
                                                
                                                if(isDragging==false)
                                                        isDragging = true;
                                                Rectangle b = ((JComponent) e.getSource()).getBounds();
                                                setForeground(Color.WHITE);
                                                Rectangle newBounds = new Rectangle(b.x + e.getX() - mouseClickOffsetX, b.y + e.getY() - mouseClickOffsety, b.width, b.height);
                                                e.getComponent().setBounds(newBounds);
                                                ((JComponent)getParent()).updateUI();
                                        }
                                });
                        }

                });
                addMouseListener(new MouseAdapter() {
                	@Override
                	public void mousePressed(MouseEvent e) {
                		// TODO Auto-generated method stub

mouseClickOffsetX = e.getX();
mouseClickOffsety = e.getY();
                	}
                        @Override
                        public void mouseReleased(MouseEvent e) {
                                // TODO Auto-generated method stub
                                SwingUtilities.invokeLater(new Runnable() {

                                        public void run() {

                                                isDragging = false;
                                               ((JComponent)getParent()).updateUI();
                                        }
                                });
                        }
                });
        }

        public SliceComponent getParentSlice() {
                return parentSlice;
        }

        public void setParentSlice(SliceComponent parent) {
                this.parentSlice = parent;
        }

        public boolean isDragging() {
                return isDragging;
        }

        protected void paintComponent(Graphics g) {
                
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint paint = new GradientPaint(0, 0, getBackground(), 0, getHeight(), getBackground().darker());
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setPaint(paint);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(new Color(64,64,64,64));
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                g2.setPaint(paint);
                g2.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 13, 13);
                g2.setColor(getForeground());
                Rectangle2D rect = getFont().getStringBounds(getText(), g2.getFontRenderContext());
                
                g2.drawString(getText(), getWidth()/2 - (int)(rect.getWidth()/2), 16);
        }

}
