package ro.project.scheduler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Random;

public class QuoteManager {
	private final String FILE_TWITTER = "D:/workspace/SocialMediaScheduler/src/main/resources/quotes/twitterQuotes.txt";
	private final String FILE_FACEBOOK = "D:/workspace/SocialMediaScheduler/src/main/resources/quotes/facebookQuotes.txt";
	private String quotesFile;

	public QuoteManager(String quotesFile) {
		this.quotesFile = "D:/workspace/SocialMediaScheduler/src/main/resources/quotes/" + quotesFile;
	}

	/**
	 * Returns a random quote to be posted on Twitter.
	 * 
	 * @return String representing a quote under 140 characters.
	 */
	public String getRandomQuoteForTwitter() {
		String quote = getRandomQuote(FILE_TWITTER);
		while (quote.length() > 140) {
			quote = getRandomQuote(FILE_TWITTER);
		}
		return quote;
	}

	/**
	 * Returns a random quote to be posted on Facebook.
	 * 
	 * @return String representing a quote.
	 */
	public String getRandomQuoteForFacebook() {
		return getRandomQuote(FILE_FACEBOOK);
	}

	/**
	 * Returns a random quote which has not been posted to the social network
	 * provided by fileName.
	 * 
	 * @param fileName
	 *            the location of the text file where the previous quotes on
	 *            that specific social network were stored.
	 * 
	 * @return String representing a random quote.
	 */
	public String getRandomQuote(String fileName) {
		Quote quote;

		/*List<Quote> quotesList = new ArrayList<Quote>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(quotesFile));
			String line;
			while ((line = br.readLine()) != null) {
				quotesList.add(new Quote(line.split(" - ")[0], line.split(" - ")[1]));
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}*/

		
		
		 List<Quote> quotesList = null;
		    try {
		        ObjectInputStream in = new ObjectInputStream(new FileInputStream(quotesFile));
		        quotesList = (List<Quote>) in.readObject(); 
		        in.close();
		    }
		    catch(Exception e) {}
		
		
		do {
			if (quotesList.size() == 0) {
				return "";
			}
			Random rand = new Random();
			int randomNum = rand.nextInt(quotesList.size());
			quote = quotesList.get(randomNum);
			quotesList.remove(randomNum);

		} while ((checkIfQuotePostedBefore(quote, fileName)));

		saveQuoteHashing(quote, fileName);

		if (quote.getQuote().trim().isEmpty()) {
			return "";
		} else {
			return quote.toString().replaceAll(" ", "+").replaceAll("’", "'");
			// REMEMBER: return (quote.split(" - ")[0] + " - " + quote.split(" - ")[1]).replaceAll(" ", "+").replaceAll("’", "'");
		}
	}

	/**
	 * Saves the quote so that in the future the same quote won't be posted on
	 * the same social network in was previously posted.
	 * 
	 * @param quote
	 *            String representing the actual quote.
	 * @param fileName
	 *            String representing the location where previously posted
	 *            quotes on a specific social network were posted.
	 */
	private void saveQuoteHashing(Quote quote, String fileName) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
			out.println(quote.getMD5());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Check if the given quote was posted previously on the specific social
	 * network.
	 * 
	 * @param quote
	 *            String representing the actual quote.
	 * @param fileName
	 *            String representing the location where previously posted
	 *            quotes on the specific social network were posted.
	 * @return true if it was posted previously, false if not.
	 */
	private boolean checkIfQuotePostedBefore(Quote quote, String fileName) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.equals(quote.getMD5())) {
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
