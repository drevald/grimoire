<%@include file="/WEB-INF/loginHeader.jsp"%>

<c:if test="${not empty param.error}">
	<font color="red"> <spring:message code="loginerror" />
	: ${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message} </font>
</c:if>

<form method="POST" action="<c:url value="/j_spring_security_check" />">
<table>
	<tr>
		<td align="right"><spring:message code="username" /></td>
		<td><input type="text" name="j_username" value="user"/></td>
	</tr>
	<tr>
		<td align="right"><spring:message code="password" /></td>
		<td><input type="password" name="j_password" value="pass"/></td>
	</tr>
	<tr>
		<td align="right"><spring:message code="remember" /></td>
		<td><input type="checkbox" name="_spring_security_remember_me" /></td>
	</tr>
	<tr>
	    <td>&nbsp;</td>
		<td><input type="submit" value="<spring:message code='login'/>" /></td>
	</tr>
</table>
</form>
</body>
</html>