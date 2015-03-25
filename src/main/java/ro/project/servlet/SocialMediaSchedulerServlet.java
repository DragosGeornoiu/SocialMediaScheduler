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
public class SocialMediaSchedulerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Scheduler scheduler;
	ServletToScheduler servletToScheduler;
	Parser parser;
	FileManager fileManager;
	PrintWriter out = null;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		out = response.getWriter();

		if (scheduler.getAccessToken() == null) {
			response.sendRedirect("http://localhost:8080/SocialMediaScheduler/Edit");
			 return;
		}
		
		System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAA: " + scheduler.getAccessToken());

		if (request.getRequestURI().equals("/SocialMediaScheduler/")) {
			printMenu();
			out.println("<br> Your accessToken is: " + scheduler.getAccessToken());
		} else if (request.getRequestURI().equals("/SocialMediaScheduler/DeletePending")) {
			printMenu();
			scheduler.deleteUpdate(request.getParameter("url"));
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
				out.println(servletToScheduler.getAllPostedQuotesByAuthor(author));
			}
		} else if (request.getRequestURI().equals("/SocialMediaScheduler/ParseWebsite")) {
			printMenu();
			if ((request.getParameter(Constants.RADIOS) == null) || (request.getParameter(Constants.WEBSITE) == null)) {
				out.println("<BR> You didn't select a parser... <br> ");
			} else {
				String link = request.getParameter(Constants.RADIOS);
				String path = getServletContext().getInitParameter(Constants.PATH);
				String website = request.getParameter(Constants.WEBSITE);
				out.println(servletToScheduler.parseWebsite(link, path, website));
			}
		} else if (request.getRequestURI().equals("/SocialMediaScheduler/Post")) {
			String path = getServletContext().getInitParameter(Constants.PATH) + Constants.QUOTES_FILE;
			out.print(servletToScheduler.postToSocialMediaView(path));
		} else if (request.getRequestURI().equals("/SocialMediaScheduler/QuoteHistory")) {
			boolean ascending = true;
			String type = "";
			if (request.getParameter(Constants.TYPE) != null) {
				type = request.getParameter(Constants.TYPE);
			}

			if (request.getParameter(Constants.ORDER) != null) {
				if (request.getParameter(Constants.ORDER).equals(Constants.ORDER_ASCENDING)) {
					ascending = true;
				} else {
					ascending = false;
				}
			}
			PostedQuotesRetriever dao = new PostedQuotesRetriever(scheduler);

			int page = 1;
			int recordsPerPage = 10;
			if (request.getParameter(Constants.PAGE) != null)
				page = Integer.parseInt(request.getParameter(Constants.PAGE));
			List<String> list = dao.getPostedQuotes((page - 1) * recordsPerPage, recordsPerPage, type, ascending);
			int noOfRecords = dao.getNoOfRecords();
			if (noOfRecords < 0 || list == null) {
				request.setAttribute("auth", 0);
			} else {
				int noOfPages = (int) Math.ceil(noOfRecords * 1.0 / recordsPerPage);
				request.setAttribute(Constants.AUTH, 1);
				request.setAttribute(Constants.QUOTES_LIST, list);
				request.setAttribute(Constants.NO_OF_PAGES, noOfPages);
				request.setAttribute(Constants.CURRENT_PAGE, page);
				request.setAttribute(Constants.LAST_TYPE, type);
				request.setAttribute(Constants.ORDER, request.getParameter("order"));
			}
			RequestDispatcher view = request.getRequestDispatcher("displayQuotes.jsp");
			view.forward(request, response);
		} else if (request.getRequestURI().equals("/SocialMediaScheduler/HelloServlet")) {
			String path = getServletContext().getInitParameter(Constants.PATH);
			String path2 = getServletContext().getInitParameter(Constants.PATH_2);
			String radios = request.getParameter(Constants.RADIOS);
			String[] where = request.getParameterValues(Constants.WHERE);
			String yearDropDown = request.getParameter(Constants.YEAR_DROP_DOWN);
			String monthDropDown = request.getParameter(Constants.MONTH_DROP_DOWN);
			String dayDropDown = request.getParameter(Constants.DAY_DROP_DOWN);
			String hourDropDown = request.getParameter(Constants.HOUR_DROP_DOWN);
			String minuteDropDown = request.getParameter(Constants.MINUTE_DROP_DOWN);
			String gmtDropDown = request.getParameter(Constants.GMT_DROP_DOWN);

			printMenu();
			out.println(servletToScheduler.postToSocialMedia(path, path2, radios, where, yearDropDown, monthDropDown,
					dayDropDown, hourDropDown, minuteDropDown, gmtDropDown));
		} else if (request.getRequestURI().equals("/SocialMediaScheduler/PendingQuotes")) {
			PendingQuotesRetriever dao = new PendingQuotesRetriever(scheduler);

			int page = 1;
			int recordsPerPage = 10;
			if (request.getParameter(Constants.PAGE) != null)
				page = Integer.parseInt(request.getParameter(Constants.PAGE));
			List<String> list = dao.getPendingQuotes((page - 1) * recordsPerPage, recordsPerPage);
			int noOfRecords = dao.getNoOfRecords();
			if (noOfRecords < 0 || list == null) {
				request.setAttribute("auth", 0);
			} else {
				int noOfPages = (int) Math.ceil(noOfRecords * 1.0 / recordsPerPage);
				request.setAttribute(Constants.AUTH, 1);
				request.setAttribute(Constants.QUOTES_LIST, list);
				request.setAttribute(Constants.NO_OF_PAGES, noOfPages);
				request.setAttribute(Constants.CURRENT_PAGE, page);
			}
			RequestDispatcher view = request.getRequestDispatcher("displayPending.jsp");
			view.forward(request, response);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		out = resp.getWriter();

		scheduler.setAccessToken(req.getParameter(Constants.ACCESS_TOKEN));
		resp.sendRedirect("http://localhost:8080/SocialMediaScheduler/");

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
		out.println("<title>Scheduler</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<a href=\"http://localhost:8080/SocialMediaScheduler\">Home</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/parse\">Parse</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/Post\">Schedule Quote</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/QuoteHistory\">Quote History</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/PendingQuotes\">Pending Quotes</a>");
		out.println("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/Search\">Search</a>");
		out.println("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/Edit\">Edit</a><br><br>");

	}
}