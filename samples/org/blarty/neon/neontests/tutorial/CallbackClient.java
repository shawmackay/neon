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

package org.jini.projects.neon.neontests.tutorial;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.http.HttpServerEndpoint;

import org.jini.projects.neon.agents.ReferenceableConstraints;
import org.jini.projects.neon.callbacks.CallbackFactory;
import org.jini.projects.neon.callbacks.DisconnectedCallback;
import org.jini.projects.neon.neontests.tutorial.mobile.TrackSvcsAgent;
import org.jini.projects.neon.service.AgentService;
import org.jini.projects.zenith.exceptions.NoSuchSubscriberException;
import org.jini.projects.zenith.messaging.messages.InvocationMessage;
import org.jini.projects.zenith.messaging.messages.MessageHeader;
import org.jini.projects.zenith.router.RouterService;


public class CallbackClient implements DiscoveryListener {

    public CallbackClient() {
        super();
        try {
            System.out.println("Discovering");
            String[] lookups=null;
            try {
                Configuration config = ConfigurationProvider.getInstance(new String[] { "client.config"});
                lookups = (String[]) config.getEntry("client", "lookupGroups", String[].class, LookupDiscovery.ALL_GROUPS);
            } catch (ConfigurationException e1) {
                // URGENT Handle ConfigurationException
                e1.printStackTrace();
            }
            LookupDiscoveryManager ldm = new LookupDiscoveryManager(lookups, null, this);
            synchronized (this) {
                wait(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void discarded(DiscoveryEvent e) {

    }

    public void discovered(DiscoveryEvent e) {
        System.out.println("Discovered");
        ServiceRegistrar[] regs = e.getRegistrars();
        AgentService svc;
        RouterService router = null;
        TrackSvcsAgent ag = null;
        try {
            svc = (AgentService) regs[0].lookup(new ServiceTemplate(null, new Class[] { AgentService.class }, null));
            router = (RouterService) regs[0].lookup(new ServiceTemplate(null, new Class[] { RouterService.class }, null));
            if (svc != null) {               
                ag = new TrackSvcsAgent();
                ag.setConstraints(new ReferenceableConstraints(new URL("http://e0052sts3s.countrywide-assured.co.uk/jinistubs/constraints.xml")));
                System.out.println("Agent name: " + ag.getName());
                // Build a callback
                Exporter callbackExporter = new BasicJeriExporter(HttpServerEndpoint.getInstance(0),new BasicILFactory());                
                DisconnectedCallback callbackToRegister = CallbackFactory.buildCallback("MobileCallback", 
                                                                                    e.getRegistrars(), 
                                                                                    callbackExporter, 
                                                                                    new MyCallback());
                
                   //svc.deployAgent(ag,callbackToRegister.getRegistrableListener());
                   System.out.println("Would deploy but won't");                 
                
            }
        } catch (RemoteException e1) {
            System.out.println("A RemoteException has occured - " + e1.getMessage());
            e1.printStackTrace();
        } catch (MalformedURLException e1) {
            // URGENT Handle MalformedURLException
            e1.printStackTrace();
        }
        if (router != null) {
            System.out.println("Sending message via router");
            try {
                Thread.sleep(2000);
                router.sendMessage("TrackSvcs",new InvocationMessage(new MessageHeader(), "GetLocation", new Object[] {
                },null));
                if (ag != null) {
//                    System.out.println("Killing the agent");
//                    router.sendMessage("Killer", "KillAll", new Object[] {"SimpleMobile" });
                }
            } catch (RemoteException e2) {
                // URGENT Handle RemoteException
                e2.printStackTrace();
            } catch (NoSuchSubscriberException e2) {
                // URGENT Handle NoSuchSubscriberException
                e2.printStackTrace();
            } catch (InterruptedException e2) {
                // URGENT Handle InterruptedException
                e2.printStackTrace();
            }
        }
       // synchronized (this) {
       //     notify();
        //}
    }

    public static void main(String[] args) {
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new RMISecurityManager());
        new CallbackClient();
    }

}
