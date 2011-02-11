
/*
 * Copyright 2005 neon.jini.org project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * neon : org.jini.projects.neon.agents.standard
 * RecoveryAgent.java
 * Created on 09-Sep-2003
 */
package org.jini.projects.neon.recovery;


import java.util.ArrayList;
import java.util.logging.Logger;

import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;
import net.jini.space.JavaSpace05;

import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.LocalAgent;
import org.jini.projects.neon.agents.sensors.SensorException;
import org.jini.projects.neon.agents.sensors.SensorFilter;
import org.jini.projects.neon.agents.sensors.SensorListener;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.neon.host.PrivilegedAgentContext;
import org.jini.projects.neon.service.ServiceAgent;

//

/**
 * Begins Neons recovery process, and processes events as other Neon instances are de-registered from the LUS
 * @author calum
 */
public class RecoveryAgent extends AbstractAgent implements LocalAgent, SensorListener, Runnable{
    /**
     *
     */
    private static final long serialVersionUID = 543440403051204378L;
    private Logger recLog = Logger.getLogger("org.jini.projects.neon.agents.standard.Recovery");
    private String spaceName;
    private int numberToTake;
    private int timeToWait;
    
    public RecoveryAgent() {
        super();
        // TODO Complete constructor stub for RecoveryAgent
        this.name = "Recovery";
        this.namespace ="neon";
        
    }
    
        /* (non-Javadoc)
         * @see org.jini.projects.neon.agents.Agent#init()
         */
    public boolean init() {
        // TODO Complete method stub for init
        try{
            ServiceAgent svc = (ServiceAgent) context.getAgent("neon.Services");
            svc.addListener(this.getIdentity(), null);
            //context.registerSensor(this, "neon.Services", null);
            
            spaceName = (String) getAgentConfiguration().getEntry("org.jini.projects.neon.Recovery","spaceName", String.class,"");
            numberToTake = ((Integer) getAgentConfiguration().getEntry("org.jini.projects.neon.Recovery","numberToTake", Integer.class,new Integer(20))).intValue();
            timeToWait = ((Integer) getAgentConfiguration().getEntry("org.jini.projects.neon.Recovery","timeToWait", Integer.class,new Integer(2))).intValue();

        }catch(Exception ex){
            System.err.println("Caught Exception: "+ ex.getClass().getName() + "; Msg: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return true;
    }
    
     
    public void run() {
        // TODO Complete method stub for run
        PrivilegedAgentContext context = (PrivilegedAgentContext) this.context;
        try {
            ServiceAgent svcAg =  (ServiceAgent) context.getAgent("Services");
            recLog.info("Recovery Agent looking for agents in: " + spaceName);
            ArrayList spaces = (ArrayList) svcAg.getNamedService(spaceName,JavaSpace.class);
            //ArrayList spaces = (ArrayList) context.sendMessage("Services", "GetService", new Object[] { JavaSpace.class }).getResponseObject();
            TransactionManager tr = (TransactionManager) svcAg.getSingleService(TransactionManager.class);
            if (spaces.size() != 0) {
                JavaSpace05 s = (JavaSpace05) ((ServiceItem) spaces.get(0)).service;
                HostRecover hr = new HostRecover(context.getAgentHost(), s, tr, numberToTake, timeToWait);
                hr.getCheckPointedAgents();
            }
            recLog.info("Recovery complete for Domain: " + getAgentContext().getDomainName());
        } catch (Exception e) {
            // TODO Handle NoSuchAgentException
            System.out.println("Error in recovery for Domain: " + getAgentContext().getDomainName());
            e.printStackTrace();
        }
        
    }
    
        /* (non-Javadoc)
         * @see org.jini.projects.neon.agents.Agent#stop()
         */
    public void stop() {
        // TODO Complete method stub for stop
        System.out.println("Nees to add deregistration of sensors here");
        super.stop();
    }
    
    
        /* (non-Javadoc)
         * @see org.jini.projects.neon.agents.sensors.SensorListener#sensorTriggered(java.lang.String, java.lang.Object)
         */
    public Object sensorTriggered(String sensortype, Object value) throws SensorException {
        
        recLog.finest("Sensor triggered: " + sensortype + " with " + value.toString());
        try {
            ServiceAgent svcAg =  (ServiceAgent) context.getAgent("Services");
            ArrayList spaces = (ArrayList) svcAg.getNamedService(spaceName,JavaSpace.class);
            if (spaces.size() != 0) {
                JavaSpace05 s = (JavaSpace05) ((ServiceItem) spaces.get(0)).service;
                TransactionManager tr = (TransactionManager) svcAg.getSingleService(TransactionManager.class);
                HostRecover hr = new HostRecover(((PrivilegedAgentContext) context).getAgentHost(), s, tr, numberToTake,timeToWait);
                hr.getCheckPointedAgentsForService((ServiceID) value);
            } else {
                recLog.warning("Host shutdown Sensor triggered but no spaces found to restart agents from");
            }
        } catch (NoSuchAgentException e) {
            // TODO Handle NoSuchAgentException
            e.printStackTrace();
        }
        return null;
    }
    
    
    
    
    /**
     * returns an Empty SensorFilter, indicating that all events should be passed to this agent
     * @return SensorFilter
     */
    public SensorFilter getFilter(){
        return null;
    }
    
}
