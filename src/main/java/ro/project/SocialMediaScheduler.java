package ro.project;

import ro.project.parser.QuoteWebsiteParser;
import ro.project.scheduler.QuoteManager;
import ro.project.scheduler.Scheduler;

/*1) Create a parser that will get all the quotes from: http://persdev-q.com/
  2) Schedule them as Tweets/Facebook posts with the buffer API

 	read all quotes from website
 	update quotes (check page by page untill the last quote is found and put them on top of the others)
 	select a random quote
 	check if quote can be posted on twitter
 	check if that quote was posted on social media before, else put it as posted
 	schedule a tweet for date X or post it now */

public class SocialMediaScheduler {
	public static void main(String[] args) {
		QuoteWebsiteParser parser = new QuoteWebsiteParser("http://persdev-q.com/");
		// pageParser.saveQuotesFromWebsite();

		QuoteManager quoteManager = new QuoteManager();
		Scheduler scheduler = new Scheduler();

		scheduler.getUpdates();
		scheduler.getPendingUpdates();
		String quote = quoteManager.getRandomQuote();
		scheduler.sendMessage(quote, "2015-03-04 12:25:00");
		quote = quoteManager.getRandomQuote();
		scheduler.sendMessageNow(quote);
		scheduler.getPendingUpdates();
		scheduler.getUpdates();
	}

}
