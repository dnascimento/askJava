<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="pt.inesc.ask.domain.Question"%>
<%@ page import="java.util.List"%>

<c:import url="header.jsp">
	<c:param name="title" value="Welcome"/>
</c:import>

<a href="new-question">New Question</a>
<ul>
	<%	List<Question> list = (List<Question>) request.getAttribute("questionList");
		for(Question question : list){ %>
		<li class="questionSummary">
			<a href="/question/${question.title}"><h3><%= question.title %></h3></a>
			<ul>
			<% for(String tag : question.tags){ %>
					<li class="tag"><%= tag %></li>
			<% } %>
		</li>
	<% }%>
</ul>

