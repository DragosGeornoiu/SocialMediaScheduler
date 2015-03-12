package ro.project.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Hashtable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import ro.project.scheduler.Quote;

/**
 * 
 * Parser for http://www.brainyquote.com/.
 *
 */
public class BrainyQuoteParser extends Parser{

	@Override
	protected Hashtable<String, Quote> getQuotesFromPage(String url) {
		Document document = null;
		Elements elements = null;
		Hashtable<String, Quote> quotesPageList = new Hashtable<String, Quote>();

		try {
			document = Jsoup.connect(url)
					.userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:32.0) Gecko/20100101 Firefox/32.0")
					.ignoreHttpErrors(true).get();

			elements = document.getElementsByClass("boxyPaddingBig");
			quotesPageList = getQuotesAsList(elements);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return quotesPageList;
	}

	@Override
	protected Hashtable<String, Quote> getQuotesAsList(Elements elements) {
		Hashtable<String, Quote> tempList = new Hashtable<String, Quote>();
		for (Element element : elements) {

			String quote = element.getElementsByClass("bqQuoteLink").select("a").text().toString();
			String auth = element.getElementsByClass("bq-aut").select("a").text().toString();

			Quote q = new Quote(quote, auth);
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
			elements = document.getElementsByClass("nav").select("a");
			for (Element element : elements) {
				if (element.text().contains("Next")) {
					return "http://www.brainyquote.com" + element.attr("href").toString();
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

}
