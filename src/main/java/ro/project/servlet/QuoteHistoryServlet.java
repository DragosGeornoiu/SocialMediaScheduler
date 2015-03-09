package ro.project.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import ro.project.scheduler.Scheduler;

/**
 * 
 * The updates posted on twitter and facebook are shown using this servlet.
 *
 */
public class QuoteHistoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Scheduler scheduler;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		scheduler = new Scheduler();
		int j;

		out.println("<html>\n <body>");
		out.println("<head>");
		out.print("<a href=\"http://localhost:8080/SocialMediaScheduler/parse\">Parse</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/Post\">Schedule Quote</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/QuoteHistory\">Quote History</a>");
		out.print("<br> <a href=\"http://localhost:8080/SocialMediaScheduler/PendingQuotes\">Pending Quotes</a><br><br>");
		out.print("</head>");

		// se calculeaza totalul pt. facebook si totalul. pt twitter
		// se face suma lor
		// se vede de cate pagini sunt necesare pt a le exprima 10 pe pagina

		/*try {
			if((request.getParameter("pageNumber") == null) || request.getParameter("pageNumber").trim().isEmpty()) {
				
			}
			int total = 0;
			String jString = scheduler.getFacebookUpdates(1);
			JSONObject jsonObject = new JSONObject(jString);
			total += jsonObject.getInt("total");
			jString = scheduler.getTwitterUpdates(1);
			jsonObject = new JSONObject(jString);
			total += jsonObject.getInt("total");
			
			int numberOfPages = total % 10 + 1;

		} catch (Exception e) {

		}*/

		
		
		
		
		try {

			j = 1;
			String jString = scheduler.getFacebookUpdates(j);
/*			out.print("<font color=\"red\">FACEBOOK</font>" + "<BR>");
*/			JSONObject jsonObject = new JSONObject(jString);
			int total = jsonObject.getInt("total");
			JSONArray updates = jsonObject.getJSONArray("updates");
			for (int i = 0; i < total; i++) {
				if ((i % 20 == 0) && (i != 0)) {
					j++;
					jString = scheduler.getFacebookUpdates(j);
					jsonObject = new JSONObject(jString);
					updates = jsonObject.getJSONArray("updates");
				}
				JSONObject update = updates.getJSONObject(i % 20);
				out.print("<BR>");
				out.print("Count: " + (i + 1) + "<BR>");
				/*out.print("Day: " + update.get("day") + "<BR>");
				out.print("Due_time: " + update.get("due_time") + "<BR>");
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(new Long(((int) update.get("due_at"))) * 1000);
				int mYear = c.get(Calendar.YEAR);
				out.print("Year: " + mYear + "<BR>");
				out.print("Service: " + update.get("profile_service") + "<BR>");
				out.print("Text: " + update.get("text") + "<BR>");
				out.print("<BR>");*/
				
				
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(new Long(((int) update.get("due_at"))) * 1000);
				int mYear = c.get(Calendar.YEAR);
				out.print("Due at: " + update.get("due_time") + "; " + update.get("day") + "; " + mYear + "<BR>");
				out.print("Service: " + update.get("profile_service") + "<BR>");
				out.print("Text: " + update.get("text") + "<BR>");
				out.print("<BR>");
			}

			j = 1;
			jString = scheduler.getTwitterUpdates(j);
/*			out.print("<font color=\"red\">TWITTER</font>" + "<BR>");
*/			jsonObject = new JSONObject(jString);
			total = jsonObject.getInt("total");
			updates = jsonObject.getJSONArray("updates");
			for (int i = 0; i < total; i++) {
				if ((i % 20 == 0) && (i != 0)) {
					j++;
					jString = scheduler.getTwitterUpdates(j);
					jsonObject = new JSONObject(jString);
					updates = jsonObject.getJSONArray("updates");
				}
				JSONObject update = updates.getJSONObject(i % 20);
				out.print("<BR>");
				out.print("Count: " + (i + 1) + "<BR>");
		/*		out.print("Id: " + update.get("_id") + "<BR>");
				out.print("Day: " + update.get("day") + "<BR>");
				out.print("Due_time: " + update.get("due_time") + "<BR>");
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(new Long(((int) update.get("due_at"))) * 1000);
				int mYear = c.get(Calendar.YEAR);
				out.print("Year: " + mYear + "<BR>");
				out.print("Service: " + update.get("profile_service") + "<BR>");
				out.print("Text: " + update.get("text") + "<BR>");
				out.print("<BR>");*/
				
				
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(new Long(((int) update.get("due_at"))) * 1000);
				int mYear = c.get(Calendar.YEAR);
				out.print("Due at: " + update.get("due_time") + "; " + update.get("day") + "; " + mYear + "<BR>");
				out.print("Service: " + update.get("profile_service") + "<BR>");
				out.print("Text: " + update.get("text") + "<BR>");
				out.print("<BR>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.print("</html>\n</body>");
	}
}