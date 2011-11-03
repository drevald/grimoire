<%@ include file = "/WEB-INF/adminHeader.jsp"%>

    <h3>Transitions</h3>

    <table>
        <c:forEach items="${transitions}" var="trans">

            <tr>
                <td>${trans.event}</td>
                <td>${trans.sourceStatus}</td>
                <td>${trans.destStatus}</td>
                <td>${trans.handlerName}</td>
            </tr>

        </c:forEach>
    </table>


<%@ include file="/WEB-INF/footer.jsp"%>