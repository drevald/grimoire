<%@ include file = "/WEB-INF/adminHeader.jsp"%>

<div class="col-sm-8 p-5">

    <h3>Translation Providers</h3>

    <c:if test="${empty providers}">
        <div class="mb-3">
            No providers
        </div>
    </c:if>

    <c:if test="${!empty providers}">
        <div class="mb-3">
            <table class="table table-condensed table-sm">
                <tr>
                    <th>Id</th>
                    <th>Title</th>
                    <th>Host</th>
                    <th>Request pattern</th>
                    <th>Response pattern</th>
                </tr>
                <c:forEach items="${providers}" var="provider">
                <tr>
                    <td>${provider.id}</td>
                    <td>${provider.title}</td>
                    <td>${provider.host}</td>
                    <td>${provider.reqPattern}</td>
                    <td>${provider.resPattern}</td>
                </tr>
                </c:forEach>
            </table>
        </div>
    </c:if>
</div>


<%@ include file="/WEB-INF/footer.jsp"%>