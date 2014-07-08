<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<!DOCTYPE HTML>
<html>
<head>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/ask.css"/>
	<script src="${pageContext.request.contextPath}/static/jquery.js"></script>
	<script src="${pageContext.request.contextPath}/static/ask.js"></script>
	<title>${param.title}</title>
</head>
<body>
	<div class="header">
		<a href="/">
			<img src="/static/logo.png"/>
			<h1>Ask</h1>
		</a>
	</div>
<div id="content">
