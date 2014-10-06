<%@page session="false"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE HTML>
<html>
<head>
	<link rel="stylesheet" type="text/css" href="/static/ask.css"/>
	<script src="/static/jquery.js"></script>
	<script src="/static/ask.js"></script>
	<title>${param.title}</title>
</head>
<body>
	<div class="header">
		<a href="/">
			<img src="/static/logo.png"/>
			<h1>Ask 2.0</h1>
		</a>
	</div>
<div id="content">
