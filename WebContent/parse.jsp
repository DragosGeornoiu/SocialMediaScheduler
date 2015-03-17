<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
	"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Parse</title>
<br>
<a href="http://localhost:8080/SocialMediaScheduler\">Home</a>
<br>
<a
	href="http://localhost:8080/SocialMediaScheduler/parse?accessToken=<%=request.getParameter("accessToken")%> + "\">Parse</a>
<br>
<a
	href="http://localhost:8080/SocialMediaScheduler/Post?accessToken=<%=request.getParameter("accessToken")%> + "\">Schedule
	Quote</a>
<br>
<a
	href="http://localhost:8080/SocialMediaScheduler/QuoteHistory?accessToken=<%=request.getParameter("accessToken")%> + "\">Quote
	History</a>
<br>
<a
	href="http://localhost:8080/SocialMediaScheduler/PendingQuotes?accessToken=<%=request.getParameter("accessToken")%> + "\">Pending
	Quotes</a>
<br>
<a
	href="http://localhost:8080/SocialMediaScheduler/search?accessToken=<%=request.getParameter("accessToken")%> + "\">Search
</a>
<br>
<br>
</head>
<body>
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