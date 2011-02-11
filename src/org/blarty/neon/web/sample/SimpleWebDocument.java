package org.jini.projects.neon.web.sample;

import java.util.ArrayList;
import java.util.List;

public class SimpleWebDocument {
        
        private List entries;
        private List parameters;
        
        public SimpleWebDocument(){
                entries = new ArrayList();
                parameters = new ArrayList();
        }
        
        public List getEntries(){
                return entries;
        }
        
        public List getParameters(){
                return parameters;
                
        }
        
        public void addEntry(String name, String value){
                entries.add(new DataEntry(name, value));
        }
        
        public void addParameter(String name, String value){
                parameters.add(new DataEntry(name, value));
        }
        
        public void addParameter(String name, String[] value){
                parameters.add(new DataEntry(name, value));
        }
}
