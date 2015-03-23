package ro.project.scheduler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ro.project.Constants;

/**
 * 
 * @author Caphyon1
 * 
 *         Used for making requests to the Buffer API.
 *
 */
public class Scheduler {
	final static Logger logger = Logger.getLogger(Scheduler.class);

	/** the userId for the BufferApi */
	private String userId;
	/** the access token of the registered appplication */
	private static Scheduler instance = null;

	private Scheduler() {
	}

	public static Scheduler getInstance() {
		if (instance == null) {
			instance = new Scheduler();
		}
		return instance;
	}

	/**
	 * Posts the specified message at the timeToPost date.
	 * 
	 * @param message
	 *            the message to be posted.
	 * @param timeToPost
	 *            the time at which to post the message.
	 * 
	 * @param responseCode
	 *            int representing the response code of the Post request.
	 */
	public int sendMessage(String accessToken, String message, String timeToPost) {
		logger.info("Sending \"" + message + "\" at " + timeToPost);
		StringBuffer response = null;
		String url = "";
		String urlParameters = "";
		int responseCode = 0;

		try {
			DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
			Date date = dateFormat.parse(timeToPost);
			long time = date.getTime();
			String sheduletAt = new Timestamp(time).toString();

			url = Constants.BUFFERAPP_CREATE + accessToken;
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			con.setRequestMethod(Constants.POST);
			con.setRequestProperty(Constants.USER_AGENT, Constants.MOZILLA);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			urlParameters = "text=" + message + "&profile_ids[]=" + userId + "&scheduled_at=" + sheduletAt;

			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			responseCode = con.getResponseCode();
			response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch (Exception e) {
			logger.error("Something went wrong trying to schedule a message!", e);
		}

		return responseCode;

	}

	/**
	 * Returns the updates for the social network given by userId.
	 * 
	 * @param page
	 *            represents what page of already posted updates you want to be
	 *            returned (20 updates per page).
	 * 
	 * @return String representing the Twitter updates.
	 */
	public String getUpdatesFor(String accessToken, int page, String userId) {
		logger.info("Retrieving updates for userId: " + userId + " at page " + page);
		this.userId = userId;
		return getUpdates(accessToken, page);
	}

	/**
	 * Returns the updates posted with Buffer API..
	 * 
	 * @param page
	 *            The number of the page to be returned. If there are more than
	 *            20 updates, they won't be returned in a single JSON.
	 * 
	 * @return String representing the updates.
	 */
	public String getUpdates(String accessToken, int page) {
		String url = Constants.BUFFERAPP_PROFILES + userId + "/updates/sent.json?" + "page=" + page
				+ Constants.ACCESS_TOKEN_PARAM + accessToken;
		System.out.println(url);

		StringBuffer response = null;
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod(Constants.GET);
			con.setRequestProperty(Constants.USER_AGENT, Constants.MOZILLA);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch (Exception e) {
			logger.error("Something went wrong when trying to get the updates!", e);
		}

		if (response == null) {
			return "";
		} else {
			return response.toString();
		}

	}

	/**
	 * Returns the pending updates from the social network specified by userId.
	 * 
	 * @param page
	 *            The number of the page to be returned. If there are more than
	 *            20 pending twitter updates, they won't be returned in a single
	 *            JSON.
	 * 
	 * @return String representing the pending Twitter updates.
	 */
	public String getPendingUpdates(String accessToken, int page, String userId) {
		logger.info("Retrieving pending updates for userId: " + userId + " at page " + page);
		this.userId = userId;
		return getPendingUpdatesAt(accessToken, page);
	}

	/**
	 * Returns the pending updates created with Buffer API..
	 * 
	 * @param page
	 *            The number of the page to be returned. If there are more than
	 *            20 pending updates, they won't be returned in a single JSON.
	 * 
	 * @return String representing the pending updates.
	 */
	public String getPendingUpdatesAt(String accessToken, int page) {
		String url = Constants.BUFFERAPP_PROFILES + userId + "/updates/pending.json?" + "page=" + page
				+ "&access_token=" + accessToken;
		StringBuffer response = null;
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod(Constants.GET);
			con.setRequestProperty(Constants.USER_AGENT, Constants.MOZILLA);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

		} catch (Exception e) {
			logger.error("Something went wrong when trying to get the pending updates!", e);
		}

		if (response == null) {
			return "";
		} else {
			return response.toString();
		}
	}

