package org.jini.projects.neon.web.admin.handlers;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jini.projects.neon.web.admin.PageHandler;

public class LogoutHandler implements PageHandler {

    public String handleRequest(HttpServletRequest req, HttpServletResponse res, ServletContext ctx) {
       req.getSession().removeAttribute("validUser");
        return "logout.jsp";
    }

}
