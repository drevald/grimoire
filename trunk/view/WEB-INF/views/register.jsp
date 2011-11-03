<%@include file="/WEB-INF/loginHeader.jsp"%>

<c:if test="${not empty param.error}">
	<div color="red"> <spring:message code="loginerror" />
	: ${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message} </div>
</c:if>

<form method="POST" action="<c:url value="/registerUser" />">
<table>
	<tr>
		<td align="right"><spring:message code="login" /></td>
		<td><input type="text" name="username" value="user"/></td>
	</tr>
	<tr>
		<td align="right"><spring:message code="password" /></td>
		<td><input type="password" name="password" value="pass"/></td>
	</tr>
	<tr>
		<td colspan="2" align="right"><input type="submit" value="Register" />
		<input type="reset" value="Reset" /></td>
	</tr>
</table>
</form>
</body>
</html>