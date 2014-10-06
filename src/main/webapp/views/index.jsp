<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="pt.inesc.ask.domain.Question"%>
<%@page session="false"%>

<jsp:include page="header.jsp">
<jsp:param name="title" value="Welcome"/>
</jsp:include>

<a href="/new-question">New Question</a>
<ul>
	<c:forEach var="question" items="${questionList}" >
		<li class="questionSummary">
			<a href="/question/${question.url}">
				<h3>${question.title}</h3>
			</a>
		</li>
	</c:forEach>
</ul>
	</div>
</body>
</html>
