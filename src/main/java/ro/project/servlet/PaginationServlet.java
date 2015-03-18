package ro.project.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class EmployeeServlet
 */
public class PaginationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public PaginationServlet() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// aici trebuie sa iau parametrii din radiogroup sau dropdownlist
		// si sa ii pazes la dao

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

		String accessToken = request.getParameter("accessToken");
		PostedQuotesRetriever dao = new PostedQuotesRetriever();

		int page = 1;
		int recordsPerPage = 10;
		if (request.getParameter("page") != null)
			page = Integer.parseInt(request.getParameter("page"));
		List<String> list = dao.getPostedQuotes((page - 1) * recordsPerPage, recordsPerPage, accessToken, type,
				ascending);
		int noOfRecords = dao.getNoOfRecords();
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
	}
}
