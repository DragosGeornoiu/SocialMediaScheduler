package ro.project.servlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		List<OrderObject> orderObjectList = new ArrayList<OrderObject>();
		List<Integer> iSocialNetworkList = Arrays.asList(new Integer[profiles.size()]);
		int totalSocialNetwork = 0;
		String jString;
		JSONObject jsonObject;
		Map<Integer, List<OrderObject>> map = new HashMap<Integer, List<OrderObject>>();
		int end;

		// se calculeaza nr. total de update-uri si nr. total de update-uri pt. fiecare profil.
		// se initiaza cu 1 nr-ul paginii de json.
		// se ia primul jstring pt. fiecare profil.
		for (int i = 0; i < profiles.size(); i++) {
			jString = scheduler
					.getUpdatesFor(1, scheduler.getProfileId(profiles.get(i).toLowerCase().replace(" ", "")));
			jsonObject = new JSONObject(jString);
			total.set(i, new Integer(jsonObject.getInt(Constants.TOTAL)));
			totalSocialNetwork += total.get(i);
			
			iSocialNetworkList.set(i, 1);
			
			if (total.get(i) > 0) {
				jString = scheduler.getUpdatesFor(iSocialNetworkList.get(i), scheduler.getProfileId(profiles.get(i)));
				if (total.get(i) >= Constants.UPDATES_PER_PAGE) {
					end = Constants.UPDATES_PER_PAGE;
				} else {
					end = total.get(i) % Constants.UPDATES_PER_PAGE;
				}

				map.put(i, parseJStringFromStartToEnd(jString, 0, end));
				orderObjectList.add(map.get(i).get(end - 1));
			}
			
		}

		while (quotes.size() < Constants.QUOTE_HISTORY_LIMIT && totalSocialNetwork > 0) {
			// verific care e cel mai recent
			Comparator<OrderObject> comparator = new OrderObjectComparator(Constants.BY_DATE, false);
			Collections.sort(orderObjectList, comparator);

			// se gaseste index-ul din liste al profilului cu cel mai recent
			// ultim update de pe fiecare profil
			int index = 0;
			String service = orderObjectList.get(0).getService();
			for (int i = 0; i < profiles.size(); i++) {
				if (profiles.get(i).equals(service) || profiles.get(i).startsWith(service)) {
					index = i;
					break;
				}
			}

			quotes.addAll(map.get(index));
			iSocialNetworkList.set(index, iSocialNetworkList.get(index) + 1);
			totalSocialNetwork -= (map.get(index).size());
			total.set(index, total.get(index) - map.get(index).size());

			// pt. cel mai recent iau urmatorul jstring si orderedObjectul
			if (total.get(index) > 0) {
				jString = scheduler.getUpdatesFor(iSocialNetworkList.get(index),
						scheduler.getProfileId(profiles.get(index)));
				if (total.get(index) >= Constants.UPDATES_PER_PAGE) {
					end = Constants.UPDATES_PER_PAGE;
				} else {
					end = total.get(index) % Constants.UPDATES_PER_PAGE;
				}

				map.put(index, parseJStringFromStartToEnd(jString, 0, end));
				orderObjectList.remove(0);
				orderObjectList.add((map.get(index)).get(map.get(index).size() - 1));
			} else {
				orderObjectList.remove(0);
			}
		}

		return quotes;
	}

	private List<OrderObject> parseJStringFromStartToEnd(String jString, int start, int end) throws JSONException {
		List<OrderObject> quoteList = new ArrayList<OrderObject>();
		jsonObject = new JSONObject(jString);
		updates = jsonObject.getJSONArray(Constants.UPDATES);

		for (int i = start; i < end; i++) {
			String quote = "";
			String author = "";
			JSONObject update = updates.getJSONObject(i);
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(new Long(((int) update.getInt(Constants.DUE_AT))) * 1000);
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
