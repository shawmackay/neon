package org.jini.projects.neon.collaboration;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Used by Collaborative agents to explicitly advertise functionality
 *  
 */
public class DelegatedCollabAdvert extends CollabAdvert{
	
	public DelegatedCollabAdvert(Advertiser ag, Object o) {
        super(ag);
		Method[] meths = ag.getClass().getMethods();
        System.out.println("Passed Object is:"  + o);
		getInterfaces(o.getClass(),arr);
		
		}
	

    
}
