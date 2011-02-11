package org.jini.projects.neon.vertigo.management.store;

import java.io.IOException;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;

public interface HibernationStorage {
    public void storeAgent(Agent a) throws IOException;
    
    public Agent loadAgent(AgentIdentity id) throws IOException;
}
