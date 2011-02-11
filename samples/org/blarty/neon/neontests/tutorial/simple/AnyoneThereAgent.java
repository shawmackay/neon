package org.jini.projects.neon.neontests.tutorial.simple;

import java.rmi.RemoteException;
import java.util.List;

import org.jini.projects.neon.agents.AbstractAgent;

import org.jini.projects.neon.agents.util.meta.AgentDependencies;
import org.jini.projects.neon.host.NoSuchAgentException;

public class AnyoneThereAgent extends AbstractAgent implements Runnable{

    @Override
    public boolean init() {
        return true;
    }

    
    public void run() {
        try {
            HelloAgent hello = (HelloAgent) context.getAgent("examples.Hello");
            getAgentLogger().info(hello.sayHello(System.getProperty("user.name")));
            getAgentLogger().fine("I got a response!");
            
        } catch (NoSuchAgentException e) {
            getAgentLogger().info("No one there....");
            e.printStackTrace();
        } catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
       
    }
}
