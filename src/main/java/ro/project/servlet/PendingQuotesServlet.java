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
 * 
 * PendingQuotesServlet returns the pending updates.
 *
 */
public class PendingQuotesServlet extends HttpServlet {
	final static Logger logger = Logger.getLogger(PendingQuotesServlet.class);
	private static final long serialVersionUID = 1L;
	private Scheduler scheduler;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String accessToken = request.getParameter("accessToken");
		PrintWriter out = response.getWriter();
		scheduler = new Scheduler(accessToken);
		int j;

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

		try {
			if ((scheduler.getPendingUpdates(1, scheduler.getProfileId("facebook")) == null)
					|| (scheduler.getPendingUpdates(1, scheduler.getProfileId("facebook")).trim().isEmpty())) {

				out.println("Something went wrong. The access token might be the problem...");
			} else {
				String jString = scheduler.getPendingUpdates(1, scheduler.getProfileId("facebook"));
				JSONObject jsonObject = new JSONObject(jString);
				int totalFacebook = jsonObject.getInt("total");

				jString = scheduler.getPendingUpdates(1, scheduler.getProfileId("twitter"));
				jsonObject = new JSONObject(jString);
				int totalTwitter = jsonObject.getInt("total");
				out.println("<html>\n <body>");

				if ((totalFacebook == 0) && (totalTwitter == 0)) {
					out.print("<BR> There are no pending quotes...");
				} else {

					j = 1;
					jString = scheduler.getPendingUpdates(j, scheduler.getProfileId("facebook"));
					jsonObject = new JSONObject(jString);
					int total = jsonObject.getInt("total");
					JSONArray updates = jsonObject.getJSONArray("updates");
					for (int i = 0; i < total; i++) {
						if ((i % 20 == 0) && (i != 0)) {
							j++;
							jString = scheduler.getPendingUpdates(j, scheduler.getProfileId("facebook"));
							jsonObject = new JSONObject(jString);
							updates = jsonObject.getJSONArray("updates");
						}
						JSONObject update = updates.getJSONObject(i % 20);
						print(update, out, accessToken);
					}

					j = 1;
					jString = scheduler.getPendingUpdates(j, scheduler.getProfileId("twitter"));
					jsonObject = new JSONObject(jString);
					total = jsonObject.getInt("total");
					updates = jsonObject.getJSONArray("updates");
					for (int i = 0; i < total; i++) {
						if ((i % 20 == 0) && (i != 0)) {
							j++;
							jString = scheduler.getPendingUpdates(j, scheduler.getProfileId("twitter"));
							jsonObject = new JSONObject(jString);
							updates = jsonObject.getJSONArray("updates");
						}
						JSONObject update = updates.getJSONObject(i % 20);
						print(update, out, accessToken);

					}

				}

				out.print("</html>\n</body>");
			}
		} catch (Exception e) {
			logger.error("Problem retrieving scheduled updates", e);
		}
	}

	private void print(JSONObject update, PrintWriter out, String accessToken) throws JSONException {
		out.print("<BR>");
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(new Long(((int) update.getInt("due_at"))) * 1000);
		int mYear = c.get(Calendar.YEAR);
		out.print("Due at:: " + update.get("due_time") + "; " + update.get("day") + "; " + mYear + " <BR> ");
		out.print("Service: " + update.get("profile_service") + "<BR>");
		out.print("Text: " + update.get("text") + "<BR>");
		out.println("<form ACTION=\"DeletePending\">");
		out.println("<INPUT TYPE=\"hidden\" name=\"accessToken\" value=" + accessToken + ">");
		out.println("<INPUT TYPE=\"hidden\" name=\"url\" value=" + update.get("_id") + ">");
		out.println("<input type=\"submit\" value=\"Delete\">");
		out.println("</form>");
		out.print("<BR>");
	}
}