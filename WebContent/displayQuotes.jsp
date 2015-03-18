<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
        "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Pending Quotes</title>
<br>
<a href="http://localhost:8080/SocialMediaScheduler\">Home</a>
<br>
<a href="http://localhost:8080/SocialMediaScheduler/parse?accessToken=<%=request.getParameter("accessToken")%> + "\">Parse</a>
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
<a href="http://localhost:8080/SocialMediaScheduler/search?accessToken=<%=request.getParameter("accessToken")%> + "\">Search
</a>
<br>
<br>
</head>
<body>

<!--  checked -->
<form ACTION="QuoteHistory">
	
	<c:choose>
	<c:when test="${lastType eq \"byDate\"}">
		<INPUT TYPE="radio" NAME="type" VALUE="byDate" checked>byDate&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	</c:when>
	<c:otherwise>
		<INPUT TYPE="radio" NAME="type" VALUE="byDate">byDate&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	</c:otherwise>
	</c:choose>	
	
	<c:choose>
	<c:when test="${order eq \"ascending\"}">
		<INPUT TYPE="radio" NAME="order" VALUE="ascending" checked>ascending&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	</c:when>
	<c:otherwise>
		<INPUT TYPE="radio" NAME="order" VALUE="ascending">ascending&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	</c:otherwise>
	</c:choose>
	
	<input type="submit" value="Submit"> 			<br>
	
	<c:choose>
	<c:when test="${lastType eq \"byAuthor\"}">
		<INPUT TYPE="radio" NAME="type" VALUE="byAuthor" checked>byAuthor&nbsp;&nbsp;&nbsp;
	</c:when>
	<c:otherwise>
		<INPUT TYPE="radio" NAME="type" VALUE="byAuthor">byAuthor&nbsp;&nbsp;&nbsp;
	</c:otherwise>
	</c:choose>
	
	
	<c:choose>
	<c:when test="${order eq \"descending\"}">
		<INPUT TYPE="radio" NAME="order" VALUE="descending" checked>descending <br>
	</c:when>
	<c:otherwise>
		<INPUT TYPE="radio" NAME="order" VALUE="descending">descending <br>
	</c:otherwise>
	</c:choose>
	
	<c:choose>
	<c:when test="${lastType eq \"byQuote\"}">
		<INPUT TYPE="radio" NAME="type" VALUE="byQuote" checked>byQuote
	</c:when>
	<c:otherwise>
		<INPUT TYPE="radio" NAME="type" VALUE="byQuote">byQuote
	</c:otherwise>
	</c:choose>
	
	

	<INPUT TYPE="hidden" NAME="accessToken" VALUE=<%=request.getParameter("accessToken")%>>
	<INPUT TYPE="hidden" NAME="page" VALUE="${currentPage}" >
	<br> <br>
</form>



	<c:choose>
		<c:when test="${auth > 0}">
			<c:forEach var="quote" items="${quotesList}">
				${quote.toString()}
				
	</c:forEach>

			<%--For displaying Previous link except for the 1st page --%>
			<%-- <c:if test="${currentPage != 1}">
				<td><a href="QuoteHistory?page=${currentPage - 1}">Previous</a></td>
			</c:if> --%>
			<%--For displaying Page numbers. 
    The when condition does not display a link for the current page--%>
			<table border="1" cellpadding="5" cellspacing="5">
				<tr>
					<c:forEach begin="1" end="${noOfPages}" var="i">
						<c:choose>
							<c:when test="${currentPage eq i}">
								<td>${i}</td>
							</c:when>
							<c:otherwise>
								<td><a
									href="QuoteHistory?type=${lastType}&order=${order}&page=${i}&accessToken=<%=request.getParameter("accessToken")%>">${i}</a></td>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</tr>
			</table>

			<%--For displaying Next link --%>
			<%-- <c:if test="${currentPage lt noOfPages}">
				<td><a href="QuoteHistory?page=${currentPage + 1}">Next</a></td>
			</c:if> --%>
		</c:when>
		<c:otherwise>
The access token is probably not a good one...
</c:otherwise>
	</c:choose>
</body>
</html>