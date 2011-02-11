package org.jini.projects.neon.tests.agents;

import java.io.File;

import org.jini.projects.neon.users.agents.UserDataAgent;

import junit.framework.TestCase;

public class UserDataTest extends TestCase {

        UserDataAgent agent;
        
        protected void setUp() throws Exception {
                agent = new UserDataAgent();
                agent.setConfigurationLocation(new File("tests/conf/userdata.config").toURL());
                agent.init();
                
        }

        
        public void testAuthentication(){
                assertNotNull(agent.authenticateUser("johns", "sesame".toCharArray()));
        }
}
 