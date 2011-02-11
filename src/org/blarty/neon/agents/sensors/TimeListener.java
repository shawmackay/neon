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

package org.jini.projects.neon.agents.sensors;

/*
 * TimeListener.java Created Tue Apr 05 10:33:39 BST 2005
 */
import java.awt.BorderLayout;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Random;

import javax.swing.JDialog;
import javax.swing.JLabel;

import net.jini.core.lookup.ServiceItem;

import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.SensorAgent;
import org.jini.projects.neon.host.AgentContext;
import org.jini.projects.neon.host.NoSuchAgentException;
import org.jini.projects.neon.service.AgentService;
import org.jini.projects.neon.service.ServiceAgent;
import org.jini.projects.neon.service.TransferAgent;

/**
 * An example agent to show how sensors, filters and registrations work in Neon.
 * Registers with the TimeSensor to be informed every 15 seconds. Provides a small visual dialog for feedback to the user.
 * @author calum
 */

public class TimeListener extends AbstractAgent implements SensorListener{
    private boolean registered = false;

    private transient JDialog visualDialog;

    private transient JLabel lastTime;

    public boolean init() {
        try {
        	getAgentLogger().info("Time Listener ID: " + getIdentity());
            AgentContext ctx = getAgentContext();
            getAgentLogger().info("Running INIT");
           
            this.visualDialog = new JDialog();
            visualDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            visualDialog.getContentPane().setLayout(new BorderLayout());
            lastTime = new JLabel();
            if (context.getAgentServiceID() != null)
                visualDialog.getContentPane().add(new JLabel("TimeListener Located @: " + context.getAgentServiceID().toString()), BorderLayout.CENTER);
            else
                visualDialog.getContentPane().add(new JLabel("TimeListener Located @: SID not yet defined (Service not joined atm)"), BorderLayout.CENTER);
            visualDialog.getContentPane().add(lastTime,BorderLayout.SOUTH);
            visualDialog.setSize(300, 100);
            visualDialog.setTitle("TimeListener Dialog window");           
            visualDialog.setVisible(true);
            if (!registered) {
                    SensorAgent sens = (SensorAgent) ctx.getAgent("neon.test.TimeSensor");
                    sens.addListener(this.getIdentity(), new TimeFilter(TimeFilter.EVERY | TimeFilter.BOUNDARY, 0, 0, 15));
                    registered = true;
                } else
                    System.out.println("Already registered");
            return true;
        } catch (Exception ex) {
            System.err.println("Caught Exception: " + ex.getClass().getName() + "; Msg: " + ex.getMessage());
            ex.printStackTrace();
        }
        return false;
    }

    public void start() {

    }

    /**
     * sensorTriggered
     * 
     * @param aString
     * @param aObject
     */
    public Object sensorTriggered(String aString, Object aObject) throws SensorException {
         
        lastTime.setText("Notified @ " + aObject.toString());
        try {
            ServiceAgent svcAgent = (ServiceAgent) context.getAgent("neon.Services");
            List l = svcAgent.getAgentHosts();
            Random r = new Random(System.currentTimeMillis());
            if (l.size() > 0) {
                ServiceItem item = (ServiceItem) l.get(r.nextInt(l.size()));               
                if (context.getAgentServiceID() != null && !item.serviceID.equals(context.getAgentServiceID())) {
                    TransferAgent transfer = (TransferAgent) context.getAgent("neon.Transfer");
                    getAgentLogger().info("Transferring");
                    if (visualDialog != null) {
                        System.out.println("Disposing dialog");
                        visualDialog.dispose();

                        visualDialog = null;
                    }
                    transfer.transferAgentTo(this, (AgentService) item.service);
                } else
                    getAgentLogger().fine("Staying in current partition");
            }
        } catch (NoSuchAgentException e) {
            // TODO Handle NoSuchAgentException
            e.printStackTrace();
        } catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
    }

    public void stop() {
        // TODO Complete method stub for stop        
        super.stop();
    }

    /**
     * getFilter
     * 
     * @return SensorFilter
     */
    public SensorFilter getFilter() {
        return new TimeFilter(TimeFilter.BOUNDARY | TimeFilter.EVERY, 0, 1, 0);
    }

}
