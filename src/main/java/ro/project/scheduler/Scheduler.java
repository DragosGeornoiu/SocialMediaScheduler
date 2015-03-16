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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Scheduler {
	private String userId;
	private String accessToken;
	private final String USER_AGENT = "Mozilla/5.0";

	public Scheduler(String accessToken) {
		this.accessToken = accessToken;
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
	public int sendMessage(String message, String timeToPost) {
		StringBuffer response = null;
		String url = "";
		String urlParameters = "";
		int responseCode = 0;

		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssz");
			Date date = dateFormat.parse(timeToPost);
			long time = date.getTime();
			String sheduletAt = new Timestamp(time).toString();

			url = "https://api.bufferapp.com/1/updates/create.json?access_token=" + accessToken;
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
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
			e.printStackTrace();
		}

		return responseCode;

	}

	/**
	 * Returns the twitter updates.
	 * 
	 * @param page
	 *            The number of the page to be returned. If there are more than
	 *            20 twitter updates, they won't be returned in a single JSON.
	 * 
	 * @return String representing the Twitter updates.
	 */
	public String getTwitterUpdates(int page, String userId) {
		this.userId = userId;
		return getUpdates(page);
	}

	/**
	 * Returns the facebook updates.
	 * 
	 * @param page
	 *            The number of the page to be returned. If there are more than
	 *            20 facebook updates, they won't be returned in a single JSON.
	 * 
	 * @return String representing the Facebook updates.
	 */
	public String getFacebookUpdates(int page, String userId) {
		this.userId = userId;
		return getUpdates(page);
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
	public String getUpdates(int page) {

		String url = "https://api.bufferapp.com/1/profiles/" + userId + "/updates/sent.json?" + "page=" + page
				+ "&access_token=" + accessToken;

		StringBuffer response = null;
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", USER_AGENT);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch (Exception e) {
			//e.printStackTrace();
		}

		if (response == null) {
			return "";
		} else {
			return response.toString();
		}

	}

	/**
	 * Returns the pending twitter updates.
	 * 
	 * @param page
	 *            The number of the page to be returned. If there are more than
	 *            20 pending twitter updates, they won't be returned in a single
	 *            JSON.
	 * 
	 * @return String representing the pending Twitter updates.
	 */
	public String getTwitterPendingUpdates(int page, String userId) {
		// userId = "54f4480b76a9a2b75cb71256";
		this.userId = userId;
		return getPendingUpdates(page);
	}

	/**
	 * Returns the pending facebook updates.
	 * 
	 * @param page
	 *            The number of the page to be returned. If there are more than
	 *            20 pending facebook updates, they won't be returned in a
	 *            single JSON.
	 * 
	 * @return String representing the pending facebook updates.
	 */
	public String getFacebookPendingUpdates(int page, String userid) {
		/* userId = "54f5cffee090e41029541d73"; */
		this.userId = userid;
		return getPendingUpdates(page);
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
	public String getPendingUpdates(int page) {
		String url = "https://api.bufferapp.com/1/profiles/" + userId + "/updates/pending.json?" + "page=" + page
				+ "&access_token=" + accessToken;
		StringBuffer response = null;
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", USER_AGENT);
			// responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

		} catch (Exception e) {
			//e.printStackTrace();
		}

		if (response == null) {
			return "";
		} else {
			return response.toString();
		}
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int deleteUpdate(String id) {
		int responseCode = 0;
		try {

			String url = "https://api.bufferapp.com/1/updates/" + id + "/destroy.json?access_token=" + accessToken;
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			// add reuqest header con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			String urlParameters = "profile_ids[]=" + userId;

			// Send post request
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
			e.printStackTrace();
		}

		return responseCode;
	}

	public String authenticate(String clientId, String redirectUri) {
		String url = "https://bufferapp.com/oauth2/authorize?client_id=" + clientId + "&redirect=" + redirectUri
				+ "&response_type=code";
		StringBuffer response = null;
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", USER_AGENT);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response.toString();

	}

	public String getProfileId(String service) {
		String url = "https://api.bufferapp.com/1/profiles.json" + "?access_token=" + accessToken;
		StringBuffer response = null;
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", USER_AGENT);
			// responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

		} catch (Exception e) {
			//e.printStackTrace();
		}

		/*
		 * if (response == null) { return ""; } else { return
		 * response.toString(); }
		 */

		String jsonResponse = "";
		if (response == null) {
			return "";
		} else {
			jsonResponse = response.toString();
		}

/*		JSONObject jsonObject;*/
		try {
			// jsonObject = new JSONObject(jsonResponse);
			JSONArray updates = new JSONArray(jsonResponse);
			// jsonObject.getJSONArray("");
			for (int i = 0; i < updates.length(); i++) {
				JSONObject update = updates.getJSONObject(i);
				/*System.out.println("service: " + update.get("service"));
				System.out.println("id: " + update.get("id"));*/
				if (((String) update.get("formatted_service")).equalsIgnoreCase(service))
					return (String) update.get("id");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "";

	}

	public List<String> getAllProfiles() {
		List<String> allProfilesList = new ArrayList<String>();

		String url = "https://api.bufferapp.com/1/profiles.json" + "?access_token=" + accessToken;
		StringBuffer response = null;
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", USER_AGENT);
			// responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

		} catch (Exception e) {
			//e.printStackTrace();
		}

		String jsonResponse = "";
		if (response == null) {
			return allProfilesList;
		} else {
			jsonResponse = response.toString();
		}
		
		/*JSONObject jsonObject;*/
		try {
			// jsonObject = new JSONObject(jsonResponse);
			JSONArray updates = new JSONArray(jsonResponse);
			// jsonObject.getJSONArray("");
			for (int i = 0; i < updates.length(); i++) {
				JSONObject update = updates.getJSONObject(i);
				allProfilesList.add((String) update.get("formatted_service"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return allProfilesList;
	}
	

	public int getMaxCharacters(String service) {
		if(service.equalsIgnoreCase("twitter")) {
			return 140;
		} 
		
		if(service.equalsIgnoreCase("facebook")) {
			return 500;
		}
		
		return 0;
	}
	
	
	/*
	 * public void sendMessageNow(String message) { try {
	 * 
	 * String url =
	 * "https://api.bufferapp.com/1/updates/create.json?access_token=" +
	 * ACCESS_TOKEN; URL obj = new URL(url); HttpsURLConnection con =
	 * (HttpsURLConnection) obj.openConnection();
	 * 
	 * // add reuqest header con.setRequestMethod("POST");
	 * con.setRequestProperty("User-Agent", USER_AGENT);
	 * con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	 * 
	 * // String urlParameters = "text=" + message + "&profile_ids[]=" + //
	 * USER_ID + "&now=true"; String urlParameters = "text=" + message +
	 * "&profile_ids[]=" + "54f5cffee090e41029541d73" + "&now=true";
	 * 
	 * // Send post request con.setDoOutput(true); DataOutputStream wr = new
	 * DataOutputStream(con.getOutputStream()); wr.writeBytes(urlParameters);
	 * wr.flush(); wr.close();
	 * 
	 * int responseCode = con.getResponseCode(); BufferedReader in = new
	 * BufferedReader(new InputStreamReader(con.getInputStream())); String
	 * inputLine; StringBuffer response = new StringBuffer();
	 * 
	 * while ((inputLine = in.readLine()) != null) { response.append(inputLine);
	 * } in.close();
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } }
	 * 
	 * public void getSpecificUpdate(String updateId) {
	 * 
	 * String url = "https://api.bufferapp.com/1/updates/" + updateId +
	 * ".json?access_token=" + ACCESS_TOKEN;
	 * 
	 * try { URL obj = new URL(url); HttpURLConnection con = (HttpURLConnection)
	 * obj.openConnection(); con.setRequestMethod("GET");
	 * con.setRequestProperty("User-Agent", USER_AGENT);
	 * 
	 * BufferedReader in = new BufferedReader(new
	 * InputStreamReader(con.getInputStream())); String inputLine; StringBuffer
	 * response = new StringBuffer();
	 * 
	 * while ((inputLine = in.readLine()) != null) { response.append(inputLine);
	 * } in.close(); } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * }
	 * 
	 * 
	 * 
	 * public void deleteUpdate(String id) { try {
	 * 
	 * String url = "https://api.bufferapp.com/1/updates/" + id +
	 * "/destroy.json?access_token=" + ACCESS_TOKEN; URL obj = new URL(url);
	 * HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
	 * 
	 * // add reuqest header con.setRequestMethod("POST");
	 * con.setRequestProperty("User-Agent", USER_AGENT);
	 * con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	 * 
	 * String urlParameters = "profile_ids[]=" + userId;
	 * 
	 * // Send post request con.setDoOutput(true); DataOutputStream wr = new
	 * DataOutputStream(con.getOutputStream()); wr.writeBytes(urlParameters);
	 * wr.flush(); wr.close();
	 * 
	 * int responseCode = con.getResponseCode(); BufferedReader in = new
	 * BufferedReader(new InputStreamReader(con.getInputStream())); String
	 * inputLine; StringBuffer response = new StringBuffer();
	 * 
	 * while ((inputLine = in.readLine()) != null) { response.append(inputLine);
	 * } in.close(); } catch (Exception e) { e.printStackTrace(); } }
	 */

}
