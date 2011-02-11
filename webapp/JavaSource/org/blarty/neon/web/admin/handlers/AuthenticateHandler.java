package org.jini.projects.neon.web.admin.handlers;

import java.rmi.RemoteException;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jini.projects.neon.users.agents.KeyStoreUtility;
import org.jini.projects.neon.users.agents.UserDataAgent;
import org.jini.projects.neon.users.agents.UserDataIntf;
import org.jini.projects.neon.web.NeonLink;
import org.jini.projects.neon.web.admin.PageHandler;

public class AuthenticateHandler implements PageHandler {

        public String handleRequest(HttpServletRequest req, HttpServletResponse res, ServletContext ctx) {
                // TODO Auto-generated method stub
                boolean newLogin = false;
                // Cookie userName=null;
                // Cookie password=null;
                // Cookie[] cookies = req.getCookies();
                System.out.println("Remember is: " + req.getParameter("remember"));
                // for(Cookie c: cookies){
                // if(c.getName().equals("userName"))
                // userName=c;
                // if(c.getName().equals("password"))
                // password=c;
                // }
                String username = req.getParameter("username");
                String password = req.getParameter("password");
                if (username == null || password == null) {
                        req.setAttribute("You must enter a username and password", "errorMsg");
                        System.out.println("Returning empty password redirect");
                        return "/login.jsp";
                }
                UserDataIntf u = null;
                try {
                        u = (UserDataIntf) NeonLink.getLink().getNeon().getStatelessAgent("neon.UserData", "global");
                } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                if (u != null) {
                        if (u.authenticateUser(username, password.toCharArray()) != null) {
                                System.out.println(("Webapp Handler sees AUTHENTICATION"));
                                if (req.getParameter("remember") != null) {
                                        Cookie usercookie = new Cookie("username", username);
                                        usercookie.setMaxAge(2592000);
                                        Cookie passcookie = new Cookie("password", password);
                                        passcookie.setMaxAge(2592000);
                                        res.addCookie(usercookie);
                                        res.addCookie(passcookie);
                                        System.out.println("Added Cookie");

                                }
                                req.getSession().setAttribute("validUser", "true");
                                System.out.println("Authenticated successfully");
                                // if (req.getParameter("origurl")!=null &&
                                // req.getParameter("origurl").indexOf("?") !=
                                // -1)
                                return ("redirect-to:" + req.getParameter("origurl"));
                        }
                        System.out.println(("Webapp Handler sees NO AUTHENTICATION"));
                }
                // if (username.equals("calum") && password.equals("openup")) {
                // if (req.getParameter("remember") != null) {
                // Cookie usercookie = new Cookie("username", username);
                // usercookie.setMaxAge(2592000);
                // Cookie passcookie = new Cookie("password", password);
                // passcookie.setMaxAge(2592000);
                // res.addCookie(usercookie);
                // res.addCookie(passcookie);
                // System.out.println("Added Cookie");
                //                
                // }
                // req.getSession().setAttribute("validUser", "true");
                // System.out.println("Authenticated successfully");
                // //if (req.getParameter("origurl")!=null &&
                // req.getParameter("origurl").indexOf("?") != -1)
                // return ("redirect-to:"+req.getParameter("origurl"));
                // //else
                // //return req.getParameter("origurl") + "?;jsessionid=" +
                // req.getSession().getId();
                // }
                req.setAttribute("errorMsg", "Login failed! Please try again");
                System.out.println("Returning failed redirect");
                return "/login.jsp";
        }

}
