package ro.project.scheduler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuoteManager {

	/**
	 * fileName String representing the file name where the id's of the posted
	 * quotes are stored
	 */
	private final String FILE_TWITTER = "D:/workspace/SocialMediaScheduler/src/main/resources/postedTwitter.txt";
	private final String FILE_FACEBOOK = "D:/workspace/SocialMediaScheduler/src/main/resources/postedFacebook.txt";
	

	public String getRandomQuoteForTwitter() {
		String quote = getRandomQuote(FILE_TWITTER);
		while(quote.length() > 140) {
			quote = getRandomQuote(FILE_TWITTER);
		}
		return quote;
	}

	public String getRandomQuoteForFacebook() {
		return getRandomQuote(FILE_FACEBOOK);
	}

	/**
	 * Returns a random quote that has not been posted before.
	 * 
	 * @return String representing a quote.
	 */
	public String getRandomQuote(String fileName) {
		String quote;

		List<String> quotesList = new ArrayList<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("D:/workspace/SocialMediaScheduler/src/main/resources/quotes.txt"));
			String line;
			while ((line = br.readLine()) != null) {
				quotesList.add(line);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		do {
			Random rand = new Random();
			int randomNum = rand.nextInt(quotesList.size());
			quote = quotesList.get(randomNum);
		} while ((checkIfQuotePostedBefore(quote, fileName)));

		saveQuoteId(quote, fileName);

		return (quote.split(" - ")[1] + " - " + quote.split(" - ")[2]).replaceAll(" ", "+").replaceAll("’", "'");
	}

	/**
	 * Saves the quote id in the fileName file.
	 * 
	 * @param quote
	 *            String representing the quote from which we can take the id.
	 */
	private void saveQuoteId(String quote, String fileName) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
			out.println(quote.split(" - ")[0]);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks if the quote has been posted before or is bigger than 160
	 * characters.
	 * 
	 * @param quote
	 *            String representing the quote.
	 * @return true if quote is under 160 characters and has not been posted
	 *         before, false otherwise.
	 */
	private boolean checkIfQuotePostedBefore(String quote, String fileName) {
		String id = quote.split(" - ")[0];

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.equals(id)) {
					return true;
				}
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

}
