package ro.project.servlet;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import ro.project.Constants;
import ro.project.parser.FileManager;
import ro.project.parser.Parser;
import ro.project.scheduler.Scheduler;
import ro.project.thread.ThreadScheduler;

/**
 * 
 *         Used for retrieving the access token and passing it to the other
 *         servlets.
 */
public class SocialMediaSchedulerServlet extends HttpServlet {
	final static Logger logger = Logger.getLogger(SocialMediaSchedulerServlet.class);
	private static final long serialVersionUID = 1L;
	Scheduler scheduler;
	ServletToScheduler servletToScheduler;
	Parser parser;
	FileManager fileManager;
	PrintWriter out = null;
	ThreadScheduler threadScheduler;

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
			scheduler.deleteUpdate(request.getParameter(Constants.URL));
			scheduler.updateWithDeletedPendingUpdate(request.getParameter(Constants.Quote),
					request.getParameter("service"), request.getParameter("text"), path);

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
				String path = getServletContext().getInitParameter(Constants.PATH_2).replace("/", "\\\\");
				;
				String website = request.getParameter(Constants.WEBSITE);
				request.setAttribute(Constants.RESPONSE, servletToScheduler.parseWebsite(link, path, website));
			}
			RequestDispatcher view = request.getRequestDispatcher("ParseWebsite.jsp");
			view.forward(request, response);
		} else if (request.getRequestURI().equals("/SocialMediaScheduler/Post")) {
			String path = getServletContext().getInitParameter(Constants.PATH_2).replace("/", "\\\\")
					+ Constants.QUOTES_FILE;
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
			String[] where = request.getParameterValues(Constants.WHERE);

			Properties prop = new Properties();
			OutputStream output = null;

			try {

				output = new FileOutputStream(getServletContext().getInitParameter(Constants.PATH_2)
						+ Constants.CONFIG_PROPERTIES);
				prop.setProperty(Constants.PATH, getServletContext().getInitParameter(Constants.PATH_2));
				prop.setProperty(Constants.RADIOS, request.getParameter(Constants.RADIOS));
				prop.setProperty(Constants.WHERE_SIZE, Integer.toString(where.length));
				for (int i = 0; i < where.length; i++) {
					prop.setProperty(Constants.WHERE + i, where[i]);
				}
				prop.setProperty(Constants.YEAR_DROP_DOWN, request.getParameter(Constants.YEAR_DROP_DOWN));
				prop.setProperty(Constants.MONTH_DROP_DOWN, request.getParameter(Constants.MONTH_DROP_DOWN));
				prop.setProperty(Constants.DAY_DROP_DOWN, request.getParameter(Constants.DAY_DROP_DOWN));
				prop.setProperty(Constants.HOUR_DROP_DOWN, request.getParameter(Constants.HOUR_DROP_DOWN));
				prop.setProperty(Constants.MINUTE_DROP_DOWN, request.getParameter(Constants.MINUTE_DROP_DOWN));
				prop.setProperty(Constants.GMT_DROP_DOWN, request.getParameter(Constants.GMT_DROP_DOWN));
				prop.setProperty(Constants.DAY_DROP_DOWN_2, request.getParameter(Constants.DAY_DROP_DOWN_2));
				prop.setProperty(Constants.HOUR_DROP_DOWN_2, request.getParameter(Constants.HOUR_DROP_DOWN_2));
				prop.setProperty(Constants.MINUTE_DROP_DOWN_2, request.getParameter(Constants.MINUTE_DROP_DOWN_2));
				prop.setProperty(Constants.NUMBER_OF_POSTS, request.getParameter(Constants.NUMBER_OF_POSTS));
				prop.setProperty(Constants.MYFILE, request.getParameter(Constants.MYFILE));

				// save properties to project root folder
				prop.store(output, null);

			} catch (IOException io) {
				logger.error(io.getMessage());
			} finally {
				if (output != null) {
					try {
						output.close();
					} catch (IOException e) {
						logger.error(e.getMessage());
					}
				}
			}

			threadScheduler.setInterval(Integer.parseInt(getServletContext().getInitParameter(Constants.INTERVAL)));
			threadScheduler.setPath(getServletContext().getInitParameter(Constants.PATH_2));
			threadScheduler.setScheduler(scheduler);
			synchronized (threadScheduler) {
				threadScheduler.notify();
			}

			//
			// threadScheduler.setInterval(Integer.parseInt(getServletContext().getInitParameter(Constants.INTERVAL)));
			// if (!threadScheduler.isAlive()) {
			// threadScheduler.setPath(getServletContext().getInitParameter(Constants.PATH_2));
			// threadScheduler.setScheduler(scheduler);
			// threadScheduler.start();
			// } else {
			// synchronized (threadScheduler) {
			// threadScheduler.notify();
			// }
			// }

			request.setAttribute(
					"message",
					"Daily posts were set between "
							+ request.getParameter(Constants.HOUR_DROP_DOWN)
							+ ":"
							+ request.getParameter(Constants.MINUTE_DROP_DOWN) + " - "
									+ request.getParameter(Constants.HOUR_DROP_DOWN_2) + ":"
									+ request.getParameter(Constants.MINUTE_DROP_DOWN_2) + ".");
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
		String path = getServletContext().getInitParameter(Constants.PATH_2).replace("/", "\\\\");
		scheduler = Scheduler.getInstance();
		scheduler.setAccessTokenWithpath("", path);
		servletToScheduler = new ServletToScheduler(scheduler);
		threadScheduler = ThreadScheduler.getInstance();
		// threadScheduler.start();
	}
}