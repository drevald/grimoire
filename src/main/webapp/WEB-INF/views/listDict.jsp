<%@ include file = "/WEB-INF/accountHeader.jsp"%>

<div class="col-sm-8 p-5">

    <h3><spring:message code="dict.list" /></h3>

    <c:if test="${empty helperList}">
        <div class="mb-3">
            <spring:message code="dict.list.empty"/>
        </div>
    </c:if>

    <c:if test="${!empty helperList}">
        <div class="mb-3">
            <table class="table table-condensed table-sm">
                <c:forEach items="${helperList}" var="helper">
                <tr>
                    <td>${helper.dict.id}</td>
                    <td width="100%">${helper.dict.name}</td>
                    <td>${helper.dict.status}</td>
                    <td>
                    <c:if test="${!empty helper.jobs}">
                        <c:forEach items="${helper.jobs}" var="job">
                            <c:if test="${job.active}">
                                ${job.progress}%
                            </c:if>
                            ${fn:substring(job.details, 0, 16)}
                        </c:forEach>
                    </c:if>
                    <c:if test="${empty helper.jobs}">
                          EMPTY
                    </c:if>
                    </td>
                    <td><a href="dict/preview/${helper.dict.id}"><spring:message code="save" /></a></td>
                    <td><a href="dict/delete/${helper.dict.id}"><spring:message code="delete" /></a></td>
                    <td><a href="dict/words/${helper.dict.id}?offset=0"><spring:message code="words" /></a></td>
                    <td><a href="text/view/${helper.dict.id}?offset=0"><spring:message code="text" /></a></td>
                </tr>
                </c:forEach>
            </table>
        </div>
    </c:if>
    <div><a href="upload" class="btn btn-primary"><spring:message code="dict.create" /></a></div>
</div>
<%@ include file="/WEB-INF/footer.jsp"%>