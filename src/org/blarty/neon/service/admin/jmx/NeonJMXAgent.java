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


/*
 * Created on 01-Oct-2003
 *To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.jini.projects.neon.service.admin.jmx;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import javax.management.Attribute;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

/**
 * @author Calum
 *To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class NeonJMXAgent {
	private MBeanServer server;
	
	public NeonJMXAgent(MBeanServer theServer) {
		server = theServer;
	}

	public void start() {
		String jmxDomain = server.getDefaultDomain();
		String mbeanName = "org.jini.projects.neon.service.admin.jmx.SimpleStandard";
		ObjectName mbeanObjectName = null;

		try {
			mbeanObjectName = new ObjectName(jmxDomain + ":type=" + mbeanName);
		} catch (MalformedObjectNameException e) {
			System.out.println("\t!!! Could not create the MBean ObjectName !!!");
			e.printStackTrace();
			System.out.println("\nEXITING...\n");
			System.exit(1);

		}

		echo("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		createSimpleBean(mbeanObjectName, mbeanName);
		echo("\npress <Enter> to continue...\n");
		waitForEnterPressed();

		// get and display the management information exposed by the MBean
		//
		echo("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		printMBeanInfo(mbeanObjectName, mbeanName);
		echo("\npress <Enter> to continue...\n");
		waitForEnterPressed();

		// manage the MBean
		// 
		echo("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		manageSimpleBean(mbeanObjectName, mbeanName);
		echo("\npress <Enter> to continue...\n");
		waitForEnterPressed();

		// trying to do illegal management actions...
		//
		echo("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		goTooFar(mbeanObjectName);
		echo("\npress <Enter> to continue...\n");
		waitForEnterPressed();
	}

	private void createSimpleBean(ObjectName mbeanObjectName, String mbeanName) {

		echo("\n>>> CREATE the " + mbeanName + " MBean within the MBeanServer:");
		String mbeanClassName = mbeanName;
		echo("\tOBJECT NAME           = " + mbeanObjectName);
		try {
			server.createMBean(mbeanClassName, mbeanObjectName);
		} catch (Exception e) {
			echo("\t!!! Could not create the " + mbeanName + " MBean !!!");
			e.printStackTrace();
			echo("\nEXITING...\n");
			System.exit(1);
		}
	}

	private void manageSimpleBean(ObjectName mbeanObjectName, String mbeanName) {

		echo("\n>>> MANAGING the " + mbeanName + " MBean");
		echo("    using its attributes and operations exposed for management");

		try {
			// Get attribute values
			sleep(1000);
			printSimpleAttributes(mbeanObjectName);

			// Change State attribute
			sleep(1000);
			echo("\n    Setting State attribute to value \"new state\"...");
			Attribute stateAttribute = new Attribute("State", "new state");
			server.setAttribute(mbeanObjectName, stateAttribute);

			// Get attribute values
			sleep(1000);
			printSimpleAttributes(mbeanObjectName);

			// Invoking reset operation
			sleep(1000);
			echo("\n    Invoking reset operation...");
			server.invoke(mbeanObjectName, "reset", null, null);

			// Get attribute values
			sleep(1000);
			printSimpleAttributes(mbeanObjectName);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void goTooFar(ObjectName mbeanObjectName) {

		echo("\n>>> Trying to set the NbChanges attribute (read-only)!");
		echo("\n... We should get an AttributeNotFoundException:\n");
		sleep(1000);
		// Try to set the NbChanges attribute
		Attribute nbChangesAttribute = new Attribute("NbChanges", new Integer(1));
		try {
			server.setAttribute(mbeanObjectName, nbChangesAttribute);
		} catch (Exception e) {
			e.printStackTrace();
		}
		echo("\n\n>>> Trying to access the NbResets property (not exposed for management)!");
		echo("\n... We should get an AttributeNotFoundException:\n");
		sleep(1000);
		// Try to access the NbResets property
		try {
			Integer NbResets = (Integer) server.getAttribute(mbeanObjectName, "NbResets");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void printMBeanInfo(ObjectName mbeanObjectName, String mbeanName) {

		echo("\n>>> Getting the management information for the " + mbeanName + " MBean");
		echo("    using the getMBeanInfo method of the MBeanServer");
		sleep(1000);
		MBeanInfo info = null;
		try {
			info = server.getMBeanInfo(mbeanObjectName);
		} catch (Exception e) {
			echo("\t!!! Could not get MBeanInfo object for " + mbeanName + " !!!");
			e.printStackTrace();
			return;
		}
		echo("\nCLASSNAME: \t" + info.getClassName());
		echo("\nDESCRIPTION: \t" + info.getDescription());
		echo("\nATTRIBUTES");
		MBeanAttributeInfo[] attrInfo = info.getAttributes();
		if (attrInfo.length > 0) {
			for (int i = 0; i < attrInfo.length; i++) {
				echo(" * NAME: \t" + attrInfo[i].getName());
				echo("    DESCR: \t" + attrInfo[i].getDescription());
				echo("    TYPE: \t" + attrInfo[i].getType() + "\tREAD: " + attrInfo[i].isReadable() + "\tWRITE: " + attrInfo[i].isWritable());
			}
		} else
			echo(" * No attributes *");
		echo("\nCONSTRUCTORS");
		MBeanConstructorInfo[] constrInfo = info.getConstructors();
		for (int i = 0; i < constrInfo.length; i++) {
			echo(" * NAME: \t" + constrInfo[i].getName());
			echo("    DESCR: \t" + constrInfo[i].getDescription());
			echo("    PARAM: \t" + constrInfo[i].getSignature().length + " parameter(s)");
		}
		echo("\nOPERATIONS");
		MBeanOperationInfo[] opInfo = info.getOperations();
		if (opInfo.length > 0) {
			for (int i = 0; i < opInfo.length; i++) {
				echo(" * NAME: \t" + opInfo[i].getName());
				echo("    DESCR: \t" + opInfo[i].getDescription());
				echo("    PARAM: \t" + opInfo[i].getSignature().length + " parameter(s)");
			}
		} else
			echo(" * No operations * ");
		echo("\nNOTIFICATIONS");
		MBeanNotificationInfo[] notifInfo = info.getNotifications();
		if (notifInfo.length > 0) {
			for (int i = 0; i < notifInfo.length; i++) {
				echo(" * NAME: \t" + notifInfo[i].getName());
				echo("    DESCR: \t" + notifInfo[i].getDescription());
			}
		} else
			echo(" * No notifications *");
	}

	private void printSimpleAttributes(ObjectName mbeanObjectName) {

		try {
			echo("\n    Getting attribute values:");
			String State = (String) server.getAttribute(mbeanObjectName, "State");
			Integer NbChanges = (Integer) server.getAttribute(mbeanObjectName, "NbChanges");
			echo("\tState     = \"" + State + "\"");
			echo("\tNbChanges = " + NbChanges);
		} catch (Exception e) {
			echo("\t!!! Could not read attributes !!!");
			e.printStackTrace();
		}
	}

	private static void echo(String msg) {
		System.out.println(msg);
	}

	private static void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void waitForEnterPressed() {
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * ------------------------------------------
	 *  PRIVATE VARIABLES
	 * ------------------------------------------
	 */

	public static void main(String[] args) {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
		 
	      try {
			ObjectName name = new ObjectName("org.jini.projects.neon.service.admin.jmx:type=SimpleStandard"); 
 
			  SimpleStandard mbean = new SimpleStandard(); 
 
			  mbs.registerMBean(mbean, name);
			MBeanInfo info = mbs.getMBeanInfo(name);
			System.out.println("Description: "+info.getDescription());
			System.out.println(info.getClassName());
			MBeanOperationInfo[] ops = info.getOperations();
			for (int i=0;i<ops.length;i++){
				System.out.println(ops[i].getName());
			}
			MBeanAttributeInfo[] attrs = info.getAttributes();
			for(MBeanAttributeInfo attr: attrs){
				System.out.println(attr.getName());
			}
			  System.out.println("Registerd MBean");
		} catch (MalformedObjectNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstanceAlreadyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MBeanRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotCompliantMBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstanceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ReflectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	 
	      System.out.println("Waiting forever..."); 
	      try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}