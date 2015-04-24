package ro.project.servlet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ro.project.Constants;
import ro.project.parser.BrainyQuoteParser;
import ro.project.parser.FileManager;
import ro.project.parser.Parser;
import ro.project.parser.PersdevParser;
import ro.project.scheduler.Quote;
import ro.project.scheduler.QuoteManager;
import ro.project.scheduler.Scheduler;

public class ServletToScheduler {
	final static Logger logger = Logger.getLogger(ServletToScheduler.class);
	private Scheduler scheduler;

	public ServletToScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public List<String> getAllPendingQuotes() {
		int j;

		List<String> out = new ArrayList<String>();
		try {
			List<String> profiles = scheduler.getAllProfiles();

			int totalFromAllSocialNetworks = 0;
			String jString;
			JSONObject jsonObject;
			for (int i = 0; i < profiles.size(); i++) {
				jString = scheduler.getPendingUpdates(1,
						scheduler.getProfileId(profiles.get(i).toLowerCase().replaceAll(" ", "")));
				jsonObject = new JSONObject(jString);
				totalFromAllSocialNetworks += jsonObject.getInt(Constants.TOTAL);
			}

			if (totalFromAllSocialNetworks == 0) {
				// out.add("<BR> There are no pending quotes...");
				return null;
			} else {
				for (int k = 0; k < profiles.size(); k++) {
					j = 1;
					jString = scheduler.getPendingUpdates(j, scheduler.getProfileId(profiles.get(k)).toLowerCase());
					jsonObject = new JSONObject(jString);
					int total = jsonObject.getInt(Constants.TOTAL);
					JSONArray updates = jsonObject.getJSONArray(Constants.UPDATES);
					for (int i = 0; i < total; i++) {
						if ((i % 20 == 0) && (i != 0)) {
							j++;
							jString = scheduler.getPendingUpdates(j, scheduler.getProfileId(profiles.get(k))
									.toLowerCase());
							jsonObject = new JSONObject(jString);
							updates = jsonObject.getJSONArray(Constants.UPDATES);
						}
						JSONObject update = updates.getJSONObject(i % 20);
						out.add(parsePendingUpdate(update));
					}
				}
			}

		} catch (Exception e) {
			logger.error("Problem retrieving scheduled updates", e);
		}
		return out;
	}

	private String parsePendingUpdate(JSONObject update) throws JSONException {
		String pendingUpdate = "";
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(new Long(((int) update.getInt(Constants.DUE_AT))) * 1000);
		int mYear = c.get(Calendar.YEAR);
		pendingUpdate += "<tr>";
		pendingUpdate += "<td>" + update.get(Constants.DUE_TIME) + "; " + update.get("day") + "; " + mYear + "</td>";
		pendingUpdate += "<td>" + update.get(Constants.PROFILE_SERVICE) + "</td>";
		pendingUpdate += "<td>" + update.get("text") + "</td>";
		//System.out.println("AAAAAAA" + update.get("text"));
		String author = ((String)update.get("text")).split(" - ")[0];
		String quote = ((String)update.get("text")).split(" - ")[1];
		Quote q = new Quote(quote, author);
		//System.out.println(q.getAuthor());
		//System.out.println(q.getQuote());
		//System.out.println(q.getMD5());
		pendingUpdate += "<td>" + "<form ACTION=\"DeletePending\">";
		pendingUpdate += "<INPUT TYPE=\"hidden\" name=\"url\" value=" + update.get("_id") + ">";
		pendingUpdate += "<INPUT TYPE=\"hidden\" name=\"quote\" value=\"" + q.getMD5() + "\">";
		pendingUpdate += "<INPUT TYPE=\"hidden\" name=\"text\" value=\"" + update.get("text") + "\">";
		pendingUpdate += "<INPUT TYPE=\"hidden\" name=\"service\" value=\"" + update.get(Constants.PROFILE_SERVICE) + "\">";
		pendingUpdate += "<input type=\"submit\" value=\"Delete\">";
		pendingUpdate += "</form>" + "</td>";
		pendingUpdate += "</tr>";

		return pendingUpdate;
	}

	public String getAllPostedQuotesByAuthor(String author) {
		String postedQuotes = "";
		try {

			List<String> profiles = scheduler.getAllProfiles();
			for (int i = 0; i < profiles.size(); i++) {
				postedQuotes += getQuotesByAuthor(profiles.get(i).toLowerCase().replaceAll(" ", ""), author);
			}
		} catch (JSONException e) {
			logger.error("Problem retrieving all the posted updates", e);
		}

		return postedQuotes;
	}

