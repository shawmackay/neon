<%@ page contentType="text/html"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
	<head>
		<title>::: Error Occured :::</title>
		<link rel="stylesheet" type="text/css" href="neon.css"/>
	</head>
	<body>
		<div class="maintitle">An Error has occured</div>
		<h3><c:out value="${error}"/></h3><br/>
		<c:out value="${errordescription}"/>
		</hr>
	</body>
</html>s
