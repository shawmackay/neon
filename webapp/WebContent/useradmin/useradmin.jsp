<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<html>
	<head>
		<title>Neon :: User Administration</title>
	</head>
	<% if(request.getParameter("submit")==null){%>
		<a href="useradmin.jsp?submit=add">Add User</a><br/>
		<a href="useradmin.jsp?submit=modify">Modify User</a><br/>		
	<%} else {%>
		<% if(request.getParameter("submit").equals("add")){%>
			<jsp:include page="adduser.jsp"/>
   		<%}%>
 
 		<% if(request.getParameter("submit").toLowerCase().equals("add user")){%>
			<h2>Add new user</h2>
			User Added!
   		<%}%>
		<% if(request.getParameter("submit").equals("modify")){%><br/>
			<jsp:include page="changeuser.jsp"/>  
   		<%}%>
   	<%}%>
</html>	