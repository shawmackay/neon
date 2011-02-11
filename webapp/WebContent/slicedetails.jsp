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
	<div class="break">Slice: <c:out value="${slice.name}"/></div><br/>
	<div class="neonback4">Context path: <c:out value="${path}"/></div>
	<form action="newslice.jsp" type="GET" id="currentslice">
	<table>
		<tr><td>Short Description</td><td><c:out value="${slice.shortDescription}"/><td></tr>
		<tr><td>Long Description</td><td><c:out value="${slice.longDescription}"/></td></tr>
		<tr><td>Slice Uuid</td><td><c:out value="${slice.sliceID}"/></td></tr>		
		<tr><td>Type</td><td><c:out value="${slice.sliceType}"/></td></tr>		
					<span style="visibility:hidden;display:none;">
					<textarea name="currentID">${slice.sliceID}</textarea>
				<textarea name="name">${slice.name}</textarea>					
				<textarea name="sdesc">${slice.shortDescription}</textarea>
				<textarea name="ldesc">${slice.longDescription}</textarea>								
			</span>			

		<tr><td><button name="submit" onclick="document.currentslice.submit()"/>Amend details</button></td></tr>
	</table>
	</form>
		<table width="300" border="1">
		<tr style="background-image: url(images/neontitledkgrn.png); color: white;"><td colspan="2"> 
Sub slices:&nbsp;&nbsp;
	</td></tr>
		<c:forEach items="${slice.sliceNames}" var="current">
		<tr><td><c:out value="${current}"/></td>
			<td width="80">
				<form action="Slice.do" type="GET" id="viewsubslice${current}">
				<span style="visibility:hidden;display:none;">
					<textarea name="sliceid">${slice.subSlices[current]}</textarea>
				</span>			
				<span style="visibility:hidden;display:none;">
					<textarea name="path">${path}</textarea>
				</span>			
				
				<button name="action" value="viewslice" onclick="document.viewsubslice${current}.submit()"/>View Slice</button>
				</form>
			</td>
		</tr>
		</c:forEach>		
	</table>
		<form action="newslice.jsp"  type="GET" id="addslice">
			<span style="visibility:hidden;display:none;">
				<textarea name="currentID">${slice.sliceID}</textarea>
			</span>	
			<span style="visibility:hidden;display:none;">
					<textarea name="path">${path}</textarea>
				</span>		
			<button name="submit" onclick="document.addslice.submit()"/>New Slice</button>

		</form>
	<br/>
	<table>
	<tr><th></th><th>Agent name</th><th>Class</th>
						<th>Agent ID</th>
						<th>Domain</th></tr>
		<c:forEach items="${agentlist}" var="current">
		<tr>
		<td>
			<input type="checkbox" name="agentchoice" value="${current.agentID.extendedString}">		
		</td>
		
		<td>
			<c:out value="${current.agentFqName}"/>			
		</td>			
		
		
		<td>
			<c:out value="${current.agentClassName}"/>
		</td>
		<c:if test="${current.resolved}">
		<td>
			<c:out value="${current.agentID}"/>
		</td>
		</c:if>
		<c:if test="${!current.resolved}">
		<td>
			<img src="images/transmit_error.png"/><c:out value="${current.agentID}"/>
		</td>
		</c:if>
		<td>
			<c:out value="${current.domain}"/>
		</td>		
		<c:if test="${!current.resolved}">
			<td>
				<input type="button" value="Reconnect to other..." name="reconnect${current.agentClassName}"/>
			</td>
		</c:if>
	</tr>
		</c:forEach>
	</table>
	<table>
	<tr><th></th><th>Agent name</th>
						<th>Number</th>
						<th>Domain</th></tr>
		<c:forEach items="${agenttypes}" var="current">
		<tr>
		<td>
			<input type="checkbox" name="agentchoiceany" value="${current.name}">		
		</td>
		
		<td>
			<c:out value="${current.name}"/>			
		</td>			
		
		
		<td>
			<c:out value="${current.number}"/>
		</td>
		
		<td>
			
		</td>		
		
	</tr>
		</c:forEach>
	</table>
	
	

	<form action="Slice.do" type="GET" id="addagents">
			<span style="visibility:hidden;display:none;">
				<textarea name="sliceID">${slice.sliceID}</textarea>
			</span>			
			<button name="action" value="addagents" onclick="document.addagents.submit()"/>Attach Agents</button>
	</form>
</div>
</body>
</html>
