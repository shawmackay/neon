package org.jini.projects.neon.agents.util.meta;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.jini.entry.AbstractEntry;
/*
 * Describes a set of agents that the agent, that has this meta entry, depends on for operation 
 */
public class AgentDependencies extends AbstractEntry {

    public Set dependencies;
    
    public AgentDependencies() {
        super();
        // TODO Auto-generated constructor stub
        dependencies = new HashSet();        
    }

    /**
     * Returns whether or not the agent has a dependency on another agent name.<br>
     * <b>Note: </b>It is important to note that this only reflects the dependencies that Neon has noted during the lifecycle
     * of the agent <em>thus far</em>, and is therefore not guaranteed to be consistent. However, once Neon discovers that 
     * an agent is being requested, the dependency will always be noted.
     * @param agentName
     * @return whether the agent has shown a dependency on this agentname.
     */
    public boolean hasCurrentDependency(String agentName){
        return dependencies.contains(agentName);
    }
    
    public void addDependency(String agentName){        
        dependencies.add(agentName);
    }
    
    public Set getDependencies(){
        return dependencies;
    }
    
    public String toString(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("Agent Depends on:");
        for(Iterator iter = dependencies.iterator();iter.hasNext();){
            buffer.append(" [" + iter.next() + "]");
        }
        return buffer.toString();
    }
}
