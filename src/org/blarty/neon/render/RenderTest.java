package org.jini.projects.neon.render;

import java.rmi.RemoteException;
import java.util.HashMap;

import org.jini.projects.neon.agents.AbstractAgent;
import org.jini.projects.neon.host.NoSuchAgentException;

public class RenderTest extends AbstractAgent implements Runnable{

    @Override
    public boolean init() {
        // TODO Auto-generated method stub
        return false;
    }

   
    public void run() {
        // TODO Auto-generated method stub
        try {
            RenderAgent render = (RenderAgent) context.getAgent("neon.Render");
            PresentableAgent presentable = (PresentableAgent) context.getAgent("neon.SimpleWeb");
            System.out.println("Your Rendered data is: \n" + render.render(presentable, "index", "xml",new HashMap(), null));
        } catch (NoSuchAgentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
