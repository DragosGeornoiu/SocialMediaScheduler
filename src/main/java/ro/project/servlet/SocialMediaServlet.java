package ro.project.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ro.project.parser.FileManager;
import ro.project.scheduler.QuoteManager;
import ro.project.scheduler.Scheduler;

public class SocialMediaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private QuoteManager quoteManager;
	private Scheduler scheduler;
	private FileManager fileManager;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		fileManager = new FileManager();
		String fileName = fileManager.createFileNameFromUrl(request.getParameter("radios"));
		//fileName += ".txt";
		fileName += ".ser";
		quoteManager = new QuoteManager(fileName);
		scheduler = new Scheduler();

		out.println("<html>\n <body>");
		out.println("<head>");
		out.print("<a href=\"http://localhost:8080/SocialMediaScheduler/parse\">Parse</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/Post\">Schedule Quote</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/QuoteHistory\">Quote History</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/PendingQuotes\">Pending Quotes</a><br><br>");
		out.print("</head>");

		String date = request.getParameter("yeardropdown") + "-" + request.getParameter("monthdropdown") + "-"
				+ request.getParameter("daydropdown") + " " + request.getParameter("hourdropdown") + ":"
				+ request.getParameter("minutedropdown") + ":00" + "GMT" + request.getParameter("gmtdropdown") + ":00" ;

		String[] where = request.getParameterValues("where");

		if (where != null) {
			for (int i = 0; i < where.length; i++) {
				if (where[i].equals("Twitter")) {
					scheduler.setUserId("54f4480b76a9a2b75cb71256");
					String quote = quoteManager.getRandomQuoteForTwitter();
					if (quote.trim().isEmpty()) {
						out.print("Found nothing to print");
					} else {
						if (scheduler.sendMessage(quote, date) == 200) {
							out.println("Quote \"" + quote.replaceAll("\\+", " ") + "\" was schedulet to be posted on "
									+ date + " on " + where[i] + "<BR>");
						} else {
							out.println("<br>Something went wrong when trying to post on Twitter" + "<BR>");
						}
					}
				} else if (where[i].equals("Facebook")) {
					scheduler.setUserId("54f5cffee090e41029541d73");
					String quote = quoteManager.getRandomQuoteForFacebook();
					if (quote.trim().isEmpty()) {
						out.print("Found nothing to print");
					} else {
						if (scheduler.sendMessage(quote, date) == 200) {
							out.println("Quote \"" + quote.replaceAll("\\+", " ") + "\" was schedulet to be posted on "
									+ date + " on " + where[i] + "<BR>");
						} else {
							out.println("<br>Something went wrong when trying to post on Facebook" + "<BR>");
						}
					}
				}
			}
		}

		out.print("</html>\n</body>");
	}
}