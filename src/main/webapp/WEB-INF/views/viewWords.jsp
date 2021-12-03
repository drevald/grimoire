<%@ page import="org.helico.domain.TranslatorProvider" %>
<%@ page import="java.util.List" %>
<%@ include file="/WEB-INF/dictHeader.jsp" %>

<h3>${dict.name}</h3>
<div style="float:left;width:100%">
    <div style="float:left;margin-right:20px">
        <table>
            <tr>
                <td><b><spring:message code="word.original"/></b></td>
                <td>&nbsp;</td>
                <td><b><spring:message code="word.occurrence"/></b></td>
                <td>&nbsp;</td>
                <td><b><spring:message code="word.translation"/></b></td>
            </tr>
            <c:forEach items="${words}" var="word">
                <tr>
                    <td>${word.word.value}</td>
                    <td>&nbsp;</td>
                    <td>${word.counter}</td>
                    <td>&nbsp;</td>
                    <td>

                    <c:forEach items="${word.word.translations}" var="translation">
                        ${translation.value}(${translation.translatorId})
                    </c:forEach>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <br>
        <a href="?offset=0"><spring:message code="pager.first"/></a>&nbsp;
        <c:if test="${offset>size}">
            <a href="?offset=${offset-size}"><spring:message code="pager.previous"/></a>&nbsp;
        </c:if>
        &nbsp;Current page ${currPage} of ${totalPage}&nbsp;
        <c:if test="${maxOffset>offset+size}">
            <a href="?offset=${offset+size}"><spring:message code="pager.next"/></a>&nbsp;
        </c:if>
        <a href="?offset=${maxOffset}"><spring:message code="pager.last"/></a>&nbsp;
        <br>
    </div>
</div>
</div>
<c:if test="${dict.status eq 'PARSED' || dict.status eq 'TRANSLATED'}">
<hr>
<form action="translate" method="post">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <input type="hidden" name="dictId" value="${dict.id}"/>
    <spring:message code="words.not.translated"/>
    <input type="submit" value="Translate"/>
    <spring:message code="using"/>
    <select name="translatorId">
        <c:forEach items="${translators}" var="translator">
                <option value="${translator.id}">${translator.provider.title}</option>
        </c:forEach>
    </select>
</form>
</c:if>
<%@ include file="/WEB-INF/footer.jsp" %>