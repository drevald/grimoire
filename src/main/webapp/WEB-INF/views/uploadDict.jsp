<%@ include file = "/WEB-INF/accountHeader.jsp"%>

<div class="col-sm-8 p-5">

    <h3><spring:message code="dict.add"/></h3>

    <form method="post" action="dict/upload" enctype="multipart/form-data">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <input type="hidden" name="account" value="${currAccount}"/>
        <div class="mb-3">
            <label for="formFile" class="form-label"><spring:message code='dict.text.file'/></label>
            <input class="form-control" type="file" name="file" id="formFile"/>
        </div>
        <div class="mb-3">
            <label for="langId" class="form-label"><spring:message code='dict.text.language'/></label>
            <select  class="form-select form-select mb-3" name="langId">
                <c:forEach items="${langs}" var="lang">
                        <option value="${lang.id}">${lang.name}</option>
                </c:forEach>
            </select>
        </div>
        <input type="submit" class="btn btn-primary" value="<spring:message code='upload'/>"/>
    </form>

</dict>

<%@ include file = "/WEB-INF/footer.jsp"%>