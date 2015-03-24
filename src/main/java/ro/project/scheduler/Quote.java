package ro.project.scheduler;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Caphyon1
 * 
 * A serializable object representing the quotes.
 *
 */
public class Quote implements Serializable {
	private static final long serialVersionUID = 1L;
	/** quote is a String representing the actual quote. */
	private String quote;
	/** author is a String representing the author of the quote.	 */
	private String author;
	/** MD5 represents the hashing by which we can determine if a quote was posted before or not */
	private String MD5;

	public Quote(String quote, String author) {
		super();
		this.quote = quote;
		this.author = author;
		MD5 = hashToMd5(toString());
	}

	public String getQuote() {
		return quote;
	}

	public void setQuote(String quote) {
		this.quote = quote;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getMD5() {
		return MD5;
	}
	
	public void setMD5(String MD5) {
		this.MD5 = MD5;
	}

	/**
	 * Hashes the quote and author to MD5.
	 * 
	 * @param quote
	 *            String representing the actual quote.
	 * @return the MD5 representation of the quotes
	 */
	private String hashToMd5(String quote) {

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md.update(quote.getBytes());

		byte byteData[] = md.digest();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return quote + " - " + author;
	}
	
	

}
