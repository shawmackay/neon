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
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import net.jini.core.entry.Entry;
import net.jini.lookup.entry.Location;
import net.jini.lookup.entry.Name;

import org.jini.projects.neon.dynproxy.MethodInvocation;
import org.jini.projects.neon.util.MetaMatcher;


/**
 * Holder class for meta information and attributed for an entity.
 * The entity may be an agent, a MethodInvocation from another host, or used in deployment
 * @see Agent
 * @see MethodInvocation
 * @author Calum
 */
public class Meta implements Serializable{
    
    /**
         * 
         */
        private static final long serialVersionUID = 7503414901082273088L;
private List metaList;
    
   public Meta(){
       metaList = new ArrayList();
   }
    
    public Entry[] toEntries(){
        Entry[] attribs = (Entry[]) metaList.toArray(new Entry[] {});
        return attribs;
    }
    
    public void addAttribute(Entry e){
        metaList.add(e);
    }
    
    public boolean removeAttribute(Entry e){        
            return metaList.remove(e);        
    }
    
    public void addAll(Entry[] list){
        for(int i=0;i<list.length;i++){
            addAttribute(list[i]);
        }
    }
    
 
    
    public <A extends Entry> List<A> getMetaOfType(Class<A> cl){
        ArrayList<A> l = new ArrayList<A>();
        for(int i=0;i<metaList.size();i++){
            if(cl.isInstance(metaList.get(i)))
                l.add((A)metaList.get(i));
        }
        return l;
    }
    
    public void removeAll(){
        metaList.clear();
    }
    
    public int size(){
        return metaList.size();
    }
    
    public String toString() {
            StringBuffer buffer = new StringBuffer();
            buffer.append("{Meta : " + metaList.size() + " [");
            for(int i=0;i<metaList.size();i++){
                    buffer.append(metaList.get(i).getClass().getName());
                    if(i<(metaList.size()-1))
                            buffer.append(", ");
            }
            buffer.append("] }");
            return buffer.toString();
    }
    
    public boolean matches(Meta toMatch){
        MetaMatcher matcher = new MetaMatcher();
        return matcher.match(toEntries(),toMatch.toEntries());
    }
    
    public static void main(String[] args){
        Meta meta = new Meta();
        
        meta.addAttribute(new Name("Calum"));
        meta.addAttribute(new Location("2","Bart's Room","42 Evergreen Terrace"));
        
        System.out.println("Meta matches itself: " + meta.matches(meta));
    
        Meta template = new Meta();
        System.out.println("Meta matches empty template: " + meta.matches(template));
        System.out.println("Empty template  matches meta: " + template.matches(meta));
        template.addAttribute(new Name(null));
        System.out.println("Meta matches empty Name template: " + meta.matches(template));
        System.out.println("Empty Name template  matches meta: " + template.matches(meta));
        template.removeAll();
        template.addAttribute(new Name("Cal"));
        System.out.println("Meta matches wrong Name template: " + meta.matches(template));
        System.out.println("Wrong Name template  matches meta: " + template.matches(meta));
        template.removeAll();
        template.addAttribute(new Name("Calum"));
        System.out.println("Meta matches right Name template: " + meta.matches(template));
        System.out.println("Right Name template  matches meta: " + template.matches(meta));
        template.addAttribute(new Location(null,null,null));
        System.out.println("Meta matches right Name, Empty Loc template: " + meta.matches(template));
        System.out.println("Right Name, Empty Loc template  matches meta: " + template.matches(meta));
        System.out.println("Template size pre-match removal: " + template.size());
        template.removeAttribute(new Location(null,null,null));
        System.out.println("Template size post-match removal: " + template.size());
        template.addAttribute(new Location("2","Bart's Room","42 Evergreen Terrace"));
        System.out.println("Meta matches right Name, Right Loc template: " + meta.matches(template));
        System.out.println("Right Name, Right Loc template  matches meta: " + template.matches(meta));
        
    }
}
