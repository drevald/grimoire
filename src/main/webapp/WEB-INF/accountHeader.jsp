<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ include file = "/WEB-INF/header.jsp"%>
<div class="col-sm-2 bg-primary p-5">
   <ul class="nav nav-pills flex-column">
        <li class="nav-item">
            <a class="nav-link text-white" href="<c:url value="/dict" />">
                <spring:message code="home" />
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link text-white" href="<c:url value="/preferences" />">
                <spring:message code="preferences" />
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link text-white" href="<c:url value="/logout" />">
                <spring:message code="logout" />
            </a>
        </li>
        <sec:authorize access="hasRole('ADMIN')">
                <li class="nav-item">
                    <a class="nav-link text-white" href="<c:url value="/admin/transitions" />">
                        Admin
                    </a>
                </li>
        </sec:authorize>
    </ul>
</div>



