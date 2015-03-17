package ro.project.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ro.project.scheduler.Quote;


/**
 * 
 * Parser for http://persdev-q.com/.
 *
 */
public class PersdevParser extends Parser {
	final static Logger logger = Logger.getLogger(PersdevParser.class);
	
	@Override
	protected Hashtable<String, Quote> getQuotesFromPage(String url) {
		Document document = null;
		Elements elements = null;
		Hashtable<String, Quote> quotesPageList = new Hashtable<String, Quote>();

		try {
			document = Jsoup.connect(url)
					.userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/20100101 Firefox/32.0")
					.ignoreHttpErrors(true).get();
			elements = document.getElementsByTag("article");
			quotesPageList = getQuotesAsList(elements);

		} catch (MalformedURLException e) {
			logger.error("Url not formated correctly", e);
		} catch (IOException e) {
			logger.error("Problem reading the quotes from given page", e);
		}

		return quotesPageList;
	}

	@Override
	protected Hashtable<String, Quote> getQuotesAsList(Elements elements) {
		Hashtable<String, Quote> tempList = new Hashtable<String, Quote>();
		for (Element element : elements) {

			String quote = element.select("p").toString();
			quote = quote.replace("&laquo;", "").replace("&raquo;", "").replace("<p>", "").replace("<em>", "")
					.replace("</em>", "").replace("<strong>", "").replace("</strong>", "").replace("</p>", "")
					.replace("<br />", "- ");

			Quote q = new Quote(quote.split(" - ")[0], quote.split(" - ")[1]);
			tempList.put(q.getMD5(), q);
		}
		return tempList;

	}

	@Override
	protected String getPreviousPageLink(String url) {
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
			logger.error("Url not formated correctly", e);
		} catch (IOException e) {
			logger.error("Problem retrieving previous page link", e);
		}
		return "";
	}

}
