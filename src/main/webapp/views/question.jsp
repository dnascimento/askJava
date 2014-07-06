<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
	<div class="viewCounter">
		<p>${questionData.views} Views</p>
		<br>
		<p>${questionData.answers} Answers</p>
	</div>
	<hr>
	<c:set var="answer" value="${question}" scope="request"/>
	<jsp:include page="answer.jsp"/>
	<h3>Answers</h3>
	<hr>
	<c:forEach var="answer" items="${answers}">
		<c:set var="answer" value="${answer}" scope="request"/>
		<jsp:include page="answer.jsp"/>
	</c:forEach>
	<div class="newAnswer">
		<textarea name="text" cols="40" rows="5"></textarea>
		<button>Answer</button>
	</div>
</div>
<c:import url="footer.jsp"/>

