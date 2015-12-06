<%@ include file = "/WEB-INF/header.jsp"%>

    <div style="float:left;margin-right:20px">
        <a href="<c:url value="/dict" />">
            <spring:message code="home" />
        </a>
	</div>

    <div style="float:left;margin-right:20px">
        <a href="<c:url value="/dict/words/${dict.id}?offset=0" />">
            <spring:message code="dict" />
        </a>
	</div>

    <div style="float:left;margin-right:20px">
        <a href="<c:url value="/text/view/${dict.id}?offset=0" />">
            <spring:message code="text" />
        </a>
	</div>


    <div style="float:right">
        <a href="<c:url value="/logout" />">
            <spring:message code="logout" />
        </a>
	</div>

    <br/>

	<hr/>