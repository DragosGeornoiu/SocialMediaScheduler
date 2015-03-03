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
	private static final String USER_ID = "54f4480b76a9a2b75cb71256";
	private final String ACCESS_TOKEN = "1/8793662c87b64d9d96d519cb84227de0";
	private final String USER_AGENT = "Mozilla/5.0";

	public void authenticate() {
		String url = "https://api.bufferapp.com/1/profiles.json?access_token=" + ACCESS_TOKEN;

		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			// add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = con.getResponseCode();

			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Posts the specified message at the timeToPost date.
	 * 
	 * @param message
	 *            the message to be posted.
	 * @param timeToPost
	 *            the time at which to post the message.
	 */
	public void sendMessage(String message, String timeToPost) {

		try {
			// yyyy-MM-dd'T'HH:mm:ss
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = dateFormat.parse(timeToPost);
			long time = date.getTime();
			String sheduletAt = new Timestamp(time).toString();

			String url = "https://api.bufferapp.com/1/updates/create.json?access_token=" + ACCESS_TOKEN;
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			// add reuqest header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			String urlParameters = "text=" + message + "&profile_ids[]=" + USER_ID + "&scheduled_at=" + sheduletAt;

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + urlParameters);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Posts the specified message now.
	 * 
	 * @param message
	 *            to be posted.
	 */
	public void sendMessageNow(String message) {
		try {

			String url = "https://api.bufferapp.com/1/updates/create.json?access_token=" + ACCESS_TOKEN;
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			// add reuqest header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			String urlParameters = "text=" + message + "&profile_ids[]=" + USER_ID + "&now=true";

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + urlParameters);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns all the updates posted with Buffer API.
	 */
	// https://api.bufferapp.com/1/profiles/54f4480b76a9a2b75cb71256/updates/sent.json?access_token=1/8793662c87b64d9d96d519cb84227de0

	public void getUpdates() {

		String url = "https://api.bufferapp.com/1/profiles/" + USER_ID + "/updates/sent.json?access_token="
				+ ACCESS_TOKEN;

		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			// add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = con.getResponseCode();

			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Returns all the updates pending to be posted.
	 */
	// https://api.bufferapp.com/1/profiles/4eb854340acb04e870000010/updates/pending.jso
	public void getPendingUpdates() {
		String url = "https://api.bufferapp.com/1/profiles/" + USER_ID + "/updates/pending.json?access_token="
				+ ACCESS_TOKEN;

		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			// add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = con.getResponseCode();

			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Returns the details of a specific update.
	 * 
	 * @param updateId
	 *            String by which the update is recognized.
	 */
	// 54f5747827a8114256bb98eb
	public void getSpecificUpdate(String updateId) {

		String url = "https://api.bufferapp.com/1/updates/" + updateId + ".json?access_token=" + ACCESS_TOKEN;

		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			// add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = con.getResponseCode();

			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Deletes a specific update
	 * 
	 * @param id
	 *            String by which the update is recognized.
	 */
	// https://api.bufferapp.com/1/updates/4ecda256512f7ee521000004/destroy.json
	public void deleteUpdate(String id) {
		try {

			String url = "https://api.bufferapp.com/1/updates/" + id + "/destroy.json?access_token=" + ACCESS_TOKEN;
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			// add reuqest header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			String urlParameters = "profile_ids[]=" + USER_ID;

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + urlParameters);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
