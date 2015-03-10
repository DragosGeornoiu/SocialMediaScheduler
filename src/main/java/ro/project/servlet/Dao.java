package ro.project.servlet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import ro.project.scheduler.Scheduler;

public class Dao {
	private Scheduler scheduler;

	public List<String> getPostedQuotes(int from, int indexesPerPage) {

		scheduler = new Scheduler();
		int j;
		List<String> quotes = null;

		try {

			String jString = scheduler.getFacebookUpdates(1);
			JSONObject jsonObject = new JSONObject(jString);
			int totalFacebook = jsonObject.getInt("total");
			jString = scheduler.getTwitterUpdates(1);
			jsonObject = new JSONObject(jString);
			int totalTwitter = jsonObject.getInt("total");
			int total = totalFacebook + totalTwitter;

			quotes = new ArrayList<>();
			String result = "";

			// facebook - 51
			// twitter - 39
			// from 40 - 60; from is 40, indexesPerPage is 20

			if (totalFacebook >= (from + indexesPerPage)) {
				// pe pagina aia se returneaza numai de la facebook;

				// din json returneaza 20 pe pagina
				// am nevoie de la 30 la 40

				// Json 1 Json 2 Json 3
				// 1+10; 10+10; 20+10; 30+10; 40+10;50+10

				// 15 - 25
				// 15->20 imi ia
				// 21-25
				jString = scheduler.getFacebookUpdates((from + indexesPerPage - 1) / 20);

			} else if ((totalFacebook >= from) && (totalFacebook <= (indexesPerPage + from))) {
				// pe pagina aia se returneaza o parte de pe facebook, o parte
				// de pe twitter

				// x -> y
				// situatii:
				// 1 -> 10
				// 31 -> 40
				// 36 - > 45

				jString = scheduler.getFacebookUpdates((from - 1) / 20 + 1);
				jsonObject = new JSONObject(jString);
				JSONArray updates = jsonObject.getJSONArray("updates");
				for (int i = from % 20; i < 20; i++) {
					if ((i < from % 20 + indexesPerPage) && (i < totalFacebook)) {
						JSONObject update = updates.getJSONObject(i);
						result = "";
						result += " <BR> ";
						result += "Count: " + (i + 1) + " <BR> ";
						Calendar c = Calendar.getInstance();
						c.setTimeInMillis(new Long(((int) update.getInt("due_at"))) * 1000);
						int mYear = c.get(Calendar.YEAR);
						result += "Due at: " + update.get("due_time") + "; " + update.get("day") + "; " + mYear
								+ " <BR> ";
						result += "Service: " + update.get("profile_service") + " <BR> ";
						result += "Text: " + update.get("text") + " <BR> ";
						quotes.add(result);
					}
				}

				if ((from + indexesPerPage) / 20 > (from / 20)) {
					jString = scheduler.getFacebookUpdates((from + indexesPerPage) / 20 + 1);
					jsonObject = new JSONObject(jString);
					updates = jsonObject.getJSONArray("updates");
					for (int i = 0; i < (from + indexesPerPage) % 20; i++) {
						JSONObject update = updates.getJSONObject(i);
						result = "";
						result += " <BR> ";
						result += "Count: " + (i + 1) + " <BR> ";
						Calendar c = Calendar.getInstance();
						c.setTimeInMillis(new Long(((int) update.getInt("due_at"))) * 1000);
						int mYear = c.get(Calendar.YEAR);
						result += "Due at: " + update.get("due_time") + "; " + update.get("day") + "; " + mYear
								+ " <BR> ";
						result += "Service: " + update.get("profile_service") + " <BR> ";
						result += "Text: " + update.get("text") + " <BR> ";
						quotes.add(result);
					}
				}

				from = 0;
				indexesPerPage = 10 - quotes.size();

				jString = scheduler.getTwitterUpdates((from - 1) / 20 + 1);
				jsonObject = new JSONObject(jString);
				updates = jsonObject.getJSONArray("updates");
				for (int i = from % 20; i < 20; i++) {
					if ((i < from % 20 + indexesPerPage) && (i < totalTwitter)) {
						System.out.println("from: " + from);
						System.out.println("i: " + i);
						System.out.println("i%20: " + i % 20);
						System.out.println("ffrom % 20 + indexesPerPage: " + (from % 20 + indexesPerPage));
						JSONObject update = updates.getJSONObject(i);
						result = "";
						result += " <BR> ";
						result += "Count: " + (i + 1) + " <BR> ";
						Calendar c = Calendar.getInstance();
						c.setTimeInMillis(new Long(((int) update.getInt("due_at"))) * 1000);
						int mYear = c.get(Calendar.YEAR);
						result += "Due at: " + update.get("due_time") + "; " + update.get("day") + "; " + mYear
								+ " <BR> ";
						result += "Service: " + update.get("profile_service") + " <BR> ";
						result += "Text: " + update.get("text") + " <BR> ";
						quotes.add(result);
					}
				}

				if ((from + indexesPerPage) / 20 > (from / 20)) {
					jString = scheduler.getTwitterUpdates((from + indexesPerPage) / 20 + 1);
					jsonObject = new JSONObject(jString);
					updates = jsonObject.getJSONArray("updates");
					for (int i = 0; i < (from + indexesPerPage) % 20; i++) {
						JSONObject update = updates.getJSONObject(i);
						result = "";
						result += " <BR> ";
						result += "Count: " + (i + 1) + " <BR> ";
						Calendar c = Calendar.getInstance();
						c.setTimeInMillis(new Long(((int) update.getInt("due_at"))) * 1000);
						int mYear = c.get(Calendar.YEAR);
						result += "Due at: " + update.get("due_time") + "; " + update.get("day") + "; " + mYear
								+ " <BR> ";
						result += "Service: " + update.get("profile_service") + " <BR> ";
						result += "Text: " + update.get("text") + " <BR> ";
						quotes.add(result);
					}
				}

			} else if (totalFacebook < from) {
				jString = scheduler.getTwitterUpdates((from - 1 - totalFacebook) / 20 + 1);
				jsonObject = new JSONObject(jString);
				JSONArray updates = jsonObject.getJSONArray("updates");
				for (int i = from % 20; i < 20; i++) {
					if ((i < from % 20 + indexesPerPage) && (i < totalTwitter)) {
						System.out.println("from: " + from);
						System.out.println("i: " + i);
						System.out.println("i%20: " + i % 20);
						System.out.println("ffrom % 20 + indexesPerPage: " + (from % 20 + indexesPerPage));
						JSONObject update = updates.getJSONObject(i);
						result = "";
						result += " <BR> ";
						result += "Count: " + (i + 1) + " <BR> ";
						Calendar c = Calendar.getInstance();
						c.setTimeInMillis(new Long(((int) update.getInt("due_at"))) * 1000);
						int mYear = c.get(Calendar.YEAR);
						result += "Due at: " + update.get("due_time") + "; " + update.get("day") + "; " + mYear
								+ " <BR> ";
						result += "Service: " + update.get("profile_service") + " <BR> ";
						result += "Text: " + update.get("text") + " <BR> ";
						quotes.add(result);
					}
				}

				if ((from + indexesPerPage) / 20 > (from / 20)) {
					jString = scheduler.getTwitterUpdates((from + indexesPerPage) / 20 + 1);
					jsonObject = new JSONObject(jString);
					updates = jsonObject.getJSONArray("updates");
					for (int i = 0; i < (from + indexesPerPage) % 20; i++) {
						JSONObject update = updates.getJSONObject(i);
						result = "";
						result += " <BR> ";
						result += "Count: " + (i + 1) + " <BR> ";
						Calendar c = Calendar.getInstance();
						c.setTimeInMillis(new Long(((int) update.getInt("due_at"))) * 1000);
						int mYear = c.get(Calendar.YEAR);
						result += "Due at: " + update.get("due_time") + "; " + update.get("day") + "; " + mYear
								+ " <BR> ";
						result += "Service: " + update.get("profile_service") + " <BR> ";
						result += "Text: " + update.get("text") + " <BR> ";
						quotes.add(result);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return quotes;
	}

	public int getNoOfRecords() {
		int total = -1;
		try {
			String jString = scheduler.getFacebookUpdates(1);
			JSONObject jsonObject = new JSONObject(jString);
			int totalFacebook = jsonObject.getInt("total");
			jString = scheduler.getTwitterUpdates(1);
			jsonObject = new JSONObject(jString);
			int totalTwitter = jsonObject.getInt("total");
			total = totalFacebook + totalTwitter;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return total;
	}

}
