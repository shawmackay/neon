package org.jini.projects.neon.vertigo.slice;

import net.jini.core.lookup.ServiceID;

import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.vertigo.management.AgentAction;
import org.jini.projects.neon.vertigo.management.SliceManager;
import org.jini.projects.neon.vertigo.management.VetoResult;

/**
 * The anchor is the currently 'alive' agent in the redundant set
 * @author calum
 *
 */
public class RedundantSlice extends DefaultSlice implements AnchorableSlice {
    /**
     * 
     */
    private static final long serialVersionUID =-3832924315095699093L;

    AgentIdentity anchor;

    public AgentIdentity getAnchorAgent() {
        // TODO Auto-generated method stub
        return anchor;
    }

    public void setAnchorAgent(AgentIdentity id) {
        // TODO Auto-generated method stub
        anchor =id;
    }

    @Override
    public VetoResult vetoes(AgentAction action, AgentIdentity agent, SliceManager sliceManager, ServiceID preAction, ServiceID postAction) {
        // TODO Auto-generated method stub
        return super.vetoes(action, agent, sliceManager, preAction, postAction);
    }
}
