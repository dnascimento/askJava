<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="pt.inesc.ask.domain.Question"%>

<c:import url="header.jsp">
	<c:param name="title" value="Welcome"/>
</c:import>

<a href="new-question">New Question</a>
<ul>
	<c:forEach var="question" items="${questionList}" >
		<li class="questionSummary">
			<a href="/question/${question.title}">
				<h3>${question.title}</h3>
			</a>
			<ul>
			<c:forEach var="tag" items="${question.tags}">
					<li class="tag">${tag}</li>
			</c:forEach>
			</ul>
		</li>
	</c:forEach>
</ul>
<c:import url="footer.jsp"/>
