<%@ include file = "/WEB-INF/adminHeader.jsp"%>

<div class="col-sm-8 p-5">

    <h3 class="mb-5">Transitions</h3>

    <c:if test="${empty transitions}">
        <div class="mb-3">
            No transitions
        </div>
    </c:if>

    <c:if test="${!empty transitions}">
        <div class="mb-3 mb-5">
            <table class="table table-condensed table-sm">
                <tr>
                    <th>Id</th>
                    <th>Event</th>
                    <th>Source state</th>
                    <th>Dest. state</th>
                    <th>Handler</th>
                </tr>
                <c:forEach items="${transitions}" var="trans">
                <tr>
                    <td>${trans.id}</td>
                    <td>${trans.event}</td>
                    <td>${trans.sourceStatus}</td>
                    <td>${trans.destStatus}</td>
                    <td>${trans.handlerName}</td>
                </tr>
                </c:forEach>
            </table>
        </div>
    </c:if>
</div>

<%@ include file="/WEB-INF/footer.jsp"%>
