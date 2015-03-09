package ro.project.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.select.Elements;

import ro.project.scheduler.Quote;

public abstract class Parser {
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
	public String updateQuotes(String website) {
		fileManager = new FileManager();
		String fileName = fileManager.createFileNameFromUrl(website);

		path = fileManager.createFileInPath(fileName+".ser");
		List<Quote> newQuotes = new ArrayList<Quote>();
		List<Quote> tempQuotes = new ArrayList<Quote>();

		String url = website;
		newQuotes = getQuotesFromFile(path);

		if (newQuotes.size() == 0) {
			saveWebsiteAsOption(website);
			saveQuotesFromWebsite(website);
		} else {
			String quote = newQuotes.get(0).getQuote();

			boolean endCondition = false;
			while (!endCondition) {

				List<Quote> pageQuotes = getQuotesFromPage(url);
				url = getPreviousPageLink(url);

				for (int i = 0; i < pageQuotes.size(); i++) {
					if (quote.equals(pageQuotes.get(i).getQuote())) {
						endCondition = true;
						break;
					} else {
						tempQuotes.add(pageQuotes.get(i));
					}
				}
			}

			newQuotes.addAll(0, tempQuotes);
			saveQuotesToFile(newQuotes);
		}
		return path;
	}

	/**
	 * Saves all quotes from a web site to a text file.
	 * 
	 * @param website
	 *            String representing the name of the web site.
	 */
	protected void saveQuotesFromWebsite(String website) {
		List<Quote> quotesList = new ArrayList<Quote>();
		quotesList.addAll(parseWebsiteForQuotes(website));
		saveQuotesToFile(quotesList);
	}

	/**
	 * Parse the URL and return all quotes from that page as Strings in a List.
	 * 
	 * @param url
	 *            the URl parsed.
	 * @return the quotes as a List of type String.
	 */
	protected List<Quote> parseWebsiteForQuotes(String url) {
		List<Quote> allQuotesFromWebsite = new ArrayList<Quote>();
		do {
			List<Quote> temp = getQuotesFromPage(url);
			allQuotesFromWebsite.addAll(temp);
			url = getPreviousPageLink(url);
		} while (!url.trim().isEmpty());

		return allQuotesFromWebsite;
	}

	/**
	 * Provided by each parser must be the method to select all quotes from a page.
	 * 
	 * @param url the URL to get the quotes from.
	 * 
	 * @return the quotes as a List of type String.
	 */
	protected abstract List<Quote> getQuotesFromPage(String url);

	protected abstract List<Quote> getQuotesAsList(Elements elements);

	protected abstract String getPreviousPageLink(String url);

	/**
	 * Returns all quotes stored in file as a List<String>.
	 * 
	 * @param fileName
	 *            String representing the name of the file.
	 * @return List<String> representing all the quotes from the file.
	 */
	protected List<Quote> getQuotesFromFile(String fileName) {
		List<Quote> quotesList = new ArrayList<Quote>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = br.readLine()) != null) {
				quotesList.add(new Quote(line.split(" - ")[0], line.split(" - ")[1]));
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return quotesList;
	}

	/**
	 * Saves all the quotes stored in the list in the quotes.txt file
	 * 
	 * @param quotesList
	 *            the list of quotes.
	 */
	protected void saveQuotesToFile(List<Quote> quotesList) {
		/*BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(path));
			for (int i = 0; i < quotesList.size(); i++) {
				writer.write(quotesList.get(i) + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		*/
		
		try {
	        FileOutputStream fileOut = new FileOutputStream(path);
	        ObjectOutputStream out = new ObjectOutputStream(fileOut);
	        out.writeObject(quotesList);
	        out.close();
	        fileOut.close();
	    } catch (IOException ex) {
	    	ex.printStackTrace();
	    }
	}
	


	
	/**
	 * Saves the web site as a option.
	 *  
	 * @param website the URL to save as  a option to post from.
	 */
	protected void saveWebsiteAsOption(String website) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(PATH + "\\\\" + "parser.txt", true)));
			out.println(website);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
