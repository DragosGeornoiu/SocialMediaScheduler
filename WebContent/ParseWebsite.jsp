<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Parse Website</title>
</head>
<body>
	<a href="http://localhost:8080/SocialMediaScheduler">Home</a>
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
		by author</a>
	<br>
	<a href="http://localhost:8080/SocialMediaScheduler/Edit">Edit</a>
	<br>
	<a href="http://localhost:8080/SocialMediaScheduler/Updates">Last Updates</a>
	<br>
	<br>
	<%
		out.println(request.getAttribute("responce"));
	%>

</body>
</html>