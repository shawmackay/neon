package org.jini.projects.neon.web.admin.filters;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AccessControlFilter implements Filter {
    
    private FilterConfig config = null;
    private String loginPage;
    
    public void destroy() {
        config = null;
    }

    public void init(FilterConfig config) throws ServletException {
        this.config = config;
        loginPage = config.getInitParameter("loginPage");
        if(loginPage==null)
            throw new ServletException("login Page parameter missing");        
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {               
        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpResp = (HttpServletResponse) response;
        String forwardURI = getForwardURI(httpReq);
        if (forwardURI.startsWith("/logout.jsp"))
            chain.doFilter(request, response);
        if(!isAuthenticated(httpReq)){
            
            ServletContext context = config.getServletContext();
            RequestDispatcher rd = context.getRequestDispatcher(forwardURI);
            if(rd == null)
                httpResp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Login page doesn't exist");
            rd.forward(request,response);
            return;            
        }
        chain.doFilter(request, response);
    }
    
    private boolean isAuthenticated(HttpServletRequest request){
        boolean isAuthenticated = false;
        HttpSession session = request.getSession();
        if(session.getAttribute("validUser") != null){
            isAuthenticated = true;        
        }
        return isAuthenticated;
    }
    
    private String getForwardURI(HttpServletRequest request){
        StringBuffer uri = new StringBuffer(loginPage);
        uri.append("?errorMsg=Please+log+in+first&origURL=").append(URLEncoder.encode(getContextRelativeURI(request)));
        return uri.toString();
    }
    
    private String getContextRelativeURI(HttpServletRequest request){
        int ctxPathLength = request.getContextPath().length();
        String requestURI = request.getRequestURI();
        StringBuffer uri = new StringBuffer(requestURI.substring(ctxPathLength));
        String query = request.getQueryString();
        if(query!=null)
            uri.append("?").append(query);        
        return uri.toString();
    }
}
