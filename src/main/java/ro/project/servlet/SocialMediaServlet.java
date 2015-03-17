package ro.project.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ro.project.parser.FileManager;
import ro.project.scheduler.Quote;
import ro.project.scheduler.QuoteManager;
import ro.project.scheduler.Scheduler;

public class SocialMediaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private QuoteManager quoteManager;
	private Scheduler scheduler;
	private FileManager fileManager;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String accessToken = request.getParameter("accessToken");
		fileManager = new FileManager();
		out.println("<html>\n <body>");
		out.println("<head>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler\">Home</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/parse?accessToken=" + accessToken
				+ "\">Parse</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/Post?accessToken=" + accessToken
				+ "\">Schedule Quote</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/QuoteHistory?accessToken=" + accessToken
				+ "\">Quote History</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/PendingQuotes?accessToken=" + accessToken
				+ "\">Pending Quotes</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/search?accessToken=" + accessToken
				+ "\">Search</a><br><br>");
		out.print("</head>");

		if ((request.getParameter("radios") == null) || (request.getParameter("where") == null)) {
			out.print("<br> A problem occured. <br> This could happen if: <br>"
					+ "- you did not parse a website before trying to schedule a quote to be poste <br>"
					+ "- you did notselect a website to get the quotes from <br>"
					+ "- you did not select a social network to post to <br>" + "");
			out.print("</html>\n</body>");
		} else if ((Integer.parseInt(request.getParameter("yeardropdown")) < 2015)
				|| (Integer.parseInt(request.getParameter("yeardropdown")) > 2034)
				|| (Integer.parseInt(request.getParameter("monthdropdown")) < 1)
				|| (Integer.parseInt(request.getParameter("monthdropdown")) > 12)
				|| (Integer.parseInt(request.getParameter("daydropdown")) < 1)
				|| (Integer.parseInt(request.getParameter("daydropdown")) > 31)
				|| (Integer.parseInt(request.getParameter("hourdropdown")) < 0)
				|| (Integer.parseInt(request.getParameter("hourdropdown")) > 23)
				|| (Integer.parseInt(request.getParameter("minutedropdown")) < 0)
				|| (Integer.parseInt(request.getParameter("minutedropdown")) > 59)) {
			out.print("<br> A problem occured. <br> This could happen if you did no pick a valid date for the quote to be scheduled <br>");
			out.print("</html>\n</body>");
		} else {
			String fileName = fileManager.createFileNameFromUrl(request.getParameter("radios"));
			// fileName += ".txt";
			fileName += ".ser";
			quoteManager = new QuoteManager(fileName);
			scheduler = new Scheduler(accessToken);

			String date = request.getParameter("yeardropdown") + "-" + request.getParameter("monthdropdown") + "-"
					+ request.getParameter("daydropdown") + " " + request.getParameter("hourdropdown") + ":"
					+ request.getParameter("minutedropdown") + ":00" + "GMT" + request.getParameter("gmtdropdown")
					+ ":00";

			String[] where = request.getParameterValues("where");

			if (where != null) {
				for (int i = 0; i < where.length; i++) {
					scheduler.setUserId(scheduler.getProfileId(where[i]));
					int max = scheduler.getMaxCharacters(where[i]);
					Quote quote = quoteManager.getRandomQuote(where[i], max);
					if ((quote == null) || (quote.getQuote().trim().isEmpty())) {
						out.print("<br> Found nothing to print on " + where[i] + " <br>");
					} else {
						int code = scheduler.sendMessage(quote.toString(), date);
						if (code == 200) {
							out.println("Quote \"" + quote.toString().replaceAll("\\+", " ")
									+ "\" was schedulet to be posted on " + date + " on " + where[i] + "<BR>");
						} else if (code == 0) {
							out.println("<br>Something went wrong. Probably the access token is not good..." + "<BR>");
						} else {
							out.println("<br> Something went wrong when trying to post on " + where[i] + " <BR>");
						}
					}

				}
			}
			out.print("</html>\n</body>");
		}
	}
}