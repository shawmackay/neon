package org.jini.projects.neon.annotations.generators;

import org.jini.projects.neon.annotations.Async;
import org.jini.projects.neon.annotations.Broadcast;
import org.jini.projects.zenith.messaging.messages.Message;
/**
 * Test Class for annotation generators and inspectors.
 * @author Calum
 *
 */
@Async(packagename="org.hmm")
public class AnnotationSample {

    @Broadcast(name="Hello")
    public void SampleBroadcast(){
    
    }
    
    public String sendMessage(Message myMessage){
            return "";
            
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    } 

}
