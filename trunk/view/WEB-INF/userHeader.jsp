<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ include file = "/WEB-INF/header.jsp"%>

    <div style="float:left">
        <a href="<c:url value="/dict" />">
            <spring:message code="home" />
        </a>
	</div>

    <div style="float:right">
        <a href="<c:url value="/logout" />">
            <spring:message code="logout" />
        </a>
	</div>

    <br/>

	<hr/>