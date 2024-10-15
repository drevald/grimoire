<%@ include file = "/WEB-INF/accountHeader.jsp"%>

    <div class="col-sm-8 p-5">

        <h3><spring:message code="dict.preview" /></h3>

        <table class="table p-10">
            <form method="post" action="/dict/store">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <input type="hidden" name="id" value="${dict.id}"/>
            <input type="hidden" name="accountId" value="${dict.accountId}"/>
            <input type="hidden" name="langId" value="${langId}"/>
            <tr>
            <td colspan="2">
                <spring:message code='dict.enc.confirm'/>
            </td>
            <c:forEach var="entry" items="${previews}">
            <tr>
                <td  style="white-space: nowrap;">
                    <input type="radio" name="encoding" value="${entry.key}"/>&nbsp;${entry.key}
                </td>
                <td>${entry.value}</td>
            </tr>
            </c:forEach>
            <tr>
            <td colspan="2">
                <input  type="button" class="btn btn-primary active"
                        value="<spring:message code='confirm'/>"
                        onclick="javascript:document.forms[0].action='/dict/store';document.forms[0].submit();"/>
                <input  type="button" class="btn btn-secondary"
                        value="<spring:message code='cancel'/>"
                        onclick="javascript:window.location.href = '/';"/>
            </td>
            </tr>
            </form>
        </table>

    </div>

<%@ include file = "/WEB-INF/footer.jsp"%>