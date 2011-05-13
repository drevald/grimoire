<%@ include file = "/WEB-INF/userHeader.jsp"%>

	<h3><spring:message code="dict.edit.properties" /></h3>

	<table>
	<form method="post" action="save">
    <input type="hidden" name="id" value="${dict.id}"/>
	<input type="hidden" name="userId" value="${dict.userId}"/>
    <input type="hidden" name="lang" value="${lang}"/>
		<tr>
			<td>
				<spring:message code='encoding'/>
			</td>
			<td>
                <c:forEach items="${encodings}" var="c">
                    <c:if test="${c eq dict.encoding}">
                        <input type="radio" name="encoding" value="${c}" checked >${c}&nbsp;
                    </c:if>
                    <c:if test="${c ne dict.encoding}">
                        <input type="radio" name="encoding" value="${c}"
                               onchange="javascript:document.forms[0].submit();">${c}&nbsp;
                    </c:if>
                </c:forEach>
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



</body>
</html>