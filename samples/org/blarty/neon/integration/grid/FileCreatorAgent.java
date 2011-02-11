package org.jini.projects.neon.integration.grid;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.jini.projects.neon.collaboration.Collaborative;

public interface FileCreatorAgent extends Collaborative, Remote{

	public abstract void setMessage(String message) throws RemoteException;

}