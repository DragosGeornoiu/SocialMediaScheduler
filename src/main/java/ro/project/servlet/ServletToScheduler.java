package ro.project.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
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
		pendingUpdate += "<td>" + update.get(Constants.TEXT) + "</td>";
		String author = ((String) update.get(Constants.TEXT)).split(" - ")[0];
		String quote = ((String) update.get(Constants.TEXT)).split(" - ")[1];
		Quote q = new Quote(quote, author);

		pendingUpdate += "<td>" + "<form ACTION=\"DeletePending\">";
		pendingUpdate += "<INPUT TYPE=\"hidden\" name=\"url\" value=" + update.get("_id") + ">";
		pendingUpdate += "<INPUT TYPE=\"hidden\" name=\"quote\" value=\"" + q.getMD5() + "\">";
		pendingUpdate += "<INPUT TYPE=\"hidden\" name=\"text\" value=\"" + update.get("text") + "\">";
		pendingUpdate += "<INPUT TYPE=\"hidden\" name=\"service\" value=\"" + update.get(Constants.PROFILE_SERVICE)
				+ "\">";
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
				quotesByAuthor += "<tr><td>" + update.get(Constants.DUE_TIME) + "; " + update.get(Constants.DAY) + "; "
						+ mYear + "</td>";
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
		} else if ((link.equals(Constants.BRAINIQUOTE_URL)) && (website.startsWith(Constants.BRAINIQUOTE_CATEGORY))) {
			parser = new BrainyQuoteParser();
		} else {
			selectionCorrect = false;
		}

		if (selectionCorrect) {
			if (parser.parseWebsite(website, path)) {
				List<String> profiles = scheduler.getAllProfiles();
				for (int i = 0; i < profiles.size(); i++) {
					fileManager.createFileInPath(profiles.get(i).replaceAll(" ", "") + Constants.QUOTES_TXT);
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

	public String postToSocialMedia(String path2, String radios, String[] where, String yearDropDown,
			String monthDropDown, String dayDropDown, String hourDropDown, String minuteDropDown, String gmtDropDown,
			String dayDropDown2, String hourDropDown2, String minuteDropDown2, String numberofQuotes, int hour,
			int minute, String myFile, Integer[] numbers) {

		String out = "";

		try {
			if ((radios == null) || (where == null)) {
				out += Constants.PROBLEM + Constants.NEW_LINE;
			} else if ((Integer.parseInt(yearDropDown) < 2015) || (Integer.parseInt(yearDropDown) > 2034)
					|| (Integer.parseInt(monthDropDown) < 1) || (Integer.parseInt(monthDropDown) > 12)
					|| (Integer.parseInt(dayDropDown) < 1) || (Integer.parseInt(dayDropDown) > 31)
					|| (Integer.parseInt(hourDropDown) < 0) || (Integer.parseInt(hourDropDown) > 23)
					|| (Integer.parseInt(minuteDropDown) < 0) || (Integer.parseInt(minuteDropDown) > 59)
					|| (Integer.parseInt(dayDropDown2) < 1) || (Integer.parseInt(dayDropDown2) > 31)
					|| (Integer.parseInt(hourDropDown2) < 0) || (Integer.parseInt(hourDropDown2) > 23)
					|| (Integer.parseInt(dayDropDown) > (Integer.parseInt(dayDropDown2)))) {
				out += Constants.INVALID_DATE + Constants.NEW_LINE;
			} else {
				String fileName = radios;
				QuoteManager quoteManager;
				if (radios.equals(Constants.SELECT)) {
					quoteManager = new QuoteManager(myFile);
				} else {
					quoteManager = new QuoteManager(fileName, path2);
				}
				if (where != null && numberofQuotes != null) {

					for (int j = 0; j < where.length; j++) {
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

						for (int i = 0; i < numbers[j]; i++) {
							Properties configProperty = new Properties();
							File file = new File(path2 + Constants.CONFIG_PROPERTIES);
							FileInputStream fileIn = new FileInputStream(file);
							configProperty.load(fileIn);
							String posted = configProperty.getProperty(Constants.POSTED + where[j]);
							String whereSocial = configProperty.getProperty(where[j]);

							FileOutputStream fileOut = new FileOutputStream(file);

							if (posted != null && whereSocial != null) {
								int t1 = Integer.parseInt(whereSocial);
								int t2 = Integer.parseInt(posted);

								if (t1 > t2) {
									configProperty.setProperty(Constants.POSTED + where[j],
											Integer.toString(t2 + numbers[j]));
								}
							} else {
								configProperty.setProperty(Constants.POSTED + where[j], Integer.toString(numbers[j]));
							}
							Calendar now = Calendar.getInstance();
							configProperty.setProperty(Constants.CALENDAR_YEAR,
									Integer.toString(now.get(Calendar.YEAR)));
							configProperty.setProperty(Constants.CALENDAR_MONTH,
									Integer.toString(now.get(Calendar.MONTH)));
							configProperty.setProperty(Constants.CALENDAR_DAY,
									Integer.toString(now.get(Calendar.DAY_OF_MONTH)));
							// configProperty.setProperty(Constants.POSTED +
							// where[j], Integer.toString(postedAfter));
							configProperty.store(fileOut, "sample properties");

							int hours = 0;
							int randomNum = 0;
							int days = 0;
							do {
								Random rand = new Random();
								randomNum = rand.nextInt(minutes + 1);
								days = randomNum / 1440 + Integer.parseInt(dayDropDown);
								randomNum = randomNum % 1440;
								hours = randomNum / 60 + +Integer.parseInt(hourDropDown);
								randomNum = randomNum % 60 + +Integer.parseInt(minuteDropDown);
							} while (!((hours > hour) || (hours == hour && randomNum > minute)));

							String date = yearDropDown + "-" + monthDropDown + "-" + days + " " + hours + ":"
									+ randomNum + ":00" + Constants.GMT + gmtDropDown + ":00";

							scheduler.setUserId(scheduler.getProfileId(where[j]));
							int max = scheduler.getMaxCharacters(where[j]);
							Quote quote = quoteManager.getRandomQuote(where[j], max);
							if ((quote == null) || (quote.getQuote().trim().isEmpty())) {
								out += Constants.NOTHING_TO_PRINT + where[j] + Constants.NEW_LINE;
							} else {
								int code = scheduler.sendMessage(quote.toString(), date);
								if (code == 200) {
									out += Constants.SUCCESS + " posting to " + where[j] + Constants.NEW_LINE;
								} else if (code == 0) {
									out += Constants.PROBLEM + Constants.NEW_LINE;
								} else {
									out += Constants.PROBLEM + Constants.NEW_LINE;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Parameters not valid to schedule post");
			out += Constants.PROBLEM + Constants.NEW_LINE;
		}

		return out;
	}
}
