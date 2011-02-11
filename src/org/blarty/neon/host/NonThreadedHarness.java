package org.jini.projects.neon.host;

import java.util.logging.Logger;

import org.jini.projects.neon.agents.Agent;


public class NonThreadedHarness implements AgentHarness {

        private Agent worker;

        Logger l = Logger.getLogger("org.jini.projects.neon.threads");

        public NonThreadedHarness(Agent worker) {
                this.worker = worker;
        }

        public Agent getWorker() {
                // TODO Auto-generated method stub
                return null;
        }

        public void go() {
                // TODO Auto-generated method stub

                if (!worker.init()) {
                        l.warning("Initialisation of " + worker.getName() + " not completed => Decommisioning");
                        worker.complete();
                }
                worker = null;

        }
}
