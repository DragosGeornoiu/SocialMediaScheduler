package ro.project.parser;

import java.util.Hashtable;

import javax.xml.bind.annotation.XmlRootElement;

import ro.project.scheduler.Quote;

@XmlRootElement
public class QuoteHashWrapper {

	private Hashtable<String, Quote> hashtable;

	public Hashtable<String, Quote> getHashtable() {
		return hashtable;
	}

	public void setHashtable(Hashtable<String, Quote> hashtable) {
		this.hashtable = hashtable;
	}

}
