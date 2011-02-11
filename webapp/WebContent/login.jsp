<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Neon Administration: Login</title>
<link rel="stylesheet" type="text/css" href="neon.css" />
</head>
<body>

<%@ include file="header.html"%>
<div class="mainbody">
<font color="red">
	<c:out value="${errorMsg}"/>
</font>

<form action="Authenticate.do" method="post">
	<input type="hidden" name="origurl" value="${fn:escapeXml(param.origurl)}"/>
	<p>Please enter your username and password</p>
	<p>
	Name:<input name="username"  size="10">${fn:escapeXml(cookie.userName.value)}</input>
	Password:<input name="password"  type="password" size="10">${fn:escapeXml(cookie.userName.value)}</input>
	<input type="submit" value="Enter">	
	</p>

	</form>
</div>
</body>
</html>