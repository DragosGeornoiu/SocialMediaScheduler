package ro.project.servlet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * An update is scheduled using this servlet.
 *
 */
public class PostToSocialMediaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String path = "D:\\\\workspace\\\\SocialMediaScheduler\\\\src\\\\main\\\\resources\\\\quotes";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		List<String> optionsList = new ArrayList<String>();
		optionsList = getOptionsList();

		out.println("<script type=\"text/javascript\">");
		out.println("var monthtext = [ '01', '02', '03', '04', '05', '06', '07', '08', '09','10', '11', '12' ];");
		out.println("function populatedropdown(dayfield, monthfield, yearfield, hourfield, minutefield) {");
		out.println("var today = new Date()");
		out.println("var dayfield = document.getElementById(dayfield)");
		out.println("var monthfield = document.getElementById(monthfield)");
		out.println("var yearfield = document.getElementById(yearfield)");
		out.println("var minutefield = document.getElementById(minutefield)");
		out.println("var hourfield = document.getElementById(hourfield)");
		out.println("");
		out.println("for (var i = 1; i < 31; i++)");
		out.println("dayfield.options[i] = new Option(i, i)");
		out.println("dayfield.options[today.getDate()] = new Option(today.getDate(), today.getDate(), true, true)");
		out.println("");
		out.println("for (var m = 0; m < 12; m++)");
		out.println("monthfield.options[m] = new Option(monthtext[m], monthtext[m])");
		out.println("monthfield.options[today.getMonth()] = new Option(monthtext[today.getMonth()], monthtext[today.getMonth()], true, true)");
		out.println("");
		out.println("var thisyear = today.getFullYear()");
		out.println("for (var y = 0; y < 20; y++) {");
		out.println("yearfield.options[y] = new Option(thisyear, thisyear)");
		out.println("thisyear += 1");
		out.println("}");
		out.println("yearfield.options[0] = new Option(today.getFullYear(), today.getFullYear(), true, true)");
		out.println("");
		out.println("for (var i = 0; i <= 23; i++)");
		out.println("hourfield.options[i] = new Option(i, i)");
		out.println("");
		out.println("for (var i = 0; i < 60; i++)");
		out.println("minutefield.options[i] = new Option(i, i)");
		out.println("minutefield.options[today.getMinutes()] = new Option(minutetext[today.getMinutes()], minutetext[today.getMinutes()], true, true)");
		out.println("}");
		out.println("</script>");
		out.println("");
		out.println("");
		out.println("<html>");
		out.println("<head>");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">");
		out.println("<title>Random Quote</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<form action=\"HelloServlet\">");
		out.println("<input type=\"checkbox\" name=\"where\" value=\"Twitter\"> Twitter<BR>");
		out.println("<input type=\"checkbox\" name=\"where\" value=\"Facebook\"> Facebook<BR>");
		out.println("<br> <br>");
		out.println("&nbsp;&nbsp;&nbsp;Year&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Month&nbsp;&nbsp;&nbsp;&nbsp;");
		out.println("Day &nbsp;&nbsp;Hour&nbsp;&nbsp; Minute<br> ");
		out.println("<select id=\"yeardropdown\" name=\"yeardropdown\"></select>");
		out.println("<select id=\"monthdropdown\" name=\"monthdropdown\"></select>");
		out.println("<select id=\"daydropdown\" name=\"daydropdown\"></select>");
		out.println("<select id=\"hourdropdown\" name=\"hourdropdown\"></select>");
		out.println("<select id=\"minutedropdown\" name=\"minutedropdown\"></select>");
		out.println("<br> <br>");
		
		for(int i=0;i<optionsList.size();i++) {
			out.println("<INPUT TYPE=\"radio\" NAME=\"radios\" VALUE=" + optionsList.get(i) + "> From " + optionsList.get(i) +  "<BR>");
		}
		out.println("<input type=\"submit\" value=\"Submit\"> <br> <br>");
		out.println("</form>");
		out.println("");
		out.println("<script type=\"text/javascript\">");
		out.println("window.onload = function() {");
		out.println("populatedropdown(\"daydropdown\", \"monthdropdown\", \"yeardropdown\", \"hourdropdown\", \"minutedropdown\")");
		out.println("}");
		out.println("</script>");
		out.println("</body>");
		out.println("</html>");
		out.println("");
		out.println("");

	}

	private List<String> getOptionsList() {
		List<String> optionList = new ArrayList<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path + "\\\\parser.txt"));
			String line;
			while ((line = br.readLine()) != null) {
				optionList.add(line);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return optionList;
	}
}