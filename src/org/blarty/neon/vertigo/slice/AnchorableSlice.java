package org.jini.projects.neon.vertigo.slice;

import org.jini.projects.neon.agents.AgentIdentity;

public interface AnchorableSlice extends Slice{

    public AgentIdentity getAnchorAgent();

    public void setAnchorAgent(AgentIdentity id);

}