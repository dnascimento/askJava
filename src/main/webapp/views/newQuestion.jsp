<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import url="header.jsp">
	<c:param name="title" value="New Question"/>
</c:import>

<form method="post" action="/new-question" >
			<label>Please enter your question</label>
			<input name="title" type="text"/>
			<br>
			<label>Add Details</label>
			<textarea name="text" cols="40" rows="10"></textarea>
			<br>
			<label>tags</label>
			 <select name="tags" multiple>
			 <option value="${tags[0]}" SELECTED>${tags[0]}</option> 
			 <c:forEach begin="1" var="tag" items="${tags}">
			 	<option value="${tag}">${tag}</option> 
			 </c:forEach>
			 </select>
			<br>
			<input value="Submit" type="submit"/>
</form>

<c:import url="footer.jsp"/>