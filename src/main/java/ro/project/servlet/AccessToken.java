package ro.project.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ro.project.parser.FileManager;
import ro.project.parser.Parser;

/**
 * 
 * @author Caphyon1
 *	
 *	Used for retrieving the access token and passing it to the other servlets.
 */
public class AccessToken extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Parser parser;
	FileManager fileManager;
	PrintWriter out;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		out = response.getWriter();

		if (request.getParameter("accessToken") == null) {
			print("");
			out.println("<BR> You didn't tell us your access token... <br> ");
		} else {
			print(request.getParameter("accessToken"));
			out.println("Your access token is: " + request.getParameter("accessToken"));
		}
		out.print("</body>\n</html>");
	}

	private void print(String get) {
		out.println("<html>");
		out.println("<head>");
		out.println("<title> Access Token </title>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler\">Home</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/parse?accessToken=" + get + "\">Parse</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/Post?accessToken=" + get
				+ "\">Schedule Quote</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/QuoteHistory?accessToken=" + get
				+ "\">Quote History</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/PendingQuotes?accessToken=" + get
				+ "\">Pending Quotes</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/search?accessToken=" + get
				+ "\">Search</a><br><br>");
		out.print("</head>");
		out.println("<body>");
	}
}