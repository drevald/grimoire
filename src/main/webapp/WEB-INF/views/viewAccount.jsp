<%@include file="/WEB-INF/accountHeader.jsp"%>

<div class="col-sm-8 p-5">

<h3 class="mb-5"><spring:message code="preferences"/></h3>

<form method="POST" action="<c:url value="updateAccount" />">
    <input type="hidden" name="accountId" value="${account.id}"/>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <div class="mb-3">
        <label class="form-label"><spring:message code='username'/></label>
        <input type="text" name="accountname" value="${account.name}" class="form-control"/>
    </div>
    <div class="mb-3">
        <label class="form-label"><spring:message code='password'/></label>
        <input type="password" name="password"  value="${account.password}" class="form-control"/>
    </div>
    <div class="mb-3">
        <label class="form-label"><spring:message code='register.lang.native'/></label>
        <select class="form-select form-select mb-3" name="nativeLangId">
            <c:forEach items="${langs}" var="lang">
                    <c:if test="${account.nativeLangId==lang.id}">
                        <option value="${lang.id}" selected><spring:message code='${lang.name}' /></option>
                    </c:if>
                    <c:if test="${account.nativeLangId!=lang.id}">
                        <option value="${lang.id}"><spring:message code='${lang.name}' /></option>
                    </c:if>
            </c:forEach>
        </select>
    </div>
    <div class="mb-3">
        <label class="form-label"><spring:message code='register.lang.learn'/></label>
        <select size="12" multiple="yes" class="form-select form-select mb-3" name="learnedLangId">
            <c:forEach items="${langs}" var="lang">
                    <c:if test="${fn:contains(account.accountLangs, lang)}">
                        <option value="${lang.id}" selected><spring:message code='${lang.name}' /></option>
                    </c:if>
                    <c:if test="${!fn:contains(account.accountLangs, lang)}">
                        <option value="${lang.id}"><spring:message code='${lang.name}' /></option>
                    </c:if>
            </c:forEach>
        </select>
    </div>
    <div class="mb-3">
        <input class="btn btn-primary" type="submit" value="<spring:message code='save' />" />
        <input class="btn btn-secondary" type="button" value="<spring:message code='cancel' />"/></td>
    </div>
    </form>
<div class="col-sm-8 p-5">
</body>
</html>
