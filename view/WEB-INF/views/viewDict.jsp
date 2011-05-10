<%@ include file = "/WEB-INF/userHeader.jsp"%>

	<h3><spring:message code="dict.generate" /></h3>
	
    <form method="post" action="generate">
        <input type="submit" value="<spring:message code='generate'/>"/>
        <input type="hidden" name="dictId" value="${dict.id}"/>
    </form>

	<h3><spring:message code="text" /></h3>

	${preview}

<%@ include file = "/WEB-INF/footer.jsp"%>