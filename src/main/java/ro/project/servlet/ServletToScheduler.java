package ro.project.servlet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

	public String getAllPendingQuotes(String accessToken) {
		int j;

		String out = "";
		try {
			if ((scheduler.getPendingUpdates(accessToken, 1,
					scheduler.getProfileId(accessToken, Constants.FACEBOOK)) == null)
					|| (scheduler.getPendingUpdates(accessToken, 1,
							scheduler.getProfileId(accessToken, Constants.FACEBOOK)).trim()
							.isEmpty())) {

				out += "Something went wrong. The access token might be the problem... <br>";
			} else {
				String jString = scheduler.getPendingUpdates(accessToken, 1,
						scheduler.getProfileId(accessToken, Constants.FACEBOOK));
				JSONObject jsonObject = new JSONObject(jString);
				int totalFacebook = jsonObject.getInt(Constants.TOTAL);

				jString = scheduler.getPendingUpdates(accessToken, 1,
						scheduler.getProfileId(accessToken, Constants.TWITTER));
				jsonObject = new JSONObject(jString);
				int totalTwitter = jsonObject.getInt(Constants.TOTAL);
				out += "<table border=\"1\" style=\"width:100%;\" cellpadding=\"5\" cellspacing=\"5\">";
				out += "<tr bgcolor=\"#d3d3d3\">";
				out += "<td>Due at</td>";
				out += "<td>Service</td>";
				out += "<td>Text</td>";
				out += "<td>Delete</td>";
				out += "</tr>";

				if ((totalFacebook == 0) && (totalTwitter == 0)) {
					out += "<BR> There are no pending quotes...";
				} else {

					j = 1;
					jString = scheduler.getPendingUpdates(accessToken, j,
							scheduler.getProfileId(accessToken, Constants.FACEBOOK));
					jsonObject = new JSONObject(jString);
					int total = jsonObject.getInt(Constants.TOTAL);
					JSONArray updates = jsonObject.getJSONArray(Constants.UPDATES);
					for (int i = 0; i < total; i++) {
						if ((i % 20 == 0) && (i != 0)) {
							j++;
							jString = scheduler.getPendingUpdates(accessToken, j,
									scheduler.getProfileId(accessToken, Constants.FACEBOOK));
							jsonObject = new JSONObject(jString);
							updates = jsonObject.getJSONArray(Constants.UPDATES);
						}
						JSONObject update = updates.getJSONObject(i % 20);
						out += parsePendingUpdate(update);
					}

					j = 1;
					jString = scheduler.getPendingUpdates(accessToken, j,
							scheduler.getProfileId(accessToken, Constants.TWITTER));
					jsonObject = new JSONObject(jString);
					total = jsonObject.getInt(Constants.TOTAL);
					updates = jsonObject.getJSONArray(Constants.UPDATES);
					for (int i = 0; i < total; i++) {
						if ((i % 20 == 0) && (i != 0)) {
							j++;
							jString = scheduler.getPendingUpdates(accessToken, j,
									scheduler.getProfileId(accessToken, Constants.TWITTER));
							jsonObject = new JSONObject(jString);
							updates = jsonObject.getJSONArray(Constants.UPDATES);
						}
						JSONObject update = updates.getJSONObject(i % 20);
						out += parsePendingUpdate(update);

					}

				}

				out += "</table>";
				out += "</html>\n</body>";
			}
		} catch (Exception e) {
			logger.error("Problem retrieving scheduled updates", e);
		}
		return out;
	}

	private String parsePendingUpdate(JSONObject update) throws JSONException {
		String pendingUpdate = "";
		pendingUpdate += "<BR>";
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(new Long(((int) update.getInt(Constants.DUE_AT))) * 1000);
		int mYear = c.get(Calendar.YEAR);
		pendingUpdate += "<tr>";
		pendingUpdate += "<td>" + update.get(Constants.DUE_TIME) + "; "
				+ update.get("day") + "; " + mYear + "</td>";
		pendingUpdate += "<td>" + update.get(Constants.PROFILE_SERVICE) + "</td>";
		pendingUpdate += "<td>" + update.get("text") + "</td>";
		pendingUpdate += "<td>" + "<form ACTION=\"DeletePending\">";
		pendingUpdate += "<INPUT TYPE=\"hidden\" name=\"url\" value="
				+ update.get("_id") + ">";
		pendingUpdate += "<input type=\"submit\" value=\"Delete\">";
		pendingUpdate += "</form>" + "</td>";
		pendingUpdate += "</tr>";

		return pendingUpdate;
	}

	public String getAllPostedQuotesByAuthor(String accessToken, String author) {
		String postedQuotes = "";
		try {
			postedQuotes += "<br> <br> Author: " + author;
			postedQuotes += "<table border=\"1\" style=\"width:100%;\" cellpadding=\"5\" cellspacing=\"5\">";
			postedQuotes += "<tr bgcolor=\"#d3d3d3\">";
			postedQuotes += "<td>Due at</td>";
			postedQuotes += "<td>Service</td>";
			postedQuotes += "<td>Text</td>";
			postedQuotes += "</tr>";
			postedQuotes += getQuotesByAuthor(accessToken, "Facebook", author);
			postedQuotes += getQuotesByAuthor(accessToken, "Twitter", author);
		} catch (JSONException e) {
			logger.error("Problem retrieving all the posted updates", e);
		}

		return postedQuotes;
	}

	private String getQuotesByAuthor(String accessToken, String socialNetwork, String author)
			throws JSONException {
		String quotesByAuthor = "";
		int j = 1;
		String jString = scheduler.getUpdatesFor(accessToken, 1,
				scheduler.getProfileId(accessToken, socialNetwork.toLowerCase()));
		JSONObject jsonObject = new JSONObject(jString);
		int total = jsonObject.getInt(Constants.TOTAL);
		JSONArray updates = jsonObject.getJSONArray(Constants.UPDATES);
		for (int i = 0; i < total; i++) {
			if ((i % 20 == 0) && (i != 0)) {
				j++;
				jString = scheduler.getUpdatesFor(accessToken, j,
						scheduler.getProfileId(accessToken, socialNetwork));
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

			if (temp.equals(author)) {
				quotesByAuthor += " <br> ";
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(new Long(((int) update.getInt(Constants.DUE_AT))) * 1000);
				int mYear = c.get(Calendar.YEAR);
				quotesByAuthor += "<tr><td>" + update.get(Constants.DUE_TIME) + "; "
						+ update.get("day") + "; " + mYear + "</td>";
				quotesByAuthor += "<td>" + update.get(Constants.PROFILE_SERVICE)
						+ "</td>";
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

		if ((link.equals(Constants.PERSDEV_URL))
				&& (website.startsWith(Constants.PERSDEV_URL))) {
			parser = new PersdevParser();
		} else if ((link.equals(Constants.BRAINIQUOTE_URL))
				&& (website.startsWith(Constants.BRAINIQUOTE_URL))) {
			parser = new BrainyQuoteParser();
		} else {
			selectionCorrect = false;
		}

		if (selectionCorrect) {
			if (parser.parseWebsite(website, path)) {
				fileManager.createFileInPath(Constants.FACEBOOK_QUOTES_TXT);
				fileManager.createFileInPath(Constants.TWITTER_QUOTES_TXT);
				return " <br> <br> The quotes from the given website were retrieved... <br> What do you want to do next? <br>";
			} else {
				return "<br> <br> The website was already parsed... <br>";
			}
		} else {
			return "<br> <br> Something went wrong, you can try again...<br>";
		}

	}

	public String postToSocialMediaView(String accessToken, String path) {
		List<String> optionsList = new ArrayList<String>();
		optionsList = getOptionsList(path);
		String out = "";
		System.out.println("accessToken in postToSocialMediaView(): " + accessToken);
		List<String> allProfiles = scheduler.getAllProfiles(accessToken);
		if (allProfiles.size() == 0) {
			out +="<html> \n";
			out +="<head> \n";
			out +="<br> <a href=\"http://localhost:8080/SocialMediaScheduler\">Home</a> \n";
			out +="<br> <a href=\"http://localhost:8080/SocialMediaScheduler/parse\">Parse</a> \n";
			out +="<br> <a href=\"http://localhost:8080/SocialMediaScheduler/Post\">Schedule Quote</a> \n";
			out +="<br> <a href=\"http://localhost:8080/SocialMediaScheduler/QuoteHistory\">Quote History</a> \n";
			out +="<br> <a href=\"http://localhost:8080/SocialMediaScheduler/PendingQuotes\">Pending Quotes</a> \n";
			out +="<br> <a href=\"http://localhost:8080/SocialMediaScheduler/search\">Search</a><br><br> \n";
			out +="</head> \n";
			out +="<body> \n";
			out +="The access token is probably not correct, please insert it again... \n";
		} else {
			out +="<script type=\"text/javascript\"> \n";
			out +="var monthtext = [ '01', '02', '03', '04', '05', '06', '07', '08', '09','10', '11', '12' ]; \n";
			out +="var gmttext = [ '-12', '-11', '-10', '-09', '-08', '-07', '-06', '-05', '-04', '-03', '-02', '-01', '00', '+01', '+02', '+03', '+04', '+05', '+06', '+07', '+08', '+09', '+10', '+11', '+12', '+13', '+14' ] \n";
			out +="function populatedropdown(dayfield, monthfield, yearfield, hourfield, minutefield, gmtfield) { \n";
			out +="var today = new Date() \n";
			out +="var dayfield = document.getElementById(dayfield) \n";
			out +="var monthfield = document.getElementById(monthfield) \n";
			out +="var yearfield = document.getElementById(yearfield) \n";
			out +="var minutefield = document.getElementById(minutefield) \n";
			out +="var hourfield = document.getElementById(hourfield) \n";
			out +="var gmtfield = document.getElementById(gmtfield) \n";
			out +=" \n";
			out +="for (var i = 1; i <= 31; i++) \n";
			out +="dayfield.options[i-1] = new Option(i, i) \n";
			out +="dayfield.options[today.getDate()] = new Option(today.getDate(), today.getDate(), true, true) \n";
			out +=" \n";
			out +="for (var m = 0; m < 12; m++) \n";
			out +="monthfield.options[m] = new Option(monthtext[m], monthtext[m]) \n";
			out +="monthfield.options[today.getMonth()] = new Option(monthtext[today.getMonth()], monthtext[today.getMonth()], true, true) \n";
			out +=" \n";
			out +="for(var m = 0; m<27;m++) \n";
			out +="gmtfield.options[m] = new Option(gmttext[m], gmttext[m+2]) \n";
			out +=" \n";
			out +="var thisyear = today.getFullYear() \n";
			out +="for (var y = 0; y < 20; y++) { \n";
			out +="yearfield.options[y] = new Option(thisyear, thisyear) \n";
			out +="thisyear += 1 \n";
			out +="} \n";
			out +="yearfield.options[0] = new Option(today.getFullYear(), today.getFullYear(), true, true) \n";
			out +=" \n";
			out +="for (var i = 0; i <= 23; i++) \n";
			out +="hourfield.options[i] = new Option(i, i) \n";
			out +=" \n";
			out +="for (var i = 0; i < 60; i++) \n";
			out +="minutefield.options[i] = new Option(i, i) \n";
			out +="minutefield.options[today.getMinutes()] = new Option(minutetext[today.getMinutes()], minutetext[today.getMinutes()], true, true) \n";
			out +="} \n";
			out +="</script> \n";
			out +=" \n";
			out +=" \n";
			out +="<html> \n";
			out +="<head> \n";
			out +="<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\"> \n";
			out +="<title>Random Quote</title> \n";
			out +="<head> \n";
			out +="<br> <a href=\"http://localhost:8080/SocialMediaScheduler\">Home</a> \n";
			out +="<br> <a href=\"http://localhost:8080/SocialMediaScheduler/parse\">Parse</a> \n";
			out +="<br> <a href=\"http://localhost:8080/SocialMediaScheduler/Post\">Schedule Quote</a> \n";
			out +="<br> <a href=\"http://localhost:8080/SocialMediaScheduler/QuoteHistory\">Quote History</a> \n";
			out +="<br> <a href=\"http://localhost:8080/SocialMediaScheduler/PendingQuotes\">Pending Quotes</a> \n";
			out +="<br> <a href=\"http://localhost:8080/SocialMediaScheduler/search\">Search</a><br><br> \n";
			out +="</head> \n";
			out +="</head> \n";
			out +="<body> \n";
			out +="<form action=\"HelloServlet\"> \n";

			// print only connected profiles
			// scheduler.
			out +=" \n";
			out +="Where to post: <br> \n";
			for (int i = 0; i < allProfiles.size(); i++) {
				out +="<input type=\"checkbox\" name=\"where\" value=\"" + allProfiles.get(i) + "\">"
						+ allProfiles.get(i) + "<BR> \n";
			}

			out +="<br> <br> \n";
			out +="&nbsp;&nbsp;&nbsp;Year&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Month&nbsp;&nbsp;&nbsp;&nbsp; \n";
			out +="Day &nbsp;&nbsp;Hour&nbsp;&nbsp; Minute&nbsp;&nbsp;GMT<br>  \n";
			out +="<select id=\"yeardropdown\" name=\"yeardropdown\"></select> \n";
			out +="<select id=\"monthdropdown\" name=\"monthdropdown\"></select> \n";
			out +="<select id=\"daydropdown\" name=\"daydropdown\"></select> \n";
			out +="<select id=\"hourdropdown\" name=\"hourdropdown\"></select> \n";
			out +="<select id=\"minutedropdown\" name=\"minutedropdown\"></select> \n";
			out +="<select id=\"gmtdropdown\" name=\"gmtdropdown\"></select> \n";
			out +="\n";
			out +="<br> <br> \n";

			for (int i = 0; i < optionsList.size(); i++) {
				out +="<INPUT TYPE=\"radio\" NAME=\"radios\" VALUE=" + optionsList.get(i) + "> From "
						+ optionsList.get(i) + "<BR> \n";
			}
			out +="<input type=\"submit\" value=\"Submit\"> <br> <br> \n";
			out +="</form> \n";
			out +=" \n";
			out +="<script type=\"text/javascript\"> \n";
			out +="window.onload = function() { \n";
			out +="populatedropdown(\"daydropdown\", \"monthdropdown\", \"yeardropdown\", \"hourdropdown\", \"minutedropdown\", \"gmtdropdown\") \n";
			out +="} \n";
			out +="</script> \n";
			out +="</body> \n";
			out +="</html> \n";
			out +=" \n";
			out +="\n";
		}
		return out;
			
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

	public String postToSocialMedia(String accessToken, String path, String path2, String radios,
			String[] where, String yearDropDown, String monthDropDown,
			String dayDropDown, String hourDropDown, String minuteDropDown,
			String gmtDropDown) {
		FileManager fileManager = new FileManager(path);
		String out = "";

		try {
			if ((radios == null) || (where == null)) {
				out += "<br> A problem occured. <br> This could happen if: <br>"
						+ "- you did not parse a website before trying to schedule a quote to be poste <br>"
						+ "- you did notselect a website to get the quotes from <br>"
						+ "- you did not select a social network to post to <br>"
						+ "";
				out += "</html>\n</body>";
			} else if ((Integer.parseInt(yearDropDown) < 2015)
					|| (Integer.parseInt(yearDropDown) > 2034)
					|| (Integer.parseInt(monthDropDown) < 1)
					|| (Integer.parseInt(monthDropDown) > 12)
					|| (Integer.parseInt(dayDropDown) < 1)
					|| (Integer.parseInt(dayDropDown) > 31)
					|| (Integer.parseInt(hourDropDown) < 0)
					|| (Integer.parseInt(hourDropDown) > 23)
					|| (Integer.parseInt(minuteDropDown) < 0)
					|| (Integer.parseInt(minuteDropDown) > 59)) {
				out += "<br> A problem occured. <br> This could happen if you did no pick a valid date for the quote to be scheduled <br>";
				out += "</html>\n</body>";
			} else {
				String fileName = fileManager.createFileNameFromUrl(radios);
				fileName += ".ser";
				QuoteManager quoteManager = new QuoteManager(fileName, path2);
				// scheduler = new Scheduler(accessToken);
				scheduler = Scheduler.getInstance();

				String date = yearDropDown + "-" + monthDropDown + "-"
						+ dayDropDown + " " + hourDropDown + ":"
						+ minuteDropDown + ":00" + "GMT" + gmtDropDown + ":00";

				if (where != null) {
					for (int i = 0; i < where.length; i++) {
						System.out.println(out);
						scheduler.setUserId(scheduler.getProfileId(accessToken, where[i]));
						int max = scheduler.getMaxCharacters(where[i]);
						Quote quote = quoteManager
								.getRandomQuote(where[i], max);
						if ((quote == null)
								|| (quote.getQuote().trim().isEmpty())) {
							out += "<br> <br> Found nothing to print on " + where[i]
									+ " \n";
						} else {
							int code = scheduler.sendMessage(accessToken, quote.toString(),
									date);
							if (code == 200) {
								out += " <br> <br> Quote \""
										+ quote.toString().replaceAll("\\+",
												" ")
										+ "\" was schedulet to be posted on "
										+ date + " on " + where[i] + " \n ";
							} else if (code == 0) {
								out += "<br> <br>  Something went wrong. Probably the access token is not good..."
										+ "\n";
							} else {
								out += "<br> <br>  Something went wrong when trying to post on "
										+ where[i] + " \n ";
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
