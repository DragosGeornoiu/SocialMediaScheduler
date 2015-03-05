package ro.project.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ro.project.scheduler.QuoteManager;
import ro.project.scheduler.Scheduler;

public class SocialMediaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private QuoteManager quoteManager;
	private Scheduler scheduler;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int code = 0;

		PrintWriter out = response.getWriter();
		quoteManager = new QuoteManager();
		scheduler = new Scheduler();
		out.println("<html>\n <body>");
		
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/\">Schedule Quote</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/QuoteHistory\">Quote History</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/PendingQuotes\">Pending Quotes</a><br><br>");
		String date = request.getParameter("date");
		/*	String date = request.getParameter("yeardropdown")
		+ "-"	+ request.getParameter("monthdropdown") 
		+ "-" + request.getParameter("daydropdown") 
		+ " " + request.getParameter("hourdropdown") 
		+ ":" + request.getParameter("minutedropdown") + ":00";*/

		String[] where = request.getParameterValues("where");
		String message = "";
		

		for (int i = 0; i < where.length; i++) {
			message = where[i];
			if (message.equals("Twitter")) {
				code = 0;
				scheduler.setUserId("54f4480b76a9a2b75cb71256");
				String quote = quoteManager.getRandomQuoteForTwitter();
				if (scheduler.sendMessage(quote, date) == 200) {
					out.println("Quote \"" + quote.replaceAll("\\+", " ") + "\" was schedulet to be posted on " + date
							+ " on " + message + "<BR>");
				} else if (code != 0) {
					out.println("Something went wrong when trying to post on Twitter" + "<BR>");
				}
			} else if (message.equals("Facebook")) {
				code = 0;
				scheduler.setUserId("54f5cffee090e41029541d73");
				String quote = quoteManager.getRandomQuoteForFacebook();
				if (scheduler.sendMessage(quote, date) == 200) {
					out.println("Quote \"" + quote.replaceAll("\\+", " ") + "\" was schedulet to be posted on " + date
							+ " on " + message + "<BR>");
				} else if (code != 0) {
					out.println("Something went wrong when trying to post on Twitter" + "<BR>");
				}
			}
		}
		
		out.print("</html>\n</body>");
	}
}