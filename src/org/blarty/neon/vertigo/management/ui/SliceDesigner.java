/*
 * SliceDesigner.java
 *
 * Created on 17 May 2006, 12:15
 */

package org.jini.projects.neon.vertigo.management.ui;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * 
 * @author calum.mackay
 */
public class SliceDesigner
                extends
                javax.swing.JPanel {

        private JLabel label1;
        
        private JComponent activeComponent;
        
        private ActionListener sharedListener = new ActionListener(){
      	   public void actionPerformed(ActionEvent e) {
        		// TODO Auto-generated method stub
        		   label1.setText("Clicked Button");
        		   
        		    if(activeComponent!=null)
        		    	activeComponent.setForeground(Color.BLACK);
        		    ((JButton) e.getSource()).setForeground(Color.WHITE);
        		    activeComponent = (JButton) e.getSource();
        	}
           };

		/** Creates new form SliceDesigner */
        public SliceDesigner() {
                initComponents();
                setLayout(null);
                doExtraInit();

        }

        /**
         * This method is called from within the constructor to initialize the
         * form. WARNING: Do NOT modify this code. The content of this method is
         * always regenerated by the Form Editor.
         */
        // <editor-fold defaultstate="collapsed" desc=" Generated Code
        // ">//GEN-BEGIN:initComponents
        private void initComponents() {

                // javax.swing.GroupLayout layout = new
                // javax.swing.GroupLayout(this);
                // this.setLayout(layout);
                // layout.setHorizontalGroup(
                // layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                // .addGap(0, 400, Short.MAX_VALUE)
                // );
                // layout.setVerticalGroup(
                // layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                // .addGap(0, 300, Short.MAX_VALUE)
                // );
        }// </editor-fold>//GEN-END:initComponents

        // Variables declaration - do not modify//GEN-BEGIN:variables
        // End of variables declaration//GEN-END:variables

        public void doExtraInit() {
//        		label1 = new JLabel();
//				label1.setBounds(0,0, 200, 32);
//        		add(label1);
//                SliceComponent button = new SliceComponent("Button");
//                button.setLocation(40, 90);
//                button.setBounds(40, 250, 100, 24);
//                this.add(button);
//               SliceComponent button2 = new SliceComponent("Button2");
//               button2.addActionListener(new ActionListener(){
//            	   public void actionPerformed(ActionEvent e) {
//            		// TODO Auto-generated method stub
//            		   label1.setText("Clicked Button 2");
//            	}
//               });
//               
//               SliceComponent button3 = new SliceComponent("BUtton3");       
//               button2.setBackground(Color.BLUE);
//                button2.setLocation(40, 90);
//                button2.setBounds(250, 130, 100, 24);
//                this.add(button2);
//              
//                button.addActionListener(sharedListener);
//                button2.addActionListener(sharedListener);
//                button3.addActionListener(sharedListener);
//                button3.addActionListener(new ActionListener(){
//             	   public void actionPerformed(ActionEvent e) {
//             		// TODO Auto-generated method stub
//             		   label1.setText("Clicked Button 3");
//             	}
//                });
//                button3.setBackground(Color.CYAN);
//                button3.setLocation(40, 90);
//                button3.setBounds(250, 130, 100, 24);
//                this.add(button3);
//               
//                ConnectorComponent connect = new ConnectorComponent();
//                connect.setStartComponent(button);
//                connect.setEndComponent(button2);
//                ConnectorComponent connect2 = new ConnectorComponent();
//                connect2.setStartComponent(button);
//                connect2.setEndComponent(button3);
//                connect.setBounds(0,0,400,400);
//                connect2.setBounds(0,0,400,400);
//                button2.setType(SliceComponent.REDUNDANT);
//                button.setType(SliceComponent.ATTRACTOR);
//                button3.setType(SliceComponent.REPELLER);
//                this.add(connect);
//                this.add(connect2);
//                buildSliceComponent("Data", button3, Color.YELLOW);
//                buildSliceComponent("FILE", button3, Color.ORANGE);
//                buildSliceComponent("blah", button2, Color.RED);
//                setTopComponent(button);
//                
//                button.addChild(button2);
//                button.addChild(button3);
//                button.layoutChildren(100, 700);
                // setComponentZOrder(connect, connect.getComponentCount());
        }
        
        public void setTopComponent(SliceComponent c){
        	c.setBounds(350, 30, 100, 24);
        }

        public void buildSliceComponent(String name, SliceComponent parent, Color col){
//                SliceComponent newComp = new SliceComponent(name);
//                newComp.setBackground(col);
//                ConnectorComponent connector = new ConnectorComponent();
//                connector.setStartComponent(parent);
//                connector.setEndComponent(newComp);
//                connector.setBounds(0,0,400,400);
//                newComp.setBounds(100,100,100,24);
//                newComp.addActionListener(sharedListener);
//                this.add(newComp);
//                this.add(connector);
//                parent.addChild(newComp);
//               newComp.setParentSlice(parent);
//                parent.layoutChildren(0, getWidth());
        }
        
        public static void main(String[] args) {
                try {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } catch (InstantiationException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } catch (IllegalAccessException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                } catch (UnsupportedLookAndFeelException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                JFrame jf = new JFrame();
                jf.add(new SliceDesigner());
                jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                jf.setSize(600, 600);
                jf.setVisible(true);
        }
}
