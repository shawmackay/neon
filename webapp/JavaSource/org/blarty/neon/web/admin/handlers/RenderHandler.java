package org.jini.projects.neon.web.admin.handlers;

import javax.servlet.ServletContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.jini.id.Uuid;
import net.jini.id.UuidFactory;

import org.jini.projects.neon.agents.AgentIdentity;

import org.jini.projects.neon.web.NeonLink;
import org.jini.projects.neon.render.RenderAgent;
import org.jini.projects.neon.web.admin.PageHandler;
/*
* RenderHandler.java
*
* Created Mon Mar 14 10:02:40 GMT 2005
*/

/**
*
* @author  calum
* @version
*/

public class RenderHandler  implements PageHandler{
	public String handleRequest(HttpServletRequest req, HttpServletResponse res, ServletContext ctx){
		String agID = req.getParameter("agentid");
		if(agID==null){
			req.setAttribute("error","Unable to find agent");
			req.setAttribute("errordescription","The system was unable to find an agent to process your request");
			
			return "errorreason.jsp";
		} else {
			try{
				NeonLink link = NeonLink.getLink();
				RenderAgent render= (RenderAgent) link.getNeon().getStatelessAgent("neon.Renderer", "global");
				Uuid agUuid = UuidFactory.create(agID);
				AgentIdentity agit = new AgentIdentity(agUuid);
				req.setAttribute("renderdata",render.render(agUuid,"index","html",req.getParameterMap(),"html"));
				return "agentrender.jsp";
			}catch(Exception ex){
				System.err.println("Caught Exception: "+ ex.getClass().getName() + "; Msg: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
		return "agentkilled.jsp";
	}
}
