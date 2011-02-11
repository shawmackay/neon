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

/*
* AgentListRenderer.java
*
* Created Tue Apr 12 11:57:54 BST 2005
*/

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;

/**
*
* @author  calum
*
*/

public class AgentListRenderer extends DefaultListCellRenderer{
	private ImageIcon avail = new ImageIcon(this.getClass().getResource("images/avail.png"));
	private ImageIcon busy = new ImageIcon(this.getClass().getResource("images/busy.png"));
	private ImageIcon locked = new ImageIcon(this.getClass().getResource("images/locked.png"));
	private ImageIcon attach= new ImageIcon(this.getClass().getResource("images/attach.png"));
	private ImageIcon transfer = new ImageIcon(this.getClass().getResource("images/transfer.png"));
	private ImageIcon death = new ImageIcon(this.getClass().getResource("images/death.png"));	
	private ImageIcon hibernated = new ImageIcon(this.getClass().getResource("images/hibernated.png"));		
	private ImageIcon dumped = new ImageIcon(this.getClass().getResource("images/dumped.png"));		
	private ImageIcon savepointed = new ImageIcon(this.getClass().getResource("images/savepoint.png"));		
	/**
	* getListCellRendererComponent
	* @param aJList
	* @param aObject
	* @param aint
	* @param aboolean3
	* @param aboolean4
	* @return Component
	*/
	public Component getListCellRendererComponent(JList  aJList, Object  aObject, int  aint, boolean  aboolean3, boolean  aboolean4){
		if(aObject instanceof AgentListItem){
			AgentListItem listItem = (AgentListItem) aObject;
			String text ="<html><body><font size=\"10px\">" + listItem.name + "</font><br><font size=\"8px\">"+ listItem.ID + "</font>";
			this.setText(text);
			String st = listItem.state.toLowerCase();
			
		}
		return super.getListCellRendererComponent(aJList,aObject, aint, aboolean3, aboolean4);
		
	}

}

