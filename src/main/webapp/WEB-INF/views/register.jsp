<%@include file="/WEB-INF/loginHeader.jsp"%>

<c:if test="${not empty param.error}">
	<div color="red"> <spring:message code="loginerror" />
	: ${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message} </div>
</c:if>

<form method="POST" action="<c:url value="/registerUser" />">
<table border=1>
	<tr>
		<td align="right"><spring:message code="login" /></td>
		<td><input type="text" name="username" value="user"/></td>
	</tr>
	<tr>
		<td align="right"><spring:message code="password" /></td>
		<td><input type="password" name="password" value="pass"/></td>
	</tr>
	<tr>
		<td align="right"><spring:message code="register.lang.native" /></td>
		<td>
            <select name="nativeLangId" style="width:100%">
                <c:forEach items="${langs}" var="lang">
                        <option value="${lang.id}">${lang.name}</option>
                </c:forEach>
            </select>
		</td>
    </tr>
    <tr>
		<td align="right"><spring:message code="register.lang.learn" /></td>
		<td>
            <select name="learnedLangId" size="12" style="width:100%" multiple="yes">
                <c:forEach items="${langs}" var="lang">
                        <option value="${lang.id}">${lang.name}</option>
                </c:forEach>
            </select>
		</td>
    </tr>
	<tr>
	    <td>&nbsp;</td>
		<td><input type="submit" value="Register" />
		<input type="reset" value="Reset" /></td>
	</tr>
</table>
</form>
</body>
</html>