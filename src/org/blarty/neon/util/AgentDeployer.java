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
 * neon : org.jini.projects.neon.util
 * AgentDeployer.java
 * Created on 27-May-2005
 *AgentDeployer
 */
package org.jini.projects.neon.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.RMIClassLoader;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.SAXParserFactory;

import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.ReferenceableConstraints;
import org.jini.projects.neon.dynproxy.PojoAgentProxyFactory;
import org.jini.projects.neon.service.AgentService;
import org.jini.projects.neon.service.admin.SeedingAgentDetails;

import com.sun.jini.system.MultiCommandLine;
import net.jini.loader.pref.PreferredClassLoader;

/**
 * <p>
 * <a name="manpage"/>AgentDeployer is used to bootstrap an agent prior to
 * installing it into a Neon instance.<br/>Bootstrapping involves setting the
 * constraints information, and the configuration information for the agent
 * (both are optional) and setting the codebase for the agent. The additional
 * options refer to locating a particular service or Jini group.
 * </p>
 * The commandline for invoking agent deployer is as follows.<br/>
 * <p>
 * java org.jini.projects.neon.util.AgentDeployer [-?] [-agentclass str]
 * [-agentconfig str] [-agentconstraints str] [-codebase str] [-serviceid str]
 * [-group str]</code>
 * </p>
 * The <code>agentclass</code> argument defines the name of the class for your
 * agent, i.e. org.somewhere.myapp.MyAgent<br/> The <code>agentconfig</code>
 * and <code>agentconstraints</code> arguments are URL's pointing to the
 * required files.<br/> The <code>codebase</code> represents the URL with
 * which your agent will be annotated.<br/> <code>serviceid</code> allows you
 * to request deployment into a specific neon instance.<br/> If <code>group</code>
 * is specified Neon will try to deploy to the first Neon instance that supports
 * the requirements of the agent as specified by its constraints.<br/> The
 * agent must be available on the classpath.
 * 
 * @author calum
 * @since 0.1
 */
public class AgentDeployer {

    URLClassLoader ucl;
    boolean deployAgent = false;
    boolean finishedDiscovery = false;
    
