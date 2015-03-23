package ro.project.servlet;

import java.util.ArrayList;
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

/**
 * @author Caphyon1
 * 
 *         Used for retrieving the posted updates.
 *
 */
public class PostedQuotesRetriever {
	final static Logger logger = Logger.getLogger(PostedQuotesRetriever.class);

	private Scheduler scheduler;
	private JSONObject jsonObject;
	private JSONArray updates;

	PostedQuotesRetriever(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public List<String> getPostedQuotes(String accessToken, int from, int indexesPerPage, String sortedBy,
			boolean ascending) {

		List<String> quotesToBeDisplayed = new ArrayList<String>();
		List<OrderObject> temp = null;

		try {
			temp = parse(accessToken);

			if (temp == null) {
				return null;
			} else  if(sortedBy != null){
				Comparator<OrderObject> comparator = new OrderObjectComparator(sortedBy, ascending);
				Collections.sort(temp, comparator);
			}
		} catch (Exception e) {
			logger.error("Problem retrieving posted quotes", e);
		}

		int end;
		if (from + indexesPerPage < temp.size()) {
			end = from + indexesPerPage;
		} else {
			end = temp.size();
		}
		for (int i = from; i < end; i++) {
			quotesToBeDisplayed.add(temp.get(i).toString());
		}

		return quotesToBeDisplayed;
	}

	private List<OrderObject> parse(String accessToken) throws JSONException {
		List<OrderObject> quotes = new ArrayList<OrderObject>();

		String jString = scheduler.getUpdatesFor(accessToken, 1,
				scheduler.getProfileId(accessToken, Constants.FACEBOOK));
		if ((jString == null) || jString.trim().isEmpty()) {
			return null;
		}
		JSONObject jsonObject = new JSONObject(jString);
		int totalFacebook = jsonObject.getInt(Constants.TOTAL);

		jString = scheduler.getUpdatesFor(accessToken, 1, scheduler.getProfileId(accessToken, Constants.TWITTER));
		if ((jString == null) || jString.trim().isEmpty()) {
			return null;
		}
		jsonObject = new JSONObject(jString);
		int totalTwitter = jsonObject.getInt(Constants.TOTAL);

		int iTwitter;
		int iFacebook;
		iFacebook = iTwitter = 1;
		int twitterEnd, facebookEnd;
		while (quotes.size() < 1000 && totalFacebook > 0 && totalTwitter > 0) {
			String twitterjString = scheduler.getUpdatesFor(accessToken, iTwitter,
					scheduler.getProfileId(accessToken, Constants.TWITTER));
			OrderObject twitterObject;
			if (totalTwitter - (iTwitter * Constants.UPDATES_PER_PAGE) > 19) {
				twitterEnd = Constants.UPDATES_PER_PAGE;
			} else {
				twitterEnd = totalTwitter % Constants.UPDATES_PER_PAGE;
			}
			twitterObject = parseJStringFromStartToEnd(twitterjString, 0, twitterEnd).get(twitterEnd-1);

			String facebookjString = scheduler.getUpdatesFor(accessToken, iFacebook,
					scheduler.getProfileId(accessToken, Constants.FACEBOOK));
			OrderObject facebookObject;
			if (totalFacebook - (iFacebook * Constants.UPDATES_PER_PAGE) >= 19) {
				facebookEnd = Constants.UPDATES_PER_PAGE;
			} else {
				facebookEnd = totalFacebook % Constants.UPDATES_PER_PAGE;
			}
			facebookObject = parseJStringFromStartToEnd(facebookjString, 0, facebookEnd).get(facebookEnd-1);

			if (twitterObject.getCalendar().compareTo(facebookObject.getCalendar()) < 0) {
				quotes.addAll(parseJStringFromStartToEnd(facebookjString, 0, facebookEnd));
				iFacebook++;
				totalFacebook -= (facebookEnd + 1);
			} else {
				quotes.addAll(parseJStringFromStartToEnd(twitterjString, 0, twitterEnd));
				iTwitter++;
				totalTwitter -= (twitterEnd + 1);
			}
			

		}

		if(quotes.size() < 1000) {
			if(totalFacebook > 0) {
				for (int i = 1; i <= (totalFacebook / Constants.UPDATES_PER_PAGE + 1); i++) {
					jString = scheduler.getUpdatesFor(accessToken, i + iFacebook - 1, scheduler.getProfileId(accessToken, Constants.FACEBOOK));
					if (totalFacebook - (i * Constants.UPDATES_PER_PAGE) > 0) {
						quotes.addAll(parseJStringFromStartToEnd(jString, 0, Constants.UPDATES_PER_PAGE));
					} else {
						quotes.addAll(parseJStringFromStartToEnd(jString, 0, (totalFacebook % Constants.UPDATES_PER_PAGE) - 1));
					}

				}
			} else if (totalTwitter > 0) {
				for (int i = 1; i <= (totalTwitter / Constants.UPDATES_PER_PAGE + 1); i++) {
					jString = scheduler.getUpdatesFor(accessToken, i + iTwitter - 1, scheduler.getProfileId(accessToken, Constants.TWITTER));
					if (totalTwitter - (i * Constants.UPDATES_PER_PAGE) > 0) {
						quotes.addAll(parseJStringFromStartToEnd(jString, 0, 20));
					} else {
						quotes.addAll(parseJStringFromStartToEnd(jString, 0, (totalTwitter % 20) - 1));
					}
					

				}
			}
		}
		
		return quotes;
	}

	private List<OrderObject> parseJStringFromStartToEnd(String jString, int start, int end) throws JSONException {
		List<OrderObject> quoteList = new ArrayList<OrderObject>();
		jsonObject = new JSONObject(jString);
		updates = jsonObject.getJSONArray("updates");

		for (int i = start; i < end; i++) {
			String quote = "";
			String author = "";
			JSONObject update = updates.getJSONObject(i);
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(new Long(((int) update.getInt("due_at"))) * 1000);
			try {
				quote = ((String) update.get("text")).split(" - ")[0];
				author = ((String) update.get("text")).split(" - ")[1];
			} catch (Exception e) {
				logger.error("Problem prasing quote", e);
			}

			OrderObject ord = new OrderObject(c, new Quote(quote, author),
					(String) update.get(Constants.PROFILE_SERVICE));
			quoteList.add(ord);

		}
		return quoteList;
	}

	public int getNoOfRecords(String accessToken) {
		int total = -1;
		try {
			String jString = scheduler.getUpdatesFor(accessToken, 1,
					scheduler.getProfileId(accessToken, Constants.FACEBOOK));
			if ((jString == null) || (jString.trim().isEmpty())) {

			} else {
				JSONObject jsonObject = new JSONObject(jString);
				int totalFacebook = jsonObject.getInt("total");
				jString = scheduler.getUpdatesFor(accessToken, 1,
						scheduler.getProfileId(accessToken, Constants.TWITTER));
				jsonObject = new JSONObject(jString);
				int totalTwitter = jsonObject.getInt("total");
				total = totalFacebook + totalTwitter;
			}
		} catch (Exception e) {
			logger.error("Problem retieving the number of total updates posted", e);
		}
		return total;
	}
}
