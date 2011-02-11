package org.jini.projects.neon.vertigo.management.store;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;


public class FileHibernationStorage implements HibernationStorage {

    File directory = new File("storage/hibernated");
    
    public FileHibernationStorage(){
        if(!directory.exists());
        directory.mkdirs();
    }
    
    public Agent loadAgent(AgentIdentity id) throws IOException{
        // TODO Auto-generated method stub
        File f = new File(directory,id.toString()+ ".hibernated");
        ObjectInputStream oos = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));
        Agent a;
        try {
            a =(Agent) oos.readObject();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new IOException(e.getMessage());                        
        }
        oos.close();
        return a;
    }

    public void storeAgent(Agent a) throws IOException {
        // TODO Auto-generated method stub
        
        File f = new File(directory,a.getIdentity().toString()+ ".hibernated");
        
        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
        oos.writeObject(a);
        oos.flush();
        oos.close();
        
    }

}