    public AgentDeployer(SeedingAgentDetails agentDetails, AgentService svc){
    	try {
			this.svc = svc;
			 System.out.println("Starting agent deployment");
			 // String oldcodebase = setNewCodebase(codebase);
			 if(!agentDetails.isAtServer()){
			  Object agt = initialiseAgent(agentDetails.getClassName(), agentDetails.getConfigurl(), agentDetails.getConstraintsurl(), agentDetails.getCodebase());
			  System.out.println("Agt class: " + agt.getClass().getName());
			  if (deployAgent) {
			      System.out.println("Deploying full agent");
			      deployTo((Agent) agt, agentDetails.getDomain());
			  } else {
			      System.out.println("Deploying POJO agent");
			      deployObjectTo(agt, agentDetails.getConstraintsurl(), agentDetails.getConfigurl(), agentDetails.getDomain());
			  }
			 } else {
				 svc.createAgent(agentDetails.getClassName(), agentDetails.getConstraintsurl(), agentDetails.getConfigurl(), agentDetails.getDomain());
			 }
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    		 
    }

    public AgentDeployer(String agentClass, String configuration, String constraints, String codebase, String jar, String group, String serviceID) {
        // TODO Complete constructor stub for AgentDeployer
        System.setSecurityManager(new RMISecurityManager());
        try {
            System.out.println("Starting agent deployment");
           // String oldcodebase = setNewCodebase(codebase);
            Object agt = initialiseAgent(agentClass, configuration, constraints, jar);
            System.out.println("Agt class: " + agt.getClass().getName());
            ServiceID sid = buildServiceID(serviceID);
            System.out.println("Starting deployment process");
            doLookup(group, sid);

            waitForLookup();

            if (svc == null) {
                System.out.println("An Agent Service Instance could not be found");
                System.exit(0);
            }

            if (deployAgent) {
                System.out.println("Deploying full agent");
                String deployto="Global";
                if(((Agent) agt).getConstraints()!=null){
                	deployto  = ((Agent) agt).getConstraints().getConstraints().getDomain();
                }
                deployTo((Agent) agt, deployto);
            } else {
                System.out.println("Deploying POJO agent");
                deployObjectTo(agt, constraints, configuration, "global");
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private ServiceID buildServiceID(String serviceID) {
        ServiceID sid = null;
        if (!serviceID.equals("")) {
            Uuid uuid = UuidFactory.create(serviceID);
            sid = new ServiceID(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
        }
        return sid;
    }

    private void resetCodebase(String oldcodebase) {
        if (oldcodebase == null) {
            System.getProperties().remove("java.rmi.server.codebase");
        } else {
            System.setProperty("java.rmi.server.codebase", oldcodebase);
        }
    }

    private String setNewCodebase(String codebase) {
        System.out.println("Setting codebase to: " + codebase);
        String oldcodebase = System.getProperty("java.rmi.server.codebase");
        System.out.println("Old codebase: " + oldcodebase);
        System.setProperty("java.rmi.server.codebase", codebase);
        System.out.println("Codebase set");
        return oldcodebase;
    }

    private Object initialiseAgent(String agentClass, String configuration, String constraints, String jar) {
        Class agentCl = null;
        Class agentDefCl = null;
      
        if (jar != null) {
            String[] urlStrings = jar.split(" ");

            URL[] urls = new URL[urlStrings.length];
            for (int i = 0; i < urls.length; i++) {
                try {
                    urls[i] = new URL(urlStrings[i]);
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
            for (int i = 0; i < urls.length; i++) {
                System.out.println("URL:" + urls[i].toExternalForm());
            }
            try {
                System.out.println("Running URLClassLoader");
                System.out.println("Parent ClassLoader: " + Thread.currentThread().getContextClassLoader().getClass().getName());
                //  PreferredClassLoader prefCL = new PreferredClassLoader(urls, prefCL, configuration, deployAgent)
                System.out.println("JARS:" + jar);
                ucl = new PreferredClassLoader(urls, this.getClass().getClassLoader(),jar,false);
                agentCl = ucl.loadClass(agentClass);
                //agentDefCl = ucl.loadClass("org.jini.projects.neon.agents.Agent");
                System.out.println("Agent class: " + agentCl.getName());
            } catch (Exception e) {
                System.err.println("Error loading class " + agentClass + " from jar");
                e.printStackTrace();

            }
        } else {
            try {
                System.out.println("Finding agent class...");
                agentCl = Class.forName(agentClass);

            } catch (Exception e) {
                System.err.println("Error loading class " + agentClass + " from classpath");
                e.printStackTrace();
            }
        }
        System.out.println("Agent Class found .... creating");
        try {
            Object ob = (Object) agentCl.newInstance();
            if (ob instanceof Agent) {
                System.out.println("This is an agent class");
                deployAgent = true;
            }
            if (ob instanceof Agent) {
                Agent agt = (Agent) ob;
                if (constraints != null && !constraints.equals("")) {
                    agt.setConstraints(new ReferenceableConstraints(new URL(constraints)));
                }
                if (configuration != null && !configuration.equals("")) {
                    if (configuration.startsWith("class:")) {
                        agt.setInternalConfiguration(true);
                        agt.setInternalConfigurationLocation(configuration.substring(6));
                    } else {
                        agt.setConfigurationLocation(new URL(configuration));
                    }
                }
                return agt;
            } else {

                return ob;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void waitForLookup() {
        long max_wait = 1 * 60 * 1000;
        System.out.println("Waiting");
        try {
            int time = 0;
            while (!finishedDiscovery && time < max_wait) {
                synchronized (this) {
                    wait(1000);
                }
                time += 1000;
            }
            if (finishedDiscovery) {
                System.out.println("Agent Deployer beginning deployment....");
            } else {
                System.out.println("Agent Deployer exiting....timeout expired");
                System.exit(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public AgentDeployer(String definition, String group, String serviceid) {
        try {
            // TODO Auto-generated constructor stub

            File f = new File(definition);
            if (f.exists()) {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                javax.xml.parsers.SAXParser parser = factory.newSAXParser();
                AgentSetHandler handler = new AgentSetHandler();
                parser.parse(f, handler);
                List definitions = handler.getDefinitions();

                ServiceID sid = buildServiceID(serviceid);
                doLookup(group, sid);

                waitForLookup();
                System.out.println("Deploying " + definitions.size() + " agents");
                for (int i = 0; i < definitions.size(); i++) {
                    AgentDeployDefinition def = (AgentDeployDefinition) definitions.get(i);
                    String oldcodebase = setNewCodebase(def.getCodebase());
                    Object agt = initialiseAgent(def.getAgentclass(), def.getAgentconfig(), def.getAgentconstraints(), null);
                    System.out.println("Agent class: " + agt);
                    if (agt instanceof Agent) {
                        deployTo((Agent) agt, "global");
                    } else {
                        deployObjectTo(agt, def.getAgentconstraints(), def.getAgentconfig(),"global");
                    }
                    resetCodebase(oldcodebase);
                }
                if (svc == null) {
                    System.out.println("An Agent Service Instance could not be found");
                    System.exit(0);
                }
                System.out.println("Deployment complete");
            } else {
                System.err.println("Deploy definition file does not exist!");
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }
    private boolean deployed;
    private AgentService svc;

    public void doLookup(final String group, final ServiceID sid) {
        try {
            LookupDiscoveryManager ldm = new LookupDiscoveryManager(new String[]{group}, null, new DiscoveryListener() {

                public void discarded(net.jini.discovery.DiscoveryEvent e) {

                }

                public void discovered(net.jini.discovery.DiscoveryEvent e) {
                    System.out.println("Discovered");
                    try {
                        ServiceRegistrar[] regs = e.getRegistrars();
                        for (int i = 0; i < regs.length; i++) {
                            ServiceTemplate stmpl = new ServiceTemplate(sid, new Class[]{AgentService.class}, null);
                            Object o = regs[i].lookup(stmpl);
                            if (o != null) {
                                svc = (AgentService) o;
                                System.out.println("Found agent service");
                                finishedDiscovery = true;
                            }
                        }
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }

                }
            });
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void deployTo(final Agent cl, String domain) throws Exception {

        svc.deployAgent(cl,domain);
        deployed = true;
        System.out.println("Deployed Agent : " + cl.getClass().getName());

    }

    public void deployObjectTo(final Object cl, String constraints, String config, String domain) throws Exception {

        svc.deployPOJOAgent(cl, constraints, config,domain);
        deployed = true;
        System.out.println("Deployed POJO Agent : " + cl.getClass().getName());

    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        // TODO Complete method stub for main
        System.setSecurityManager(new RMISecurityManager());
        MultiCommandLine mcl = new MultiCommandLine(args);
        String definition = mcl.getString("def", null);
        String serviceid = mcl.getString("serviceid", "");
        String group = mcl.getString("group", "public");
        if (definition == null) {
            String agentClass = mcl.getString("agentclass", null);
            String configuration = mcl.getString("agentconfig", "");
            String constraints = mcl.getString("agentconstraints", "");
            String codebase = mcl.getString("codebase", null);
            String jar = mcl.getString("jar", null);
            if (args.length == 0 || agentClass == null || codebase == null) {
                System.out.print("usage: java AgentDeployer ");
                mcl.usage();
                System.exit(0);
            }
            new AgentDeployer(agentClass, configuration, constraints, codebase, jar, group, serviceid);
        } else {
            System.out.println("Deploying definition file");
            new AgentDeployer(definition, group, serviceid);
        }

    }
}
