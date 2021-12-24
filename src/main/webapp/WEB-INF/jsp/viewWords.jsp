<%@ page import="org.helico.domain.TranslatorProvider" %>
<%@ page import="java.util.List" %>
<%@ include file="/WEB-INF/dictHeader.jsp" %>

<div class="col-sm-6 p-5">

<h3>${dict.name}</h3>

        <table class="table table-condensed table-sm">
            <tr>
                <th><spring:message code="word.original"/></th>
                <th><spring:message code="word.occurrence"/></th>
                <th><spring:message code="word.translation"/></th>
                <th width="100%">&nbsp;</th>
            </tr>
            <c:forEach items="${words}" var="word">
                <tr>
                    <td>${word.word.value}</td>
                    <td>${word.counter}</td>
                    <td>
                    <c:forEach items="${word.word.translations}" var="translation">
                        ${translation.value}
                    </c:forEach>
                    </td>
                    <td width="100%">&nbsp;</td>
                </tr>
            </c:forEach>
        </table>

        <div>
            <a href="?offset=0" class="btn"><spring:message code="pager.first"/></a>
            <c:if test="${offset>size}">
                <a href="?offset=${offset-size}" class="btn"><spring:message code="pager.previous"/></a>
            </c:if>
            Current page ${currPage} of ${totalPage}
            <c:if test="${maxOffset>offset+size}">
                <a href="?offset=${offset+size}" class="btn"><spring:message code="pager.next"/></a>
            </c:if>
            <a href="?offset=${maxOffset}" class="btn"><spring:message code="pager.last"/></a>
        </div>

        <c:if test="${dict.status eq 'PARSED' || dict.status eq 'TRANSLATED'}">
            <div>
                <form action="translate" method="post">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <input type="hidden" name="dictId" value="${dict.id}"/>
                    <spring:message code="words.not.translated"/>
                    <input type="submit" value="Translate" class="btn btn-info"/>
                    <spring:message code="using"/>
                    <select name="translatorId">
                        <c:forEach items="${translators}" var="translator">
                                <option value="${translator.id}">${translator.provider.title}</option>
                        </c:forEach>
                    </select>
                </form>
            </div>
        </c:if>

</div>

<%@ include file="/WEB-INF/footer.jsp" %>