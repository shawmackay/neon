<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<html>
	<body>
	<jsp:useBean id="clock" class="java.util.Date"/>
	<c:out value="${clock.hours}"/>
	<c:choose>
		<c:when test="${clock.hours < 12}">
			<h1>Good morning</h1>
		</c:when>
	<c:when test="${clock.hours < 18}">
			<h1>Good day</h1>
		</c:when>
<c:otherwise>
			<h1>Good evening</h1>
		</c:otherwise>
	</c:choose>
Wlecome to our site
</body>
</html>
