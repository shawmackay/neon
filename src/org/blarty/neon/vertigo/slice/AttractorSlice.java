package org.jini.projects.neon.vertigo.slice;

import java.util.Iterator;

import net.jini.core.lookup.ServiceID;

import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.vertigo.management.AgentAction;
import org.jini.projects.neon.vertigo.management.SliceManager;
import org.jini.projects.neon.vertigo.management.VetoResult;

public class AttractorSlice extends DefaultSlice implements AnchorableSlice {
    private AgentIdentity anchor;

     
    
    public VetoResult vetoes(AgentAction action, AgentIdentity agent, SliceManager sliceManager, ServiceID preAction, ServiceID postAction) {
        // TODO Auto-generated method stub
        SliceManager mgr = sliceManager;
        if (action == AgentAction.TRANSFER || action == AgentAction.DEPLOY) {            
            ServiceID currentSvc_ID =  preAction;
            ServiceID moveToSvc_ID =  postAction;
            boolean sendwarning = false;
            if (agent.equals(anchor)) {
                return VetoResult.ALL;
            } else {
                for (Iterator iter = this.agentList.iterator(); iter.hasNext();) {
                    AgentIdentity id = (AgentIdentity) iter.next();
                    if (!id.equals(agent)) {
                        ServiceID internalID = mgr.getHostingServiceID(id);
                        if (internalID.equals(moveToSvc_ID)) {
                            System.out.println("Agent " + id + " hosted in receiving host");
                        } else if (internalID.equals(currentSvc_ID)) {
                            if(enforced){                        	
                        	System.out.println("Policy is enforced: non-anchor agents cannot move");
                        	return VetoResult.NO;
                            }                        	
                            System.out.println("Agent " + id + " hosted in transmitting host");
                            sendwarning = true;
                        } else {
                            System.out.println("Agent " + id + " hosted in neither host");
                            sendwarning = true;
                        }
                    }
                    if (sendwarning)
                        return VetoResult.WARNING;
                }
            }
        }
        return VetoResult.YES;
    }

    public AgentIdentity getAnchorAgent() {
        // TODO Auto-generated method stub
        return anchor;
    }

    public void setAnchorAgent(AgentIdentity id) {
        // TODO Auto-generated method stub
        this.anchor = id;
    }

    @Override
    public boolean isEnforceable() {
        // TODO Auto-generated method stub
        return true;
    }
}
