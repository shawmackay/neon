package org.jini.projects.neon.sample.cyber;

import java.net.URL;
import java.util.Map;

import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.render.BadFormatException;
import org.jini.projects.neon.render.EngineInstruction;
import org.jini.projects.neon.render.PresentableAgent;
import org.jini.projects.neon.web.WebUtils;

public class GenerateCharacterAgent extends AbstractAgent implements PresentableAgent{

        @Override
        public boolean init() {
                // TODO Auto-generated method stub
                return true;
        }

        public EngineInstruction getPresentableFormat(String type, String action, Map params, boolean getTemplate) {
        		Character c = new Character("bob",21,"Solo");
        		c.setCurrentStats(new Stats());
        		c.setOriginalStats(new Stats());
        		
                try {
                	System.out.println(WebUtils.convertObjectToXML("character", c));
					EngineInstruction instruction = new EngineInstruction("xsl",getTemplate(type, action),WebUtils.convertObjectToXML("character", c));
					return instruction;
				} catch (BadFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
        }

        public URL getTemplate(String type, String action) {                
                return this.getClass().getResource("cyber.xsl");
        }
        
        

}
