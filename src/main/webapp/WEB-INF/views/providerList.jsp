<%@ include file = "/WEB-INF/adminHeader.jsp"%>

<div class="col-sm-8 p-5">

    <div class="d-flex justify-content-between align-items-center mb-4">
        <h3>Translation Providers</h3>
        <a href="${pageContext.request.contextPath}/admin/providers/new" class="btn btn-primary">
            Add New Provider
        </a>
    </div>

    <c:if test="${empty providers}">
        <div class="alert alert-info">
            No providers configured yet. Click "Add New Provider" to create one.
        </div>
    </c:if>

    <c:if test="${!empty providers}">
        <div class="mb-3">
            <table class="table table-striped table-hover">
                <thead class="table-dark">
                    <tr>
                        <th>Id</th>
                        <th>Title</th>
                        <th>Method</th>
                        <th>Host</th>
                        <th>Content-Type</th>
                        <th>Charset</th>
                        <th>Headers</th>
                        <th>Enabled</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${providers}" var="provider">
                    <tr>
                        <td>${provider.id}</td>
                        <td><strong>${provider.title}</strong></td>
                        <td>
                            <c:choose>
                                <c:when test="${provider.method == 'POST'}">
                                    <span class="badge bg-success">POST</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-primary">GET</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <small class="text-muted font-monospace">
                                <c:choose>
                                    <c:when test="${not empty provider.host}">
                                        ${provider.host}
                                    </c:when>
                                    <c:otherwise>
                                        <em>(URL in pattern)</em>
                                    </c:otherwise>
                                </c:choose>
                            </small>
                        </td>
                        <td><small>${provider.contentType}</small></td>
                        <td><small>${provider.charset}</small></td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty provider.headers}">
                                    <span class="badge bg-info text-dark" title="${provider.headers}">
                                        Custom
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <small class="text-muted">-</small>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${provider.enabled}">
                                    <span class="badge bg-success">Yes</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-secondary">No</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="text-nowrap">
                            <a href="${pageContext.request.contextPath}/admin/providers/edit/${provider.id}"
                               class="btn btn-sm btn-outline-primary"
                               title="Edit provider">
                                Edit
                            </a>
                            <form method="post"
                                  action="${pageContext.request.contextPath}/admin/providers/delete/${provider.id}"
                                  style="display: inline;"
                                  onsubmit="return confirm('Are you sure you want to delete provider: ${provider.title}?');">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                <button type="submit" class="btn btn-sm btn-outline-danger" title="Delete provider">
                                    Delete
                                </button>
                            </form>
                        </td>
                    </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:if>
</div>


<%@ include file="/WEB-INF/footer.jsp"%>