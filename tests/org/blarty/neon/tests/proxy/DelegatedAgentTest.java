package org.jini.projects.neon.tests.proxy;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.annotations.tests.SimplePOJO;
import org.jini.projects.neon.dynproxy.PojoAgentProxyFactory;
import org.jini.projects.neon.host.AgentDomainImpl;

import junit.framework.TestCase;

public class DelegatedAgentTest
                extends
                TestCase {

        protected void setUp() throws Exception {
                super.setUp();
                
        }

        /*
         * Test method for 'org.jini.projects.neon.dynproxy.PojoAgentProxyFactory.create(Object)'
         */
        public void testCreate() {
                Agent a = PojoAgentProxyFactory.create(new SimplePOJO());
                a.init();
                a.setAgentContext(null);
        }

}
