/*
 * Copyright 2005 neon.jini.org project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



/*
 * neon : org.jini.projects.neon.org.jini.projects.neon.neontests.tutorial.payload
 * SimplePayloadAgent.java Created on 13-Nov-2003
 *SimplePayloadAgent
 */
package org.jini.projects.neon.neontests.tutorial.payload;

import java.util.ArrayList;
import java.util.List;

import org.jini.projects.neon.agents.AbstractAgent;


/**
 * @author calum
 */
public class SimplePayloadAgent extends AbstractAgent {
    
    /**
     *
     */
    public SimplePayloadAgent() {
        super();
        // URGENT Complete constructor stub for SimplePayloadAgent
    }
    
    /*
     * @see org.jini.projects.neon.agents.Agent#init()
     */
    public boolean init() {
        getAgentLogger().fine("SimplePayloadAgent Deployed");
        return false;
    }
    
    
    
    /*
     * @see org.jini.projects.neon.agents.Agent#stop()
     */
    
    public void findPrimes(int start, int end) {
        List list = new ArrayList(100);
        long tstart = System.currentTimeMillis();
        if (start < 2)
            start = 2;
        for (int i = start; i < end; i++) {
            if (i % 5000 == 0)
                System.out.println(i);
            boolean prime = true;
            int loopend = (i / 2) + 1;
            
            for (int j = 2; j < loopend; j++) {
                
                if (i % j == 0)
                    prime = false;
                
            }
            if (prime)
                list.add(new Integer(i));
        }
        long tend = System.currentTimeMillis();
        System.out.println("Time taken:" + (tend - tstart) + " ms");
        System.out.println("There are " + list.size() + " prime numbers between " + start + " and " + end);
        // System.out.println(list);
    }
    
    public void findPrimes2(int start, int end) {
        List list = new ArrayList(1000);
        long tstart = System.currentTimeMillis();
        if (start < 2)
            start = 2;
        for (int i = start; i < end; i++) {
            if (i % 100000 == 0)
                System.out.println(i);
            boolean prime = true;
            int loopend = (i / 2) + 1;
            if (i == 2)
                list.add(new Integer(2));
            if (i % 2 == 0)
                prime = false;
            else
                for (int j = 3; j < loopend & prime; j = j + 2) {
                if (i % j == 0)
                    prime = false;
                
                }
            if (prime)
                list.add(new Integer(i));
        }
        long tend = System.currentTimeMillis();
        long tsecs = (tend-tstart)/1000;
        System.out.println("Time taken:" + (tend - tstart) + " ms (" + tsecs+ " s or " +tsecs/60 +"m " + tsecs%60 + "s)");
        System.out.println("There are " + list.size() + " prime numbers between " + start + " and " + end);
        // System.out.println(list);
    }
    public void findPrimes3(int start, int end) {
        List list = new ArrayList(1000);
        long tstart = System.currentTimeMillis();
        if (start < 2)
            start = 2;
        for (int i = start; i < end; i++) {
            // if (i % 100000 == 0)
            //    System.out.println(i);
            boolean prime = true;
            int loopend = (i / 2) + 1;
            if (i == 2)
                list.add(new Integer(2));
            if (i % 2 == 0)
                prime = false;
            else
                for (int j = 3; j < loopend & prime; j = j + 2) {
                if (i % j == 0)
                    prime = false;
                
                }
            if (prime)
                list.add(new Integer(i));
        }
        long tend = System.currentTimeMillis();
        System.out.println("Time taken:" + (tend - tstart) + " ms");
        System.out.println("There are " + list.size() + " prime numbers between " + start + " and " + end);
        // System.out.println(list);
    }
    
    public static void main(String[] args) {
        SimplePayloadAgent app = new SimplePayloadAgent();
        //app.findPrimes(00000, 100000);
        app.findPrimes2(0,Integer.parseInt(args[0]));
        //    app.findPrimes3(00000, 100000);
    }
    
}
