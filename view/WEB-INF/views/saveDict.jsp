<%@ include file = "/WEB-INF/userHeader.jsp"%>

	<h3><spring:message code="dict.edit.properties" /></h3>

	<table>
	<form method="post" action="save">
	<input type="hidden" name="id" value="${dict.id}"/>
	<input type="hidden" name="userId" value="${dict.userId}"/>
		<tr>						
			<td><nobr><spring:message code="dict.name"/></nobr></td>
			<td>
				<input type="text" name="name" value="${dict.name}"/>
			</td>
		</tr>
		<tr>
			<td><spring:message code="dict.status"/></td>
			<td>${dict.status}</td>
		</tr>
		<tr>
			<td><spring:message code="dict.your.language"/></td>
			<td><input type="text"/></td>
		</tr>
		<tr>
			<td><spring:message code="dict.text.language"/></td>
			<td><input type="text"/></td>
		</tr>
		<tr>
			<td>
				<spring:message code='encoding'/>
			</td>
			<td>
				<select name="encoding" onchange="javascript:document.forms[0].submit();">
					<c:forEach items="${charsetList}" var="c">
					    <c:if test="${c eq dict.encoding}">
    	  					<option selected>${c}</option>
	  					</c:if>
					    <c:if test="${c ne dict.encoding}">
    	  					<option>${c}</option>
	  					</c:if>						
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td>
			<input type="submit" value="<spring:message code='apply'/>"/>
			<input type="button" value="<spring:message code='save'/>"
                       onclick="javascript:document.forms[0].action='store';document.forms[0].submit();"/>
			</td>
		</tr>
		<tr><td colspan="2">&nbsp;</td></tr>
		<tr>
			<td valign="top"><spring:message code='preview'/></td>
			<td>${preview}</td>
		</tr>		
	</form>
	</table>



</body>
</html>