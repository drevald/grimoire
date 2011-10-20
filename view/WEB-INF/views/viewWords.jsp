<%@ include file = "/WEB-INF/dictHeader.jsp"%>

	<h3>${dict.name}</h3>
	<div style="float:left;width:100%">
	<div style="float:left;margin-right:20px">
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
    <form action="translate">
        <input type="hidden" name="dictId" value="${dict.id}"/>
        <input type="hidden" name="userId" value="${dict.userId}"/>
        <input type="hidden" name="langId" value="${langId}"/>
        <div style="float:right;width:50%">Dictionary is not translated yet.<br>
	    To translate choose your language:<br><br>
	    <select>
		    <option>English</option>
	    </select>
	    <br><br>And translation service:<br><br>
	    <select>
		    <option>Google</option>
	    </select>
	    <br><br>
	    <input type="button" value="Translate"/>
    </div>
    </form>
</div>
<%@ include file = "/WEB-INF/footer.jsp"%>