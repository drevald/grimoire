<%@ include file = "/WEB-INF/header.jsp"%>
  
<h1>Please upload a file</h1>
<form method="post" action="form" enctype="multipart/form-data">
	<input type="text" name="name"/>
	<input type="file" name="file"/>
	<input type="submit"/>
</form>

<h2><spring:message code="title" /></h2>

<form:form method="post" action="add" commandName="user">
<table>
	<tr>
		<td><spring:message code="username" /></td>
		<td><form:input path="name" /></td>
	</tr>
	<tr>
	<td colspan="2">
		<input type="submit" value="<spring:message code="adduser"/>" />
	</td>
	</tr>
</table>
</form:form>

<h3><spring:message code="users" /></h3>

<c:if test="${empty userList}">
User list is empty
</c:if>
<c:if test="${!empty userList}">
	<table class="data">
		<tr>
			<th><spring:message code="username" /></th>
			<th>&nbsp;</th>
		</tr>
		<c:forEach items="${userList}" var="user">
			<tr>
				<td>${user.name}</td>
				<td><a href="delete/${user.id}"><spring:message code="delete" /></a></td>
			</tr>
		</c:forEach>
	</table>
</c:if>

</body>
</html>