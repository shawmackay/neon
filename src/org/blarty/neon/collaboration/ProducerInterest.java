/*
 * Apollo : org.jini.projects.neon.collaboration
 * 
 * 
 * ProducerInterest.java
 * Created on 11-Nov-2003
 * 
 * ProducerInterest
 *
 */
package org.jini.projects.neon.collaboration;

import org.jini.projects.zenith.endpoints.Producer;

/**
 * @author calum
 */
public interface ProducerInterest {
    public void informOfProducer(Producer p);
}
