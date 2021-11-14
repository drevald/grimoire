<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ include file = "/WEB-INF/header.jsp"%>

    <div style="float:left;margin-right:20px">
        <a href="<c:url value="/dict" />">
            <spring:message code="home" />
        </a>
	</div>

    <div style="float:left;margin-right:20px">
        <a href="<c:url value="/preferences" />">
            <spring:message code="preferences" />
        </a>
	</div>

    <div style="float:right">
        <%=request.getUserPrincipal().getName()%>
        <a href="<c:url value="/logout" />">
            <spring:message code="logout" />
        </a>
	</div>

    <br/>

	<hr/>