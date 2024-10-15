<%@ include file = "/WEB-INF/accountHeader.jsp"%>

<div class="col-sm-8 p-5">

    <h3><spring:message code="dict.upload"/></h3>

    <form method="post" action="dict/upload" enctype="multipart/form-data">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <input type="hidden" name="account" value="${currAccount}"/>
        <div class="mb-3">
          <label for="formFile" class="form-label">Default file input example</label>
          <input class="form-control" type="file" name="file" id="formFile"/>&nbsp;
        </div>
        <spring:message code='dict.text.language'/>
        <select  class="form-select form-select-lg mb-3" name="langId">
            <c:forEach items="${langs}" var="lang">
                    <option value="${lang.id}">${lang.name}</option>
            </c:forEach>
        </select>
        <input type="submit" value="<spring:message code='upload'/>"/>
    </form>

</dict>

<%@ include file = "/WEB-INF/footer.jsp"%>