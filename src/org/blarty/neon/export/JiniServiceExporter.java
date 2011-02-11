/*
 * JiniServiceExporter.java
 * 
 * Created on 30-Aug-2007, 13:02:36
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jini.projects.neon.export;

import java.util.List;

import net.jini.config.ConfigurationException;

import org.jini.glyph.ContainerControlled;
import org.jini.glyph.chalice.DefaultExporterManager;
import org.jini.glyph.chalice.ExporterManager;
import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.Meta;
import org.jini.projects.neon.agents.util.meta.ServiceInformation;

/**
 *
 * @author calum
 */
public class JiniServiceExporter {
    
    ExporterManager expMgr;
    
    public JiniServiceExporter() {
        expMgr = DefaultExporterManager.getManager();
    }

    public void exportAgent(Agent agent){
    	System.out.println("EXPORTING AGENT AS JINI SERVICE");
        Meta m = agent.getMetaAttributes();
        List<ServiceInformation> l = m.getMetaOfType(ServiceInformation.class);
        for (ServiceInformation info  : l) {
            System.out.println(info.serviceName);
        }
        String className  = agent.getClass().getName();
        //System.out.println("Exporting class '" + className + "' as a Jini service");
        String packageRoot = className.substring(0, className.lastIndexOf('.'));
        Class loaded = null;
        String classRoot = className.substring(className.lastIndexOf('.') +1);
        if(classRoot.endsWith("Impl"))
        	classRoot = classRoot.substring(0,classRoot.length()-4);
        
        try {
			Class cl = Class.forName(packageRoot + "." + classRoot +"ServiceImpl" );
			//System.out.println("Loading class: " + packageRoot + "." + classRoot +"ServiceImpl" );
			ContainerControlled controller = (ContainerControlled) cl.newInstance();
			controller.init(agent, agent.getAgentConfiguration());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

    }
    
}
