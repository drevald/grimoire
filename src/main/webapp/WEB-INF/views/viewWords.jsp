<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/dictHeader.jsp" %>

<div class="col-sm-10 p-5">

    <h3 class="mb-4">${dict.name}</h3>

    <table class="table table-sm table-hover">
        <thead>
            <tr>
                <th><spring:message code="word.original"/></th>
                <th><spring:message code="word.occurrence"/></th>
                <th><spring:message code="word.translation"/></th>
            </tr>
        </thead>
        <tbody>
        <c:forEach items="${words}" var="word">
            <tr>
                <td class="text-nowrap">${word.word.value}</td>
                <td>${word.counter}</td>
                <td class="text-nowrap">
                    <c:set var="matched" value=""/>
                    <c:forEach items="${word.word.translations}" var="tr">
                        <c:forEach items="${translators}" var="tl">
                            <c:if test="${tr.translatorId == tl.id and empty matched}">
                                <c:set var="matched" value="${tr.value}"/>
                            </c:if>
                        </c:forEach>
                    </c:forEach>
                    <c:choose>
                        <c:when test="${not empty matched}">${matched}</c:when>
                        <c:otherwise><span style="color:#bbb">&mdash;</span></c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <nav class="mb-4">
        <ul class="pagination pagination-sm">
            <li class="page-item">
                <a class="page-link" href="?offset=0"><spring:message code="pager.first"/></a>
            </li>
            <c:if test="${offset>size}">
                <li class="page-item">
                    <a href="?offset=${offset-size}" class="page-link"><spring:message code="pager.previous"/></a>
                </li>
            </c:if>
            <li class="page-item active">
                <a class="page-link" href="#">${currPage}/${totalPage}</a>
            </li>
            <c:if test="${maxOffset>offset+size}">
                <li class="page-item">
                    <a href="?offset=${offset+size}" class="page-link"><spring:message code="pager.next"/></a>
                </li>
            </c:if>
            <li class="page-item">
                <a href="?offset=${maxOffset}" class="page-link"><spring:message code="pager.last"/></a>
            </li>
        </ul>
    </nav>

    <%-- Translate controls --%>
    <c:if test="${dict.status == 'PARSED' || dict.status == 'TRANSLATED'}">
    <div class="mt-2">
        <form action="/dict/words/translate" method="post" style="display:inline">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <input type="hidden" name="dictId" value="${dict.id}"/>
            <c:forEach var="translator" items="${translators}" varStatus="s">
                <label style="margin-right:12px;cursor:pointer">
                    <input type="radio" name="translatorId" value="${translator.id}" ${s.first ? 'checked' : ''}/>
                    &nbsp;${translator.provider.title}
                </label>
            </c:forEach>
            <input type="submit" value="Translate all" class="btn btn-primary btn-sm"/>
        </form>
    </div>
    </c:if>

    <c:if test="${dict.status == 'TRANSLATING'}">
    <div class="mt-2 d-flex align-items-center gap-3">
        <span class="text-muted">Translation in progress… ${job.progress}%</span>
        <form method="post" action="/dict/stop/${dict.id}" style="display:inline">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <button type="submit" class="btn btn-warning btn-sm">Stop</button>
        </form>
    </div>
    </c:if>

</div>

<%@ include file="/WEB-INF/footer.jsp" %>
