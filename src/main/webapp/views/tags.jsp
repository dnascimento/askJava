<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="pt.inesc.ask.domain.Question"%>

<c:import url="header.jsp">
	<c:param name="title" value="Categories"/>
</c:import>

<a href="new-question">New Question</a>
<ul>
	<c:forEach var="tag" items="${tags}" >
		<li>
			<a href="/tags/${tag}">
				<h3>${tag}</h3>
			</a>
		</li>
	</c:forEach>
</ul>
<c:import url="footer.jsp"/>

