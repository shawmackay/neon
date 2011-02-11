package org.jini.projects.neon.annotations.tests;

import java.io.Serializable;
import java.util.logging.Logger;

import org.jini.glyph.Injection;
import org.jini.projects.neon.annotations.Agent;

import org.jini.projects.neon.host.AgentContext;

@Agent(name = "POJO", namespace = "simpler", init = "initMe")
@Injection
public class SimplePOJO implements Runnable, Serializable{
        
     transient private Logger log =Logger.getLogger("neon");
        
    public boolean initMe() {
    	log= Logger.getLogger("neon");
        log.fine("I should be called instead of agent.init() method!");
        return true;
    }
    
    public void setGreeting(String greeting){
        log.fine("Greeting was Set to: " + greeting);
    }
    
    public void setAgentContext(AgentContext hostContext){
            log.fine("AGENT CONTEXT IS SET IN POJO!!!!!");
    }
    public void run() {
        // TODO Auto-generated method stub
        
}
}
