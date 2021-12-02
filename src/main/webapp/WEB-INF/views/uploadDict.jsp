<%@ include file = "/WEB-INF/accountHeader.jsp"%>

    <h3><spring:message code="dict.upload"/></h3>

    <form method="post" action="dict/upload" enctype="multipart/form-data">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <input type="hidden" name="account" value="${currAccount}"/>
        <input type="file" name="file"/>&nbsp;
        <spring:message code='dict.text.language'/>
        <select name="langId">
            <c:forEach items="${langs}" var="lang">
                    <option value="${lang.id}">${lang.name}</option>
            </c:forEach>
        </select>
	    <input type="submit" value="<spring:message code='upload'/>"/>
    </form>

<%@ include file = "/WEB-INF/footer.jsp"%>