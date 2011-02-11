package org.jini.projects.neon.web.admin;

/*
* Controller.java
*
* Created Wed Mon Feb 14 10:41:55 GMT 2005
*/


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
*
* @author calum
* @version 1.0
*/

public class Controller extends HttpServlet {
	
	/**
     * 
     */
    private static final long serialVersionUID = -7480072096297862751L;
    public void doGet (HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException{
		
		PrintWriter out=res.getWriter();

		String pathInfo = req.getServletPath();
		

		String part = pathInfo.substring(0,pathInfo.indexOf("."));
        System.out.println("Initial ReqDep points to: " + part);
		RequestDispatcher rd = this.getServletContext().getRequestDispatcher(part+ ".jsp");
		String handler = part.substring(part.lastIndexOf("/")+1);
		String subContext = part.substring(0,part.lastIndexOf("/"));
		
		try{
			Class cl=null;
			try {
				cl = Class.forName("org.jini.projects.neon.web.admin.handlers."+handler+"Handler");
			}catch (Exception ex){
				
			}
			if(cl!=null){
                
				org.jini.projects.neon.web.admin.PageHandler ph = (org.jini.projects.neon.web.admin.PageHandler) cl.newInstance();
				System.out.println("Forwarding to the handler");
                String redir = ph.handleRequest(req,res,getServletContext());
                System.out.println("Redirecting to : " + redir);
				rd = getServletContext().getRequestDispatcher("/"+redir);
                
				
                if(redir.startsWith("redirect-to:")){
                    redir = redir.substring(redir.indexOf(":")+1);
                    //res.encodeRedirectUrl(redir);
                    System.out.println("Sending redirect");
                    res.sendRedirect(redir);
                    return;
                }
                rd.forward(req,res);                
			} else {       
                System.out.println("Class not found: forwarding normally");                
				rd.forward(req,res);
			}
			
		}catch(Exception ex){
			System.err.println("Caught Exception: "+ ex.getClass().getName() + "; Msg: " + ex.getMessage());
			ex.printStackTrace();
		}	
	}
	public void doPost (HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException{
		doGet(req,res);		
	}
}