	private String getQuotesByAuthor(String socialNetwork, String author) throws JSONException {
		String quotesByAuthor = "";
		int j = 1;
		String jString = scheduler.getUpdatesFor(1, scheduler.getProfileId(socialNetwork.toLowerCase()));
		JSONObject jsonObject = new JSONObject(jString);
		int total = jsonObject.getInt(Constants.TOTAL);
		JSONArray updates = jsonObject.getJSONArray(Constants.UPDATES);
		for (int i = 0; i < total; i++) {
			if ((i % 20 == 0) && (i != 0)) {
				j++;
				jString = scheduler.getUpdatesFor(j, scheduler.getProfileId(socialNetwork));
				jsonObject = new JSONObject(jString);
				updates = jsonObject.getJSONArray(Constants.UPDATES);
			}
			JSONObject update = updates.getJSONObject(i % 20);

			String temp = "";
			try {
				temp = ((String) update.get(Constants.TEXT)).split(" - ")[1];
			} catch (Exception e) {
				logger.error("Problem splitting the text.", e);
			}

			if (temp.contains(author) || temp.equals(author)) {
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(new Long(((int) update.getInt(Constants.DUE_AT))) * 1000);
				int mYear = c.get(Calendar.YEAR);
				quotesByAuthor += "<tr><td>" + update.get(Constants.DUE_TIME) + "; " + update.get("day") + "; " + mYear
						+ "</td>";
				quotesByAuthor += "<td>" + update.get(Constants.PROFILE_SERVICE) + "</td>";
				quotesByAuthor += "<td>" + update.get(Constants.TEXT) + "</td>";
				quotesByAuthor += "</tr> ";
			}
		}
		return quotesByAuthor;
	}

	public String parseWebsite(String link, String path, String website) {
		Parser parser = null;
		FileManager fileManager = new FileManager(path);
		boolean selectionCorrect = true;

		if ((link.equals(Constants.PERSDEV_URL)) && (website.startsWith(Constants.PERSDEV_URL))) {
			parser = new PersdevParser();
		} else if ((link.equals(Constants.BRAINIQUOTE_URL)) && (website.startsWith(Constants.BRAINIQUOTE_URL))) {
			parser = new BrainyQuoteParser();
		} else {
			selectionCorrect = false;
		}

		if (selectionCorrect) {
			if (parser.parseWebsite(website, path)) {
				List<String> profiles = scheduler.getAllProfiles();
				for (int i = 0; i < profiles.size(); i++) {
					// maybe add here +quotes.txt
					fileManager.createFileInPath(profiles.get(i).replaceAll(" ", "") + "quotes.txt");
				}

				return " <br> <br> The quotes from the given website were retrieved... <br> What do you want to do next? <br>";
			} else {
				return "<br> <br> The website was already parsed... <br>";
			}
		} else {
			return "<br> <br> Something went wrong, you can try again...<br>";
		}

	}

