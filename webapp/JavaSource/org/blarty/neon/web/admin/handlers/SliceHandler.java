package org.jini.projects.neon.web.admin.handlers;

/*
 * SliceHandler.java
 *
 * Created Tue May 17 11:00:48 BST 2005
 */

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.jini.id.Uuid;
import net.jini.id.UuidFactory;

import org.jini.projects.neon.agents.Agent;
import org.jini.projects.neon.agents.AgentIdentity;
import org.jini.projects.neon.dynproxy.FacadeProxy;
import org.jini.projects.neon.host.AgentRegistry;
import org.jini.projects.neon.host.DomainRegistry;
import org.jini.projects.neon.host.ManagedDomain;
import org.jini.projects.neon.service.AgentBackendService;
import org.jini.projects.neon.vertigo.management.AgentListObject;
import org.jini.projects.neon.vertigo.management.SliceAgent;
import org.jini.projects.neon.vertigo.management.SliceType;
import org.jini.projects.neon.vertigo.slice.AttractorSlice;
import org.jini.projects.neon.vertigo.slice.DefaultSlice;
import org.jini.projects.neon.vertigo.slice.RedundantSlice;
import org.jini.projects.neon.vertigo.slice.RepellerSlice;
import org.jini.projects.neon.vertigo.slice.Slice;
import org.jini.projects.neon.web.NeonLink;
import org.jini.projects.neon.web.admin.PageHandler;

/**
 * 
 * @author calum
 * @version
 */

public class SliceHandler implements PageHandler {

	/**
	 * handleRequest
	 * 
	 * @param aHttpServletRequest
	 * @param aHttpServletResponse
	 * @param aServletContext
	 * @return String
	 */
	public String handleRequest(HttpServletRequest req, HttpServletResponse resp, ServletContext context) {
		Map m = req.getParameterMap();
		NeonLink link = NeonLink.getLink();
		AgentBackendService svc = link.getNeon();
		try {
			SliceAgent ag = (SliceAgent) svc.getStatelessAgent("vertigo.Slice", "Global");

			req.setAttribute("appnames", ag.getApplicationNames());
			String action = (String) req.getParameter("action");
			if (action.equals("createapp")) {
				return createSlice(req, ag);
			}

			if (action.equals("viewapp")) {
				return viewApp(req, ag);
			}

			if (action.equals("viewslice")) {
				return viewSlice(req, ag);
			}

			if (action.equals("newslice")) {
				return newSlice(req, ag);
			}

			if (action.equals("amendslice")) {
				return amendSlice(req, ag);
			}

			if (action.equals("addselected")) {
				return addSelectedAgents(req, ag);
			}

			if (action.equals("addagents")) {
				req.setAttribute("agents", ag.getAllAgentInfo(req.getParameter("filter")));

				return "agentlisting.jsp";

			}
			return "slice.jsp";
		} catch (Exception ex) {
			System.err.println("Caught Exception: " + ex.getClass().getName() + "; Msg: " + ex.getMessage());
			ex.printStackTrace();
		}
		return "error.jsp";
	}

	private String addSelectedAgents(HttpServletRequest req, SliceAgent ag) throws IOException {
		String[] reqd = req.getParameterValues("agentchoice");
		System.out.println("Param:" + req.getParameter("toslice"));
		Uuid sliceID = UuidFactory.create(req.getParameter("toslice"));
		Slice s = ag.getSlice(sliceID);
		if (s != null) {
			if (reqd != null)
				for (int i = 0; i < reqd.length; i++) {
					ag.attachAgentToSlice(new org.jini.projects.neon.agents.AgentIdentity(reqd[i]), sliceID);
					// ag.attachAgentToSlice(a, slicename)
				}
		}
		String[] anyreqd = req.getParameterValues("agentchoiceany");
		if (s != null) {
			if (anyreqd != null)
				for (int i = 0; i < anyreqd.length; i++) {
					ag.attachAgentTypeToSlice(anyreqd[i], 1, sliceID);
				}
		}
		ag.storeSlice(s);
		viewSlice(req, s);
		String path = req.getParameter("path");
		req.setAttribute("path", path + "/" + s.getName());
		return "slicedetails.jsp";
	}

	private String amendSlice(HttpServletRequest req, SliceAgent ag) throws IOException {
		Slice slice = ag.getSlice(UuidFactory.create(req.getParameter("parent")));
		slice.setShortDescription(req.getParameter("sdesc"));
		slice.setLongDescription(req.getParameter("ldesc"));
		ag.storeSlice(slice);
		return "amendslice.jsp";
	}

	private String newSlice(HttpServletRequest req, SliceAgent ag) throws IOException {
		SliceType type = SliceType.valueOf(req.getParameter("type").toUpperCase());
		Slice newSlice;
		switch (type) {
		case ATTRACTOR:
			newSlice = new AttractorSlice();
			break;
		case REDUNDANT:
			newSlice = new RedundantSlice();
			break;
		case REPELLER:
			newSlice = new RepellerSlice();
			break;
		case FACTORY:
		case VIRTUAL:
		default:
			newSlice = new DefaultSlice();
		}
		newSlice.setName(req.getParameter("name"));
		Slice slice = ag.getSlice(UuidFactory.create(req.getParameter("parent")));
		newSlice.setShortDescription(req.getParameter("sdesc"));
		newSlice.setLongDescription(req.getParameter("ldesc"));
		ag.deploySlice(newSlice);
		ag.attachSubSlice(slice, newSlice);
		ag.storeSlice(slice);
		ag.storeSlice(newSlice);
		req.setAttribute("child", req.getParameter("name"));
		req.removeAttribute("name");
		req.setAttribute("parentid", req.getParameter("parent"));
		req.setAttribute("parent", slice.getName());
		String path = req.getParameter("path");
		if (path == null)
			path = "";
		req.setAttribute("path", path + "/");
		req.setAttribute("parentpath", path.substring(0, path.lastIndexOf("/")));
		return "slicecreated.jsp";
	}

	private String viewSlice(HttpServletRequest req, SliceAgent ag) {
		Slice s = ag.getSlice(UuidFactory.create(req.getParameter("sliceid")));
		viewSlice(req, s);
		String path = req.getParameter("path");
		if (path == null)
			path = "";
		req.setAttribute("path", path + "/" + s.getName());
		return "slicedetails.jsp";
	}

	private String viewApp(HttpServletRequest req, SliceAgent ag) {
		Slice s = ag.findSlice(req.getParameter("name"));
		viewSlice(req, s);
		String path = req.getParameter("path");
		if (path == null)
			path = "";
		req.setAttribute("path", path + "/" + s.getName());
		return "slicedetails.jsp";
	}

	private String createSlice(HttpServletRequest req, SliceAgent ag) throws IOException {
		ag.createApplication((String) req.getParameter("name"));
		req.setAttribute("name", req.getParameter("name"));
		ag.storeSlice(ag.findSlice((String) req.getParameter("name")));
		return "slicecreated.jsp";
	}

	void viewSlice(HttpServletRequest req, Slice s) {
		req.setAttribute("slice", s);
		List<AgentListObject> agentlist = new ArrayList<AgentListObject>();

		for (Iterator IDiter = s.getAgentIDs().iterator(); IDiter.hasNext();) {
			AgentListObject agOb = (AgentListObject) IDiter.next();

			agentlist.add(agOb);

		}

		req.setAttribute("agenttypes", s.getAgentTypeReferences());
		req.setAttribute("agentlist", agentlist);
	}

}
