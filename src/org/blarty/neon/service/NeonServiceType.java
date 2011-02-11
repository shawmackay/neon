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
 * zenith : org.jini.projects.zenith.messaging.broker
 * ZenithServiceInfo.java
 * Created on 08-Mar-2005
 *ZenithServiceInfo
 */
package org.jini.projects.neon.service;

import java.awt.Image;

import javax.swing.ImageIcon;

import net.jini.lookup.entry.ServiceType;

/**
 * @author calum
 */
public class NeonServiceType extends ServiceType{

	
	public String getDisplayName() {
		// TODO Complete method stub for getDisplayName
		return "Neon Agent Service";
	}
	
	public java.awt.Image getIcon(int param) {
        ImageIcon imic = new ImageIcon(this.getClass().getResource("neonembosssmall_green.jpg"));
        ImageIcon imicmono = new ImageIcon(this.getClass().getResource("neonembosssmall_green.jpg"));
        if (param == java.beans.BeanInfo.ICON_COLOR_16x16) {
            return imic.getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT);
        }
        if (param == java.beans.BeanInfo.ICON_COLOR_32x32) {
            return imic.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT);
        }

        if (param == java.beans.BeanInfo.ICON_MONO_16x16) {
            return imicmono.getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT);
        }
        if (param == java.beans.BeanInfo.ICON_MONO_32x32) {
            return imicmono.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT);
        }
        return imic.getImage();
    }

	
	public String getShortDescription() {
		// TODO Complete method stub for getShortDescription
		return "Provides hosting of collaborative components";
	}

}
