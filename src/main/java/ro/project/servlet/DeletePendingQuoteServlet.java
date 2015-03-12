package ro.project.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ro.project.scheduler.Scheduler;

public class DeletePendingQuoteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Scheduler scheduler;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		scheduler = new Scheduler();
		
		scheduler.deleteUpdate(request.getParameter("url"));
		response.sendRedirect("PendingQuotes");

	}
}