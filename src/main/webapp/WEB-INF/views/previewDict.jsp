<%@ include file = "/WEB-INF/accountHeader.jsp"%>

    <div class="col-sm-8 p-5">

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
<%--
                <c:forEach items="${encodings}" var="c">
                    <c:if test="${c eq encoding}">
                        <input type="radio" name="encoding" value="${c}" checked>${c}&nbsp;
                    </c:if>
                    <c:if test="${c ne encoding}">
                        <input type="radio" name="encoding" value="${c}"
                               onchange='javascript:alert();';>${c}&nbsp;
                    </c:if>
--%>
                </c:forEach>
                    <c:forEach var="entry" items="${previews}">
                        <tr>
                            <td>
                                <input type="radio" name="encoding" value="${entry.key}" checked>${entry.key}&nbsp;
                            </td>
                            <td>${entry.key}</td>
                            <td>${entry.value}</td>
                        </tr>
                    </c:forEach>

            </td>
        </tr>
        <tr>
            <td colspan="2">
                <spring:message code="dict.enc.confirm"/>
                <input  type="button"
                        value="<spring:message code='save'/>"
                        onclick="javascript:document.forms[0].action='store';document.forms[0].submit();"/>
                <input  type="button"
                        value="<spring:message code='cancel'/>"
                        onclick="javascript:history.back();"/>
            </td>
        </tr>
        <tr><td colspan="2">&nbsp;</td></tr>
        <tr>
            <td valign="top"><spring:message code='preview'/></td>
            <td>${preview}</td>
        </tr>
    </form>
    </table>

    </div>

<%@ include file = "/WEB-INF/footer.jsp"%>