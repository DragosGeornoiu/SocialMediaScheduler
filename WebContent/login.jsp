<%@ page contentType="text/html; charset=utf-8" language="java"
	import="java.sql.*, java.text.*, java.util.*;" errorPage=""%>
<%!DateFormat tipe = new SimpleDateFormat("EEE, MMM d, ''yy");
	Calendar cal = Calendar.getInstance();%>
<%
	out.print(tipe.format(cal.getTime()));
%>