<%-- <h2>Select Languages:</h2>

<!-- <form ACTION="jspCheckBox.jsp">
	<input type="checkbox" name="id" value="Twitter"> Twitter<BR>
	<input type="checkbox" name="id" value="Facebook"> Facebook<BR>
	<input type="month" name="id2" value="Text"> Text<BR> <input
		type="submit" value="Submit">
</form> -->



<%
	String select[] = request.getParameterValues("id");
	if (select != null && select.length != 0) {
		out.println("You have selected: ");
		for (int i = 0; i < select.length; i++) {
			out.println(select[i]);

		}
	}
%>
</html> --%>

<!-- <form action="some.jsp">
  <select name="item">
    <option value="1">1</option>
    <option value="2">2</option>
    <option value="3">3</option>
  </select>
  <input type="submit" value="Submit">
</form> -->


<%-- <%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*, java.text.*, java.util.*;" errorPage="" %>
	try {
		Calendar date = Calendar.getInstance();
		date.set(Calendar.DAY_OF_MONTH, 1);
		Date date1;
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String str = df.format(date.getTime());
		//out.println(str); this is showing date formate 01/03/2014 
		date1 = (Date) df.parse(str);
		out.println(date1); //but this is showing as "Sat Mar 01 00:00:00 IST 2014" but i want to show like that 01/03/2014
	} catch (Exception e) {
	}
%> --%>

<%@ page contentType="text/html; charset=utf-8" language="java"
	import="java.sql.*, java.text.*, java.util.*;" errorPage=""%>
<%!DateFormat tipe = new SimpleDateFormat("EEE, MMM d, ''yy");
	Calendar cal = Calendar.getInstance();%>
<%
	out.print(tipe.format(cal.getTime()));
%>