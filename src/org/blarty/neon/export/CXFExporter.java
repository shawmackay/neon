package org.jini.projects.neon.export;

import java.util.logging.Logger;
import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.RemoteAgentState;

public class CXFExporter {
    
    private Logger exportLog = Logger.getLogger("org.jini.neon.export");
    
	public boolean exportAgent(Agent linkedAgent, String addressRoot){
		Class[] collabIntfs = linkedAgent.advertise().getCollaborativeClasses();
		ServerFactoryBean svrFactory = new ServerFactoryBean();
		
		if(collabIntfs[0].isAssignableFrom(RemoteAgentState.class))
		svrFactory.setServiceClass(collabIntfs[1]);
		else
			svrFactory.setServiceClass(collabIntfs[0]);
			exportLog.info("Exporting agent on: " + addressRoot + "/" + linkedAgent.getName());
		svrFactory.setAddress(addressRoot + "/" + linkedAgent.getName());
		svrFactory.setServiceBean(linkedAgent);
		svrFactory.create();
		exportLog.info("Web Service created for '" + linkedAgent.getName() + "' (" + linkedAgent.getNamespace() + "." + linkedAgent.getName());
		return true;
	}
	
	public static void main(String[] args){
//		CXFExporter myExporter = new CXFExporter();
//		HelloAgentImpl impl = new HelloAgentImpl();
//		impl.init();
//		myExporter.exportAgent(impl, "http://localhost:9000");
//		myExporter.exportAgent(impl, "http://localhost:9001");
//		try {
//			Thread.sleep(2000);
//		} catch (Exception e){
//			e.printStackTrace();
//		}
//		System.out.println("Completed - Testing with Client");
//		HelloAgent client = (HelloAgent) myExporter.generateClient(HelloAgent.class, "http://localhost:9000/Hello");
//		try {
//			System.out.println("Response: " + client.sayHello("Calum"));
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		HelloAgent client2 = (HelloAgent) myExporter.generateClient(HelloAgent.class, "http://localhost:9001/Hello");
//		try {
//			System.out.println("Response: " + client2.sayHello("Calum"));
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.exit(0);
	}
	
	public Object generateClient(Class cl, String address){
		ClientProxyFactoryBean factory = new ClientProxyFactoryBean();
		factory.setServiceClass(cl);
		factory.setAddress(address);
		return factory.create();
	}
}
