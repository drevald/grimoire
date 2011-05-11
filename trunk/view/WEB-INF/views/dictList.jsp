<%@ include file = "/WEB-INF/userHeader.jsp"%>

    <h3><spring:message code="dict.upload"/></h3>

    <table>
    <form method="post" action="dict/upload" enctype="multipart/form-data">
    <input type="hidden" name="user" value="${currUser}"/>
    <input type="hidden" name="name" value="name"/>
        <tr>
            <td><spring:message code='dict.text.file'/></td>
            <td><input type="file" name="file"/></td>
        </tr>
        <tr>
            <td><spring:message code='dict.text.language'/></td>
            <td>
                <select>
                    <option>Language 1</option>
                    <option>Language 2</option>
                </select>
            </td>
        </tr>
        <tr>
            <td>&nbsp;</td>
            <td><input type="submit" value="<spring:message code='upload'/>"/></td>
        </tr>
    </form>
    </table>

    <h3><spring:message code="dict.list" /></h3>

    <c:if test="${empty dictList}">
    	  <spring:message code="dict.list.empty"/>
	  </c:if>

	  <c:if test="${!empty dictList}">
	      <table>
	      <c:forEach items="${dictList}" var="dict">
			<tr>
				<td>${dict.id}</td>
				<td>${dict.name}</td>
				<td>${dict.status}</td>		
				<td><a href="dict/delete/${dict.id}"><spring:message code="delete" /></a></td>
				<td><a href="dict/edit/${dict.id}"><spring:message code="edit" /></a></td>
				<td><a href="dict/view/${dict.id}"><spring:message code="dict.preview" /></a></td>
			</tr>
		</c:forEach>
		</table>
	</c:if>

<%@ include file="/WEB-INF/footer.jsp"%>