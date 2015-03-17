package ro.project.parser;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.select.Elements;

import ro.project.scheduler.Quote;

public abstract class Parser {
	final static Logger logger = Logger.getLogger(Parser.class);
	protected FileManager fileManager;
	protected String path;
	protected final String PATH = "D:\\\\workspace\\\\SocialMediaScheduler\\\\src\\\\main\\\\resources\\\\quotes";

	/**
	 * Gets all quotes from a given link, if the web site was already parsed, it
	 * just updates with new added quotes, if they exist.
	 * 
	 * @param website
	 *            String representing the website to be parsed
	 * 
	 * @return path created from the PATH variable and the name of the text file
	 *         formed from the web site name.
	 */
	public boolean updateQuotes(String website) {
		fileManager = new FileManager();
		String fileName = fileManager.createFileNameFromUrl(website);

		path = fileManager.createFileInPath(fileName + ".ser");
		if (path.trim().isEmpty()) {
			return false;
		}
		Hashtable<String, Quote> newQuotes = new Hashtable<String, Quote>();
		Hashtable<String, Quote> tempQuotes = new Hashtable<String, Quote>();

		String url = website;
		newQuotes = getQuotesFromFile(path);

		if (newQuotes.size() == 0) {
			saveWebsiteAsOption(website);
			saveQuotesFromWebsite(website);
		} else {
			/*
			 * String quote = newQuotes.get(0).getQuote();
			 */
			boolean endCondition = false;
			while (!endCondition) {

				Hashtable<String, Quote> pageQuotes = getQuotesFromPage(url);
				url = getPreviousPageLink(url);

				Set<Map.Entry<String, Quote>> entrySet = pageQuotes.entrySet();
				Iterator<Entry<String, Quote>> it = entrySet.iterator();
				while (it.hasNext()) {
					// System.out.println(((Map.Entry<String,Quote>)it.next()).getKey());
					Map.Entry<String, Quote> entry = (Map.Entry<String, Quote>) it.next();
					if (newQuotes.contains(entry.getValue())) {
						endCondition = true;
						break;
					}
					tempQuotes.put(entry.getValue().getMD5(), entry.getValue());
				}

				/*
				 * Set entrySet = pageQuotes.entrySet(); Iterator it =
				 * entrySet.iterator(); while (it.hasNext()) { if
				 * (quote.equals(((Quote)it.next()
				 * ;pageQuotes.get(i).getQuote())) { endCondition = true; break;
				 * } else { tempQuotes.add(pageQuotes.get(i)); }
				 * 
				 * }
				 */

				/*
				 * for (int i = 0; i < pageQuotes.size(); i++) { if
				 * (quote.equals(pageQuotes.get(i).getQuote())) { endCondition =
				 * true; break; } else { tempQuotes.add(pageQuotes.get(i)); } }
				 */
			}

			newQuotes.putAll(tempQuotes);
			saveQuotesToFile(newQuotes);
		}
		return true;
	}

	/**
	 * Saves all quotes from a web site to a text file.
	 * 
	 * @param website
	 *            String representing the name of the web site.
	 */
	protected void saveQuotesFromWebsite(String website) {
		Hashtable<String, Quote> quotesList = new Hashtable<String, Quote>();
		quotesList.putAll(parseWebsiteForQuotes(website));
		saveQuotesToFile(quotesList);
	}

	/**
	 * Parse the URL and return all quotes from that page as Strings in a List.
	 * 
	 * @param url
	 *            the URl parsed.
	 * @return the quotes as a List of type String.
	 */
	protected Hashtable<String, Quote> parseWebsiteForQuotes(String url) {
		Hashtable<String, Quote> allQuotesFromWebsite = new Hashtable<String, Quote>();
		do {
			Hashtable<String, Quote> temp = getQuotesFromPage(url);
			allQuotesFromWebsite.putAll(temp);
			url = getPreviousPageLink(url);
		} while (!url.trim().isEmpty());

		return allQuotesFromWebsite;
	}

	/**
	 * Provided by each parser must be the method to select all quotes from a
	 * page.
	 * 
	 * @param url
	 *            the URL to get the quotes from.
	 * 
	 * @return the quotes as a List of type String.
	 */
	protected abstract Hashtable<String, Quote> getQuotesFromPage(String url);

	protected abstract Hashtable<String, Quote> getQuotesAsList(Elements elements);

	protected abstract String getPreviousPageLink(String url);

	/**
	 * Returns all quotes stored in file as a List<String>.
	 * 
	 * @param fileName
	 *            String representing the name of the file.
	 * @return List<String> representing all the quotes from the file.
	 */
	protected Hashtable<String, Quote> getQuotesFromFile(String fileName) {
		Hashtable<String, Quote> quotes = new Hashtable<String, Quote>();
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
			quotes = (Hashtable<String, Quote>) in.readObject();
			in.close();
		} catch (Exception e) {
			logger.error("Deserialisation of quotes hashtable unsuccesfull.", e);
		}
		return quotes;
	}

	/**
	 * Saves all the quotes stored in the list in the quotes.txt file
	 * 
	 * @param quotesList
	 *            the list of quotes.
	 */
	protected void saveQuotesToFile(Hashtable<String, Quote> quotesList) {
		try {
			FileOutputStream fileOut = new FileOutputStream(path);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(quotesList);
			out.close();
			fileOut.close();
		} catch (IOException e) {
			logger.error("Problem in serialising the quotes hashtable", e);
		}
	}

	/**
	 * Saves the web site as a option.
	 * 
	 * @param website
	 *            the URL to save as a option to post from.
	 */
	protected void saveWebsiteAsOption(String website) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(PATH + "\\\\" + "parser.txt", true)));
			out.println(website);
			out.close();
		} catch (IOException e) {
			//e.printStackTrace();
			logger.error("Problem in saving parsed website as option for quote retrieving", e);
		}
	}

}
