package ro.project.servlet;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ro.project.scheduler.Scheduler;

public class PostedQuotesRetriever {
	private Scheduler scheduler;
	private int j = 0;
	// private String jString;
	private JSONObject jsonObject;
	private int total;
	private JSONArray updates;

	// private JSONObject update;
	// private PrintWriter out;

	public List<String> getPostedQuotes(int from, int indexesPerPage, String accessToken) {

		scheduler = new Scheduler(accessToken);
		/* List<String> quotes = new ArrayList<String>(); */
		List<String> quotesToBeDisplayed = new ArrayList<String>();

		try {

			List<String> temp = parse(from, indexesPerPage, accessToken);
			if (temp == null) {
				return null;
			} else {

				quotesToBeDisplayed.addAll(temp);

				/*
				 * jString = scheduler.getFacebookUpdates(1);
				 * quotes.addAll(parseJString(jString, "Facebook"));
				 * 
				 * jString = scheduler.getTwitterUpdates(1);
				 * quotes.addAll(parseJString(jString, "Twitter"));
				 * 
				 * for(int i=from; i<from+10; i++) {
				 * quotesToBeDisplayed.add(quotes.get(i)); }
				 */
				return quotesToBeDisplayed;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return quotesToBeDisplayed;
	}

	private List<String> parse(int from, int indexPerPage, String accessToken) throws JSONException {

		scheduler = new Scheduler(accessToken);
		List<String> quotes = new ArrayList<String>();
		// List<String> quotesToBeDisplayed = new ArrayList<String>();

		String jString = scheduler.getFacebookUpdates(1, scheduler.getProfileId("facebook"));
		if ((jString == null) || jString.trim().isEmpty()) {
			return null;
		} else {
			JSONObject jsonObject = new JSONObject(jString);
			int totalFacebook = jsonObject.getInt("total");
			jString = scheduler.getTwitterUpdates(1, scheduler.getProfileId("twitter"));
			jsonObject = new JSONObject(jString);
			int totalTwitter = jsonObject.getInt("total");
			total = totalFacebook + totalTwitter;

			// total/10 + 1 -> numarul de pagini
			// totalFacebook / 10 -> numarul de pagini complete de facebook
			// totalFacebook % 10 -> cate mesaje sunt pe pagina incompleta de
			// facebook
			// 10 - cate mesaje sunt pe pagina incompleta de facebook -> cate
			// mesaje
			// sunt pe pagina incompleta de facebook
			// totalTwitter - cate mesaje sunt pe pagina incompleta de facebook
			// ->
			// de completat cu twitter
			// de completat cu twitter / 10 -> numarul de pagini complete de
			// twitter
			// de completat cu twitter % 10 -> cate mesaje sunt pe ultima pagina

			// int numberOfPages = total/10;
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
				// 2 pagini per json
				// pagina x

				// pagina 1 -> pagina 1 din json prima parte
				// pagina 2 -> pagina 1 din json a doua parte
				// pagina 3 -> pagina 2 din json prima parte
				// pagina 4 -> pagina 2 din json a doua parte
				// pagina 5 -> pagina 3 din json prima parte
				// pagina 6 -> pagina 3 din json a doua parte

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

				jString = scheduler.getFacebookUpdates(jsonPage, scheduler.getProfileId("facebook"));
				quotes.addAll(parseJStringFromStartToEnd(jString, start, end));

			} else if ((currentPage == completedFacebookPages + 1) && (facebookMessagesRemaining != 0)) {
				// se completeaza cu ce a ramas din facebook
				// se adauga ce ramane pentru twitter

				int facebookPage = completedFacebookPages / 2 + 1;

				jString = scheduler.getFacebookUpdates(facebookPage, scheduler.getProfileId("facebook"));
				quotes.addAll(parseJStringFromStartToEnd(jString, 0, facebookMessagesRemaining));

				jString = scheduler.getTwitterUpdates(1, scheduler.getProfileId("twitter"));
				quotes.addAll(parseJStringFromStartToEnd(jString, 0, twitterToComplete));
			} else if (currentPage > completedFacebookPages + completeTwitterPages + numberOfSharedPages) {
				int beforeTweets = totalTwitter - twitterMessagesOnLastPage;

				// 48 inainte
				// 2
				int jsonPage = beforeTweets / 20 + 1;
				int usedFromLastJson = beforeTweets % 20;
				int remainingFromLastJson = 20 - usedFromLastJson;

				System.out.println();
				System.out.println();
				System.out.println(jsonPage);
				System.out.println(beforeTweets);
				System.out.println(usedFromLastJson);
				System.out.println(remainingFromLastJson);
				if ((remainingFromLastJson > twitterMessagesOnLastPage) || (remainingFromLastJson < 10)) {
					jString = scheduler.getTwitterUpdates(jsonPage, scheduler.getProfileId("twitter"));
					quotes.addAll(parseJStringFromStartToEnd(jString, usedFromLastJson, usedFromLastJson
							+ twitterMessagesOnLastPage));
				} else {
					jString = scheduler.getTwitterUpdates(jsonPage, scheduler.getProfileId("twitter"));
					quotes.addAll(parseJStringFromStartToEnd(jString, usedFromLastJson, 20));

					jString = scheduler.getTwitterUpdates(jsonPage + 1, scheduler.getProfileId("twitter"));
					quotes.addAll(parseJStringFromStartToEnd(jString, 0, 10 - (20 - usedFromLastJson)));
				}
			} else {

				// 0 - 8 pt pagina comuna
				// pagina 1
				// 9 -> 9 + 10 ->18
				// pagina 2
				// 19 -> 19 + 20 -> 28
				// pagina 3
				// 29 -> 29 + 30 -> 38
				// pagina 4
				// 39 -> 39 + 40 -> 48
				// pagina 5
				// 49 -> 49 + 50 -> 58
				// pagina 6
				// 59 -> 59 + 60 -> 68

				// facebookRemaining: 1
				// twitterToComplete: 9

				// se incepe din primul json de la pozitia la care am ramas,
				// adica
				// twitterToComplete

				// prima pagina <- facebookRemaining din prima parte a paginii 1
				// a
				// jsonului +
				// + twitterToComplete din a doua parte a paginii 1 a jsonului

				// a doua pagina <- facebookRemaining din a doua parte a paginii
				// 1 a
				// jsonului
				// + twitterToComplete din prima parte a paginii 2 a jsonului

				// a treia pagina <- facebookRemaining din prima parte a paginii
				// 2 a
				// jsonului
				// + twitterToComplete din a doua parte a paginii 2 a jsonului

				// a patra pagina <- facebookRemaining din a doua parte a
				// paginii 2
				// a jsonului
				// + twitterToComplete din prima parte a paginii 3 a jsonului

				// a cincia pagina <- facebookRemaining din a prima parte a
				// paginii
				// 3 a jsonului
				// + twitterToComplete din a doua parte a paginii 3 a jsonului

				// a sasea pagina <- facebookRemaining din a doua parte a
				// paginii 3
				// a jsonului
				// + twitterToComplete din prima parte a paginii 4 a jsonului

				int twitterPage = currentPage - completedFacebookPages - numberOfSharedPages;

				// case 1 - pagini impare
				// case 2 - pagini pare

				int scenario = twitterPage % 2;

				// twitterpage / 2 + 1

				int jsonPage;
				if (scenario == 1) {
					jsonPage = twitterPage / 2 + 1;
					jString = scheduler.getTwitterUpdates(jsonPage, scheduler.getProfileId("twitter"));
					// de la 9 la 18; ramane 19
					quotes.addAll(parseJStringFromStartToEnd(jString, twitterToComplete, 20 - facebookMessagesRemaining));
					// facebookRemaining din prima parte a paginii 1 a jsonului
					// +
					// + twitterToComplete din a doua parte a paginii 1 a
					// jsonului
				} else {
					jsonPage = twitterPage / 2;
					jString = scheduler.getTwitterUpdates(jsonPage, scheduler.getProfileId("twitter"));
					// 39 -> 39 + 40 -> 48
					quotes.addAll(parseJStringFromStartToEnd(jString, 10 + twitterToComplete, 20));
					jString = scheduler.getTwitterUpdates(jsonPage + 1, scheduler.getProfileId("twitter"));
					quotes.addAll(parseJStringFromStartToEnd(jString, 0, 10 - facebookMessagesRemaining));
					// facebookRemaining din a doua parte a paginii 1 a jsonului
					// + twitterToComplete din prima parte a paginii 2 a
					// jsonului
				}

			}

			// System.out.println("numberOfPages: " + numberOfPages);
			System.out.println("completedFacebookPages: " + completedFacebookPages);
			System.out.println("facebookRemaining: " + facebookMessagesRemaining);
			System.out.println("twitterToComplete: " + twitterToComplete);
			System.out.println("remainingTwitterMessage: " + remainingTwitterMessage);
			System.out.println("completeTwitterPages: " + completeTwitterPages);
			System.out.println("twitterMessagesOnLastPage: " + twitterMessagesOnLastPage);

			/*
			 * for(int i=0; i <quotes.size();i++) {
			 * System.out.println(quotes.get(i)); }
			 */
			return quotes;
		}

	}

	private Collection<? extends String> parseJStringFromStartToEnd(String jString, int start, int end)
			throws JSONException {
		List<String> quoteList = new ArrayList<String>();
		j = 1;
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
			String jString = scheduler.getFacebookUpdates(1, scheduler.getProfileId("facebook"));
			if ((jString == null) || (jString.trim().isEmpty())) {

			} else {
				JSONObject jsonObject = new JSONObject(jString);
				int totalFacebook = jsonObject.getInt("total");
				jString = scheduler.getTwitterUpdates(1, scheduler.getProfileId("twitter"));
				jsonObject = new JSONObject(jString);
				int totalTwitter = jsonObject.getInt("total");
				total = totalFacebook + totalTwitter;
			}
		} catch (Exception e) {
			e.printStackTrace();
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
