package ro.project.servlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ro.project.Constants;
import ro.project.scheduler.Quote;
import ro.project.scheduler.Scheduler;

public class PendingQuotesRetriever {
	final static Logger logger = Logger.getLogger(PostedQuotesRetriever.class);

	ServletToScheduler servletToScheduler;
	private Scheduler scheduler;
	private JSONObject jsonObject;
	private JSONArray updates;

	PendingQuotesRetriever(Scheduler scheduler) {
		this.scheduler = scheduler;
		servletToScheduler = new ServletToScheduler(scheduler);
	}

	public List<String> getPendingQuotes(int from, int indexesPerPage) {

		List<String> quotesToBeDisplayed = new ArrayList<String>();
		List<String> temp = null;

		try {
			/*out += "<table border=\"1\" style=\"width:100%;\" cellpadding=\"5\" cellspacing=\"5\">";
			out += "<tr bgcolor=\"#d3d3d3\">";
			out += "<td>Due at</td>";
			out += "<td>Service</td>";
			out += "<td>Text</td>";
			out += "<td>Delete</td>";
			out += "</tr>";*/
			temp = servletToScheduler.getAllPendingQuotes();
			
		/*	out += "</table>";
			out += "</html>\n</body>";*/
			

			if (temp == null) {
				return null;
			}
			
			int end;
			if (from + indexesPerPage < temp.size()) {
				end = from + indexesPerPage;
			} else {
				end = temp.size();
			}
			
			for (int i = from; i < end; i++) {
				quotesToBeDisplayed.add(temp.get(i));
			}
		} catch (Exception e) {
			logger.info("Something went wrong retrieving pending quotes");
		}
			return quotesToBeDisplayed;
	}

	public int getNoOfRecords() {
		List<String> profiles = scheduler.getAllProfiles();

		int totalFromAllSocialNetworks = 0;
		String jString;
		JSONObject jsonObject;
		for(int i =0; i<profiles.size(); i++) {
			try {
			jString = scheduler.getPendingUpdates(1, scheduler.getProfileId(profiles.get(i).toLowerCase().replaceAll(" ", "")));
			jsonObject = new JSONObject(jString);
			totalFromAllSocialNetworks += jsonObject.getInt(Constants.TOTAL);
			} catch(Exception e) {
				logger.info("Something went wrong etrieving no. of pending quotes.");
			}
		}
		return totalFromAllSocialNetworks;
		
	}
}
