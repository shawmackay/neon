package org.jini.projects.neon.neontests.tutorial.simple;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.jini.glyph.Exportable;
import org.jini.glyph.Service;
import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.annotations.ServiceBinding;

@Exportable
@ServiceBinding(type="Jini") 
@Service(containerControlled = true)
public class HelloAgentImpl extends AbstractAgent implements HelloAgent {

    public HelloAgentImpl(){
        this.name = "Hello";
        this.namespace = "examples";
    }
    
    @Override
    public boolean init() {
        // TODO Auto-generated method stub
    	System.out.println("Initialising HelloAgent");
        return true;
    }

   

    public String sayHello(String name) throws RemoteException {
        // TODO Auto-generated method stub
    	getAgentLogger().info("Getting greeting for: " + name);
        Date d = new Date();
       Calendar cal = new GregorianCalendar();
       cal.setTime(d);
       int hrs = cal.get(Calendar.HOUR_OF_DAY);
       StringBuffer greeting = new StringBuffer();
       greeting.append("Good ");
       if(hrs <12)
           greeting.append("Morning");
       else if(hrs<18)
           greeting.append("Afternoon");
       else if(hrs <24)
           greeting.append("Evening");
       greeting.append(", " + name);
       return greeting.toString();
    }

}
