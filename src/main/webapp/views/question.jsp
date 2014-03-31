<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:import url="header.jsp">
	<c:param name="title" value="${questionData.title}"/>
</c:import>

<div class="questionWrapper" question-title="${questionData.title}">
	<h1>${questionData.title}</h1>
	<button class="questionDelete">Delete</button>
	<ul>
		<c:forEach var="tag" items="${questionData.tags}">
			<li class="tag">${tag}</li>
		</c:forEach>
	</ul>
	<hr>
	<c:set var="answer" value="${question}" scope="request"/>
	<jsp:include page="answer.jsp"/>
	<h3>Answers</h3>
	<hr>
	<c:forEach var="answer" items="${answers}">
		<jsp:include page="answer.jsp"/>
	</c:forEach>
	<form:form  action="${questionData.title}/answer" method="post">
		<textarea name="text" cols="40" rows="5"></textarea>
		<input type="submit" value="Answer"/>
	</form:form >
</div>
<c:import url="footer.jsp"/>

