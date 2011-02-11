package org.jini.projects.neon.neontests.tutorial.simple;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.jini.projects.neon.collaboration.Collaborative;

public interface HelloAgent extends Collaborative, Remote {
    public String sayHello(String name) throws RemoteException;
}
