package ro.project.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ro.project.Constants;
import ro.project.parser.FileManager;
import ro.project.parser.Parser;
import ro.project.scheduler.Scheduler;

/**
 * 
 * @author Caphyon1
 *
 *         Used for retrieving the access token and passing it to the other
 *         servlets.
 */
public class AccessToken extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Scheduler scheduler;
	ServletToScheduler servletToScheduler;
	Parser parser;
	FileManager fileManager;
	PrintWriter out = null;
	String accessToken = null;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		out = response.getWriter();

		if ((accessToken == null) && request.getParameter(Constants.ACCESS_TOKEN) != null) {
			accessToken = request.getParameter(Constants.ACCESS_TOKEN);
		}

		if (request.getRequestURI().equals("/SocialMediaScheduler/PendingQuotes")) {
			printMenu();
			out.print(servletToScheduler.getAllPendingQuotes(accessToken));
		} else if (request.getRequestURI().equals("/SocialMediaScheduler/DeletePending")) {
			printMenu();
			scheduler.deleteUpdate(accessToken, request.getParameter("url"));
			response.sendRedirect("http://localhost:8080/SocialMediaScheduler/PendingQuotes");
		} else if (request.getRequestURI().equals("/SocialMediaScheduler/Search")) {
			printMenu();
			out.println("<form ACTION=\"Search\">");
			out.println("Insert author: <INPUT TYPE=\"text\" name=\"author\"> ");
			out.println("<input type=\"submit\">;");
			out.println("</form>");

			if (request.getParameter("author") == null) {
				out.println("<br> <br> Please enter an author <br> <br>");
			} else {
				String author = request.getParameter("author");
				out.println(servletToScheduler.getAllPostedQuotesByAuthor(accessToken, author));
			}
		} else if (request.getRequestURI().equals("/SocialMediaScheduler/ParseWebsite")) {
			printMenu();
			if ((request.getParameter("radios") == null) || (request.getParameter("website") == null)) {
				out.println("<BR> You didn't select a parser... <br> ");
			} else {
				String link = request.getParameter("radios");
				String path = getServletContext().getInitParameter("path");
				String website = request.getParameter("website");
				out.println(servletToScheduler.parseWebsite(link, path, website));
			}
		} else if (request.getRequestURI().equals("/SocialMediaScheduler/Post")) {
			String path = getServletContext().getInitParameter("path") + "quotes/";
			out.print(servletToScheduler.postToSocialMediaView(accessToken, path));
		} else if (request.getRequestURI().equals("/SocialMediaScheduler/QuoteHistory")) {
			boolean ascending = true;
			String type = "";
			if (request.getParameter("type") != null) {
				type = request.getParameter("type");
			}

			if (request.getParameter("order") != null) {
				if (request.getParameter("order").equals("ascending")) {
					ascending = true;
				} else {
					ascending = false;
				}
			}
			PostedQuotesRetriever dao = new PostedQuotesRetriever(scheduler);

			int page = 1;
			int recordsPerPage = 10;
			if (request.getParameter("page") != null)
				page = Integer.parseInt(request.getParameter("page"));
			List<String> list = dao.getPostedQuotes(accessToken, (page - 1) * recordsPerPage, recordsPerPage, type,
					ascending);
			int noOfRecords = dao.getNoOfRecords(accessToken);
			if (noOfRecords < 0 || list == null) {
				request.setAttribute("auth", 0);
			} else {
				int noOfPages = (int) Math.ceil(noOfRecords * 1.0 / recordsPerPage);
				request.setAttribute("auth", 1);
				request.setAttribute("quotesList", list);
				request.setAttribute("noOfPages", noOfPages);
				request.setAttribute("currentPage", page);
				request.setAttribute("lastType", type);
				request.setAttribute("order", request.getParameter("order"));
			}
			RequestDispatcher view = request.getRequestDispatcher("displayQuotes.jsp");
			view.forward(request, response);
		} else if (request.getRequestURI().equals("/SocialMediaScheduler/HelloServlet")) {
			String path = getServletContext().getInitParameter("path");
			String path2 = getServletContext().getInitParameter("path2");
			String radios = request.getParameter("radios");
			String[] where = request.getParameterValues("where");
			String yearDropDown = request.getParameter("yeardropdown");
			String monthDropDown = request.getParameter("monthdropdown");
			String dayDropDown = request.getParameter("daydropdown");
			String hourDropDown = request.getParameter("hourdropdown");
			String minuteDropDown = request.getParameter("minutedropdown");
			String gmtDropDown = request.getParameter("gmtdropdown");

			printMenu();
			out.println(servletToScheduler.postToSocialMedia(accessToken, path, path2, radios, where, yearDropDown,
					monthDropDown, dayDropDown, hourDropDown, minuteDropDown, gmtDropDown));
		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		out = resp.getWriter();

		if ((accessToken == null) && req.getParameter(Constants.ACCESS_TOKEN) != null) {
			accessToken = req.getParameter(Constants.ACCESS_TOKEN);
		}

		if (req.getRequestURI().equals("/SocialMediaScheduler/AccessToken")) {
			printMenu();
			if (req.getParameter(Constants.ACCESS_TOKEN) == null) {
				out.println("<BR> You didn't tell us your access token... <br> ");
			} else {
				out.println("<BR> Your access token is: " + req.getParameter(Constants.ACCESS_TOKEN));
			}
			out.print("</body>\n</html>");
		}
	}

	@Override
	public void init() throws ServletException {
		super.init();
		scheduler = Scheduler.getInstance();
		servletToScheduler = new ServletToScheduler(scheduler);
	}

	private void printMenu() {
		out.println("<html>");
		out.println("<head>");
		out.println("<title> Access Token </title>");
		out.println("<a href=\"http://localhost:8080/SocialMediaScheduler\">Home</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/parse\">Parse</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/Post\">Schedule Quote</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/QuoteHistory\">Quote History</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/PendingQuotes\">Pending Quotes</a>");
		out.println("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/Search\">Search</a>");
		out.println("</head>");
		out.println("<body>");
	}
}