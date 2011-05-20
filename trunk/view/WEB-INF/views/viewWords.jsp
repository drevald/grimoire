<%@ include file = "/WEB-INF/dictHeader.jsp"%>

	<h3><spring:message code="text" /></h3>

	<c:forEach items="${words}" var="word">
       		   ${word}
        </c:forEach>

<%@ include file = "/WEB-INF/footer.jsp"%>