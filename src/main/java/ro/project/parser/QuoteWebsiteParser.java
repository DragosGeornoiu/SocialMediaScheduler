package ro.project.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class QuoteWebsiteParser {

	/** website String the url of the website from which we get the quotes	 */
	private String website;
	
	public QuoteWebsiteParser(String website) {
		this.website = website;
	}
	
	/** Save quotes from the web site in quotes.txt file.  */
	public void saveQuotesFromWebsite() {
		List<String> quotesList = new ArrayList<String>();
		quotesList.addAll(parseWebsiteForQuotes(website));

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter("quotes"));
			for (int i = 0; i < quotesList.size(); i++) {
				writer.write(quotesList.get(i) + "\n");
			}

		} catch (IOException e) {
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Parse entire web site and return the quotes as a list
	 * 
	 * @param url String pointing to the web site.  
	 * @return List<String> containing all the quotes as a list.
	 */
	private List<String> parseWebsiteForQuotes(String url) {

		List<String> allQuotesFromWebsite = new ArrayList<String>();
		do {
			List<String> temp = getQuotesFromPage(url);
			allQuotesFromWebsite.addAll(temp);
			url = getPreviousPageLink(url);
		} while (!url.trim().isEmpty());

		return allQuotesFromWebsite;
	}

	/**
	 * Parse current page for all the quotes on it.
	 * 
	 * @param url String pointing to the current page.
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
	 * @param elements All the quotes returned as type Elements from the web page.
	 * @return List<String> of all quotes stored as elements initially.
	 */
	private List<String> getQuotesAsList(Elements elements) {
		List<String> tempList = new ArrayList<String>();
		for (Element element : elements) {

			String value = element.attr("id");
			String quote = element.select("p").toString();
			quote = value.replace("post-", "")
					+ " -"
					+ quote.replace("&laquo;", "").replace("&raquo;", "").replace("<p>", "").replace("<em>", "")
							.replace("</em>", "").replace("<strong>", "").replace("</strong>", "").replace("</p>", "")
							.replace("<br />", "- ");
			tempList.add(quote);
		}
		return tempList;

	}

	/**
	 * Get the URL pointing to the previous page.
	 *  
	 * @param url the URL of current page.
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

	// NOT TESTED
	public void updateQuotes() {
		List<String> quotesList = getQuotesFromFile("quotes");

		boolean isUpToDate = false;

		List<String> recentQuotes = new ArrayList<String>();
		String url = website;

		do {
			List<String> temp = getQuotesFromPage(url);

			for (int i = 0; i < temp.size(); i++) {
				if (temp.get(i).equals(quotesList.get(0))) {
					isUpToDate = true;
				} else {
					recentQuotes.add(temp.get(i));
				}
			}

			for (int i = 0; i < recentQuotes.size(); i++) {
				quotesList.add(0, recentQuotes.get(i));
				url = getPreviousPageLink(url);
			}
		} while (isUpToDate);
		
		try {
			PrintWriter writer = new PrintWriter("quotes");
			
			for (int i = 0; i < quotesList.size(); i++) {
				writer.print(quotesList.get(i) + "\n");
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		};

	}

	
	/**
	 * Returns all quotes stored in file as a List<String>
	 * 
	 * @param fileName String representing the name of the file.
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
}
