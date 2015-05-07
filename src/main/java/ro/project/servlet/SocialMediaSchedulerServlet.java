package ro.project.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import ro.project.Constants;
import ro.project.parser.FileManager;
import ro.project.parser.Parser;
import ro.project.scheduler.Scheduler;
import ro.project.thread.SchedulerThread;

/**
 * 
 * Used for retrieving the access token and passing it to the other servlets.
 */
public class SocialMediaSchedulerServlet extends HttpServlet {
	final static Logger logger = Logger.getLogger(SocialMediaSchedulerServlet.class);
	private static final long serialVersionUID = 1L;
	Scheduler scheduler;
	ServletToScheduler servletToScheduler;
	Parser parser;
	FileManager fileManager;
	PrintWriter out = null;
	SchedulerThread threadScheduler;

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
			scheduler.updateWithDeletedPendingUpdate(request.getParameter(Constants.QUOTE),
					request.getParameter(Constants.SERVICE), request.getParameter(Constants.TEXT), path);

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
			request.setAttribute(Constants.ALL_PROFILES, scheduler.getAllProfiles());

			List<String> previousSelected = null;
			// read from properties file last hours set
			Properties prop = new Properties();
			InputStream input = null;
			String fromHourToSet = "";
			String toHourToSet = "";
			String gmtSet = "";
			String when = "";
			try {

				input = new FileInputStream(getServletContext().getInitParameter(Constants.PATH_2)
						+ Constants.CONFIG_PROPERTIES);
				prop.load(input);

				fromHourToSet = prop.getProperty(Constants.HOUR_DROP_DOWN);
				toHourToSet = prop.getProperty(Constants.HOUR_DROP_DOWN_2);
				gmtSet = prop.getProperty(Constants.GMT_DROP_DOWN);
				when = prop.getProperty(Constants.WHEN);

				previousSelected = new ArrayList<String>();
				for (int i = 0; i < scheduler.getAllProfiles().size(); i++) {
					String temp = prop.getProperty(scheduler.getAllProfiles().get(i));
					if (temp == null) {
						previousSelected.add(" ");
					} else {
						previousSelected.add(temp);
					}
				}

			} catch (Exception ex) {
				logger.error(ex.getMessage());
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						logger.error(e.getMessage());
					}
				}
			}

			request.setAttribute(Constants.FROM_HOUR_TO_SET, fromHourToSet);
			request.setAttribute(Constants.TO_HOUR_TO_SET, toHourToSet);
			request.setAttribute(Constants.GMT_SET, gmtSet);
			request.setAttribute(Constants.WHEN, when);
			request.setAttribute(Constants.PREVIOUS_SELECTED, previousSelected);

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
					request.setAttribute(Constants.ORDER, request.getParameter(Constants.ORDER));
				}
			}
			RequestDispatcher view = request.getRequestDispatcher("displayQuotes.jsp");
			view.forward(request, response);
		} else if (request.getRequestURI().equals("/SocialMediaScheduler/HelloServlet")) {
			String[] where = request.getParameterValues(Constants.WHERE);

			Properties prop = new Properties();
			OutputStream output = null;
			InputStream input = null;
			int hourTemp1 = 0, hourTemp2 = 0, gmtTemp = 0;

			try {

				File file = new File(getServletContext().getInitParameter(Constants.PATH_2)
						+ Constants.CONFIG_PROPERTIES);
				input = new FileInputStream(file);

				prop.load(input);
				String year = null;
				String month = null;
				String day = null;
				String date = prop.getProperty(Constants.CALENDAR_DATE);

				if (date != null) {
					year = date.split(" - ")[0];
					month = date.split(" - ")[1];
					day = date.split(" - ")[2];
				}

				output = new FileOutputStream(file);
				prop.setProperty(Constants.PATH, getServletContext().getInitParameter(Constants.PATH_2));
				prop.setProperty(Constants.RADIOS, request.getParameter(Constants.RADIOS));
				if (where != null) {
					List<String> allProfiles = scheduler.getAllProfiles();
					prop.setProperty(Constants.PROFILESIZES, Integer.toString(allProfiles.size()));
					for (int i = 0; i < allProfiles.size(); i++) {
						prop.setProperty(Constants.PROFILES + i, allProfiles.get(i));
						if (prop.getProperty(allProfiles.get(i)) != null) {
							prop.setProperty(allProfiles.get(i), "0");
						}
					}

					prop.setProperty(Constants.WHERE_SIZE, Integer.toString(where.length));
					for (int i = 0; i < where.length; i++) {
						prop.setProperty(Constants.WHERE + i, where[i]);
						String whereTemp = prop.getProperty(where[i]);
						if (whereTemp != null) {
							int temp = Integer.parseInt(prop.getProperty(where[i]));
							int temp2 = Integer.parseInt(request.getParameter(where[i]));
							prop.setProperty(where[i], request.getParameter(where[i]));
						} else {
							prop.setProperty(where[i], request.getParameter(where[i]));
						}
					}
					
					for (int i = where.length; i < scheduler.getAllProfiles().size(); i++) {
						String temp = prop.getProperty(Constants.WHERE + i);
						prop.setProperty(prop.getProperty(temp), "0");
					}
				}

				hourTemp1 = Integer.parseInt(request.getParameter(Constants.HOUR_DROP_DOWN));
				hourTemp2 = Integer.parseInt(request.getParameter(Constants.HOUR_DROP_DOWN_2));
				gmtTemp = Integer.parseInt(request.getParameter(Constants.GMT_DROP_DOWN));

				if ((hourTemp1 >= hourTemp2) || (hourTemp1 + 1 != hourTemp2)) {
					hourTemp2 = hourTemp1 + 1;
				}

				if (hourTemp1 == Constants.HOUR_24 && hourTemp2 == Constants.HOUR_25) {
					hourTemp1 = Constants.HOUR_0;
					hourTemp2 = Constants.HOUR_1;
				}

				prop.setProperty(Constants.YEAR_DROP_DOWN, request.getParameter(Constants.YEAR_DROP_DOWN));
				prop.setProperty(Constants.MONTH_DROP_DOWN, request.getParameter(Constants.MONTH_DROP_DOWN));
				prop.setProperty(Constants.DAY_DROP_DOWN, request.getParameter(Constants.DAY_DROP_DOWN));
				prop.setProperty(Constants.HOUR_DROP_DOWN, Integer.toString(hourTemp1));
				prop.setProperty(Constants.MINUTE_DROP_DOWN, request.getParameter(Constants.MINUTE_DROP_DOWN));
				prop.setProperty(Constants.GMT_DROP_DOWN, request.getParameter(Constants.GMT_DROP_DOWN));
				prop.setProperty(Constants.DAY_DROP_DOWN_2, request.getParameter(Constants.DAY_DROP_DOWN_2));
				prop.setProperty(Constants.HOUR_DROP_DOWN_2, Integer.toString(hourTemp2));
				prop.setProperty(Constants.MINUTE_DROP_DOWN_2, request.getParameter(Constants.MINUTE_DROP_DOWN_2));
				prop.setProperty(Constants.NUMBER_OF_POSTS, request.getParameter(Constants.NUMBER_OF_POSTS));
				prop.setProperty(Constants.WHEN, request.getParameter(Constants.WHEN));
				prop.setProperty(Constants.MYFILE, request.getParameter(Constants.MYFILE));

				if (year != null && month != null && day != null) {
					prop.setProperty(Constants.CALENDAR_DATE, year + " - " + month + " - " + day);
				}
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

			String message = "";
			if (where == null) {
				message += "<br> You did not select any social networks to post on...";
			}

			if (request.getParameter(Constants.RADIOS).equals(Constants.SELECT)
					&& request.getParameter(Constants.MYFILE).trim().isEmpty()) {
				message += "<br> You selected \"Select your own file \" option, but did not choose a path to the file...";
			}

			int beginHour = hourTemp1;
			int endHour = hourTemp2;
			int beginMinutes = Integer.parseInt(request.getParameter(Constants.MINUTE_DROP_DOWN));
			int endMinutes = Integer.parseInt(request.getParameter(Constants.MINUTE_DROP_DOWN_2));

			// if (beginHour > endHour) {
			// message +=
			// "<br> Your 'FROM' hour option of the schedule is after the 'TO' hour of the schedule...";
			// } else if (beginHour == endHour && beginMinutes >= endMinutes) {
			// message +=
			// "<br> Your 'FROM' minutes of the schedule is after the 'TO' minutes of the schedule...";
			// }

			if (beginHour > endHour) {
				message += "<br> Your 'FROM' hour option of the schedule is after the 'TO' hour of the schedule...";
			} else if (beginHour == endHour && beginMinutes >= endMinutes) {
				message += "<br> Your 'FROM' minutes of the schedule is after the 'TO' minutes of the schedule...";
			}

			if (message.trim().isEmpty()) {
				threadScheduler.setInterval(Integer.parseInt(getServletContext().getInitParameter(Constants.INTERVAL)));
				threadScheduler.setPath(getServletContext().getInitParameter(Constants.PATH_2));
				threadScheduler.setScheduler(scheduler);
				synchronized (threadScheduler) {
					threadScheduler.notify();
				}

				message = "";
				if (request.getParameter(Constants.WHEN).equals(Constants.Workdays)) {
					message += Constants.MESSAGE_WORKDAYS;
				} else {
					message += Constants.MESSAGE_WEEKDAYS;
				}

				request.setAttribute(
						Constants.MESSAGE,
						"Daily posts were set between " + hourTemp1 + ":"
								+ request.getParameter(Constants.MINUTE_DROP_DOWN) + " - " + hourTemp2 + ":"
								+ request.getParameter(Constants.MINUTE_DROP_DOWN_2) + message + " .");
			} else {
				request.setAttribute(Constants.MESSAGE, new String("The scheduler was not updated <br><br>" + message));
			}

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
		} else if (request.getRequestURI().equals("/SocialMediaScheduler/Updates")) {
			String message = "";
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(getServletContext().getInitParameter(Constants.PATH_2)
						+ Constants.RESPONSE + Constants.TXT));
				String line;
				while ((line = br.readLine()) != null) {
					if (!line.trim().isEmpty() && line.startsWith(" ")) {
						message += Constants.LI_OPEN + line + Constants.LI_CLOSE;
					} else if (!line.trim().isEmpty()) {
						message += line + Constants.BR;
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
				try {
					br.close();
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}

			request.setAttribute(Constants.MESSAGE, message);
			RequestDispatcher view = request.getRequestDispatcher("Updates.jsp");
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
		threadScheduler = SchedulerThread.getInstance();
	}
}