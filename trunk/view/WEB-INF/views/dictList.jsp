<%@ include file = "/WEB-INF/userHeader.jsp"%>

    <h3><spring:message code="dict.upload"/></h3>

    <form method="post" action="dict/upload" enctype="multipart/form-data">
        <input type="hidden" name="user" value="${currUser}"/>
        <input type="file" name="file"/>&nbsp;
        <spring:message code='dict.text.language'/>
        <select name="langId">
            <c:forEach items="${langs}" var="lang">
                    <option value="${lang.id}">${lang.name}</option>
            </c:forEach>
        </select>
	    <input type="submit" value="<spring:message code='upload'/>"/>
    </form>

    <h3><spring:message code="dict.list" /></h3>

    <c:if test="${empty helperList}">
    	  <spring:message code="dict.list.empty"/>
    </c:if>

    <c:if test="${!empty helperList}">
    <table>
        <c:forEach items="${helperList}" var="helper">
        <tr>
	       	<td>${helper.dict.id}</td>
            <td>${helper.dict.name}</td>
            <td>${helper.dict.status}</td>
            <td>
            <c:if test="${!empty helper.jobs}">
                  <c:forEach items="${helper.jobs}" var="job">
		  <c:if test="${job.active}">
                    ${job.progress}%
		  </c:if>
                    ${job.details}
                  </c:forEach>
            </c:if>
            <c:if test="${empty helper.jobs}">
                  EMPTY
            </c:if>
            </td>
            <td><a href="dict/delete/${helper.dict.id}"><spring:message code="delete" /></a></td>
            <td><a href="dict/words/${helper.dict.id}?offset=0"><spring:message code="words" /></a></td>
            <td><a href="dict/view/${helper.dict.id}"><spring:message code="dict.preview" /></a></td>
	    </tr>
		</c:forEach>
	</table>
	</c:if>

<%@ include file="/WEB-INF/footer.jsp"%>