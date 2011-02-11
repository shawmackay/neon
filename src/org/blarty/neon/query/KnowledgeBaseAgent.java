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


package org.jini.projects.neon.query;


import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.LocalAgent;
import org.jini.projects.neon.agents.NonContinuousAgent;
//import org.xmldb.api.base.XMLDBException;

/**
 * Allows querying of the Knowledge base and storage of new data to it
 */
public class KnowledgeBaseAgent extends AbstractAgent implements LocalAgent, KnowledgeBase,NonContinuousAgent {
    KnowledgeBaseImpl kb = new KnowledgeBaseImpl();
    
    public KnowledgeBaseAgent() {
        this.name = "KnowledgeBase";
        this.namespace = "Neon";
        
    }
    
    
    
    public boolean init() {
//		try {
//			kb.initialise();
//		} catch (XMLDBException e) {
//			// TODO Handle XMLDBException
//			e.printStackTrace();
//			return false;
//		} catch (Exception e) {
//			// TODO Handle Exception
//			e.printStackTrace();
//			return false;
//		}
        return true;
    }
    
    
    
    
    public void StoreData(Agent agent, String name, String data) {
//		try {
//			kb.setCurrentCollection("/db","agents",true);
//			kb.setCurrentCollection("/db/agents",agent.getName(), true);
//			String newdata = "<agentinfo name=\"" + agent.getName() + "\" date=\"" + new java.util.Date() + "\" " + "identity=\"" + agent.getIdentity().toString() + "\">" + data + "</agentinfo>";
//			kb.storeToCollection(name+"~"+agent.getIdentity().toString(), newdata);
//			kb.executeQuery("\\agent[@name='" + agent.getName() + "']");
//		} catch (Exception e) {
//			System.out.println("Err: " + e.getMessage());
//			e.printStackTrace();
//		}
    }
    
    public void Query(String agentName, String query){
//		try {
//			kb.setCurrentCollection("/db/agents", agentName,false);
//			kb.executeQuery(query);
//			StringBuffer Commarea = new StringBuffer();
//		} catch (XMLDBException e) {
//			// TODO Handle XMLDBException
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Handle Exception
//			e.printStackTrace();
//		}
    }
}
