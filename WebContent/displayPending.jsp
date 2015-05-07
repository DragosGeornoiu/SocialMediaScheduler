<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Pending Quotes</title>
</head>
<body>
	<br>
	<a href="http://localhost:8080/SocialMediaScheduler\">Home</a>
	<br>
	<a href="http://localhost:8080/SocialMediaScheduler/parse">Parse</a>
	<br>
	<a href="http://localhost:8080/SocialMediaScheduler/Post">Schedule
		Quote</a>
	<br>
	<a href="http://localhost:8080/SocialMediaScheduler/QuoteHistory">Quote
		History</a>
	<br>
	<a href="http://localhost:8080/SocialMediaScheduler/PendingQuotes">Pending
		Quotes</a>
	<br>
	<a href="http://localhost:8080/SocialMediaScheduler/Search">Search
		by author </a>
	<br>
	<a href="http://localhost:8080/SocialMediaScheduler/Edit">Edit</a>
	<br>
	<a href="http://localhost:8080/SocialMediaScheduler/Updates">Last Updates</a>

	<br>
	<br>

	<c:choose>
		<c:when test="${auth > 0}">
			<table border="1" style="width: 100%;">
				<tr bgcolor="#d3d3d3">
					<td>Due at</td>
					<td>Service</td>
					<td>Text</td>
				</tr>
				<c:forEach var="quote" items="${quotesList}">
				${quote.toString()}
				</c:forEach>
			</table>
			<table border="1" cellpadding="5" cellspacing="5">
				<!-- <tr> -->
				<c:forEach begin="1" end="${noOfPages}" var="i">
					<c:choose>
						<c:when test="${currentPage eq i}">
							<td>${i}</td>
						</c:when>
						<c:otherwise>
							<td><a
								href="PendingQuotes?type=${lastType}&order=${order}&page=${i}">${i}</a></td>
						</c:otherwise>
					</c:choose>
				</c:forEach>
				<!-- </tr> -->
			</table>
		</c:when>
		<c:otherwise>
Either there are no pending quotes or the access token is probably not a good one...
</c:otherwise>
	</c:choose>
</body>
</html>