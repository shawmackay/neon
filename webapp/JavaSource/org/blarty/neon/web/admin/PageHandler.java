package org.jini.projects.neon.web.admin;

/*
* PageHandler.java
*
* Created Wed Feb 09 09:46:40 GMT 2005
*/

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;
/**
*
* @author  calum
* @version
*/

public interface PageHandler{
	public String handleRequest(HttpServletRequest req, HttpServletResponse res, ServletContext ctx);
}
