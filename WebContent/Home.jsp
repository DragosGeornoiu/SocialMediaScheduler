<%-- <%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
	"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>My first JSP</title>
</head>
<body>
	<form ACTION="HelloServlet">
		<input type="checkbox" name="where" value="Twitter"> Twitter<BR>
		<input type="checkbox" name="where" value="Facebook"> Facebook<BR>
		Please enter a date ("yyyy-MM-dd HH:mm:ss") <br> <input
			type="text" name="date" size="20px"> <input type="submit"
			value="Submit"> <br> <br>
	</form>

</body>
</html>  --%>

<script type="text/javascript">
	/* 	var monthtext = [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug',
	 'Sept', 'Oct', 'Nov', 'Dec' ]; */
	var monthtext = [ '01', '02', '03', '04', '05', '06', '07', '08', '09',
			'12', '11', '12' ];

	function populatedropdown(dayfield, monthfield, yearfield, hourfield,
			minutefield) {
		var today = new Date()
		var dayfield = document.getElementById(dayfield)
		var monthfield = document.getElementById(monthfield)
		var yearfield = document.getElementById(yearfield)
		var minutefield = document.getElementById(minutefield)
		var hourfield = document.getElementById(hourfield)

		for (var i = 0; i < 31; i++)
			dayfield.options[i] = new Option(i, i + 1)
		dayfield.options[today.getDate()] = new Option(today.getDate(), today
				.getDate(), true, true) //select today's day

		for (var m = 0; m < 12; m++)
			monthfield.options[m] = new Option(monthtext[m], monthtext[m])
		monthfield.options[today.getMonth()] = new Option(monthtext[today
				.getMonth()], monthtext[today.getMonth()], true, true) //select today's month

		var thisyear = today.getFullYear()
		for (var y = 0; y < 20; y++) {
			yearfield.options[y] = new Option(thisyear, thisyear)
			thisyear += 1
		}
		yearfield.options[0] = new Option(today.getFullYear(), today
				.getFullYear(), true, true)

		for (var i = 0; i < 23; i++)
			hourfield.options[i] = new Option(i, i + 1)

		for (var i = 0; i < 60; i++)
			minutefield.options[i] = new Option(i, i + 1)
		minutefield.options[today.getMinutes()] = new Option(minutetext[today
				.getMinutes()], minutetext[today.getMinutes()], true, true)

	}
</script>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Random Quote</title>
</head>
<body>
	<form action="HelloServlet">
		<input type="checkbox" name="where" value="Twitter"> Twitter<BR>
		<input type="checkbox" name="where" value="Facebook"> Facebook<BR>
		<br> <br>
		&nbsp;&nbsp;&nbsp;Year&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Month&nbsp;&nbsp;&nbsp;&nbsp;
		Day &nbsp;&nbsp;Hour&nbsp;&nbsp; Minute<br> <select
			id="yeardropdown" name="yeardropdown"></select> <select
			id="monthdropdown" name="monthdropdown"></select> <select
			id="daydropdown" name="daydropdown"></select> <select
			id="hourdropdown" name="hourdropdown"></select> <select
			id="minutedropdown" name="minutedropdown"></select> <br> <br>
		<br> <br> <INPUT TYPE="radio" NAME="radios" VALUE="http://persdev-q.com/"
			CHECKED> http://persdev-q.com/ <BR> <INPUT TYPE="radio"
			NAME="radios" VALUE="radio2"> Radio Button 2 <BR> <INPUT
			TYPE="radio" NAME="radios" VALUE="radio3"> Radio Button 3 <BR>
		</select> <input type="submit" value="Submit"> <br> <br>
	</form>


	<script type="text/javascript">
		//populatedropdown(id_of_day_select, id_of_month_select, id_of_year_select)
		window.onload = function() {
			populatedropdown("daydropdown", "monthdropdown", "yeardropdown",
					"hourdropdown", "minutedropdown")
		}
	</script>
</body>
</html>