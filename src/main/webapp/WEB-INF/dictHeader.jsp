<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ include file = "/WEB-INF/header.jsp"%>
<div class="col-sm-2 bg-primary p-5">
   <ul class="nav nav-pills flex-column">
        <li class="nav-item">
            <a class="nav-link text-white" href='<c:url value="/dict" />'>
                <spring:message code="home" />
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link text-white" href='<c:url value="/dict/words/${dict.id}?offset=0" />'>
                <spring:message code="dict" />
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link text-white" href='<c:url value="/text/view/${dict.id}?offset=0" />'>
                <spring:message code="text" />
            </a>
        </li>
    </ul>
</div>