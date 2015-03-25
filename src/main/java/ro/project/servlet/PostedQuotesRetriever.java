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

import com.sun.corba.se.impl.orbutil.closure.Constant;

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

	public List<String> getPostedQuotes(int from, int indexesPerPage, String sortedBy, boolean ascending) {

		List<String> quotesToBeDisplayed = new ArrayList<String>();
		List<OrderObject> temp = null;

		try {
			temp = parse();

			if (temp == null) {
				return null;
			} else if (sortedBy != null) {
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

	private List<OrderObject> parse() throws JSONException {
		List<OrderObject> quotes = new ArrayList<OrderObject>();

		List<String> profiles = scheduler.getAllProfiles();
		List<Integer> total = Arrays.asList(new Integer[profiles.size()]);
		int totalSocialNetwork = 0;
		String jString;
		JSONObject jsonObject;
		for (int i = 0; i < profiles.size(); i++) {
			jString = scheduler
					.getUpdatesFor(1, scheduler.getProfileId(profiles.get(i).toLowerCase().replace(" ", "")));
			jsonObject = new JSONObject(jString);
			total.set(i, new Integer(jsonObject.getInt(Constants.TOTAL)));
			totalSocialNetwork += total.get(i);
		}

		List<Integer> iSocialNetworkList = Arrays.asList(new Integer[profiles.size()]);
		for (int i = 0; i < iSocialNetworkList.size(); i++) {
			iSocialNetworkList.set(i, 1);
		}

		List<OrderObject> orderObjectList = Arrays.asList(new OrderObject[profiles.size()]);

		while (quotes.size() < Constants.QUOTE_HISTORY_LIMIT && totalSocialNetwork > 0) {
			int end;
			for (int i = 0; i < profiles.size(); i++) {

				if (i == 0) {
					orderObjectList = new ArrayList<OrderObject>();
				}

				if (total.get(i) > 0) {

					jString = scheduler.getUpdatesFor(iSocialNetworkList.get(i),
							scheduler.getProfileId(profiles.get(i)));
					if (total.get(i) >= Constants.UPDATES_PER_PAGE) {
						end = Constants.UPDATES_PER_PAGE;
					} else {
						end = total.get(i) % Constants.UPDATES_PER_PAGE;
					}

					orderObjectList.add(parseJStringFromStartToEnd(jString, 0, end).get(end - 1));
				}
			}

			Comparator<OrderObject> comparator = new OrderObjectComparator(Constants.BY_DATE, false);
			Collections.sort(orderObjectList, comparator);

			int index = 0;
			String service = orderObjectList.get(0).getService();
			for (int i = 0; i < profiles.size(); i++) {

				if (profiles.get(i).equals(service) || profiles.get(i).startsWith(service)) {
					index = i;
					break;
				}
			}

			jString = scheduler.getUpdatesFor(iSocialNetworkList.get(index),
					scheduler.getProfileId(profiles.get(index)));

			if (total.get(index) >= Constants.UPDATES_PER_PAGE) {
				end = Constants.UPDATES_PER_PAGE;
			} else {
				end = total.get(index) % Constants.UPDATES_PER_PAGE;
			}
			quotes.addAll(0, parseJStringFromStartToEnd(jString, 0, end));
			iSocialNetworkList.set(index, iSocialNetworkList.get(index) + 1);
			totalSocialNetwork -= (end);
			total.set(index, total.get(index) - end);
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
				quote = ((String) update.get(Constants.TEXT)).split(" - ")[0];
				author = ((String) update.get(Constants.TEXT)).split(" - ")[1];
			} catch (Exception e) {
				logger.error("Problem prasing quote", e);
			}

			OrderObject ord = new OrderObject(c, new Quote(quote, author),
					(String) update.get(Constants.PROFILE_SERVICE));
			quoteList.add(ord);
		}
		return quoteList;
	}

	public int getNoOfRecords() {
		int totalSocialNetwork = 0;
		try {
			List<String> profiles = scheduler.getAllProfiles();
			String jString;
			JSONObject jsonObject;
			for (int i = 0; i < profiles.size(); i++) {
				jString = scheduler.getUpdatesFor(1,
						scheduler.getProfileId(profiles.get(i).toLowerCase().replace(" ", "")));
				jsonObject = new JSONObject(jString);
				totalSocialNetwork += jsonObject.getInt(Constants.TOTAL);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return totalSocialNetwork;
	}
}
