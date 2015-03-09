package ro.project.scheduler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class Scheduler {
	private String userId = "54f4480b76a9a2b75cb71256";
	private final String ACCESS_TOKEN = "1/8793662c87b64d9d96d519cb84227de0";
	private final String USER_AGENT = "Mozilla/5.0";

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
			timeToPost += "GMT-06:00";
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssz");
			Date date = dateFormat.parse(timeToPost);
			long time = date.getTime();
			String sheduletAt = new Timestamp(time).toString();

			url = "https://api.bufferapp.com/1/updates/create.json?access_token=" + ACCESS_TOKEN;
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
	 * @return  String representing the Twitter updates.
	 */
	public String getTwitterUpdates(int page) {
		userId = "54f4480b76a9a2b75cb71256";
		return getUpdates(page);
	}

	/**
	 * Returns the facebook updates.
	 * 
	 * @param page
	 *            The number of the page to be returned. If there are more than
	 *            20 facebook updates, they won't be returned in a single JSON.
	 *            
	 * @return  String representing the Facebook updates.
	 */
	public String getFacebookUpdates(int page) {
		userId = "54f5cffee090e41029541d73";
		return getUpdates(page);
	}

	/**
	 * Returns the updates posted with Buffer API..
	 * 
	 * @param page
	 *            The number of the page to be returned. If there are more than
	 *            20 updates, they won't be returned in a single JSON.
	 *            
	 * @return  String representing the updates.
	 */
	public String getUpdates(int page) {

		String url = "https://api.bufferapp.com/1/profiles/" + userId + "/updates/sent.json?" + "page=" + page
				+ "&access_token=" + ACCESS_TOKEN;

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

	/**
	 * Returns the pending twitter updates.
	 * 
	 * @param page
	 *            The number of the page to be returned. If there are more than
	 *            20 pending twitter updates, they won't be returned in a single JSON.
	 *            
	 * @return  String representing the pending Twitter updates.
	 */
	public String getTwitterPendingUpdates(int page) {
		userId = "54f4480b76a9a2b75cb71256";
		return getPendingUpdates(page);
	}

	/**
	 * Returns the pending facebook updates.
	 * 
	 * @param page
	 *            The number of the page to be returned. If there are more than
	 *            20 pending facebook updates, they won't be returned in a single JSON.
	 *            
	 * @return  String representing the pending facebook updates.
	 */
	public String getFacebookPendingUpdates(int page) {
		userId = "54f5cffee090e41029541d73";
		return getPendingUpdates(page);
	}

	/**
	 * Returns the pending updates created with Buffer API..
	 * 
	 * @param page
	 *            The number of the page to be returned. If there are more than
	 *            20 pending updates, they won't be returned in a single JSON.
	 *            
	 * @return  String representing the pending updates.
	 */
	public String getPendingUpdates(int page) {
		String url = "https://api.bufferapp.com/1/profiles/" + userId + "/updates/pending.json?" + "page=" + page
				+ "&access_token=" + ACCESS_TOKEN;
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
			e.printStackTrace();
		}

		return response.toString();

	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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
	 * public void authenticate() { String url =
	 * "https://api.bufferapp.com/1/profiles.json?access_token=" + ACCESS_TOKEN;
	 * 
	 * try { URL obj = new URL(url); HttpURLConnection con = (HttpURLConnection)
	 * obj.openConnection(); con.setRequestMethod("GET");
	 * con.setRequestProperty("User-Agent", USER_AGENT); BufferedReader in = new
	 * BufferedReader(new InputStreamReader(con.getInputStream())); String
	 * inputLine; StringBuffer response = new StringBuffer();
	 * 
	 * while ((inputLine = in.readLine()) != null) { response.append(inputLine);
	 * } in.close(); } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * }
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
