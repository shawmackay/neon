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

package org.jini.projects.neon.service;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.activation.ActivationException;
import java.rmi.activation.ActivationID;

import net.jini.config.ConfigurationException;
import net.jini.security.TrustVerifier;

import org.jini.projects.eros.ErosLogger;
import org.jini.projects.eros.ErosService;
import org.jini.projects.neon.ui.ProgressFrame;

import com.sun.jini.start.LifeCycle;

/**
 * Major hack to allow activation restart
 */
public class ActivatableServiceStartup implements Serializable, Remote{
    
       
    
    ServiceStartupIntf starter;
    
    
    public static void main(String[] args) throws Exception{
        if (System.getProperty("org.jini.projects.neon.ui") != null) {
            ProgressFrame app = new ProgressFrame("Neon Console");
            app.setSize(600, 600);
            app.setVisible(true);
        }
        
        new ActivatableServiceStartup(args,null);
    }

    private ActivationID activationID;

    
    public ActivatableServiceStartup(ActivationID aid, MarshalledObject data) throws IOException, ActivationException, ConfigurationException, ClassNotFoundException {
        System.out.println("Runnnig AID constructor");
        Object obj = data.get();
        try {
            Class cl = Class.forName("org.jini.projects.neon.service.ServiceStartup");
            starter = (ServiceStartupIntf) cl.newInstance();        
            starter.init((String[]) obj, aid);
            starter=null;
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ActivatableServiceStartup(String[] args, LifeCycle lifeCycle) throws RemoteException {
        System.out.println("Running LifeCycle constructor");
        try {
            Class cl = Class.forName("org.jini.projects.neon.service.ServiceStartup");
            starter = (ServiceStartupIntf) cl.newInstance();        
            starter.init(args, null);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    
    

    
    public Object getProxy() {
        return this.starter.getProxy();
    }

    public TrustVerifier getProxyVerifier() {
        return this.starter.getProxyVerifier();
    }


    

}
