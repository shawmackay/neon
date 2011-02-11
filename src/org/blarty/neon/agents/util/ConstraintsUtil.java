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

package org.jini.projects.neon.agents.util;

import java.util.List;
import java.util.logging.Logger;

import org.jini.projects.neon.agents.AgentConstraints;
import org.jini.projects.neon.agents.ConfigurationConstraints;
import org.jini.projects.neon.agents.constraints.AgentRequirements;
import org.jini.projects.neon.agents.constraints.Restrictions;
import org.jini.projects.neon.agents.constraints.SystemConstraints;



/**
 * Used in the evaluation in agent constraints
 */
public class ConstraintsUtil {

    private static Logger constraintsLogger = Logger.getLogger("org.jini.projects.neon.util.constraints");

    /**
     * Evaluates a particular set of constraints against the current system configuration
     * @param constraints The constraints to check
     * @return Whether the constraints/requirements can be satisfied.
     */
    public static boolean evaluateConstraints(AgentConstraints constraints) {
        constraintsLogger.fine("Checking constraints");
        AgentRequirements agrestrict = constraints.getConstraints();
        Restrictions res = agrestrict.getSystemRestrictions();
        SystemConstraints sys = res.getSystemConstraints();

        if (!evaluateSystemLevelConstraints(sys))
            return false;
        return true;
    }

    private static boolean evaluateClassLevelConstraints(List classList) {        
        for (int i = 0; i < classList.size(); i++) {
            String cl = (String) classList.get(i);            
            try {
                Class.forName(cl);
            } catch (ClassNotFoundException e) {
                // TODO Handle ClassNotFoundException
                e.printStackTrace();
                return false;
            }

        }
        return true;

    }

    private static boolean evaluateSystemLevelConstraints(SystemConstraints sys) {
        Runtime r = Runtime.getRuntime();

        //CheckNumber of processors
        constraintsLogger.finer("Processors");
        if (r.availableProcessors() < sys.getMinCPUNumber())
            return false;

        //Evaluate the %age free and minimum amount free values for memory
        constraintsLogger.finer("FreeMem");
        int pcfree = sys.getMemPercentFree();
        int minMem = sys.getMemMinimumMBFree();

        long total = r.totalMemory();
        long free = r.freeMemory();

        int freem = (int) ((double) free / (1024 * 1024));
        int pcfreem = (int) (((double) free / (double) total) * 100);
        
        constraintsLogger.finer("Needed: " + minMem + ", Actual: " + freem);
        if (freem < minMem)
            return false;

        constraintsLogger.finer("%age FreeMem");
        if (pcfreem < pcfree) {
            constraintsLogger.finer("PCFREEM: " + pcfreem + " < " + pcfree);
            return false;
        }
        //Will need to do major.minor.revision checking here
        constraintsLogger.finer("JVM Checks");
        if (!checkVersion(sys.getJVMVersion(), System.getProperty("java.version")))
            return false;
        
        System.out.println("Checking Operating System");
        if (sys.getOSFamily() != null && !(sys.getOSFamily().toLowerCase().equals("any")))
            if (System.getProperty("os.name").indexOf(sys.getOSFamily()) == -1)
                return false;
        System.out.println("Checking Architecture");
        //Need to handle Os.version as a string inc Linux Kernel numbers
        if (sys.getOSArchitecture() != null && !(sys.getOSArchitecture().toLowerCase().equals("any")))
            if (!sys.getOSArchitecture().equals(System.getProperty("os.arch")))
                return false;

        // If we get here the system requirements for this code can be met
        return true;
    }

    private static boolean checkVersion(String minimumVersion, String actualVersion) {
    	if(minimumVersion==null){
    		return true;
    	}
        String[] minBits = minimumVersion.split("(\\.|_)");
        String[] actualBits = actualVersion.split("(\\.|_)");
        for (int i = 0; i < minBits.length; i++)
            if (actualBits[i].compareTo(minBits[i]) >= 0)
                return false;
        return true;
    }
	
	public static void main(String[] args){
		AgentConstraints con= new ConfigurationConstraints("/home/calum/workspace/neon/constraints.config");
		evaluateConstraints(con);
	}
}
