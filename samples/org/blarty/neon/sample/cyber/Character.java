package org.jini.projects.neon.sample.cyber;

import java.util.ArrayList;
import java.util.List;

public class Character {
        private String name;
        private int age;
        private String career;
        private Stats originalStats;
        private Stats currentStats;
        private List skills;
        
        public Character(){
                this("UNKNOWN", -1, "UNKNOWN");
        }
        
        public Character(String name, int age, String career){
                this.name = name;
                this.age = age;
                this.career = career;
                this.skills = new ArrayList();
                this.originalStats = new Stats();
                this.currentStats = new Stats();
        }
        
        public int getAge() {
                return age;
        }
        public void setAge(int age) {
                this.age = age;
        }
        public Stats getCurrentStats() {
                return currentStats;
        }
        public void setCurrentStats(Stats currentStats) {
                this.currentStats = currentStats;
        }
        public String getName() {
                return name;
        }
        public void setName(String name) {
                this.name = name;
        }
        public Stats getOriginalStats() {
                return originalStats;
        }
        public void setOriginalStats(Stats originalStats) {
                this.originalStats = originalStats;
        }
        public String getCareer() {
                return career;
        }
        public void setCareer(String role) {
                this.career = role;
        }
        public List getSkills() {
                return skills;
        }
        public void addSkill(String skill) {
                this.skills.add(skill);
        }
        
}
