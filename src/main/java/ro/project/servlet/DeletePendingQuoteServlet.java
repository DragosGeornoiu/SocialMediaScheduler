package ro.project.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ro.project.scheduler.Scheduler;

/**
 * @author Caphyon1
 * 
 * Used for deleting a pending update.
 *
 */
public class DeletePendingQuoteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Scheduler scheduler;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		scheduler = new Scheduler(request.getParameter("accessToken"));
		scheduler.deleteUpdate(request.getParameter("url"));
		response.sendRedirect("http://localhost:8080/SocialMediaScheduler/PendingQuotes?accessToken="
				+ request.getParameter("accessToken"));

	}
}