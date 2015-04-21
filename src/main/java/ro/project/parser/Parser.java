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

import ro.project.Constants;
import ro.project.scheduler.Quote;

/**
 * 
 * @author Caphyon1
 *
 *         Abstract class Parser defines the parsing logic for the application
 *         and forces the subclasses to retrieve the quotes from the web site
 *         and the URL for next/previous page.
 * 
 */
public abstract class Parser {
	final static Logger logger = Logger.getLogger(Parser.class);
	/**
	 * fileManager is used to create a unique name from the given URL for
	 * storing the retrieved quotes.
	 */
	protected FileManager fileManager;
	/** path represents where the quotes are saved. */
	protected String path;
	/**
	 * optionPath is the path where the URL of the already parsed web sites are
	 * stored.
	 */
	protected String optionPath;

	/**
	 * Gets all quotes from a given link, if the web site was already parsed, it
	 * just updates with new added quotes, if they exist.
	 * 
	 * @param website
	 *            String representing the web site to be parsed.
	 * @param optionPath
	 *            String representing the path where the URL of the already
	 *            parsed web sites are stored.
	 * 
	 * @return true if the URL can be parsed, false otherwise.
	 */
	public boolean parseWebsite(String website, String optionPath) {
		this.optionPath = optionPath + Constants.QUOTES_FILE;
		fileManager = new FileManager(optionPath);
		String fileName = fileManager.createFileNameFromUrl(website);

		path = fileManager.createFileInPath(fileName + ".xml");
		if (path.trim().isEmpty()) {
			return false;
		}
		Hashtable<String, Quote> newQuotes = new Hashtable<String, Quote>();
		Hashtable<String, Quote> tempQuotes = new Hashtable<String, Quote>();

		String url = website;
		newQuotes = getQuotesFromFile(path);

		logger.info("Started the parsing proccess for " + website);
		if (newQuotes.size() == 0) {
			
			logger.info("Saving the quotes retrieved from " + website);
			saveQuotesFromWebsite(website);
			logger.info("Saving the website as parsed for future usage. " + website);
			saveWebsiteAsOption(fileName + ".xml");
		} else {
			boolean endCondition = false;
			while (!endCondition) {

				Hashtable<String, Quote> pageQuotes = getQuotesFromPage(url);
				url = getPreviousPageLink(url);

				Set<Map.Entry<String, Quote>> entrySet = pageQuotes.entrySet();
				Iterator<Entry<String, Quote>> it = entrySet.iterator();
				while (it.hasNext()) {
					Map.Entry<String, Quote> entry = (Map.Entry<String, Quote>) it.next();
					if (newQuotes.contains(entry.getValue())) {
						endCondition = true;
						break;
					}
					tempQuotes.put(entry.getValue().getMD5(), entry.getValue());
				}
			}

			newQuotes.putAll(tempQuotes);
			saveQuotesToFile(newQuotes);
		}
		return true;
	}

	/**
	 * Retrieves the quotes from the given URL and saves them.
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
	 * Parses the given URL and returns all quotes from that page.
	 * 
	 * @param url
	 *            the URl to be parsed.
	 * @return the quotes as a Hashtable of type <String,Quote>.
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
	 * Provided by each parser extending Parser class must be the method to select all quotes from a
	 * page.
	 * 
	 * @param url
	 *            the URL to the page where the quotes are located.
	 * 
	 * @return the quotes as a Hashtable of type <String, Quote>.
	 */
	protected abstract Hashtable<String, Quote> getQuotesFromPage(String url);

	/**
	 * The quotes are returned in a Hashtable of type <String, Quote>.
	 * 
	 * @param elements represent the quotes as org.jsoup.select.Elements.
	 * 
	 * @return the quotes as a hashtable of type <String, Quote>.
	 */
	protected abstract Hashtable<String, Quote> getQuotesAsHashtable(Elements elements);

	/**
	 * Returns the URL for next/previous page.
	 * 
	 * @param url of current page; 
	 * 
	 * @return url of next/previous page.
	 */
	protected abstract String getPreviousPageLink(String url);

	/**
	 * Retrieves all quotes stored in file fileName.
	 * 
	 * @param fileName
	 *            String representing the name of the file.
	 * @return the quotes as a Hashtable of type <String, Quote>.
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
	 * Saves all the quotes from the Hashtable<String, Quote> to the fileOut file.
	 * 
	 * @param quotesList the Hashtable of type <String, Quote> representing the retrieved quotes.
	 * 
	 */
	protected void saveQuotesToFile(Hashtable<String, Quote> quotesList) {
		try {
			//aici ar trebui sa le salvez in xml 
			//here
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
	 * Saves the web site to further know that it was already parsed.
	 * 
	 * @param website
	 *            the URL to save as a option to post from.
	 */
	protected void saveWebsiteAsOption(String website) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(optionPath + "\\\\" + "parser.txt",
					true)));
			out.println(website);
			out.close();
		} catch (IOException e) {
			logger.error("Problem in saving parsed website as option for quote retrieving", e);
		}
	}

}
