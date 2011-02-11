package org.jini.projects.neon.agents.util.meta;

import net.jini.entry.AbstractEntry;

public class DomainInvocation extends AbstractEntry {
	public String domain;

	public DomainInvocation(String domain) {
		super();
		this.domain = domain;
	}
	
}
