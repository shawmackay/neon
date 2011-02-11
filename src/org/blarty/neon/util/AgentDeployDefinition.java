package org.jini.projects.neon.util;

public class AgentDeployDefinition {
        private String agentclass;
        private String agentconfig;
        private String agentconstraints;
        private String codebase;
        public String getAgentclass() {
                return agentclass;
        }
        public void setAgentclass(String agentclass) {
                this.agentclass = agentclass;
        }
        public String getAgentconfig() {
                return agentconfig;
        }
        public void setAgentconfig(String agentconfig) {
                this.agentconfig = agentconfig;
        }
        public String getAgentconstraints() {
                return agentconstraints;
        }
        public void setAgentconstraints(String agentconstraints) {
                this.agentconstraints = agentconstraints;
        }
        public String getCodebase() {
                return codebase;
        }
        public void setCodebase(String codebase) {
                this.codebase = codebase;
        }
}
