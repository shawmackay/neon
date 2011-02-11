/*
 * Apollo : org.jini.projects.neon.collaboration
 * 
 * 
 * Advertiser.java
 * Created on 29-Jul-2003
 *
 */
package org.jini.projects.neon.collaboration;

import net.jini.id.Uuid;
import org.jini.projects.zenith.messaging.messages.Message;

/**
 * Specifies that a class wishes to  be advertised to other parts of the system
 * @author calum
 */
public interface Advertiser {

	/**
	 * Called by the collaboration subscriber after initialisation to register
	 * the messages that the Agent can receive. All agents can be collaborated with
	 * @return
	 */
	public CollabAdvert advertise();
    
    public void unadvertise();

    public Message getCurrentMessage();

    public void setCurrentMessage(org.jini.projects.zenith.messaging.messages.Message newMessage);
    
    public void clearCurrentMessage();
	
	public Uuid getSubscriberIdentity();
    
    public String getName();
    
    public boolean isLocked();
    
    public boolean isBusy();
}
