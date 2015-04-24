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

		if (request.getRequestURI().equals("/SocialMediaScheduler/")) {
			request.setAttribute(Constants.ACCESS_TOKEN, scheduler.getAccessToken());
			RequestDispatcher view = request.getRequestDispatcher("Home.jsp");
			view.forward(request, response);
		} else if (request.getRequestURI().equals("/SocialMediaScheduler/DeletePending")) {
			String path = getServletContext().getInitParameter(Constants.PATH_2).replace("/", "\\\\");
			path += "\\\\quotes\\\\";
			//System.out.println("IN SOCIALMEDIASCHEDULERSERVLET");
			//System.out.println("retrieved parameter: " + request.getParameter(Constants.Quote));
			scheduler.deleteUpdate(request.getParameter(Constants.URL));
			scheduler.updateWithDeletedPendingUpdate(request.getParameter(Constants.Quote), request.getParameter("service"), request.getParameter("text"), path);
			//System.out.println("BBBBBBBBBBBBBBB: " + request.getParameter("text"));
			
			response.sendRedirect("http://localhost:8080/SocialMediaScheduler/PendingQuotes");
		} else if (request.getRequestURI().equals("/SocialMediaScheduler/Search")) {
			if (request.getParameter(Constants.AUTHOR) == null) {
				request.setAttribute(Constants.AUTHOR_ENTRIES, "");
			} else {
				String author = request.getParameter(Constants.AUTHOR);
				request.setAttribute(Constants.AUTHOR_ENTRIES, servletToScheduler.getAllPostedQuotesByAuthor(author));
			}
			RequestDispatcher view = request.getRequestDispatcher("SearchByAuthor.jsp");
			view.forward(request, response);
		} else if (request.getRequestURI().equals("/SocialMediaScheduler/ParseWebsite")) {
			if ((request.getParameter(Constants.RADIOS) == null) || (request.getParameter(Constants.WEBSITE) == null)) {
				request.setAttribute(Constants.RESPONSE, "You didn't select a parser... ");
			} else {
				String link = request.getParameter(Constants.RADIOS);
				String path = getServletContext().getInitParameter(Constants.PATH_2).replace("/", "\\\\");;
				String website = request.getParameter(Constants.WEBSITE);
				request.setAttribute(Constants.RESPONSE, servletToScheduler.parseWebsite(link, path, website));
			}
			RequestDispatcher view = request.getRequestDispatcher("ParseWebsite.jsp");
			view.forward(request, response);
		} else if (request.getRequestURI().equals("/SocialMediaScheduler/Post")) {
			String path = getServletContext().getInitParameter(Constants.PATH_2).replace("/", "\\\\") + Constants.QUOTES_FILE;
			request.setAttribute(Constants.PATH, path);
			// out.print(servletToScheduler.postToSocialMediaView(path));
			request.setAttribute(Constants.OPTIONS_LIST, servletToScheduler.getOptionsList(path));
			List<String> a = servletToScheduler.getOptionsList(path);
			request.setAttribute(Constants.ALL_PROFILES, scheduler.getAllProfiles());

			RequestDispatcher view = request.getRequestDispatcher("ScheduleQuote.jsp");
			view.forward(request, response);
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
				request.setAttribute(Constants.AUTH, 0);
			} else {
				if (list.size() == 0) {
					request.setAttribute(Constants.AUTH, 0);
				} else {
					int noOfPages = (int) Math.ceil(noOfRecords * 1.0 / recordsPerPage);
					request.setAttribute(Constants.AUTH, 1);
					request.setAttribute(Constants.QUOTES_LIST, list);
					request.setAttribute(Constants.NO_OF_PAGES, noOfPages);
					request.setAttribute(Constants.CURRENT_PAGE, page);
					request.setAttribute(Constants.LAST_TYPE, type);
					request.setAttribute(Constants.ORDER, request.getParameter("order"));
				}
			}
			RequestDispatcher view = request.getRequestDispatcher("displayQuotes.jsp");
			view.forward(request, response);
		} else if (request.getRequestURI().equals("/SocialMediaScheduler/HelloServlet")) {
			String path = getServletContext().getInitParameter(Constants.PATH_2).replace("/", "\\\\");;
			String path2 = getServletContext().getInitParameter(Constants.PATH_2);
			String radios = request.getParameter(Constants.RADIOS);
			String where = request.getParameter(Constants.WHERE);
			String yearDropDown = request.getParameter(Constants.YEAR_DROP_DOWN);
			String monthDropDown = request.getParameter(Constants.MONTH_DROP_DOWN);
			String dayDropDown = request.getParameter(Constants.DAY_DROP_DOWN);
			String hourDropDown = request.getParameter(Constants.HOUR_DROP_DOWN);
			String minuteDropDown = request.getParameter(Constants.MINUTE_DROP_DOWN);
			String gmtDropDown = request.getParameter(Constants.GMT_DROP_DOWN);
			
			String dayDropDown2 = request.getParameter(Constants.DAY_DROP_DOWN_2);
			String hourDropDown2 = request.getParameter(Constants.HOUR_DROP_DOWN_2);
			String minuteDropDown2 = request.getParameter(Constants.MINUTE_DROP_DOWN_2);
			
			String numberofQuotes = request.getParameter(Constants.NUMBER_OF_POSTS);
			request.setAttribute(Constants.POST_TO_SM, servletToScheduler.postToSocialMedia(path, path2, radios, where,
					yearDropDown, monthDropDown, dayDropDown, hourDropDown, minuteDropDown, gmtDropDown, 
					dayDropDown2, hourDropDown2, minuteDropDown2, numberofQuotes));
			RequestDispatcher view = request.getRequestDispatcher("PostingRandomQuote.jsp");
			view.forward(request, response);
		} else if (request.getRequestURI().equals("/SocialMediaScheduler/PendingQuotes")) {
			PendingQuotesRetriever dao = new PendingQuotesRetriever(scheduler);

			int page = 1;
			int recordsPerPage = 10;
			if (request.getParameter(Constants.PAGE) != null)
				page = Integer.parseInt(request.getParameter(Constants.PAGE));
			List<String> list = dao.getPendingQuotes((page - 1) * recordsPerPage, recordsPerPage);
			int noOfRecords = dao.getNoOfRecords();
			if (noOfRecords < 0 || list == null) {
				request.setAttribute(Constants.AUTH, 0);
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

		scheduler.setAccessTokenWithpath(req.getParameter(Constants.ACCESS_TOKEN), getServletContext()
				.getInitParameter(Constants.PATH_2).replace("/", "\\\\"));
		resp.sendRedirect("http://localhost:8080/SocialMediaScheduler/");
	}

	@Override
	public void init() throws ServletException {
		super.init();
		String path = getServletContext().getInitParameter(Constants.PATH_2).replace("/", "\\\\");;
		scheduler = Scheduler.getInstance();
		scheduler.setAccessTokenWithpath("", path);
		servletToScheduler = new ServletToScheduler(scheduler);
	}

}