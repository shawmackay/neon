package org.jini.projects.neon.web.admin.handlers;

/*
* KillHandler.java
*
* Created Mon Feb 14 11:37:27 GMT 2005
*/
import org.jini.projects.neon.web.admin.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
import org.jini.projects.neon.web.NeonLink;
import org.jini.projects.neon.recovery.KillerAgent;
import net.jini.id.*;
import org.jini.projects.neon.agents.*;
/**
*
* @author  calum
* @version
*/

public class KillHandler implements PageHandler{
	public String handleRequest(HttpServletRequest req, HttpServletResponse res, ServletContext ctx){
		String agID = req.getParameter("agentid");
		if(agID==null){
			req.setAttribute("error","Unable to find agent");
			req.setAttribute("errordescription","The system was unable to find an agent to process your request");
			
			return "errorreason.jsp";
		} else {
			try{
				NeonLink link = NeonLink.getLink();
				KillerAgent killer =(KillerAgent) link.getNeon().getStatelessAgent("neon.Killer", "global");
				Uuid agUuid = UuidFactory.create(agID);
				AgentIdentity agit = new AgentIdentity(agUuid);
				if( killer.Kill(agit)){
					return "agentkilled.jsp";
				} else{
					req.setAttribute("error","Unable to remove agent");
					req.setAttribute("errordescription","The system was unable to kill the requested agent");
					return "errorreason.jsp";
				}				
			}catch(Exception ex){
				System.err.println("Caught Exception: "+ ex.getClass().getName() + "; Msg: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
		return "agentkilled.jsp";
	}
}
