package ro.project.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ro.project.parser.FileManager;
import ro.project.parser.QuoteWebsiteParser;
import ro.project.scheduler.Scheduler;

public class ParseWebsiteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Scheduler scheduler;
	QuoteWebsiteParser parser;
	FileManager fileManager;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		out.println("<html>\n <body>");
		scheduler = new Scheduler();
		fileManager = new FileManager();
		// fileManager = new FileManager();
		String website = request.getParameter("website");
		parser = new QuoteWebsiteParser();
		String path = parser.updateQuotes(website);
		fileManager.createFileInPath("facebookquotes");
		fileManager.createFileInPath("twitterquotes");
		
		
		out.println("The quotes from the given website were retrieved... <br> What do you want to do next? <br>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/\">Schedule Quote</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/QuoteHistory\">Quote History</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/PendingQuotes\">Pending Quotes</a><br><br>");
		out.print("</html>\n</body>");
	}
}