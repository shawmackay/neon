package org.jini.projects.neon.host;

import net.jini.entry.AbstractEntry;

public class BootstrappedAgent extends AbstractEntry {
    public boolean isBootstrappedIn;
    
    public BootstrappedAgent(boolean bootstrappedin){
        this.isBootstrappedIn = bootstrappedin;
    }
}