	/**
	 * Deletes a pending update specified by the given id.
	 * 
	 * @param id
	 *            used for determinating what update is going to be deleted.
	 * 
	 * @return int representing the response code of request.
	 */
	public int deleteUpdate(String accessToken, String id) {
		logger.info("Deleting update " + id);
		int responseCode = 0;
		try {

			String url = Constants.BUFFERAPP_UPDATES + id + "/destroy.json?access_token=" + accessToken;
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			con.setRequestProperty(Constants.USER_AGENT, Constants.MOZILLA);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			String urlParameters = "profile_ids[]=" + userId;

			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			responseCode = con.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch (Exception e) {
			logger.error("Something went wrong when trying to delete the update", e);
		}

		return responseCode;
	}

	/**
	 * Returns the id of the profile specified by the service parameter.
	 * 
	 * @param service
	 *            the name of the social network.
	 * 
	 * @return the id of the profile.
	 */
	public String getProfileId(String accessToken, String service) {
		String url = Constants.BUFFERAPP_PROFILES_JSON + "?access_token=" + accessToken;
		StringBuffer response = null;
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", Constants.MOZILLA);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

		} catch (Exception e) {
			logger.error("Something went wrong then trying to get the profiles for user!", e);
		}

		String jsonResponse = "";
		if (response == null) {
			return "";
		} else {
			jsonResponse = response.toString();
		}

		try {
			JSONArray updates = new JSONArray(jsonResponse);
			for (int i = 0; i < updates.length(); i++) {
				JSONObject update = updates.getJSONObject(i);
				if (((String) update.get(Constants.FORMATED_SERVICE)).equalsIgnoreCase(service))
					return (String) update.get("id");
			}
		} catch (JSONException e) {
			logger.error("Something went wrong when trying to parse Json for id!", e);
		}
		return "";

	}

	/**
	 * Used for retrieving all the profiles of the user.
	 * 
	 * @return a list where each member represents a profile of the user.
	 */
	public List<String> getAllProfiles(String accessToken) {
		List<String> allProfilesList = new ArrayList<String>();

		String url = Constants.BUFFERAPP_PROFILES_JSON + "?access_token=" + accessToken;
		StringBuffer response = null;
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod(Constants.GET);
			con.setRequestProperty(Constants.USER_AGENT, Constants.MOZILLA);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch (Exception e) {
			logger.error("Something went wrong when trying to get all the profiles of the user", e);
		}

		String jsonResponse = "";
		if (response == null) {
			return allProfilesList;
		} else {
			jsonResponse = response.toString();
		}

		try {
			JSONArray updates = new JSONArray(jsonResponse);
			for (int i = 0; i < updates.length(); i++) {
				JSONObject update = updates.getJSONObject(i);
				allProfilesList.add((String) update.get("formatted_service"));
			}
		} catch (JSONException e) {
			logger.error("Something went wrong when trying to parse the name of the user's profiles", e);
		}
		return allProfilesList;
	}

	/**
	 * Used for retrieving the maximum characters allowed on the specified
	 * social network.
	 * 
	 * @param service
	 *            represents the social network's name.
	 * @return the number of maximum characters allowed.
	 */
	public int getMaxCharacters(String service) {
		if (service.equalsIgnoreCase(Constants.TWITTER)) {
			return 140;
		}

		if (service.equalsIgnoreCase(Constants.FACEBOOK)) {
			return 500;
		}

		return 0;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
