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



package org.jini.projects.neon.agents;

import java.io.Serializable;
import java.net.UnknownHostException;

import net.jini.id.Uuid;
import net.jini.id.UuidFactory;

/**
 * Provides uniqueness among agents
 */
public class AgentIdentity implements Serializable {
	private String originatorIP;
	private long creationTime;
	private Uuid ID;
	public static final long serialVersionUID = 4057062707324824434L;

	/**
	 * Provides data about the agent ID. An agent ID is guaranteed to be unique.
	 * It is constructed from a combination of the IP address of the originator,
	 * the current system time and a generated Uuid. In most circumstances the
	 * Uuid will be sufficient to differentiate between agents
	 */
	public AgentIdentity() {
		try {

			if (ID == null) {
				
				originatorIP = java.net.InetAddress.getLocalHost().getHostAddress();
				creationTime = System.currentTimeMillis();

				ID = UuidFactory.generate();
				// System.out.println("Identity is: " + this.ID);
			}
		} catch (UnknownHostException e) {

			System.out.println("Err: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public AgentIdentity(Uuid withID) {
		try {

			// System.out.println("Generating Identity");
			originatorIP = java.net.InetAddress.getLocalHost().getHostAddress();
			creationTime = System.currentTimeMillis();

			ID = withID;
			// System.out.println("Identity is: " + this.ID);

		} catch (UnknownHostException e) {

			System.out.println("Err: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public boolean equals(Object o) {
		if (o instanceof AgentIdentity) {
			AgentIdentity cmp = (AgentIdentity) o;
			if (cmp.getOriginatorIP().equals(originatorIP)) {
				if (cmp.getCreationTime() == creationTime) {
					if (cmp.getID().equals(ID)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public AgentIdentity(String ID) {
		String[] parts = ID.split("\\|");
		
		if (parts.length != 3)
			throw new RuntimeException("AgentIdentity not in correct format");
		originatorIP = parts[0];
		creationTime = Long.parseLong(parts[1]);
		this.ID = UuidFactory.create(parts[2]);
	}

	public Uuid getID() {
		return ID;
	}

	public String toString() {
		return getID().toString();
	}

	public String getExtendedString() {
		return originatorIP + "|" + creationTime + "|" + ID.toString();
	}

	/**
	 * The timestamp for the agent
	 * @return time the agent was first created
	 */
	public long getCreationTime() {
		return creationTime;
	}

	/**
	 * Gets the IP address for the system that created this agent. This is not
	 * necessarily going to be a Neon instance, but can also be the address of the
	 * machine that first deployed the agent. 
	 * @return IP address of the system that spawned this agent
	 */
	public String getOriginatorIP() {
		return originatorIP;
	}

	public int hashCode() {
		return originatorIP.hashCode() + ID.hashCode();		
	}

	public static void main(String[] args) {
		AgentIdentity id1 = new AgentIdentity();
		System.out.println("ID1: " + id1.getExtendedString());
		AgentIdentity id2 = new AgentIdentity(id1.getExtendedString());
		System.out.println("ID2: " + id2.getExtendedString());
		System.out.println("ID1<=>ID2 ? " + id1.equals(id2));
	}

}
