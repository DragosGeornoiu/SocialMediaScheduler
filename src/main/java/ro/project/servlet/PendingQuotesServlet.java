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
 * The pending updates on twitter and facebook are shown using this servlet.
 *
 */
public class PendingQuotesServlet extends HttpServlet {
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
		try {
			j = 1;
			String jString = scheduler.getFacebookPendingUpdates(j);
			JSONObject jsonObject = new JSONObject(jString);
			int total = jsonObject.getInt("total");
			JSONArray updates = jsonObject.getJSONArray("updates");
			for (int i = 0; i < total; i++) {
				if ((i % 20 == 0) && (i != 0)) {
					j++;
					jString = scheduler.getFacebookPendingUpdates(j);
					jsonObject = new JSONObject(jString);
					updates = jsonObject.getJSONArray("updates");
				}
				JSONObject update = updates.getJSONObject(i % 20);
				out.print("<BR>");
				out.print("Count: " + (i + 1) + "<BR>");
				out.print("Id: " + update.get("_id") + "<BR>");
				out.print("Day: " + update.get("day") + "<BR>");
				out.print("Due_time: " + update.get("due_time") + "<BR>");
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(new Long(((int)update.getInt("due_at")))*1000);
				int mYear = c.get(Calendar.YEAR);
				out.print("Year: " + mYear + "<BR>");
				out.print("Service: " + update.get("profile_service") + "<BR>");
				out.print("Text: " + update.get("text") + "<BR>");
				out.println("<form ACTION=\"DeletePending\">");
				out.println("<INPUT TYPE=\"hidden\" name=\"url\" value=" + update.get("_id") + ">");
				out.println("<input type=\"submit\" value=\"Delete\">");
				out.println("</form>");
				out.print("<BR>");
			}

			j = 1;
			jString = scheduler.getTwitterPendingUpdates(j);
			jsonObject = new JSONObject(jString);
			total = jsonObject.getInt("total");
			updates = jsonObject.getJSONArray("updates");
			for (int i = 0; i < total; i++) {
				if ((i % 20 == 0) && (i != 0)) {
					j++;
					jString = scheduler.getTwitterPendingUpdates(j);
					jsonObject = new JSONObject(jString);
					updates = jsonObject.getJSONArray("updates");
				}
				JSONObject update = updates.getJSONObject(i % 20);
				out.print("<BR>");
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(new Long(((int)update.getInt("due_at")))*1000);
				int mYear = c.get(Calendar.YEAR);
				out.print("Due at:: " + update.get("due_time") + "; " + update.get("day") + "; " + mYear + " <BR> ");
				out.print("Service: " + update.get("profile_service") + "<BR>");
				out.print("Text: " + update.get("text") + "<BR>");
				out.println("<form ACTION=\"DeletePending\">");
				out.println("<INPUT TYPE=\"hidden\" name=\"url\" value=" + update.get("_id") + ">");
				out.println("<input type=\"submit\" value=\"Delete\">");
				out.println("</form>");
				out.print("<BR>");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		out.print("</html>\n</body>");
	}
}