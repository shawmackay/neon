package org.jini.projects.neon.annotations.tests;

import net.jini.core.lookup.ServiceTemplate;

import org.jini.glyph.Injection;
import org.jini.projects.neon.annotations.Agent;


@Agent(name = "SvcPOJO", namespace = "simpler", init = "initMe")
@Injection
public class ServiceDependentPOJO {

	public void setService(Object theService){

		System.out.println("Acquired Reference to Service: " + theService.getClass().getName());
	}
	
	public boolean initMe(){
		System.out.println("Initialised Service Dependent POJO");
		return true;
	}
}
