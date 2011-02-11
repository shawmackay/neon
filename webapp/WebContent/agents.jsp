
<html>
	<head>
		<title>Agent View</title>
		<link rel="stylesheet" type="text/css" href="neon.css"/>
	</head>
	<body>	
		<%@ include file="header.html"%>		
		<div class="maintitle"><center>Agent Information</center></div>		
		<div class="mainbody">
		<%
		String s="FAILURE";
		org.jini.projects.neon.service.AgentBackendService svc = org.jini.projects.neon.web.NeonLink.getLink().getNeon();
		try {
			if (svc == null) {
				s = "null";
			} else {
				org.jini.projects.neon.service.MonitorAgent monitor = (org.jini.projects.neon.service.MonitorAgent) svc.getStatelessAgent("neon.Monitor", "global");
				s = monitor.getAgentInformation(request.getParameter("id"));
			}
		} catch (java.rmi.RemoteException e) {
			// TODO Handle RemoteException
			e.printStackTrace();
		}
		out.print(s);
		
			%>
		<%@include file="invokeLog.jsp" %> 
		</div>
	</body>
</html>
