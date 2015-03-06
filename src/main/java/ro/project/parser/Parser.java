package ro.project.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.select.Elements;

public abstract class Parser {
	protected FileManager fileManager;
	protected String path;
	
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
	
	private void saveQuotesFromWebsite(String website) {
		List<String> quotesList = new ArrayList<String>();
		quotesList.addAll(parseWebsiteForQuotes(website));
		saveQuotesToFile(quotesList);
	}
	
	private List<String> parseWebsiteForQuotes(String url) {
		List<String> allQuotesFromWebsite = new ArrayList<String>();
		do {
			List<String> temp = getQuotesFromPage(url);
			allQuotesFromWebsite.addAll(temp);
			url = getPreviousPageLink(url);
		} while (!url.trim().isEmpty());

		return allQuotesFromWebsite;
	}
	
	public abstract List<String> getQuotesFromPage(String url);
	
	public abstract List<String> getQuotesAsList(Elements elements);
	
	public abstract String getPreviousPageLink(String url);
	
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