	public List<String> getOptionsList(String path) {
		List<String> optionList = new ArrayList<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path + "\\\\" + Constants.PARSER_TXT));
			String line;
			while ((line = br.readLine()) != null) {
				optionList.add(line);
			}
			br.close();
		} catch (Exception e) {
			logger.error("Problem retrieving options of parsed websites", e);
		}

		return optionList;
	}

	public String postToSocialMedia(String path, String path2, String radios, String where, String yearDropDown,
			String monthDropDown, String dayDropDown, String hourDropDown, String minuteDropDown, String gmtDropDown,
			String dayDropDown2, String hourDropDown2, String minuteDropDown2, String numberofQuotes) {

		FileManager fileManager = new FileManager(path);
		String out = "";

		try {
			if ((radios == null) || (where == null)) {
				out += "<br> A problem occured. <br> This could happen if: <br>"
						+ "- you did not parse a website before trying to schedule a quote to be poste <br>"
						+ "- you did notselect a website to get the quotes from <br>"
						+ "- you did not select a social network to post to <br>" + "";
				out += "</html>\n</body>";
			} else if ((Integer.parseInt(yearDropDown) < 2015) || (Integer.parseInt(yearDropDown) > 2034)
					|| (Integer.parseInt(monthDropDown) < 1) || (Integer.parseInt(monthDropDown) > 12)
					|| (Integer.parseInt(dayDropDown) < 1) || (Integer.parseInt(dayDropDown) > 31)
					|| (Integer.parseInt(hourDropDown) < 0) || (Integer.parseInt(hourDropDown) > 23)
					|| (Integer.parseInt(minuteDropDown) < 0) || (Integer.parseInt(minuteDropDown) > 59)
					|| (Integer.parseInt(dayDropDown2) < 1) || (Integer.parseInt(dayDropDown2) > 31)
					|| (Integer.parseInt(hourDropDown2) < 0) || (Integer.parseInt(hourDropDown2) > 23)
					|| (Integer.parseInt(dayDropDown) > (Integer.parseInt(dayDropDown2)))) {
				out += "<br> A problem occured. <br> This could happen if you did no pick a valid date for the quote to be scheduled <br>";
				out += "</html>\n</body>";
			} else {
				// String fileName = fileManager.createFileNameFromUrl(radios);
				String fileName = radios;
				// fileName += ".xml";
				QuoteManager quoteManager = new QuoteManager(fileName, path2);
				if (where != null && numberofQuotes != null) {
					// calculate number of minutes to random from.
					int minutes = 0;
					if (Integer.parseInt(hourDropDown) > Integer.parseInt(hourDropDown2)) {
						minutes += (Integer.parseInt(dayDropDown2) - Integer.parseInt(dayDropDown) - 1) * 1440;
					} else {
						minutes += (Integer.parseInt(dayDropDown2) - Integer.parseInt(dayDropDown)) * 1440;
					}
					if (Integer.parseInt(hourDropDown) > Integer.parseInt(hourDropDown2)) {
						if (Integer.parseInt(minuteDropDown) > Integer.parseInt(minuteDropDown2)) {
							minutes += (24 - Integer.parseInt(hourDropDown) + Integer.parseInt(hourDropDown2) - 1) * 60;
						} else {
							minutes += (24 - Integer.parseInt(hourDropDown) + Integer.parseInt(hourDropDown2)) * 60;
						}
					} else {
						if (Integer.parseInt(minuteDropDown) > Integer.parseInt(minuteDropDown2)) {
							minutes += (Integer.parseInt(hourDropDown2) - Integer.parseInt(hourDropDown) - 1) * 60;
						} else {
							minutes += (Integer.parseInt(hourDropDown2) - Integer.parseInt(hourDropDown)) * 60;
						}
					}

					if (Integer.parseInt(minuteDropDown) > Integer.parseInt(minuteDropDown2)) {
						minutes += 60 - Integer.parseInt(minuteDropDown) + Integer.parseInt(minuteDropDown2);
					} else {
						minutes += Integer.parseInt(minuteDropDown2) - Integer.parseInt(minuteDropDown);
					}

					for (int i = 0; i < Integer.parseInt(numberofQuotes); i++) {
						// calculate random
						Random rand = new Random();
						int randomNum = rand.nextInt(minutes + 1);
						// calculate years, months, days, etc
						// must check if the sum after integer + parseint is ok
						// to display it. ex before minutes are 49, after they
						// are 66

						System.out.println("----");
						System.out.println("randomNum: " + randomNum);
						int days = randomNum / 1440 + Integer.parseInt(dayDropDown);
						System.out.println("added days: " + randomNum / 1440);
						System.out.println("days: " + days);
						randomNum = randomNum % 1440;
						int hours = randomNum / 60 + + Integer.parseInt(hourDropDown);
						System.out.println("added hours: " + randomNum /60);
						System.out.println("hours: " + hours);
						randomNum = randomNum % 60 + +Integer.parseInt(minuteDropDown);
						System.out.println("minutes: " + randomNum );
						String date = yearDropDown + "-" + monthDropDown + "-" + days + " " + hours + ":" + randomNum + ":00" + Constants.GMT
								+ gmtDropDown + ":00";
						System.out.println(date);

						scheduler.setUserId(scheduler.getProfileId(where));
						int max = scheduler.getMaxCharacters(where);
						Quote quote = quoteManager.getRandomQuote(where, max);
						if ((quote == null) || (quote.getQuote().trim().isEmpty())) {
							out += "<br> <br> Found nothing to print on " + where + " \n";
						} else {
							int code = scheduler.sendMessage(quote.toString(), date);
							if (code == 200) {
								out += " <br> <br> Quote \"" + quote.toString().replaceAll("\\+", " ")
										+ "\" was schedulet to be posted on " + date + " on " + where + " \n ";
							} else if (code == 0) {
								out += "<br> <br>  Something went wrong. Probably the access token is not good..."
										+ "\n";
							} else {
								out += "<br> <br>  Something went wrong when trying to post on " + where + " \n ";
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Parameters not valid to schedule post");
			out += "Parameters not valid to schedule post";
		}
		return out;
	}
}
