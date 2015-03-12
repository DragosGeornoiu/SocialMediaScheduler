package ro.project.scheduler;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Quote implements Serializable {
	private static final long serialVersionUID = 1L;
	private String quote;
	private String author;
	private String MD5;

	public Quote(String quote, String author) {
		super();
		this.quote = quote;
		this.author = author;
		MD5 = hashToMd5(quote);
	}

	public String getQuote() {
		return quote;
	}

	public void setQuote(String quote) {
		this.quote = quote;
		hashToMd5(quote);
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
	 * Converts the quote to MD5.
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

		// convert the byte to hex format method 1
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
