package ro.project.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class QuoteWebsiteParser {

	private FileManager fileManager;
	private String path;
	/**
	 * Check if any new quotes were posted on web site. If the file of quotes is
	 * empty, it will proceed to download the entire web site.
	 */
	public String updateQuotes(String website) {
		fileManager = new FileManager();
		String fileName = fileManager.createFileNameFromUrl(website);
		path = fileManager.createFileInPath(fileName);
		List<String> newQuotes = new ArrayList<String>();
		List<String> tempQuotes = new ArrayList<String>();

		String url = website;
		newQuotes = getQuotesFromFile(path);

		if (newQuotes.size() == 0) {
			saveQuotesFromWebsite(website);
		} else {
			String quote = newQuotes.get(0).split(" - ")[0];

			boolean endCondition = false;
			while (!endCondition) {

				List<String> pageQuotes = getQuotesFromPage(url);
				url = getPreviousPageLink(url);

				for (int i = 0; i < pageQuotes.size(); i++) {
					if (quote.equals(pageQuotes.get(i).split(" - ")[0])) {
						endCondition = true;
						// i = pageQuotes.size();
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

	/** Save quotes from the web site in quotes.txt file. */
	private void saveQuotesFromWebsite(String website) {
		List<String> quotesList = new ArrayList<String>();
		quotesList.addAll(parseWebsiteForQuotes(website));
		saveQuotesToFile(quotesList);
	}

	/**
	 * Parse entire web site and return the quotes as a list
	 * 
	 * @param url
	 *            String pointing to the web site.
	 * @return List<String> containing all the quotes as a list.
	 */
	private List<String> parseWebsiteForQuotes(String url) {
		List<String> allQuotesFromWebsite = new ArrayList<String>();
		do {
			// System.out.println("Parsing page " + url);
			List<String> temp = getQuotesFromPage(url);
			allQuotesFromWebsite.addAll(temp);
			url = getPreviousPageLink(url);
		} while (!url.trim().isEmpty());

		return allQuotesFromWebsite;
	}

	/**
	 * Parse current page for all the quotes on it.
	 * 
	 * @param url
	 *            String pointing to the current page.
	 * @return List<String> containing all the quotes from current page.
	 */
	private List<String> getQuotesFromPage(String url) {
		Document document = null;
		Elements elements = null;
		List<String> quotesPageList = new ArrayList<String>();

		try {
			document = Jsoup.connect(url)
					.userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/20100101 Firefox/32.0")
					.ignoreHttpErrors(true).get();
			elements = document.getElementsByTag("article");
			quotesPageList = getQuotesAsList(elements);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return quotesPageList;
	}

	/**
	 * Returns the quotes a a List of type String.
	 * 
	 * @param elements
	 *            All the quotes returned as type Elements from the web page.
	 * @return List<String> of all quotes stored as elements initially.
	 */
	private List<String> getQuotesAsList(Elements elements) {
		List<String> tempList = new ArrayList<String>();
		for (Element element : elements) {

			String quote = element.select("p").toString();
			quote = quote.replace("&laquo;", "").replace("&raquo;", "").replace("<p>", "").replace("<em>", "")
					.replace("</em>", "").replace("<strong>", "").replace("</strong>", "").replace("</p>", "")
					.replace("<br />", "- ");

			tempList.add(quote);
		}
		return tempList;

	}

	/**
	 * Get the URL pointing to the previous page.
	 * 
	 * @param url
	 *            the URL of current page.
	 * @return the URL of previous page.
	 */
	private String getPreviousPageLink(String url) {
		Document document = null;
		Elements elements = null;
		try {
			document = Jsoup.connect(url)
					.userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/20100101 Firefox/32.0")
					.ignoreHttpErrors(true).get();
			elements = document.getElementsByTag("nav").select("a");
			for (Element element : elements) {
				if (element.text().contains("Previous")) {
					return element.attr("href").toString();
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Returns all quotes stored in file as a List<String>.
	 * 
	 * @param fileName
	 *            String representing the name of the file.
	 * @return List<String> representing all the quotes from the file.
	 */
	private List<String> getQuotesFromFile(String fileName) {
		List<String> quotesList = new ArrayList<String>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = br.readLine()) != null) {
				quotesList.add(line);
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
	private void saveQuotesToFile(List<String> quotesList) {
		BufferedWriter writer = null;
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
	}

}
