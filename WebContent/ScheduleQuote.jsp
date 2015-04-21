<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Schedule Quote</title>
</head>
<body>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
	<%
		String path = (String) request.getAttribute("path");
		List<String> optionsList = (List<String>) request.getAttribute("optionsList");
		List<String> allProfiles = (List<String>) request.getAttribute("allProfiles");
		String outS = "";
		if (allProfiles.size() == 0) {
			out.println("<br> <a href=\"http://localhost:8080/SocialMediaScheduler\">Home</a> \n");
			out.println("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/parse\">Parse</a> \n");
			out.println("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/Post\">Schedule Quote</a> \n");
			out.println("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/QuoteHistory\">Quote History</a> \n");
			out.println("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/PendingQuotes\">Pending Quotes</a> \n");
			out.println("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/Search\">Search by author</a> \n");
			out.println("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/Edit\">Edit</a><br><br>");
			out.println("</head> \n");
			out.println("<body> \n");
			out.println("The access token is probably not correct, please insert it again... \n");
		} else {
			out.println("<script type=\"text/javascript\"> \n");
			out.println("var monthtext = [ '01', '02', '03', '04', '05', '06', '07', '08', '09','10', '11', '12' ]; \n");
			out.println("var gmttext = [ '-12', '-11', '-10', '-09', '-08', '-07', '-06', '-05', '-04', '-03', '-02', '-01', '00', '+01', '+02', '+03', '+04', '+05', '+06', '+07', '+08', '+09', '+10', '+11', '+12', '+13', '+14' ] \n");
			out.println("function populatedropdown(dayfield, monthfield, yearfield, hourfield, minutefield, gmtfield) { \n");
			out.println("var today = new Date() \n");
			out.println("var dayfield = document.getElementById(dayfield) \n");
			out.println("var monthfield = document.getElementById(monthfield) \n");
			out.println("var yearfield = document.getElementById(yearfield) \n");
			out.println("var minutefield = document.getElementById(minutefield) \n");
			out.println("var hourfield = document.getElementById(hourfield) \n");
			out.println("var gmtfield = document.getElementById(gmtfield) \n");
			out.println(" \n");
			out.println("for (var i = 1; i <= 31; i++) \n");
			out.println("dayfield.options[i-1] = new Option(i, i) \n");
			out.println("dayfield.options[today.getDate()] = new Option(today.getDate(), today.getDate(), true, true) \n");
			out.println(" \n");
			out.println("for (var m = 0; m < 12; m++) \n");
			out.println("monthfield.options[m] = new Option(monthtext[m], monthtext[m]) \n");
			out.println("monthfield.options[today.getMonth()] = new Option(monthtext[today.getMonth()], monthtext[today.getMonth()], true, true) \n");
			out.println(" \n");
			out.println("for(var m = 0; m<27;m++) \n");
			out.println("gmtfield.options[m] = new Option(gmttext[m], gmttext[m+2]) \n");
			out.println(" \n");
			out.println("var thisyear = today.getFullYear() \n");
			out.println("for (var y = 0; y < 20; y++) { \n");
			out.println("yearfield.options[y] = new Option(thisyear, thisyear) \n");
			out.println("thisyear += 1 \n");
			out.println("} \n");
			out.println("yearfield.options[0] = new Option(today.getFullYear(), today.getFullYear(), true, true) \n");
			out.println(" \n");
			out.println("for (var i = 0; i <= 23; i++) \n");
			out.println("hourfield.options[i] = new Option(i, i) \n");
			out.println(" \n");
			out.println("for (var i = 0; i < 60; i++) \n");
			out.println("minutefield.options[i] = new Option(i, i) \n");
			out.println("minutefield.options[today.getMinutes()] = new Option(minutetext[today.getMinutes()], minutetext[today.getMinutes()], true, true) \n");
			out.println("} \n");
			out.println("</script> \n");
			out.println(" \n");
			out.println(" \n");
			out.println("<html> \n");
			out.println("<head> \n");
			out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\"> \n");
			out.println("<title>Random Quote</title> \n");
			out.println("<head> \n");
			out.println("<br> <a href=\"http://localhost:8080/SocialMediaScheduler\">Home</a> \n");
			out.println("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/parse\">Parse</a> \n");
			out.println("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/Post\">Schedule Quote</a> \n");
			out.println("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/QuoteHistory\">Quote History</a> \n");
			out.println("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/PendingQuotes\">Pending Quotes</a> \n");
			out.println("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/Search\">Search by author</a>\n");
			out.println("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/Edit\">Edit</a><br><br>");
			out.println("</head> \n");
			out.println("</head> \n");
			out.println("<body> \n");
			out.println("<form action=\"HelloServlet\"> \n");

			out.println(" \n");
			out.println("Where to post: <br> \n");
			for (int i = 0; i < allProfiles.size(); i++) {
				out.println("<input type=\"checkbox\" name=\"where\" value=\"" + allProfiles.get(i) + "\">"
						+ allProfiles.get(i).replaceAll(" ", "") + "<BR> \n");
			}

			out.println("<br> <br> \n");
			out.println("&nbsp;&nbsp;&nbsp;Year&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Month&nbsp;&nbsp;&nbsp;&nbsp; \n");
			out.println("Day &nbsp;&nbsp;Hour&nbsp;&nbsp; Minute&nbsp;&nbsp;GMT<br>  \n");
			out.println("<select id=\"yeardropdown\" name=\"yeardropdown\"></select> \n");
			out.println("<select id=\"monthdropdown\" name=\"monthdropdown\"></select> \n");
			out.println("<select id=\"daydropdown\" name=\"daydropdown\"></select> \n");
			out.println("<select id=\"hourdropdown\" name=\"hourdropdown\"></select> \n");
			out.println("<select id=\"minutedropdown\" name=\"minutedropdown\"></select> \n");
			out.println("<select id=\"gmtdropdown\" name=\"gmtdropdown\"></select> \n");
			out.println("\n");
			out.println("<br> <br> \n");

			for (int i = 0; i < optionsList.size(); i++) {
				out.println("<INPUT TYPE=\"radio\" NAME=\"radios\" VALUE=\"" + optionsList.get(i) + "\"> "
						+ optionsList.get(i) + "<BR> \n");
				
			}
			out.println("<input type=\"submit\" value=\"Submit\"> <br> <br> \n");
			out.println("</form> \n");
			out.println(" \n");
			out.println("<script type=\"text/javascript\"> \n");
			out.println("window.onload = function() { \n");
			out.println("populatedropdown(\"daydropdown\", \"monthdropdown\", \"yeardropdown\", \"hourdropdown\", \"minutedropdown\", \"gmtdropdown\") \n");
			out.println("} \n");
			out.println("</script> \n");
			out.println("</body> \n");
			out.println("</html> \n");
			out.println(" \n");
			out.println("\n");
		}
		
		out.println(outS);
	%>
</body>
</html>