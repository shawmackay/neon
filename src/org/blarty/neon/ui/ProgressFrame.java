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
 * neon : org.jini.projects.neon.ui
 * ProgressFrame.java
 * Created on 29-Aug-2003
 */
package org.jini.projects.neon.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.StringWriter;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * @author calum
 */
public class ProgressFrame extends JFrame {

	/**
	 * @throws java.awt.HeadlessException
	 */
	public ProgressFrame() throws HeadlessException {
		super();
		init();
		// TODO Complete constructor stub for ProgressFrame
	}

	/**
	 * @param gc
	 */
	public ProgressFrame(GraphicsConfiguration gc) {
		super(gc);
		init();
		// TODO Complete constructor stub for ProgressFrame
	}

	/**
	 * @param title
	 * @throws java.awt.HeadlessException
	 */
	public ProgressFrame(String title) throws HeadlessException {
		super(title);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		init();
		// TODO Complete constructor stub for ProgressFrame
	}

	/**
	 * @param title
	 * @param gc
	 */
	public ProgressFrame(String title, GraphicsConfiguration gc) {
		super(title, gc);
		init();
		// TODO Complete constructor stub for ProgressFrame
	}
	
	PipedInputStream pipe_in = new PipedInputStream();
		PipedOutputStream pipe_out = new PipedOutputStream();

		PipedInputStream pipe_errin = new PipedInputStream();
		PipedOutputStream pipe_err = new PipedOutputStream();

		BufferedReader reader = new BufferedReader(new InputStreamReader(pipe_in));
		BufferedReader errreader = new BufferedReader(new InputStreamReader(pipe_errin));
		StyledDocument doc;

		MutableAttributeSet output = new SimpleAttributeSet();
		MutableAttributeSet echo = new SimpleAttributeSet();
		MutableAttributeSet error = new SimpleAttributeSet();

		StringWriter outwriter = new StringWriter();
		StringWriter errwriter = new StringWriter();
		Thread st = new ReadOutput();
		Thread st3 = new ReadError();
		

		public void init() {
			
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			//ImagePanel imgpan = new ImagePanel(new ImageIcon("/home/calum/neon.jpg"),0,0,1.0,ImagePanel.CENTER);
			//this.getContentPane().add(imgpan);
			Container pane = this.getContentPane();
			//imgpan.setLayout(new BorderLayout());
			
			

			JTextPane console = new JTextPane();
			console.setEditable(false);
			console.setOpaque(true);
			doc = console.getStyledDocument();
			JScrollPane pan = new JScrollPane(console);			
			//pan.getViewport().setBackground(new Color(0,0,0,0));
		
			
			//pan.setBackground(new Color(255,255,255,255));
		//	pan.setOpaque(false);
			pan.setForeground(Color.WHITE);
			
			pane.add(pan, BorderLayout.CENTER);
			this.setResizable(true);					
			this.setTitle("Output");
		
		
				StyleConstants.setForeground(output, Color.blue);
			StyleConstants.setForeground(echo, Color.GREEN.darker().darker());
			StyleConstants.setForeground(error, Color.red);
			
			try {
				pipe_out.connect(pipe_in);
				pipe_err.connect(pipe_errin);
				PrintStream str = new PrintStream(pipe_err);
				PrintStream strout = new PrintStream(pipe_out);
				System.setOut(strout);
				System.setErr(str);	
				st.start();
				st3.start();
			} catch (IOException e) {
				System.out.println("Could not connect the pipes together: " + e.getMessage());

				e.printStackTrace();

			}
		
		}

		public static void main(String[] args) {
			
			System.out.println("This is output");
			System.err.println("This is an error!");
		}
	
			private class ReadOutput extends Thread {

			/**
			 * @see java.lang.Runnable#run()
			 */
			public void run() {

				for (;;) {
					try {
						while (reader.ready()) {
							String x = reader.readLine();
							if (x.startsWith(">>>"))
								doc.insertString(doc.getLength(), x + "\n", echo);
							else
								doc.insertString(doc.getLength(), x + "\n", output);
						}
						yield();
						synchronized(this) {
							sleep(10);
						}
					} catch (Exception e) {
						System.err.println("Help!");
					}
				}
			}

		}

		private class ReadError extends Thread {

			/**
			 * @see java.lang.Runnable#run()
			 */
			public void run() {

				for (;;) {
					try {
						while (errreader.ready()) {
							doc.insertString(doc.getLength(), errreader.readLine() + "\n", error);
						}
						synchronized(this) {
							sleep(10);
						}

					} catch (Exception e) {
						System.err.println("Help!");
					}
				}
			}

		}

}
