package org.jini.projects.neon.vertigo.management;

import java.io.IOException;
import java.util.Date;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.vertigo.management.store.FileHibernationStorage;
import org.jini.projects.neon.vertigo.management.store.HibernationStorage;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;

public class HibernationManager {
    
    Configuration config;
    
    HibernationStorage store;
    
    public HibernationManager(Configuration config){
        try {
            HibernationStorage store = (HibernationStorage) config.getEntry("org.jini.projects.vertigo.management.hibernation", "store", HibernationStorage.class, new FileHibernationStorage());
        } catch (ConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void hibernateIndefinitely(Agent a) throws IOException{
        store.storeAgent(a);
    }
    
    public void hibernateUntilTime(Date after,Agent a){
        
    }
    
    public void hibernateUntilServiceChanges(Agent a){
        
    }
    
    public Agent wakeAgent(AgentIdentity id) throws IOException{
        return store.loadAgent(id);
    }
    
}
