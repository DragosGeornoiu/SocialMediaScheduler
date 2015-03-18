package ro.project.servlet;

import java.util.Calendar;

import ro.project.scheduler.Quote;

public class OrderObject {
	private Calendar calendar;
	private Quote quote;
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
		return " <BR> <BR> " + " Due at: " + mYear + "-" + (mMonth + 1) + "-" + mDay + " " + mHour + ":" + mMinute
				+ " <BR >" + " Service: " + service + " <BR> " + " Text: " + quote.toString() + " <BR> ";

	}

}
