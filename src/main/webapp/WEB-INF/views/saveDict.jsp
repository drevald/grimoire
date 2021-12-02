<%@ include file = "/WEB-INF/accountHeader.jsp"%>

	<h3><spring:message code="dict.edit.properties" /></h3>

	<table>
	<form method="post" action="save">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
	<input type="hidden" name="id" value="${dict.id}"/>
	<input type="hidden" name="accountId" value="${dict.accountId}"/>
	<input type="hidden" name="langId" value="${langId}"/>
		<tr>
			<td>
				<spring:message code='encoding'/>
			</td>
			<td>
                <c:forEach items="${encodings}" var="c">
                    <c:if test="${c eq encoding}">
                        <input type="radio" name="encoding" value="${c}" checked>${c}&nbsp;
                    </c:if>
                    <c:if test="${c ne encoding}">
                        <input type="radio" name="encoding" value="${c}"
                               onchange="javascript:document.forms[0].submit();">${c}&nbsp;
                    </c:if>
                </c:forEach>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <spring:message code="dict.enc.confirm"/>
                <input  type="button"
                        value="<spring:message code='save'/>"
                        onclick="javascript:document.forms[0].action='store';document.forms[0].submit();"/>
			</td>
		</tr>
		<tr><td colspan="2">&nbsp;</td></tr>
		<tr>
			<td valign="top"><spring:message code='preview'/></td>
			<td>${preview}</td>
		</tr>		
	</form>
	</table>

<%@ include file = "/WEB-INF/footer.jsp"%>