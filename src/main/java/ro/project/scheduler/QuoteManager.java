package ro.project.scheduler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuoteManager {
	private final String FILE_TWITTER = "D:/workspace/SocialMediaScheduler/src/main/resources/quotes/twitterQuotes.txt";
	private final String FILE_FACEBOOK = "D:/workspace/SocialMediaScheduler/src/main/resources/quotes/facebookQuotes.txt";
	private String quotesFile;

	public QuoteManager(String quotesFile) {
		this.quotesFile = "D:/workspace/SocialMediaScheduler/src/main/resources/quotes/" + quotesFile;
	}

	public String getRandomQuoteForTwitter() {
		String quote = getRandomQuote(FILE_TWITTER);
		System.out.println("quote length: " + quote.length());
		while (quote.length() > 140) {
			System.out.println("Measurement of length: " + quote);
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
			br = new BufferedReader(new FileReader(quotesFile));
			String line;
			while ((line = br.readLine()) != null) {
				quotesList.add(line);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		do {
			if (quotesList.size() == 0) {
				return "";
			}
			Random rand = new Random();
			int randomNum = rand.nextInt(quotesList.size());
			quote = quotesList.get(randomNum);
			quotesList.remove(randomNum);

		} while ((checkIfQuotePostedBefore(quote, fileName)));

		saveQuoteId(quote, fileName);

		if (quote.trim().isEmpty()) {
			return "";
		} else {
			return (quote.split(" - ")[0] + " - " + quote.split(" - ")[1]).replaceAll(" ", "+").replaceAll("’", "'");
		}
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
			out.println(hashToMd5(quote.split(" - ")[0]));
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
		id = hashToMd5(id);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println("line: " + line);
				System.out.println("id: " + id);
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

	private String hashToMd5(String quote) {

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md.update(quote.getBytes());

		byte byteData[] = md.digest();

		// convert the byte to hex format method 1
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}
}
