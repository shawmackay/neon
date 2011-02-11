package org.jini.projects.neon.agents;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.jini.projects.neon.annotations.Agent;
import org.jini.projects.neon.collaboration.CollabAdvert;
import org.jini.projects.neon.collaboration.Collaborative;
import org.jini.projects.neon.collaboration.DelegatedCollabAdvert;

public class DelegatedAgent extends AbstractAgent {

    
    
    private String nameToUse;

    private String namespaceToUse;

    private String initMethodName;


    private transient Object pojo;

    private Object serializePojo;
    
    private String pojoClassName;
    
    public DelegatedAgent(Object delegateTo) throws UnsupportedOperationException {
        pojo = delegateTo;
        if(delegateTo instanceof Serializable)
            serializePojo = delegateTo;
        else
            pojoClassName = delegateTo.getClass().getName();
        Agent a = (Agent) pojo.getClass().getAnnotation(Agent.class);
        nameToUse = a.name();
        namespaceToUse = a.namespace();
        initMethodName = a.init();     
    }

    public void reconnect(){
    	if(pojo==null && serializePojo!=null){
    		//We've been transferred or restarted
    		pojo = serializePojo;
    		pojoClassName = pojo.getClass().getName();
    	}
    	if(pojo==null && pojoClassName!=null){
    		try {
				pojo = Class.forName(pojoClassName).newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

    public CollabAdvert advertise() {
    	
            if (this.advert == null)
                advert = new DelegatedCollabAdvert(this, pojo);
            return advert;
        }
    
    public String getName() {
        // TODO Auto-generated method stub
        return nameToUse;
    }

    public String getNamespace() {
        // TODO Auto-generated method stub
        return namespaceToUse;
    }

    public Class[] getCollaborativeInterfaces() {
        ArrayList<Class> arr = new ArrayList<Class>();
        getInterfaces(pojo.getClass(), arr);
        return (Class[]) arr.toArray(new Class[] {});
    }

    private void getInterfaces(Class cl, ArrayList<Class> arr) {
        Class[] classes = cl.getInterfaces();

        for (int i = 0; i < classes.length; i++) {
            Class subclass = classes[i];
            if (subclass.isInterface()) {
                getInterfaces(subclass, arr);
            }
            if (subclass.equals(Collaborative.class)) {
                if (!arr.contains(cl))
                    arr.add(cl);
            }
        }

        // arr.add(Serializable.class);

    }


    public boolean init() {
        try {
                if(pojo==null){
                    if(serializePojo !=null)
                        pojo = serializePojo;
                    else
                        try {
                            pojo = Class.forName(pojoClassName).newInstance();
                        } catch (InstantiationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                }
            Method m = pojo.getClass().getMethod(initMethodName, new Class[] {});
            Boolean bool = (Boolean) m.invoke(pojo, new Object[] {});
            return bool.booleanValue();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }


    

    public Object getDelegatedObject(){
        return pojo;
    }
    
   
}
