package org.jini.projects.neon.vertigo.management.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class ConnectorComponent
                extends
                JComponent {

        int currentx1 = -1;

        int currenty1;

        int currentx2;

        int currenty2;

        private SliceComponent endComponent;

        private SliceComponent startComponent;

        public SliceComponent getEndComponent() {
                return endComponent;
        }

        public void setEndComponent(SliceComponent endComponent) {
                this.endComponent = endComponent;
        }

        public SliceComponent getStartComponent() {
                return startComponent;
        }

        public void setStartComponent(SliceComponent startComponent) {
                this.startComponent = startComponent;
        }

        protected void paintComponent(Graphics g) {
        	
                setBounds(getParent().getBounds());

                // if(!startComponent.isDragging() &&
                // !endComponent.isDragging()){
                Graphics2D g2 = (Graphics2D) g;
             g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.BLUE);
                g2.setStroke(new BasicStroke(2.0f));
                Rectangle endBounds = endComponent.getBounds();
                Rectangle startBounds = startComponent.getBounds();
                double startx = startBounds.x + (0.5 * startBounds.width);
                double starty = startBounds.y + (0.5 * startBounds.height);
                double endx = endBounds.x + (0.5 * endBounds.width);
                double endy = endBounds.y + (0.5 * endBounds.height);
//                if (endComponent.isDragging() || startComponent.isDragging()) {
//                        if (currentx1 != -1)
//                                g2.drawLine((int) currentx1, (int) currenty1, (int) currentx2, (int) currenty2);
//                }
                if (endComponent.isDragging() || startComponent.isDragging()) {
                       g2.setColor(getParent().getBackground());
                     
                       g2.drawLine((int) currentx1, (int) currenty1, (int) currentx2, (int) currenty2);
                       
                }
                g2.setColor(Color.BLACK);
                
                g2.drawLine((int) startx, (int) starty, (int) endx, (int) endy);
                
             // g2.drawArc((int)endx-20,(int) endy-20,40 , 40, 0, 360);
                currentx1 = (int) startx;
                currenty1 = (int) starty;
                currentx2 = (int) endx;
                currenty2 = (int) endy;
                // }
        }

}
