package org.jini.projects.neon.web.admin;

/*
 * Controller.java
 *
 * Created Wed Mon Feb 14 10:41:55 GMT 2005
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.annotations.Render;

import org.jini.projects.neon.render.BadFormatException;
import org.jini.projects.neon.render.PresentableAgent;
import org.jini.projects.neon.render.RenderAgent;
import org.jini.projects.neon.render.RenderResponse;
import org.jini.projects.neon.render.RenderUtils;
import org.jini.projects.neon.service.AgentBackendService;
import org.jini.projects.neon.web.NeonLink;

/**
 * 
 * @author calum
 * @version 1.0
 */

public class PathRenderer extends HttpServlet {

	/**
         * 
         */
	private static final long serialVersionUID = -7480072096297862751L;

	private int num = 0;

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		try {

			String servletPath = req.getServletPath();

			// res.getWriter().write("ServletPath: " + servletPath);
			// res.getWriter().write("; Context path: " +
			// req.getContextPath());
			// res.getWriter().write("; PathInfo: " +
			// req.getPathInfo());
			String path = req.getPathInfo();
			String query = req.getQueryString();
			String base = null;
			StringBuffer agent = null;
			String action = null;

			base = path.substring(1);

			boolean actionSet = true;
			boolean resourceRequest = false;
			// Chop the base up

			if (base.endsWith("/"))
				actionSet = false;

			String[] locator = base.split("/");

			agent = new StringBuffer();
			agent.append(locator[0]);
			if (locator.length > 1 && !locator[1].contains(".")) {
				action = locator[1];
			} else {
				action = "index";

			}

			// res.getWriter().write("\nAgent: " + agent.toString()
			// + "; action: " + action);

			NeonLink link = NeonLink.getLink();
			AgentBackendService svc = link.getNeon();
			RenderAgent ag = (RenderAgent) svc.getStatelessAgent("neon.Renderer", "global");

			if (locator.length > 1) {
				if (locator[locator.length - 1].contains(".")) {
					StringBuffer resourceBuffer = new StringBuffer();
					for (int i = 1; i < locator.length - 1; i++)
						resourceBuffer.append(locator[i] + "/");
					resourceBuffer.append(locator[locator.length - 1]);
					String resource = resourceBuffer.toString();
					res.setContentType(ag.getAgentResourceMimeType(resource));

					BufferedOutputStream fos = new BufferedOutputStream(res.getOutputStream());
					int buffersize = 4;
					byte[] buffer = new byte[buffersize * 1024];
					BufferedInputStream bis = new BufferedInputStream(ag.getAgentResource(agent.toString(), resource));
					int numread = bis.read(buffer);
					while (numread != -1) {
						fos.write(buffer, 0, numread);
						fos.flush();
						numread = bis.read(buffer);
					}
					res.flushBuffer();
					res.getOutputStream().close();
					return;
				}
				// if(!action.equals("index")){
				// try {
				// Object invokeAgent = (Object)
				// svc.getStatelessAgent(agent.toString(), "global");
				// Class[] intfs = invokeAgent.getClass().getInterfaces();
				//
				// Method m = invokeAgent.getClass().getMethod(action, new
				// Class[]{Map.class});
				// if(m.getAnnotation(Render.class)!=null)
				// m.invoke(invokeAgent,new Object[]{req.getParameterMap()});
				// } catch (SecurityException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// } catch (IllegalArgumentException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// } catch (NoSuchMethodException e) {
				// //We skip this exception because we pass this through to the
				// render function
				// // We can have actions that don't update but appear to be
				// different pages
				// } catch (IllegalAccessException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// } catch (InvocationTargetException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// }
				RenderResponse response = ag.render(agent.toString(), action, "html", req.getParameterMap(), "html");
//				if (response.getContentType().equals("text/html")) {
//					req.setAttribute("renderdata", response.getContent());
//					String redir = "agentrender.jsp";
//					RequestDispatcher rd = getServletContext().getRequestDispatcher("/" + redir);
//
//					if (redir.startsWith("redirect-to:")) {
//						System.out.println("Redirecting.....to agentrender.jsp");
//						redir = redir.substring(redir.indexOf(":") + 1);
//						// res.encodeRedirectUrl(redir);
//						// System.out.println("Sending redirect");
//						res.sendRedirect(redir);
//						return;
//					}
//					rd.forward(req, res);
//				}
				res.setContentType(response.getContentType());
				res.setContentLength(response.getContent().length);
				if (!response.isCacheable())
					res.setHeader("Cache-Control", "no-cache");
			
				res.getOutputStream().write(response.getContent());
				res.getOutputStream().flush();
				// Handle method invocation here!
				// Need to add data back into request/session
			} else {
				RenderResponse response = ag.render(agent.toString(), action, "html", req.getParameterMap(), "html");
				// if (response.getContentType().equals("text/html")) {
				// req.setAttribute("renderdata", response.getContent());
				// System.out.println("Redirecting.....to agentrender.jsp");
				// String redir = "agentrender.jsp";
				// RequestDispatcher rd =
				// getServletContext().getRequestDispatcher("/" + redir);
				//
				// if (redir.startsWith("redirect-to:")) {
				// redir = redir.substring(redir.indexOf(":") + 1);
				// // res.encodeRedirectUrl(redir);
				// // System.out.println("Sending redirect");
				// res.sendRedirect(redir);
				// return;
				// }
				// rd.forward(req, res);
				// }
				res.setContentType(response.getContentType());
	
				res.setContentLength(response.getContent().length);
				if (!response.isCacheable())
					res.setHeader("Cache-Control", "no-cache");
			
				res.getOutputStream().write(response.getContent());
				res.getOutputStream().flush();
			}

		} catch (BadFormatException ex) {
			System.err.println("Caught Exception: " + ex.getClass().getName() + "; Msg: " + ex.getMessage());
			ex.printStackTrace();

		}

	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doGet(req, res);
	}
}
