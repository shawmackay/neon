/*
 * ServiceInformation.java
 * 
 * Created on 30-Aug-2007, 13:34:45
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jini.projects.neon.agents.util.meta;

import net.jini.entry.AbstractEntry;
import net.jini.id.Uuid;

/**
 * Entry indicating how an agent should be exported, will be cached once the initial information is loaded from the configuration
 * @author calum
 */
public class ServiceInformation extends AbstractEntry{

    public String serviceName;
    
    public String[] groups;
    
    public Uuid serviceID;
    
    public ServiceInformation() {
    }

    
}
