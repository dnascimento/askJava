<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page session="false"%>

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
			<input name="tags" type="text"/>
			<br>
			<input value="Submit" type="submit"/>
</form>

	</div>
</body>
</html>