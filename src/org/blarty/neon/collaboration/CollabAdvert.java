package org.jini.projects.neon.collaboration;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jini.projects.neon.agents.RemoteAgentState;

/**
 * Used by Collaborative agents to explicitly advertise functionality
 *  
 */
public class CollabAdvert {
	protected HashMap adverts = new HashMap();
	protected ArrayList arr;

	/**
	 * @param ag
	 */

	public CollabAdvert(Advertiser ag) {
        
		Method[] meths = ag.getClass().getMethods();
        arr = new ArrayList();
		getInterfaces(ag.getClass(),arr);
		for (int i = 0; i < meths.length; i++) {
			if (meths[i].getName().startsWith("handle")) {
				Method meth = meths[i];
				String name = meth.getName().substring(6);				
				adverts.put(name, new Handle(meth.getName(), meth.getParameterTypes(), meth.getReturnType()));
			}
		}
	}

    public List getCollaborativeMethods(){
        ArrayList methList = new ArrayList();
        for(Iterator iter = arr.iterator();iter.hasNext();){
            Class cl = (Class) iter.next();
            Method[] methods = cl.getMethods();
            for(int i=0;i<methods.length;i++)
                methList.add(methods[i]);           
        }        
        return methList;
    }
    
	protected void getInterfaces(Class cl, ArrayList arr) {
		Class[] classes = cl.getInterfaces();

		for (int i = 0; i < classes.length; i++) {
			Class subclass = classes[i];
			if (subclass.isInterface()) {
				getInterfaces(subclass,arr);
			}
            if(subclass.equals(Collaborative.class)) {
                if(!arr.contains(cl))
                	arr.add(cl);   
            }
		}
		if(!arr.contains(RemoteAgentState.class))
        arr.add(RemoteAgentState.class);
        //arr.add(Serializable.class);
        		
	}
    
    

	public void addAdvert(Method meth) {
		String name = meth.getName();
		Class[] params = meth.getParameterTypes();
		Class ret = meth.getReturnType();
		adverts.put(name, new Handle(name, params, ret));
	}

	public void addAdvert(String alias, Method meth) {
		String name = meth.getName();
		Class[] params = meth.getParameterTypes();
		Class ret = meth.getReturnType();
		adverts.put(alias, new Handle(name, params, ret));
	}

	public Handle getAdvert(String name) {
		return (Handle) adverts.get(name);
	}
    
    public Class[] getCollaborativeClasses(){
        return (Class[]) arr.toArray(new Class[0]); 
       
    }

	public class Handle {
		String name;
		Class[] params;
		Class ret;

		public String getName() {
			return name;
		}

		public Class[] getParams() {
			return params;
		}

		public Class getRet() {
			return ret;
		}

		public Handle(String name, Class[] params, Class ret) {
			this.name = name;
			this.params = params;
			this.ret = ret;
		}

	}
	public boolean advertises(String name) {
		return adverts.containsKey(name);
	}

}
