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
	<div class="neonback3">Applications</div>
	<table width="300">

	<c:forEach items="${appnames}" var="current">
	<tr><td width="100%">		
		<form action="Slice.do" method="GET" id="form${current}">
			<div style="visibility:hidden;display:none;"><textarea name="name">${current}</textarea>
			<textarea name="action">viewapp</textarea>
			</div>						
			<button style="width:300; text-align:center;" name="submit" onclick="document.form${current}.submit()" >View ${current}</button>
		</form>		
		</td></tr>
	</c:forEach>
	</table>
	<form action="Slice.do" method="GET" id="newslice">
		New Application: <input name="name">
		<button name="action" onclick="document.newslice.submit()" value="createapp">Create</button>
	</form>
	</div>
</body>
</html>
