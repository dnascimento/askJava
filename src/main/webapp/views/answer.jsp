<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="pt.inesc.ask.domain.Answer" %>

<div class="answerArea" answer-id="${answer.id}">
	<div class="vote">
		<img class="up" src="/static/up.png"/>
		<p>${answer.votes}</p>
		<img class="down" src="/static/down.png"/>
	</div>
	<div class="answerContent">
		<% out.println( ((Answer)request.getAttribute("answer")).getText()); %> 
			<br>
			<c:if test="${answer.isQuestion}">
				<button class="edit">Edit Question</button>
			</c:if>
			<c:if test="${not answer.isQuestion}">
				<button class="delete">Delete</button>
				<button class="edit">Edit</button>
			</c:if>
			<div class="author">
				<a href="/user/${answer.author}">
					<h4>${answer.author}</h4>
				</a>
			</div>
		</div>
		<div class="commentsArea">
			<h3>Comments</h3>
				<c:forEach var="comment" items="${answer.comments}">
					<div class="comment" comment-id="${comment.id}">
						<p>${comment.text}</p>
						<button class="delete">Delete</button>
						<button class="edit">Edit</button>
						<a href="/user/${comment.author}">${comment.author}</a>
					</div>
					<hr>
				</c:forEach>
				<button class="newComment">Comment</button>
		</div>
	</div>
</div>

<c:import url="footer.jsp"/>
