<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
	"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Parse</title>
</head>
<body>
<br>
<a href="http://localhost:8080/SocialMediaScheduler\">Home</a>
<br>
<a
	href="http://localhost:8080/SocialMediaScheduler/parse">Parse</a>
<br>
<a
	href="http://localhost:8080/SocialMediaScheduler/Post">Schedule
	Quote</a>
<br>
<a
	href="http://localhost:8080/SocialMediaScheduler/QuoteHistory">Quote
	History</a>
<br>
<a
	href="http://localhost:8080/SocialMediaScheduler/PendingQuotes">Pending
	Quotes</a>
<br>
<a
	href="http://localhost:8080/SocialMediaScheduler/search">Search
</a>
<br>
<br>

	<form ACTION="ParseWebsite">
		Please enter a website: <br> <input type="text" name="website"
			size="20px"> <br> <INPUT TYPE="radio" NAME="radios"
			VALUE="http://persdev-q.com/"> http://persdev-q.com/; <br>
		<INPUT TYPE="radio" NAME="radios" VALUE="http://www.brainyquote.com/">
		http://www.brainyquote.com/; <br> <INPUT TYPE="hidden"
			NAME="accessToken" VALUE=<%=request.getParameter("accessToken")%>>
		<input type="submit" value="Submit"> <br> <br>
	</form>

</body>
</html>