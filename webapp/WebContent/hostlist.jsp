
<html>
	<head>
		<title>Welcome</title>
<link rel="stylesheet" type="text/css" href="neon.css"/>
</head>
<body>

	<%@ include file="header.html"%>
	<%
		String s="FAILURE";
		org.jini.projects.neon.service.AgentBackendService svc = org.jini.projects.neon.web.NeonLink.getLink().getNeon();
try {
			if (svc == null) {
				s = "null";
			} else {
				org.jini.projects.neon.service.MonitorAgent monitor = (org.jini.projects.neon.service.MonitorAgent) svc.getStatelessAgent("neon.Monitor", "global");
				if(request.getParameter("category")==null)
					s = monitor.getInformation("host");
				else
					s= monitor.getInformation(request.getParameter("category"));
			}
		} catch (java.rmi.RemoteException e) {
			// TODO Handle RemoteException
			e.printStackTrace();
		}
		out.print(s);
		%>
	</body>
</html>

