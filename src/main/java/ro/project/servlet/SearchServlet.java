package ro.project.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ro.project.scheduler.Scheduler;

/**
 * @author Caphyon1
 * 
 * Used for searching for the quotes of a specific author.
 *
 */
public class SearchServlet extends HttpServlet {
	final static Logger logger = Logger.getLogger(SearchServlet.class);
	private static final long serialVersionUID = 1L;
	private Scheduler scheduler;
	private int j = 0;
	private JSONObject jsonObject;
	private int total;
	private JSONArray updates;
	private PrintWriter out;

	private String author;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String accessToken = "";
		if(request.getParameter("accessToken") != null)
			accessToken = request.getParameter("accessToken");
		scheduler = new Scheduler(accessToken);
		out = response.getWriter();
		
		out.println("<html>");
		out.println("<body>");
		
		
		
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
		
		
		
		out.println("<form ACTION=\"search\">");
		out.println("Insert author: <INPUT TYPE=\"text\" name=\"author\"> ");
		out.println("<INPUT TYPE=\"hidden\" name=\"accessToken\" value = " + request.getParameter("accessToken") + "> ");
		out.println("<input type=\"submit\">;");
		out.println("</form>");
		
		
		if (request.getParameter("author") == null) {
			out.println("<br> <br> Please enter an author <br> <br>");
		} else {
			author = request.getParameter("author");
			String jString;
			try {
				out.println("<br> <br> Author: " + author +" <br> <br>");
				jString = scheduler.getUpdatesFor(1, scheduler.getProfileId("facebook"));
				out.println(parseJString(jString, "Facebook"));
				jString = scheduler.getUpdatesFor(1, scheduler.getProfileId("twitter"));
				out.println(parseJString(jString, "Twitter"));
			} catch (JSONException e) {
				//e.printStackTrace();
				logger.error("Problem retrieving all the posted updates", e);
			}
		}
		
	}
	
	private String parseJString(String jString, String socialNetwork) throws JSONException {
		String quotesByAuthor = "";
		
		
		j = 1;
		jsonObject = new JSONObject(jString);
		total = jsonObject.getInt("total");
		updates = jsonObject.getJSONArray("updates");
		for (int i = 0; i < total; i++) {
			if ((i % 20 == 0) && (i != 0)) {
				j++;
				jString = scheduler.getUpdatesFor(j, scheduler.getProfileId(socialNetwork));
				jsonObject = new JSONObject(jString);
				updates = jsonObject.getJSONArray("updates");
			}
			JSONObject update = updates.getJSONObject(i % 20);

			String temp = "";
			try {
				temp =((String) update.get("text")).split(" - ")[1];
			} catch (Exception e) {
				//e.printStackTrace();
				logger.error("Problem splitting the text.", e);
			}
			if(temp.equals(author)) {
				quotesByAuthor += " <br> ";
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(new Long(((int) update.getInt("due_at"))) * 1000);
				int mYear = c.get(Calendar.YEAR);
				quotesByAuthor += "Due at: " + update.get("due_time") + "; " + update.get("day") + "; " + mYear + "<BR>";
				quotesByAuthor += "Service: " + update.get("profile_service") + "<BR>";
				quotesByAuthor += "Text: " + update.get("text") + "<BR>";
				quotesByAuthor += " <br> ";
			}
		}
		return quotesByAuthor;
	}
}
