package ro.project.servlet;

import java.util.Calendar;

import ro.project.scheduler.Quote;

/**
 *         Easier to sort through the posted quotes by saving the date it was
 *         posted on as a Calendar, the quote as a quote and the social network
 *         it was posted on as a String.
 *
 */
public class OrderObject {
	/** The date the quote was posted on	 */
	private Calendar calendar;
	/** The quote and author saved as a Quote type. */
	private Quote quote;
	/** service is a String representing the social network where the quote was posted */
	private String service;

	public OrderObject(Calendar calendar, Quote quote, String service) {
		super();
		this.calendar = calendar;
		this.quote = quote;
		this.service = service;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public Quote getQuote() {
		return quote;
	}

	public void setQuote(Quote quote) {
		this.quote = quote;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	@Override
	public String toString() {
		int mYear = calendar.get(Calendar.YEAR);
		int mMonth = calendar.get(Calendar.MONTH);
		int mDay = calendar.get(Calendar.DAY_OF_MONTH);
		int mHour = calendar.get(Calendar.HOUR_OF_DAY);
		int mMinute = calendar.get(Calendar.MINUTE);
		
		return "<tr> <td>" + mYear + "-" + (mMonth + 1) + "-" + mDay + " " + mHour + ":" + mMinute
				+ "</td>" + "<td>" + service + "</td><td>" + quote.toString() + "</td></tr> ";
	}
}
