/*
 * SequentialCallTest.java
 *
 * Created on 11 July 2005, 09:07
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jini.projects.neon.agents.tests;

import net.jini.core.transaction.server.TransactionManager;
import net.jini.event.EventMailbox;
import net.jini.lease.LeaseRenewalService;
import net.jini.space.JavaSpace;
import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.service.ServiceAgent;
import org.jini.projects.neon.host.NoSuchAgentException;

/**
 *
 * @author calum
 */
public class SequentialCallAgent extends AbstractAgent implements Runnable{
    
    /** Creates a new instance of SequentialCallTest */
    public SequentialCallAgent() {
        this.name = "SequenceCall";
        this.namespace = "neon.tests";
    }

    public boolean init() {
        return true;
    }

    public void run() {
        try {
            ServiceAgent svc = (ServiceAgent) context.getAgent("neon.Services",0);

            Object o = svc.getSingleService(net.jini.space.JavaSpace.class);
            doAssertChecks(o,JavaSpace.class);
            o = svc.getSingleService(TransactionManager.class);
            doAssertChecks(o,TransactionManager.class);
            o = svc.getSingleService(EventMailbox.class);
            doAssertChecks(o, EventMailbox.class);
            o = svc.getSingleService(LeaseRenewalService.class);
            doAssertChecks(o, LeaseRenewalService.class);
        } catch (NoSuchAgentException e) {
            System.out.println("Services Agent could not be found");
        }
    }
    
    private void doAssertChecks(Object o, Class cl){
    assert cl.isInstance(o): "Agent " + getIdentity() + ": Should be " + cl.getName()+ " but is " + o.getClass().getName();
    }
}
