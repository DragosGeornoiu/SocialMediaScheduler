package ro.project.servlet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ro.project.scheduler.QuoteManager;
import ro.project.scheduler.Scheduler;

public class PostedQuotesRetriever {
	final static Logger logger = Logger.getLogger(PostedQuotesRetriever.class);

	private Scheduler scheduler;
	private JSONObject jsonObject;
	private JSONArray updates;

	public List<String> getPostedQuotes(int from, int indexesPerPage, String accessToken) {

		scheduler = new Scheduler(accessToken);
		List<String> quotesToBeDisplayed = new ArrayList<String>();

		try {

			List<String> temp = parse(from, indexesPerPage, accessToken);
			if (temp == null) {
				return null;
			} else {
				quotesToBeDisplayed.addAll(temp);
				return quotesToBeDisplayed;
			}
		} catch (Exception e) {
			logger.error("Problem retrieving posted quotes", e);
		}
		return quotesToBeDisplayed;
	}

	private List<String> parse(int from, int indexPerPage, String accessToken) throws JSONException {

		scheduler = new Scheduler(accessToken);
		List<String> quotes = new ArrayList<String>();
		// List<String> quotesToBeDisplayed = new ArrayList<String>();

		String jString = scheduler.getUpdatesFor(1, scheduler.getProfileId("facebook"));
		if ((jString == null) || jString.trim().isEmpty()) {
			return null;
		} else {
			JSONObject jsonObject = new JSONObject(jString);
			int totalFacebook = jsonObject.getInt("total");
			jString = scheduler.getUpdatesFor(1, scheduler.getProfileId("twitter"));
			jsonObject = new JSONObject(jString);
			int totalTwitter = jsonObject.getInt("total");
			int completedFacebookPages = totalFacebook / 10;
			int facebookMessagesRemaining = totalFacebook % 10;
			int twitterToComplete = 10 - facebookMessagesRemaining;
			int remainingTwitterMessage = totalTwitter - twitterToComplete;
			int completeTwitterPages = remainingTwitterMessage / 10;
			int twitterMessagesOnLastPage = remainingTwitterMessage % 10;

			// can only be 0 or 1
			int numberOfSharedPages = 0;
			if (facebookMessagesRemaining != 0)
				numberOfSharedPages = 1;

			int currentPage = from / 10 + 1;
			if (currentPage < completedFacebookPages) {
				int jsonPage;
				int start, end;
				if (currentPage % 2 == 0) {
					jsonPage = currentPage / 2 + 1;
					start = 10;
					end = 20;
				} else {
					jsonPage = currentPage / 2;
					start = 0;
					end = 10;
				}

				jString = scheduler.getUpdatesFor(jsonPage, scheduler.getProfileId("facebook"));
				quotes.addAll(parseJStringFromStartToEnd(jString, start, end));

			} else if ((currentPage == completedFacebookPages + 1) && (facebookMessagesRemaining != 0)) {
				int facebookPage = completedFacebookPages / 2 + 1;

				jString = scheduler.getUpdatesFor(facebookPage, scheduler.getProfileId("facebook"));
				quotes.addAll(parseJStringFromStartToEnd(jString, 0, facebookMessagesRemaining));

				jString = scheduler.getUpdatesFor(1, scheduler.getProfileId("twitter"));
				quotes.addAll(parseJStringFromStartToEnd(jString, 0, twitterToComplete));
			} else if (currentPage > completedFacebookPages + completeTwitterPages + numberOfSharedPages) {
				int beforeTweets = totalTwitter - twitterMessagesOnLastPage;
				int jsonPage = beforeTweets / 20 + 1;
				int usedFromLastJson = beforeTweets % 20;
				int remainingFromLastJson = 20 - usedFromLastJson;

				if ((remainingFromLastJson > twitterMessagesOnLastPage) || (remainingFromLastJson < 10)) {
					jString = scheduler.getUpdatesFor(jsonPage, scheduler.getProfileId("twitter"));
					quotes.addAll(parseJStringFromStartToEnd(jString, usedFromLastJson, usedFromLastJson
							+ twitterMessagesOnLastPage));
				} else {
					jString = scheduler.getUpdatesFor(jsonPage, scheduler.getProfileId("twitter"));
					quotes.addAll(parseJStringFromStartToEnd(jString, usedFromLastJson, 20));

					jString = scheduler.getUpdatesFor(jsonPage + 1, scheduler.getProfileId("twitter"));
					quotes.addAll(parseJStringFromStartToEnd(jString, 0, 10 - (20 - usedFromLastJson)));
				}
			} else {
				int twitterPage = currentPage - completedFacebookPages - numberOfSharedPages;
				int scenario = twitterPage % 2;
				int jsonPage;
				if (scenario == 1) {
					jsonPage = twitterPage / 2 + 1;
					jString = scheduler.getUpdatesFor(jsonPage, scheduler.getProfileId("twitter"));

				} else {
					jsonPage = twitterPage / 2;
					jString = scheduler.getUpdatesFor(jsonPage, scheduler.getProfileId("twitter"));
					quotes.addAll(parseJStringFromStartToEnd(jString, 10 + twitterToComplete, 20));
					jString = scheduler.getUpdatesFor(jsonPage + 1, scheduler.getProfileId("twitter"));
					quotes.addAll(parseJStringFromStartToEnd(jString, 0, 10 - facebookMessagesRemaining));
				}

			}
			return quotes;
		}

	}

