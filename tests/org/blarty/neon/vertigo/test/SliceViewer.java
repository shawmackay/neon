package org.jini.projects.neon.vertigo.test;

import net.jini.config.ConfigurationProvider;
import net.jini.core.lookup.ServiceID;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.tests.MockAgent;
import org.jini.projects.neon.vertigo.management.AgentAction;
import org.jini.projects.neon.vertigo.management.AgentListObject;
import org.jini.projects.neon.vertigo.management.SliceManager;
import org.jini.projects.neon.vertigo.management.SliceManagerImpl;
import org.jini.projects.neon.vertigo.management.VetoResult;
import org.jini.projects.neon.vertigo.slice.AnchorableSlice;
import org.jini.projects.neon.vertigo.slice.AttractorSlice;
import org.jini.projects.neon.vertigo.slice.RepellerSlice;
import org.jini.projects.neon.vertigo.slice.Slice;

import junit.framework.TestCase;

public class SliceViewer
	extends
	TestCase {

    SliceManager sliceMgr;

    Agent mockAgent1 =new MockAgent("mock1", "neon.tests");

    Agent mockAgent2 =new MockAgent("mock2", "neon.tests");

    ServiceID mockLocalServiceID;

    ServiceID mockRemoteServiceID;

    protected void setUp() throws Exception {

	super.setUp();
	Uuid uid =UuidFactory.generate();
	mockLocalServiceID =new ServiceID(uid.getMostSignificantBits(), uid.getLeastSignificantBits());
	uid =UuidFactory.generate();
	mockRemoteServiceID =new ServiceID(uid.getMostSignificantBits(), uid.getLeastSignificantBits());
	this.sliceMgr =new MockSliceManagerImpl(ConfigurationProvider.getInstance(new String[] { "src/conf/slicemgr.config" }), mockLocalServiceID);

    }

    public void testRepeller() {
	Slice s =new RepellerSlice();
	/*s.attachAgent(new AgentListObject mockAgent1.getIdentity());
	s.attachAgent(mockAgent2.getIdentity());
	assertEquals(s.vetoes(AgentAction.DEPLOY, mockAgent2.getIdentity(), sliceMgr, null, mockLocalServiceID), VetoResult.WARNING);
	s.setEnforced(true);
	assertEquals(s.vetoes(AgentAction.DEPLOY, mockAgent2.getIdentity(), sliceMgr, null, mockLocalServiceID), VetoResult.NO);
	assertEquals(s.vetoes(AgentAction.DEPLOY, mockAgent2.getIdentity(), sliceMgr, null, mockRemoteServiceID), VetoResult.YES);
    }
    */
	
    }
    public void testAttractor() {
	/*AnchorableSlice s =new AttractorSlice();
	s.attachAgent(mockAgent1.getIdentity());
	s.attachAgent(mockAgent2.getIdentity());
	assertEquals(VetoResult.YES,s.vetoes(AgentAction.DEPLOY, mockAgent2.getIdentity(), sliceMgr, null, mockLocalServiceID));
	s.setEnforced(true);
	assertEquals(VetoResult.NO,s.vetoes(AgentAction.DEPLOY, mockAgent2.getIdentity(), sliceMgr,mockLocalServiceID, mockRemoteServiceID));	
	assertEquals(VetoResult.WARNING,s.vetoes(AgentAction.DEPLOY, mockAgent2.getIdentity(), sliceMgr, null, mockRemoteServiceID));
	s.setAnchorAgent(mockAgent2.getIdentity());
	assertEquals(VetoResult.ALL,s.vetoes(AgentAction.DEPLOY, mockAgent2.getIdentity(), sliceMgr,mockLocalServiceID, mockRemoteServiceID));	
	assertEquals(VetoResult.ALL,s.vetoes(AgentAction.DEPLOY, mockAgent2.getIdentity(), sliceMgr, null, mockRemoteServiceID));
	s.setAnchorAgent(mockAgent1.getIdentity());
	assertEquals(VetoResult.NO,s.vetoes(AgentAction.DEPLOY, mockAgent2.getIdentity(), sliceMgr,mockLocalServiceID, mockRemoteServiceID));	
	assertEquals(VetoResult.WARNING,s.vetoes(AgentAction.DEPLOY, mockAgent2.getIdentity(), sliceMgr, null, mockRemoteServiceID));
    */}

    protected void tearDown() throws Exception {
	super.tearDown();
    }

}
