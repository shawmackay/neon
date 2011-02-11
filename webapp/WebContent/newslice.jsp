<%@page contentType="text/html"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
	<head>
		<title>:: Neon :: Slice details</title>




<link rel="stylesheet" type="text/css" href="neon.css"/>
</head>
<body>

	<%@ include file="header.html"%>
	<div class="mainbody">
	<c:choose>
	<c:when test="${empty param.name}">	
	<h1>Add a new slice</h1>
	</c:when>
	<c:otherwise>
	<h1>Amend a slice</h1>
	</c:otherwise>
	</c:choose>
	<form action="Slice.do" type="GET">
		<div style="visibility:hidden;display:none;">
				<textarea name="parent">${param.currentID}</textarea>
		</div>			
		<span style="visibility:hidden;display:none;">
					<textarea name="path">${param.path}</textarea>
				</span>
		<table>
		<tr><td>Name</td><td> <input type="text" name="name" value="${param.name}"/></td></tr>
		<tr><td>Short description</td><td><input name="sdesc" value="${param.sdesc}"/></td></tr>
		<tr><td>Long description</td><td><textarea rows="3" cols="40" name="ldesc">${param.ldesc}</textarea></td></tr> 
		<tr><td>Slice Type</td><td><select name="type"><option>Default</option><option>Attractor</option><option>Repeller</option><option>Redundant</option><option>Factory</option></select></td></tr>
		<c:choose>
	<c:when test="${empty param.name}">	
		<tr><td><button type="SUBMIT" name="action" value="newslice"/>New Slice</button></td></tr>
	</c:when>
	<c:otherwise>
		<tr><td><button type="SUBMIT" name="action" value="amendslice"/>Apply Changes</button></td></tr>
	</c:otherwise>
	</c:choose>

		</table>
		
	</form>
	</div>
</body>
</html>
