package ro.project.servlet;

import java.util.Calendar;

import ro.project.scheduler.Quote;

public class OrderObject {
	private Calendar calendar;
	private Quote quote;
	private String service;
	
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

}
