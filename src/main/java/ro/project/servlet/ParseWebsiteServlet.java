package ro.project.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ro.project.parser.BrainyQuoteParser;
import ro.project.parser.FileManager;
import ro.project.parser.Parser;
import ro.project.parser.PersdevParser;

/**
 * Gives the URL of the website to be parsed.
 *
 */
public class ParseWebsiteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Parser parser;
	FileManager fileManager;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		boolean selectionCorect = true;
		String get = request.getParameter("accessToken");
		
		out.println("<html>\n <body>");
		out.println("<head>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler\">Home</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/parse?accessToken=" + get + "\">Parse</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/Post?accessToken=" + get + "\">Schedule Quote</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/QuoteHistory?accessToken=" + get + "\">Quote History</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/PendingQuotes?accessToken=" + get + "\">Pending Quotes</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/search?accessToken=" + get
				+ "\">Search</a><br><br>");
		out.print("</head>");

		if ((request.getParameter("radios") == null) || (request.getParameter("website") == null)) {
			out.println("<BR> You didn't select a parser... <br> ");
		} else {
			String link = request.getParameter("radios");
			fileManager = new FileManager();
			String website = request.getParameter("website");
			if ((link.equals("http://persdev-q.com/")) && (website.startsWith("http://persdev-q.com/"))) {
				parser = new PersdevParser();
			} else if ((link.equals("http://www.brainyquote.com/"))
					&& (website.startsWith("http://www.brainyquote.com/"))) {
				parser = new BrainyQuoteParser();
			} else {
				selectionCorect = false;
			}

			/* String path = parser.updateQuotes(website); */

			if (selectionCorect) {

				if (parser.updateQuotes(website)) {
					fileManager.createFileInPath("facebookquotes.txt");
					fileManager.createFileInPath("twitterquotes.txt");
					out.println("The quotes from the given website were retrieved... <br> What do you want to do next? <br>");
				} else {
					out.println("The website was already parsed... <br>");
				}
			} else {
				out.println("Something went wrong, you can try again...<br>");

			}
			
		}
		
		out.print("</body>\n</html>");
	}
	

}
