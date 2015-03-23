package ro.project.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ro.project.Constants;
import ro.project.scheduler.Quote;

/**
 * 
 * Parser for http://www.brainyquote.com/.
 *
 */
public class BrainyQuoteParser extends Parser {
	final static Logger logger = Logger.getLogger(BrainyQuoteParser.class);

	/**
	 * Select all quotes from the given URL.
	 * 
	 * @param url
	 *            the URL to the page where the quotes are located.
	 * 
	 * @return the quotes as a Hashtable of type <String, Quote>.
	 */
	@Override
	protected Hashtable<String, Quote> getQuotesFromPage(String url) {
		Document document = null;
		Elements elements = null;
		Hashtable<String, Quote> quotesPageList = new Hashtable<String, Quote>();

		try {
			document = Jsoup.connect(url)
					.userAgent(Constants.MOZILLA_USER_AGENT)
					.ignoreHttpErrors(true).get();

			elements = document.getElementsByClass(Constants.BOXY_PADDING_BIG);
			quotesPageList = getQuotesAsHashtable(elements);

		} catch (MalformedURLException e) {
			logger.error("Url not formated correctly", e);
		} catch (IOException e) {
			logger.error("Problem reading the quotes from given page", e);
		}

		return quotesPageList;
	}

	/**
	 * The quotes are returned in a Hashtable of type <String, Quote>.
	 * 
	 * @param elements represent the quotes as org.jsoup.select.Elements.
	 * 
	 * @return the quotes as a hashtable of type <String, Quote>.
	 */
	@Override
	protected Hashtable<String, Quote> getQuotesAsHashtable(Elements elements) {
		Hashtable<String, Quote> tempList = new Hashtable<String, Quote>();
		for (Element element : elements) {
			String quote = element.getElementsByClass(Constants.BQ_QUOTE_LINK).select(Constants.HREF_A).text().toString();
			String auth = element.getElementsByClass(Constants.BQ_AUT).select(Constants.HREF_A).text().toString();

			Quote q = new Quote(quote, auth);
			tempList.put(q.getMD5(), q);
		}
		return tempList;

	}

	/**
	 * The quotes are returned in a Hashtable of type <String, Quote>.
	 * 
	 * @param elements represent the quotes as org.jsoup.select.Elements.
	 * 
	 * @return the quotes as a hashtable of type <String, Quote>.
	 */
	@Override
	protected String getPreviousPageLink(String url) {
		Document document = null;
		Elements elements = null;
		try {
			document = Jsoup.connect(url)
					.userAgent(Constants.MOZILLA_USER_AGENT)
					.ignoreHttpErrors(true).get();
			elements = document.getElementsByClass(Constants.NAV).select(Constants.HREF_A);
			for (Element element : elements) {
				if (element.text().contains("Next")) {
					return Constants.BRAINIQUOTE_URL + element.attr(Constants.HREF).toString();
				}
			}
		} catch (MalformedURLException e) {
			logger.error("Url not formated correctly", e);
		} catch (IOException e) {
			logger.error("Problem retrieving previous page link", e);
		}
		return "";
	}

}
