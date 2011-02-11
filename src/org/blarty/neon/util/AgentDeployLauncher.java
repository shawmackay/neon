package org.jini.projects.neon.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import com.sun.jini.system.MultiCommandLine;
import com.sun.jini.system.CommandLine.BadInvocationException;
/**
 * Boot strap class to run the AgentDeployer.<br>
 * Boots a second VM to deploy the agent, ensuring that thevalue of the
 * <code>-jar</code> attribute is placed on the class path to the second VM.
 * Otherwise, attempting to load the <code>-jar</code> attribute through URLClassLoader
 * will result in an incorrect codebase being annotated.
 * @author Calum
 *
 */
public class AgentDeployLauncher {

    public static void main(String[] args) {
        try {
            MultiCommandLine mcl = new MultiCommandLine(args);
            String agentClass = mcl.getString("agentclass", null);
            String configuration = mcl.getString("agentconfig", null);
            String constraints = mcl.getString("agentconstraints", null);
            String codebase = mcl.getString("codebase", null);
            String serviceid = mcl.getString("serviceid", null);
            String group = mcl.getString("group", "public");
            String jar = mcl.getString("jar", null);
            String classpath = System.getProperty("java.class.path");
            String policy = System.getProperty("java.security.policy");
            if (args.length == 0 || agentClass == null || codebase == null) {
                System.out.print("usage: java AgentDeployer ");
                mcl.usage();
                System.exit(0);
            }

            String newClasspath = classpath;
            if (jar != null)
                newClasspath = classpath + File.pathSeparatorChar + jar;
            StringBuffer buffer = new StringBuffer();
            buffer.append("java -classpath " + newClasspath +" -Djava.rmi.server.codebase=\"" + codebase + "\" -Djava.security.policy=" + policy + " org.jini.projects.neon.util.AgentDeployer ");
            if (agentClass != null)
                buffer.append("-agentclass " + agentClass + " ");
            if (constraints != null)
                buffer.append("-agentconstraints " + constraints + " ");
            if (configuration != null)
                buffer.append("-agentconfig " + configuration + " ");
            if (codebase != null)
                buffer.append("-codebase \"" + codebase + "\" ");
            if (serviceid != null)
                buffer.append("-serviceid " + serviceid + " ");
            if (group != null)
                buffer.append("-group " + group + " ");
            
            
            try {                
                 System.out.println(buffer.toString());
                Process p = Runtime.getRuntime().exec(buffer.toString().trim());
                BufferedReader bos = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getInputStream())));
                
                String line = bos.readLine();
                while (line != null) {
                    System.out.println(line);
                    line = bos.readLine();
                }
                p.waitFor();
                System.out.println("Complete");
                System.exit(0);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        } catch (BadInvocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
