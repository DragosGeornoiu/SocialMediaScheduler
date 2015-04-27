package ro.project.scheduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ro.project.Constants;

/**
 * 
 * QuoteManager is used for retrieving a random quote from a specific web site.
 */
public class QuoteManager {
	final static Logger logger = Logger.getLogger(QuoteManager.class);

	/** the file where the posted quotes are stored. */
	private String file;
	/** the file where the quotes from a specific web site are stored. */
	private String quotesFile;

	public QuoteManager(String quotesFile) {
		this.quotesFile = quotesFile;
		//this.file = quotesFile.split(Constants.BRAINIQUOTE)[0];

		this.file = "";
		String[] splitStrings = quotesFile.replace("\\", "/").split("/");
		for (int i = 0; i < splitStrings.length; i++) {
			if (!splitStrings[i].contains(".xml")) {
				this.file += splitStrings[i] + "\\";
			}
		}

	}

	public QuoteManager(String quotesFile, String file) {
		this.file = file + Constants.QUOTES_FILE + "\\\\";
		this.quotesFile = this.file + quotesFile;
	}

	/**
	 * Return a random quote to be posted.
	 * 
	 * @param where
	 *            represents the social network for which the quote is
	 *            requested.
	 * @param max
	 *            represents the maximum number of characters accepted on that
	 *            specific social network.
	 * @return the Quote to be posted on the social network.
	 */
	public Quote getRandomQuote(String where, int max) {
		logger.info("Retrieving a random quote for " + where);
		Quote quote;
		do {
			quote = getRandomQuoteForSocialMedia(file + where.toLowerCase() + "quotes.txt");

			if (quote == null) {
				return null;
			}
		} while (quote.toString().length() > max);

		return quote;
	}

	/**
	 * Returns a random quote from the given fileName.
	 * 
	 * @param fileName
	 *            the name of the file from which the quote is retrieved.
	 * @return the Quote to be posted on the social network.
	 */
	private Quote getRandomQuoteForSocialMedia(String fileName) {
		Quote quote = null;
		Hashtable<String, Quote> quoteHash = new Hashtable<String, Quote>();
		try {
			quoteHash = parseXML(quotesFile);

			// ObjectInputStream in = new ObjectInputStream(new
			// FileInputStream(quotesFile));
			// quotesList.putAll((Hashtable<String, Quote>) in.readObject());
			// in.close();
		} catch (Exception e) {
			logger.error("Deserialisation of quotes hashtable unsuccesfull", e);
		}
		List<Quote> randomQuotesList = new ArrayList<Quote>(quoteHash.values());

		boolean endCondition = false;
		do {
			if ((randomQuotesList == null) || (randomQuotesList.size() == 0)) {
				return null;
			}

			Random rand = new Random();
			int randomNum = rand.nextInt(randomQuotesList.size());
			quote = randomQuotesList.get(randomNum);
			randomQuotesList.remove(randomNum);

			randomQuotesList.remove(quote.getMD5());
		} while ((endCondition = checkIfQuotePostedBefore(quote, fileName)));

		if ((endCondition) || (quote.getQuote().trim().isEmpty())) {
			return null;
		} else {
			quote.setQuote(quote.getQuote().replaceAll(" ", "+").replaceAll("’", "'"));
			return quote;
		}
	}

	private Hashtable<String, Quote> parseXML(String filePath) throws ParserConfigurationException, SAXException,
			IOException {
		Hashtable<String, Quote> hash = new Hashtable<String, Quote>();

		File fXmlFile = new File(filePath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("entry");

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				String key = eElement.getElementsByTagName("key").item(0).getTextContent();

				Element eElement2 = (Element) eElement.getElementsByTagName("value").item(0);

				Quote quote = new Quote(eElement2.getElementsByTagName("author").item(0).getTextContent(), eElement
						.getElementsByTagName("quote").item(0).getTextContent());
				hash.put(key, quote);
			}
		}

		return hash;
	}

	/**
	 * Saves the quote so that in the future the same quote won't be posted on
	 * the same social network where it was previously posted.
	 * 
	 * @param quotesList
	 *            is all the quotes posted before on a specific social network.
	 * 
	 * @param fileName
	 *            String representing the location where previously posted
	 *            quotes on a specific social network were posted.
	 */
	private void saveQuote(Hashtable<String, Quote> quotesList, String fileName) {
		try {
			FileOutputStream fileOut = new FileOutputStream(fileName);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(quotesList);
			out.close();
			fileOut.close();
		} catch (IOException e) {
			logger.error("Serialisation of hashtable of quotes unsuccesfull", e);
		}
	}

	/**
	 * Check if the given quote was posted previously on the specific social
	 * network.
	 * 
	 * @param quote
	 *            represents the actual quote as a Quote object.
	 * @param fileName
	 *            String representing the location where previously posted
	 *            quotes on the specific social network were posted.
	 * @return true if it was posted previously, false if not.
	 */
	private boolean checkIfQuotePostedBefore(Quote quote, String fileName) {
		Hashtable<String, Quote> quotes = new Hashtable<String, Quote>();
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
			quotes = (Hashtable<String, Quote>) in.readObject();
			in.close();
		} catch (Exception e) {
			logger.error("Deserialisation of already posted quotes unsuccesfull.", e);
		}

		if (quotes.containsKey(quote.getMD5())) {
			return true;
		} else {
			quotes.put(quote.getMD5(), quote);
			saveQuote(quotes, fileName);
			return false;
		}
	}

}
