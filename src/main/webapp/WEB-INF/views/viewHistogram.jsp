<%@ page import="org.helico.domain.TranslatorProvider" %>
<%@ page import="java.util.List" %>
<%@ include file="/WEB-INF/dictHeader.jsp" %>

<div class="col-sm-6 p-5">

    <h3>Words occurances per hundred</h3>

    <c:if test="${empty histogram}">
        <div class="mb-3">
            No words
        </div>
    </c:if>

    <c:if test="${!empty histogram}">
        <div class="mb-3">
            <table class="table table-condensed table-sm">
                <tr>
                    <th>Nth hundred</th>
                    <th>Words occurances</th>
                </tr>
                <c:forEach items="${histogram}" var="item">
                <tr>
                    <td>${item.key}</td>
                    <td>${item.value}</td>
                </tr>
                </c:forEach>
            </table>
        </div>
    </c:if>

</div>

<%@ include file="/WEB-INF/footer.jsp" %>