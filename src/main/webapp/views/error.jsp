<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:import url="header.jsp">
	<c:param name="title" value="Error"/>
</c:import>

<h2>${error}</h2>
<c:import url="footer.jsp"/>
