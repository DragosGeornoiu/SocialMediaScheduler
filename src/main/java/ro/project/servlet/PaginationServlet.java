package ro.project.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import ro.project.scheduler.Scheduler;
 
/**
 * Servlet implementation class EmployeeServlet
 */
public class PaginationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
   
    
    public PaginationServlet() {
            super();
    }
 
    public void doGet(HttpServletRequest request, 
            HttpServletResponse response) 
            throws ServletException, IOException {
    	
		PostedQuotesRetriever dao = new PostedQuotesRetriever();
		
        int page = 1;
        int recordsPerPage = 10;
        if(request.getParameter("page") != null)
            page = Integer.parseInt(request.getParameter("page"));
        List<String> list = dao.getPostedQuotes((page-1)*recordsPerPage,
                                 recordsPerPage);
        int noOfRecords = dao.getNoOfRecords();
        int noOfPages = (int) Math.ceil(noOfRecords * 1.0 / recordsPerPage);
        request.setAttribute("quotesList", list);
        request.setAttribute("noOfPages", noOfPages);
        request.setAttribute("currentPage", page);
        RequestDispatcher view = request.getRequestDispatcher("displayQuotes.jsp");
        view.forward(request, response);
    }
    
}
