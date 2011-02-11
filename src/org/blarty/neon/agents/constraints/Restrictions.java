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
 * neon : org.jini.projects.neon.agents.newconstraints
 * Restrictions.java
 * Created on 09-Mar-2005
 *Restrictions
 */

package org.jini.projects.neon.agents.constraints;

import java.util.List;

/**
 * Imposes a set of restrictions against the agent, including what classes are available to the JVM,
 * and any meta (i.e. user configured) constraints have been imposed.
 * @author calum
 */
public interface Restrictions {
    /**
     * Match against a set of meta data defined as Entries. Any entries used
     * in the meta information, must be available on the local classpath of the neon instance
     * and not in the agent download (dl) file, as constraints are evaluated before the agent is sent
     * to the instance.
     * @return a list of <code>Entry</code> objects.
     */
	public List getMeta();
    /**
     * Get a list of classes that should be available on the host system, within the main system ClassLoader
     * Such that <code>Class.forName(<i>X</i>)</code> return true for each X in the list  
     * @return a list of Strings representing class names of type
     */
	public List getRequiredClasses();
    /**
     * Return the set of system requirements for the agent
     * @return the System constraints
     */
	public SystemConstraints getSystemConstraints();
    /**
     * Return a list of agent names (<i>&quot;namespace&quot;.&quot;name&quot;</i>) that the domain
     * needs to be hosting at deployment time. <i>Currently, not evaluated</i>
     * @return a list of Strings of agent names (i.e <i>String[]{"neon.Services","neon.CheckPoint"}</i>,etc
     */
	public List getInstances();
}

