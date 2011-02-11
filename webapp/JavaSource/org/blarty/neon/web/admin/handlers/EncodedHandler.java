package org.jini.projects.neon.web.admin.handlers;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jini.projects.neon.web.admin.PageHandler;

public class EncodedHandler implements PageHandler {

        public String handleRequest(HttpServletRequest req, HttpServletResponse res, ServletContext ctx) {
                // TODO Auto-generated method stub
                System.out.println("Request URL: " + req.getRequestURL());
                String name = ctx.getServletContextName();
                System.out.println("Servlet name: " + name);
                System.out.println("Context path: " + req.getContextPath());
                System.out.println();
                return "agentkilled.jsp";
        }

}
