package org.jini.projects.neon.dynproxy.invoker;

import java.lang.reflect.Method;

import org.jini.projects.neon.host.ManagedDomain;

public class StartableAgentDelegate implements InvokerDelegate {

        public Object invokeDelegate(Method m, Object source, Object originalObject, Object receiver, Object[] args) throws Throwable {
                // TODO Auto-generated method stub
                
                System.out.println("Redirecting start method to original agent");
                Method newMethod = originalObject.getClass().getMethod("start", new Class[0]);
                
                return newMethod.invoke(originalObject, new Object[0]);
                
        }

        public void setDomain(ManagedDomain domain) {
                // TODO Auto-generated method stub

        }

}
