<%@ include file = "/WEB-INF/dictHeader.jsp"%>

<div class="col-sm-10 p-5">

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4>${dict.name}</h4>
        <a href="${pageContext.request.contextPath}/text/view/${dict.id}?offset=0" class="btn btn-secondary btn-sm">Cancel</a>
    </div>

    <form method="post" action="${pageContext.request.contextPath}/text/edit/${dict.id}/save">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <textarea name="content" class="form-control font-monospace"
                  style="height:75vh;resize:vertical;white-space:pre;overflow-wrap:normal;overflow-x:scroll"
                  spellcheck="false">${content}</textarea>
        <div class="mt-3">
            <button type="submit" class="btn btn-primary">Save</button>
            <a href="${pageContext.request.contextPath}/text/view/${dict.id}?offset=0" class="btn btn-secondary">Cancel</a>
        </div>
    </form>

</div>

<%@ include file = "/WEB-INF/footer.jsp"%>
