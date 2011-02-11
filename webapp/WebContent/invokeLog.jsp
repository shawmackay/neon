<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

		<%
		String outpur="FAILURE";
		org.jini.projects.neon.service.AgentBackendService asvc = org.jini.projects.neon.web.NeonLink.getLink().getNeon();
		try {
			if (asvc == null) {
				outpur = "null";
			} else {
				org.jini.projects.neon.service.MonitorAgent monitor = (org.jini.projects.neon.service.MonitorAgent) asvc.getStatelessAgent("neon.Monitor", "global");
				outpur = monitor.getAgentLogInformation(request.getParameter("id"));
			}
		} catch (java.rmi.RemoteException e) {
			e.printStackTrace();
		}
		out.print(outpur);
			%>
