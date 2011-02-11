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
	<c:if test="${empty child}">
	
	<h2>Application slice <c:out value="${name}"/> has been created</h2><br/>
		<hr>
	
	<a href="Slice.do?action=viewapps">Back to Application Manager</a>
	
	</c:if>
	<c:if test="${empty name}">
	
	<h2>${type} Slice <c:out value="${child}"/> has been created and added to Slice <c:out value="${parent}"/></h2><br/>
	<h2>@ Context Path: ${path}</h2>
		<hr>
	
	<a href="Slice.do?action=viewslice&sliceid=${parentid}&path=${parentpath}">Back to Application Manager</a>
	
	</c:if>
	
	</div>
</body>
</html>
