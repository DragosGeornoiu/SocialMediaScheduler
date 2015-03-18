package ro.project.servlet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ro.project.scheduler.Quote;
import ro.project.scheduler.QuoteManager;
import ro.project.scheduler.Scheduler;

public class PostedQuotesRetriever {
	final static Logger logger = Logger.getLogger(PostedQuotesRetriever.class);

	private Scheduler scheduler;
	private JSONObject jsonObject;
	private JSONArray updates;

	public List<String> getPostedQuotes(int from, int indexesPerPage, String accessToken, String sortedBy,
			boolean ascending) {

		scheduler = new Scheduler(accessToken);
		List<String> quotesToBeDisplayed = new ArrayList<String>();
		List<OrderObject> temp = null;

		try {
			temp = parse(accessToken);

			if (temp == null) {
				return null;
			} else {

				if (sortedBy.equals("byDate") && ascending == true) {
					// sort list by calendar, ascending
					Collections.sort(temp, new Comparator<OrderObject>() {
						public int compare(OrderObject one, OrderObject two) {
							return one.getCalendar().compareTo(two.getCalendar());
						}
					});

				} else if (sortedBy.equals("byDate") && ascending == false) {
					// sort list by calendar, descending
					Collections.sort(temp, new Comparator<OrderObject>() {
						public int compare(OrderObject one, OrderObject two) {
							return -one.getCalendar().compareTo(two.getCalendar());
						}
					});
				} else if (sortedBy.equals("byAuthor") && ascending == true) {
					// sort list by author, ascending
					Collections.sort(temp, new Comparator<OrderObject>() {
						public int compare(OrderObject one, OrderObject two) {
							return one.getQuote().getAuthor().compareToIgnoreCase(two.getQuote().getAuthor());
						}
					});

				} else if (sortedBy.equals("byAuthor") && ascending == false) {
					// sort list by author, descending
					Collections.sort(temp, new Comparator<OrderObject>() {
						public int compare(OrderObject one, OrderObject two) {
							return -one.getQuote().getAuthor().compareToIgnoreCase(two.getQuote().getAuthor());
						}
					});
				} else if (sortedBy.equals("byQuote") && ascending == true) {
					// sort list by quote, ascending
					Collections.sort(temp, new Comparator<OrderObject>() {
						public int compare(OrderObject one, OrderObject two) {
							return one.getQuote().getQuote().compareToIgnoreCase(two.getQuote().getQuote());
						}
					});
				} else if (sortedBy.equals("byQuote") && ascending == false) {
					// sort list by quote, descending
					Collections.sort(temp, new Comparator<OrderObject>() {
						public int compare(OrderObject one, OrderObject two) {
							return -one.getQuote().getQuote().compareToIgnoreCase(two.getQuote().getQuote());
						}
					});
				}

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

		scheduler = new Scheduler(accessToken);
		List<OrderObject> quotes = new ArrayList<OrderObject>();

		// alea patru cazuri sa nu le uiti, dar momentan sunt neglijate,
		// totalTwitter
		// si totalFacebook sunt ambele sub 500
		String jString = scheduler.getUpdatesFor(1, scheduler.getProfileId("facebook"));
		if ((jString == null) || jString.trim().isEmpty()) {
			return null;
		}
		JSONObject jsonObject = new JSONObject(jString);
		int totalFacebook = jsonObject.getInt("total");

		jString = scheduler.getUpdatesFor(1, scheduler.getProfileId("twitter"));
		if ((jString == null) || jString.trim().isEmpty()) {
			return null;
		}
		jsonObject = new JSONObject(jString);
		int totalTwitter = jsonObject.getInt("total");

		
		int endTwitter = 0;
		int endFacebook =0;
		if (totalFacebook < 500 && totalTwitter < 500) {
			endTwitter = totalTwitter;
			endFacebook = totalFacebook;
		} else if (totalFacebook > 500 && totalTwitter > 500) {
			endTwitter = 500;
			endFacebook = 500;
		} else if (totalFacebook > 500 && totalTwitter < 500) {
			// 750 si 450
			endTwitter = totalTwitter;
			if(totalFacebook + totalTwitter < 1000) {
				endFacebook = totalFacebook;
			} else {
				endFacebook = 500 + (500 - totalTwitter);
			}

		} else if (totalFacebook < 500 && totalTwitter > 500) {
			endFacebook = totalFacebook;
			if(totalFacebook + totalTwitter < 1000) {
				endTwitter = totalTwitter;
			} else {
				endTwitter = 500 + (500 - totalFacebook);
			}

		}
	
		
		for (int i = 1; i <= (endTwitter / 20 + 1); i++) {
			jString = scheduler.getUpdatesFor(i, scheduler.getProfileId("twitter"));

			if (endTwitter - (i * 20) > 0) {
				quotes.addAll(parseJStringFromStartToEnd(jString, 0, 19));
			} else {
				quotes.addAll(parseJStringFromStartToEnd(jString, 0, totalTwitter % 20));
			}

		}

		for (int i = 1; i <= (endFacebook / 20 + 1); i++) {
			jString = scheduler.getUpdatesFor(i, scheduler.getProfileId("facebook"));

			if (endFacebook - (i * 20) > 0) {
				quotes.addAll(parseJStringFromStartToEnd(jString, 0, 19));
			} else {
				quotes.addAll(parseJStringFromStartToEnd(jString, 0, totalFacebook % 20));
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

			OrderObject ord = new OrderObject(c, new Quote(quote, author), (String) update.get("profile_service"));
			/* System.out.println(ord); */
			quoteList.add(ord);

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
