<%@ include file = "accountHeader.jsp"%>

<div class="col-sm-8 p-5">

    <h3><spring:message code="dict.upload"/></h3>

    <form method="post" action="dict/upload" enctype="multipart/form-data">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <input type="hidden" name="account" value="${currAccount}"/>
        <div class="form-group">
            <label for="file">File with document</label>
            <input type="file" class="form-control" id="file" name="file">
            <small class="form-text text-muted">Plain text document are supported now</small>
        </div>
        <div class="form-group">
            <label for="langId"><spring:message code='dict.text.language'/></label>
            <select class="form-control" name="langId" id="langId">
                <c:forEach items="${langs}" var="lang">
                        <option value="${lang.id}">${lang.name}</option>
                </c:forEach>
            </select>
            <small class="form-text text-muted">Select document language</small>
        </div>
        <div class="form-group">
            <input type="submit" value="<spring:message code='upload'/>"/>
        </div>
    </form>

</dict>

<%@ include file = "footer.jsp"%>