package org.jini.projects.neon.sample.cyber;

public class Stats {
        private int movementAllowance;
        private int reflexes;
        private int empathy;
        private int intelligence;
        private int tech;
        private int attractiveness;
        private int body;
        private int cool;
        
        public Stats(){                
        }
        
        public Stats(int movementAllowance, int reflexes, int empathy, int intelligence, int tech, int attractiveness, int body, int cool) {
                super();
                this.movementAllowance = movementAllowance;
                this.reflexes = reflexes;
                this.empathy = empathy;
                this.intelligence = intelligence;
                this.tech = tech;
                this.attractiveness = attractiveness;
                this.body = body;
                this.cool = cool;
        }
        public int getAttractiveness() {
                return attractiveness;
        }
        public void setAttractiveness(int attractiveness) {
                this.attractiveness = attractiveness;
        }
        public int getBody() {
                return body;
        }
        public void setBody(int body) {
                this.body = body;
        }
        public int getCool() {
                return cool;
        }
        public void setCool(int cool) {
                this.cool = cool;
        }
        public int getEmpathy() {
                return empathy;
        }
        public void setEmpathy(int empathy) {
                this.empathy = empathy;
        }
        public int getIntelligence() {
                return intelligence;
        }
        public void setIntelligence(int intelligence) {
                this.intelligence = intelligence;
        }
        public int getMovementAllowance() {
                return movementAllowance;
        }
        public void setMovementAllowance(int movementAllowance) {
                this.movementAllowance = movementAllowance;
        }
        public int getReflexes() {
                return reflexes;
        }
        public void setReflexes(int reflexes) {
                this.reflexes = reflexes;
        }
        public int getTech() {
                return tech;
        }
        public void setTech(int tech) {
                this.tech = tech;
        }
                
}
