package org.jini.projects.neon.vertigo.slice;

import java.util.Iterator;

import net.jini.core.lookup.ServiceID;

import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.vertigo.management.AgentAction;
import org.jini.projects.neon.vertigo.management.SliceManager;
import org.jini.projects.neon.vertigo.management.VetoResult;

public class RepellerSlice extends DefaultSlice {

    /**
     * 
     */
    private static final long serialVersionUID = -7341704316246184357L;

    @Override
    public VetoResult vetoes(AgentAction action, AgentIdentity agent, SliceManager sliceManager, ServiceID preAction, ServiceID postAction) {
        SliceManager mgr = sliceManager;
        if (action == AgentAction.TRANSFER || action == AgentAction.DEPLOY) {
            System.out.println("Recording TRANSFER or DEPLOY action");
            //ServiceID currentSvc_ID = (ServiceID) preAction;
            ServiceID moveToSvc_ID = (ServiceID) postAction;
            

            for (Iterator iter = this.agentList.iterator(); iter.hasNext();) {
                AgentIdentity id = (AgentIdentity) iter.next();
                if (!id.equals(agent))
                    if (mgr.getHostingServiceID(id).equals(moveToSvc_ID)) {                        
                        if (enforcesPolicy()){                        	
                            return VetoResult.NO;
                        }else 
                            return VetoResult.WARNING;
                    }
            }
             return VetoResult.YES;
        }
        return VetoResult.YES;
    }
    
    @Override
    public boolean isEnforceable() {
        // TODO Auto-generated method stub
        return true;
    }
}
