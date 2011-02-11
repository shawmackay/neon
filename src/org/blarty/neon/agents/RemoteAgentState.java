package org.jini.projects.neon.agents;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteAgentState extends Remote {
	 public void setRemoteAgentState(AgentState newstate) throws RemoteException;
}
