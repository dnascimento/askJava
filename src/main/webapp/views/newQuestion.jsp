<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<c:import url="header.jsp">
	<c:param name="title" value="New Question"/>
</c:import>

<form:form method="post" action="/new-question"  modelAttribute="question" >
			<label>Please enter your question</label>
			<form:input path="title" type="text"/>
			<br>
			<label>Add Details</label>
			<form:textarea path="text" cols="40" rows="10"></form:textarea>
			<br>
			<label>tags</label>
			<form:select path="tags"  items="${tags}"/>
			<br>
			<input value="Submit" type="submit"/>
</form:form>

<c:import url="footer.jsp"/>