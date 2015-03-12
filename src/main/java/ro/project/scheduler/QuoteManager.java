package ro.project.scheduler;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
	public Quote getRandomQuoteForTwitter() {
		Quote quote = getRandomQuote(FILE_TWITTER);
		
		if(quote==null)
			return null;
		while (quote.toString().length() > 140) {
			quote = getRandomQuote(FILE_TWITTER);
		}
		return quote;
	}

	/**
	 * Returns a random quote to be posted on Facebook.
	 * 
	 * @return String representing a quote.
	 */
	public Quote getRandomQuoteForFacebook() {
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
	public Quote getRandomQuote(String fileName) {
		Quote quote = null;

		/*
		 * List<Quote> quotesList = new ArrayList<Quote>(); BufferedReader br =
		 * null; try { br = new BufferedReader(new FileReader(quotesFile));
		 * String line; while ((line = br.readLine()) != null) {
		 * quotesList.add(new Quote(line.split(" - ")[0],
		 * line.split(" - ")[1])); } br.close(); } catch (Exception e) {
		 * e.printStackTrace(); }
		 */

		Hashtable<String, Quote> quotesList = new Hashtable<String, Quote>();
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(quotesFile));
			quotesList.putAll((Hashtable<String, Quote>) in.readObject());
			in.close();
		} catch (Exception e) {
		}

		int size = quotesList.size();
		int j = 0; // for not entering a never ending loop
		boolean endCondition = false;
		do {
			if ((quotesList == null) || (quotesList.size() == 0)) {
				return null;
			}
			/*
			 * Random rand = new Random(); int randomNum =
			 * rand.nextInt(quotesList.size()); quote =
			 * quotesList.get(randomNum); quotesList.remove(randomNum);
			 */

			// The random is really not good

			j++;
			int item = new Random().nextInt(size);
			int i = 0;
			Set<Map.Entry<String, Quote>> entrySet = quotesList.entrySet();
			for (Map.Entry<String, Quote> obj : entrySet) {
				if (i == item)
					quote = obj.getValue();
				i = i + 1;
			}

			quotesList.remove(quote.getMD5());
		} while ((endCondition = checkIfQuotePostedBefore(quote, fileName, j)));

		if ((endCondition) || (quote.getQuote().trim().isEmpty())) {
			return null;
		} else {
			quote.setQuote(quote.getQuote().replaceAll(" ", "+").replaceAll("’", "'"));
			return quote;
			// REMEMBER: return (quote.split(" - ")[0] + " - " +
			// quote.split(" - ")[1]).replaceAll(" ", "+").replaceAll("’", "'");
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
	private void saveQuote(Hashtable<String, Quote> quotesList, String fileName) {
		try {
			FileOutputStream fileOut = new FileOutputStream(fileName);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(quotesList);
			out.close();
			fileOut.close();
		} catch (IOException ex) {
			ex.printStackTrace();
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
	private boolean checkIfQuotePostedBefore(Quote quote, String fileName, int j) {
		Hashtable<String, Quote> quotes = new Hashtable<String, Quote>();
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
			quotes = (Hashtable<String, Quote>) in.readObject();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (quotes.containsKey(quote.getMD5())) {
			return true;
		} else {
			quotes.put(quote.getMD5(), quote);
			saveQuote(quotes, fileName);
			return false;
		}

		/*
		 * BufferedReader br = null; try { br = new BufferedReader(new
		 * FileReader(fileName)); String line; while ((line = br.readLine()) !=
		 * null) { if (line.equals(quote.getMD5())) { return true; } }
		 * br.close(); } catch (Exception e) { e.printStackTrace(); } finally {
		 * try { br.close(); } catch (IOException e) { e.printStackTrace(); } }
		 * return false;
		 */
	}

}
