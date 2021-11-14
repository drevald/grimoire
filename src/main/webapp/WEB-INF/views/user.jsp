<%@ include file = "/WEB-INF/header.jsp"%>
  
<h1>Please upload a file</h1>
<form method="post" action="form" enctype="multipart/form-data">
	<input type="text" name="name"/>
	<input type="file" name="file"/>
	<input type="submit"/>
</form>

<h2><spring:message code="title" /></h2>

<form:form method="post" action="add" commandName="account">
<table>
	<tr>
		<td><spring:message code="accountname" /></td>
		<td><form:input path="name" /></td>
	</tr>
	<tr>
	<td colspan="2">
		<input type="submit" value="<spring:message code="addaccount"/>" />
	</td>
	</tr>
</table>
</form:form>

<h3><spring:message code="accounts" /></h3>

<c:if test="${empty accountList}">
Account list is empty
</c:if>
<c:if test="${!empty accountList}">
	<table class="data">
		<tr>
			<th><spring:message code="accountname" /></th>
			<th>&nbsp;</th>
		</tr>
		<c:forEach items="${accountList}" var="account">
			<tr>
				<td>${account.name}</td>
				<td><a href="delete/${account.id}"><spring:message code="delete" /></a></td>
			</tr>
		</c:forEach>
	</table>
</c:if>

</body>
</html>