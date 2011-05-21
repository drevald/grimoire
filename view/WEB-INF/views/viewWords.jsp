<%@ include file = "/WEB-INF/dictHeader.jsp"%>

	<h3>${dict.name}</h3>
	<table>
		<tr>
			<td><b><spring:message code="word.original"/></b></td>
			<td>&nbsp;</td>
			<td><b><spring:message code="word.occurrence"/></b></td>
		</tr>
	<c:forEach items="${words}" var="word">
		   <tr>
			<td>${word.word.value}</td>
			<td>&nbsp;</td>
			<td>${word.counter}</td>
		   </tr>
        </c:forEach>
	</table>

<%@ include file = "/WEB-INF/footer.jsp"%>