<%@ include file = "/WEB-INF/adminHeader.jsp"%>

    <h3>Translation Providers</h3>

    <table>
        <c:forEach items="${providers}" var="provider">

            <tr>
                <td>${provider.title}</td>
            </tr>

        </c:forEach>
    </table>


<%@ include file="/WEB-INF/footer.jsp"%>