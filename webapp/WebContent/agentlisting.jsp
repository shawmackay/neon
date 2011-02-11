<%@page contentType="text/html"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<title>:: Neon :: Application Management</title>
<link rel="stylesheet" type="text/css" href="neon.css" />
</head>
<body>

<script type="text/javascript">
function toggleDivVisibility(id){
	var div = document.getElementById("agentsection"+id);
	var anydiv = document.getElementById("anysection"+id);
 if(div.style.display=='none'){
 div.style.display="block";
 div.style.visibility='visible';
 anydiv.style.display="none";
 anydiv.style.visibility='hidden';
 } else {
 div.style.display="none";
 div.style.visibility='hidden';
 anydiv.style.display="block";
 anydiv.style.visibility='visible';
 
 }


 }
</script>

<%@ include file="header.html"%>
<div
	style="background-image: url(images/neontitledkblue.png); color: white; font-size: 18px; height: 30px;">
<div style="text-align: right;">
<form action="Slice.do" id="filterform" type="GET">
<div style="visibility: hidden; display: none;"><textarea
	name="sliceID">${param.sliceID}</textarea></div>
<input name="filter" value="${param.filter}" />
<button name="action" value="addagents"
	onClick="document.filterform.submit()">Apply filter</button>
</form>


</div>
</div>

<form action="Slice.do" id="agentform" type="GET">
<div style="visibility: hidden; display: none;"><textarea
	name="toslice">${param.sliceID}</textarea></div>


<c:forEach items="${agents}" var="currentList">
	<div
		style="background-image: url(images/neontitlelgtblue.png); color: white; font-size: 18px;">

	<span style="left: 16px; margin-left: 16px;">Agent Host <c:out
		value="${currentList.key}" /></span></div>
	<table>
		<tr>
			<th></th>
			<th>Agent name</th>
			<th>Details</th>
		</tr>

		<c:forEach items="${agents[currentList.key]}" var="current">
			<tr>
				<td><c:out value="${current.key}" /></td>
				<td><input name="hello"
					onchange="javascript:toggleDivVisibility('${current.key}');"
					type="checkbox">Show Individual?</input></td>
				<td>
				<div id="anysection${current.key }">
					<input type="checkbox" name="agentchoiceany"
								value="${current.key}">Use any
				</div>
				<div id="agentsection${current.key}" style="display: none;">
				<table>
					<tr>
						<th>Use</th>
						<th>Class</th>
						<th>Agent ID</th>
						<th>Domain</th>
					</tr>

					<c:forEach items="${current.value}" var="agentsection">
						<tr>

							<td><input type="checkbox" name="agentchoice"
								value="${agentsection.agentID.extendedString}"></td>
							
							<td><c:out value="${agentsection.agentClassName}" /></td>
							<td><c:out value="${agentsection.agentID}" /></td>
							<td><c:out value="${agentsection.domain}" /></td>

						</tr>
					</c:forEach>
				</table>
				</div>
				</td>
			</tr>
		</c:forEach>

	</table>
	<hr>
</c:forEach>
<button name="action" value="addselected"
	onClick="document.agentform.submit()">Add Selected Agents</button>
</form>

</body>
</html>
