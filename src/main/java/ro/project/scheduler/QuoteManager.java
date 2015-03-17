package ro.project.scheduler;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import ro.project.parser.PersdevParser;

public class QuoteManager {
	final static Logger logger = Logger.getLogger(QuoteManager.class);
	private final String FILE = "D:/workspace/SocialMediaScheduler/src/main/resources/quotes/";
	private String quotesFile;

	public QuoteManager(String quotesFile) {
		this.quotesFile = "D:/workspace/SocialMediaScheduler/src/main/resources/quotes/" + quotesFile;
	}

	public Quote getRandomQuote(String where, int max) {
		Quote quote;
		do {
			quote = getRandomQuoteForSocialMedia(FILE + where.toLowerCase() + "quotes.txt");

			if (quote == null) {
				return null;
			}
		} while (quote.toString().length() > max);

		return quote;
	}

	private Quote getRandomQuoteForSocialMedia(String fileName) {
		Quote quote = null;

		Hashtable<String, Quote> quotesList = new Hashtable<String, Quote>();
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(quotesFile));
			quotesList.putAll((Hashtable<String, Quote>) in.readObject());
			in.close();
		} catch (Exception e) {
			logger.error("Deserialisation of quotes hashtable unsuccesfull", e);
		}
		List<Quote> randomQuotesList = new ArrayList<Quote>(quotesList.values());

		boolean endCondition = false;
		do {
			if ((randomQuotesList == null) || (randomQuotesList.size() == 0)) {
				return null;
			}

			Random rand = new Random();
			int randomNum = rand.nextInt(randomQuotesList.size());
			quote = randomQuotesList.get(randomNum);
			randomQuotesList.remove(randomNum);

			randomQuotesList.remove(quote.getMD5());
		} while ((endCondition = checkIfQuotePostedBefore(quote, fileName)));

		if ((endCondition) || (quote.getQuote().trim().isEmpty())) {
			return null;
		} else {
			quote.setQuote(quote.getQuote().replaceAll(" ", "+").replaceAll("’", "'"));
			return quote;
			// return (quote.split(" - ")[0] + " - " +
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
		} catch (IOException e) {
			//ex.printStackTrace();
			logger.error("Serialisation of hashtable of quotes unsuccesfull", e);
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
		Hashtable<String, Quote> quotes = new Hashtable<String, Quote>();
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
			quotes = (Hashtable<String, Quote>) in.readObject();
			in.close();
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("Deserialisation of already posted quotes unsuccesfull.", e);
		}

		if (quotes.containsKey(quote.getMD5())) {
			return true;
		} else {
			quotes.put(quote.getMD5(), quote);
			saveQuote(quotes, fileName);
			return false;
		}
	}

}
