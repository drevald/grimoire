<%@include file="/WEB-INF/dictHeader.jsp"%>

<c:if test="${not empty param.error}">
	<div color="red"> <spring:message code="loginerror" />
	: ${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message} </div>
</c:if>

<form method="POST" action="<c:url value="updateAccount" />">
<input type="hidden" name="accountId" value="${account.id}"/>
<table>
	<tr>
		<td align="right"><spring:message code="login" /></td>
		<td><input type="text" name="accountname" value="${account.name}"/></td>
	</tr>
	<tr>
		<td align="right"><spring:message code="password" /></td>
		<td><input type="password" name="password"  value="${account.password}"/></td>
	</tr>
	<tr>
		<td align="right"><spring:message code="register.lang.native" /></td>
		<td>
            <select name="nativeLangId" style="width:100%">
                <c:forEach items="${langs}" var="lang">
                    <c:if test="${account.nativeLangId==lang.id}">
                        <option value="${lang.id}" selected><spring:message code='${lang.name}' /></option>
                    </c:if>
                    <c:if test="${account.nativeLangId!=lang.id}">
                        <option value="${lang.id}"><spring:message code='${lang.name}' /></option>
                    </c:if>
                </c:forEach>
            </select>
		</td>
    </tr>
    <tr>
		<td align="right" valign="top"><spring:message code="register.lang.learn" /></td>
		<td>
            <select name="learnedLangId" size="12" style="width:100%" multiple="yes">
            <c:out value="${account}"/>
            <c:out value="${account.accountLangs}"/>
                <c:forEach items="${langs}" var="lang">
                    <c:out value="${lang}"/>
                    <c:if test="${fn:contains(account.accountLangs, lang)}">
                        <option value="${lang.id}" selected><spring:message code='${lang.name}' /></option>
                    </c:if>
                    <c:if test="${!fn:contains(account.accountLangs, lang)}">
                        <option value="${lang.id}"><spring:message code='${lang.name}' /></option>
                    </c:if>
                </c:forEach>
            </select>
		</td>
    </tr>
	<tr>
	    <td>&nbsp;</td>
		<td><input type="submit" value="<spring:message code='save' />" />
		<input type="button" value="<spring:message code='cancel' />"/></td>
	</tr>
</table>
</form>
</body>
</html>
