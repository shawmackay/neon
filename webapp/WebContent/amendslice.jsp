<%@page contentType="text/html"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
	<head>
		<title>:: Neon :: Application Management</title>
		<link rel="stylesheet" type="text/css" href="neon.css"/>
	</head>
<body>

	<%@ include file="header.html"%>
	<div class="mainbody">

	
	<h2>Application slice <c:out value="${name}"/> has been amended</h2><br/>

	<a href="Slice.do?action=view">Back to Application Manager</a>
	</div>
</body>
</html>
