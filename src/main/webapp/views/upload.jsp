<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import url="header.jsp">
	<c:param name="title" value="Upload"/>
</c:import>

<form action="/upload" method="post" enctype="multipart/form-data">
	<input type="file" name="upload"/>
	<input type="submit" value="upload"/>
</form>
<c:import url="footer.jsp"/>


