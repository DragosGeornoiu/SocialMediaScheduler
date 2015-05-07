<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Updates</title>
</head>
<body>
<a href="http://localhost:8080/SocialMediaScheduler\">Return to Home</a>
<br><br>
<b> Your last time scheduling quotes:  </b><br><br>
<ul>
<%
	out.println(request.getAttribute("message"));
%>
</ul>

</body>
</html>