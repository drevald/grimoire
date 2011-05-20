<%@ include file = "/WEB-INF/dictHeader.jsp"%>

	<h3><spring:message code="text" /></h3>
	<table>
		<tr>
			<th>original</th>
			<th>occurances</th>
		</tr>
	<c:forEach items="${words}" var="word">
		   <tr>
			<td>${word.word.value}</td>
			<td>${word.counter}</td>
		   </tr>
        </c:forEach>
	</table>

<%@ include file = "/WEB-INF/footer.jsp"%>