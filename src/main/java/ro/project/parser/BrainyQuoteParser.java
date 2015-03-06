package ro.project.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BrainyQuoteParser extends Parser{

	@Override
	public List<String> getQuotesFromPage(String url) {
		Document document = null;
		Elements elements = null;
		List<String> quotesPageList = new ArrayList<String>();

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
	public List<String> getQuotesAsList(Elements elements) {
		List<String> tempList = new ArrayList<String>();
		for (Element element : elements) {
			System.out.println(element);

			String quote = element.getElementsByClass("bqQuoteLink").select("a").text().toString();
			String auth = element.getElementsByClass("bq-aut").select("a").text().toString();

			System.out.println(quote + " - " + auth);
			tempList.add(quote + " - " + auth);
		}
		return tempList;

	}

	@Override
	public String getPreviousPageLink(String url) {
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