	private Collection<? extends String> parseJStringFromStartToEnd(String jString, int start, int end)
			throws JSONException {
		List<String> quoteList = new ArrayList<String>();
		jsonObject = new JSONObject(jString);
		updates = jsonObject.getJSONArray("updates");

		for (int i = start; i < end; i++) {
			String result = "";
			JSONObject update = updates.getJSONObject(i);
			result += " <BR> ";
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(new Long(((int) update.getInt("due_at"))) * 1000);
			int mYear = c.get(Calendar.YEAR);
			result += " Due at: " + update.get("due_time") + "; " + update.get("day") + "; " + mYear + " <BR> ";
			result += " Service: " + update.get("profile_service") + " <BR> ";
			result += " Text: " + update.get("text") + "<BR>";
			result += " <BR> ";
			quoteList.add(result);

		}
		return quoteList;
	}

	public int getNoOfRecords() {
		int total = -1;
		try {
			String jString = scheduler.getUpdatesFor(1, scheduler.getProfileId("facebook"));
			if ((jString == null) || (jString.trim().isEmpty())) {

			} else {
				JSONObject jsonObject = new JSONObject(jString);
				int totalFacebook = jsonObject.getInt("total");
				jString = scheduler.getUpdatesFor(1, scheduler.getProfileId("twitter"));
				jsonObject = new JSONObject(jString);
				int totalTwitter = jsonObject.getInt("total");
				total = totalFacebook + totalTwitter;
			}
		} catch (Exception e) {
			logger.error("Problem retieving the number of total updates posted", e);
		}
		return total;
	}

	/*
	 * private List<String> parseJString(String jString, String socialNetwork)
	 * throws JSONException { List<String> quoteList = new ArrayList<String>();
	 * j = 1; jsonObject = new JSONObject(jString); total =
	 * jsonObject.getInt("total"); updates = jsonObject.getJSONArray("updates");
	 * for (int i = 0; i < total; i++) { if ((i % 20 == 0) && (i != 0)) { j++;
	 * if(socialNetwork.equals("Twitter")) { jString =
	 * scheduler.getTwitterUpdates(j); } else
	 * if(socialNetwork.equals("Facebook")) { jString =
	 * scheduler.getFacebookUpdates(j); } jsonObject = new JSONObject(jString);
	 * updates = jsonObject.getJSONArray("updates"); } String result = "";
	 * JSONObject update = updates.getJSONObject(i % 20); result += " <BR> ";
	 * Calendar c = Calendar.getInstance(); c.setTimeInMillis(new Long(((int)
	 * update.getInt("due_at"))) * 1000); int mYear = c.get(Calendar.YEAR);
	 * result += " Due at: " + update.get("due_time") + "; " + update.get("day")
	 * + "; " + mYear + " <BR> "; result += " Service: " +
	 * update.get("profile_service") + " <BR> "; result += " Text: " +
	 * update.get("text") + "<BR>"; result += " <BR> "; quoteList.add(result);
	 * 
	 * } return quoteList; }
	 */

	/*
	 * private Collection<? extends String> parseJString(String jString, int
	 * part, int remaining) throws JSONException { List<String> quoteList = new
	 * ArrayList<String>(); j = 1; jsonObject = new JSONObject(jString); updates
	 * = jsonObject.getJSONArray("updates");
	 * 
	 * int start, end;
	 * 
	 * if(part == 1) { start = 0; end = remaining;
	 * 
	 * } else { start = 10; end = 10 + remaining; } for (int i = start; i < end;
	 * i++) { String result = ""; JSONObject update = updates.getJSONObject(i);
	 * result += " <BR> "; Calendar c = Calendar.getInstance();
	 * c.setTimeInMillis(new Long(((int) update.getInt("due_at"))) * 1000); int
	 * mYear = c.get(Calendar.YEAR); result += " Due at: " +
	 * update.get("due_time") + "; " + update.get("day") + "; " + mYear +
	 * " <BR> "; result += " Service: " + update.get("profile_service") +
	 * " <BR> "; result += " Text: " + update.get("text") + "<BR>"; result +=
	 * " <BR> "; quoteList.add(result);
	 * 
	 * } return quoteList; }
	 */

}
