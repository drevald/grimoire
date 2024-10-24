<%@ page import="org.helico.domain.TranslatorProvider" %>
<%@ page import="java.util.List" %>
<%@ include file="/WEB-INF/dictHeader.jsp" %>

<div class="col-sm-6 p-5">

    <h3 class="mb-5">${dict.name}</h3>

    <table class="table table-condensed table-sm p-5 mb-5">
        <tr>
            <th class="pe-5"><spring:message code="word.original"/></th>
            <th class="pe-5"><spring:message code="word.occurrence"/></th>
            <th class="pe-5"><spring:message code="word.translation"/></th>
            <th width="100%" class="ps-3 pe-3">&nbsp;</th>
        </tr>
        <c:forEach items="${words}" var="word">
            <tr>
                <td class="text-nowrap">${word.word.value}</td>
                <td>${word.counter}</td>
                <td class="text-nowrap">
                <c:forEach items="${word.word.translations}" var="translation">
                    ${translation.value}
                </c:forEach>
                </td>
                <td width="100%">&nbsp;</td>
            </tr>
        </c:forEach>
    </table>

    <nav class="mb-5">
        <ul class="pagination">
            <li class="page-item">
                <a class="page-link" href="?offset=0"><spring:message code="pager.first"/></a>
            </li>
            <c:if test="${offset>size}">
                <li class="page-item">
                    <a href="?offset=${offset-size}" class="page-link"><spring:message code="pager.previous"/></a>
                </li>
            </c:if>
            <li class="page-item active" aria-current="page">
                <a class="page-link" href="#">${currPage}/${totalPage}</a>
            </li>
            <c:if test="${maxOffset>offset+size}">
                <li class="page-item">
                  <a href="?offset=${offset+size}" class="page-link"><spring:message code="pager.next"/></a>
                </li>
            </c:if>
            <a href="?offset=${maxOffset}" class="page-link"><spring:message code="pager.last"/></a>
        </ul>
    </nav>

    <div class="mb-5">
        <form action="translate" method="post">
            Translate using &nbsp;
            <c:forEach var="translator" items="${translators}">
                    <input type="radio" name="encoding" value="${translator.id}" checked/>
                    &nbsp;${translator.provider.title} &nbsp;
                    <input type="radio" name="encoding" value="${translator.id}" checked/>
                    &nbsp;${translator.provider.title} &nbsp;
            </c:forEach>
            <input type="submit" value="Translate" class="btn btn-primary"/>
        </form>
    </div>

</div>

<%@ include file="/WEB-INF/footer.jsp" %>