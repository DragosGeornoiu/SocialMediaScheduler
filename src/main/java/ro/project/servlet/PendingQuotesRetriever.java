package ro.project.servlet;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import ro.project.Constants;
import ro.project.scheduler.Scheduler;

public class PendingQuotesRetriever {
	final static Logger logger = Logger.getLogger(PostedQuotesRetriever.class);

	ServletToScheduler servletToScheduler;
	private Scheduler scheduler;

	PendingQuotesRetriever(Scheduler scheduler) {
		this.scheduler = scheduler;
		servletToScheduler = new ServletToScheduler(scheduler);
	}

	public List<String> getPendingQuotes(int from, int indexesPerPage) {

		List<String> quotesToBeDisplayed = new ArrayList<String>();
		List<String> temp = null;

		try {
			temp = servletToScheduler.getAllPendingQuotes();

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
