/*
 * CollaborationDirectProxy.java
 *
 * Created on 11 August 2006, 10:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jini.projects.neon.dynproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.logging.Logger;

import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.host.transactions.NonTransactionalResource;
import org.jini.projects.neon.host.transactions.TransactionalResource;

/**
 *
 * @author calum
 */
public class CollaborationDirectProxy implements InvocationHandler{
    
    private Object theAgent;
    private Object caller;
    private AgentIdentity theID;
    Logger l = Logger.getLogger("org.jini.projects.neon.dynproxy");
    
    /** Creates a new instance of CollaborationDirectProxy */
    public CollaborationDirectProxy(Object theAgent, Object caller, AgentIdentity theID) {
        this.theAgent = theAgent;
        this.caller = caller;
        this.theID = theID;
    }
    
    public AgentIdentity getIdentity() {
        return theID;
}
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (caller instanceof TransactionalResource) {
            l.finest("...trying to Propagate Transaction");
            TransactionalResource res = (TransactionalResource) caller;
            if (res.getTransaction() != null) {
                String txnIdentifier = res.getTransaction();
                l.finest("transaction Propagation Confirmed CP = " + txnIdentifier);
                if(!(theAgent instanceof NonTransactionalResource)){
                    TransactionalResource theAgentTxnRes = (TransactionalResource) theAgent;
                    if(theAgentTxnRes.inTransaction() ){
                        if(!theAgentTxnRes.getTransaction().equals(txnIdentifier))
                            l.severe("Attempting to propagate transaction but called agent is in another txn.");
                    } else
                        theAgentTxnRes.setTransaction(txnIdentifier);
                    
                }
                
            }
        }
//        System.out.println("theAgent class to be called is of type" + theAgent.getClass());
//        if(theAgent instanceof Proxy){
//            System.out.println("Proxy InvHandlr is of type " + Proxy.getInvocationHandler(theAgent).getClass());
//        }
                
        return method.invoke(theAgent,args);
    }
    
}
