<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Search by author</title>
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

	<form ACTION="Search">
		Insert author: <INPUT TYPE="text" name="author"> <input
			type="submit">
	</form>

	<%
		if(request.getParameter("author") == null) {
			out.println("Please insert an author");
		} else if (request.getAttribute("authorEntries").toString().trim().isEmpty()) {
				out.println("No entries for author: " + request.getParameter("author"));
		} else {
			out.println("<br> <br> Author: " + request.getParameter("author"));
			out.println("<table border=\"1\" style=\"width:100%;\" cellpadding=\"5\" cellspacing=\"5\">");
			out.println("<tr bgcolor=\"#d3d3d3\">");
			out.println("<td>Due at</td>");
			out.println("<td>Service</td>");
			out.println("<td>Text</td>");
			out.println("</tr>");
			out.println(request.getAttribute("authorEntries"));
		}
	%>

</body>
</html>