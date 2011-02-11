package org.jini.projects.neon.service;

import java.rmi.RemoteException;
import java.rmi.activation.ActivationID;

import net.jini.security.TrustVerifier;

public interface ServiceStartupIntf {

    public abstract Object getProxy();

    public abstract TrustVerifier getProxyVerifier();

    /**
     * Begins the kernel
     * 
     * @param deployMobile
     */
    public abstract void init(String[] args, final ActivationID aid) throws RemoteException;

}