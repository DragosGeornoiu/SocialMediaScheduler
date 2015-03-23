package ro.project.servlet;

import java.util.Comparator;

import ro.project.Constants;
import ro.project.servlet.OrderObject;

public class OrderObjectComparator implements Comparator<OrderObject> {

	String sortedBy;
	boolean ascending;
	
	public OrderObjectComparator(String sortedBy, boolean ascending) {
		this.sortedBy = sortedBy;
		this.ascending = ascending;
	}
	
	@Override
	public int compare(OrderObject one, OrderObject two) {
		if (sortedBy.equals(Constants.BY_DATE) && ascending == true) {
			return one.getCalendar().compareTo(two.getCalendar());
		} else if (sortedBy.equals(Constants.BY_DATE) && ascending == false) {
			return -one.getCalendar().compareTo(two.getCalendar());
		} else if (sortedBy.equals(Constants.BY_AUTHOR) && ascending == true) {
			return one.getQuote().getAuthor().compareToIgnoreCase(two.getQuote().getAuthor());
		} else if (sortedBy.equals(Constants.BY_AUTHOR) && ascending == false) {
			return -one.getQuote().getAuthor().compareToIgnoreCase(two.getQuote().getAuthor());
		} else if (sortedBy.equals(Constants.BY_QUOTE) && ascending == true) {
			return one.getQuote().getQuote().compareToIgnoreCase(two.getQuote().getQuote());
		} else if (sortedBy.equals(Constants.BY_QUOTE) && ascending == false) {
			return -one.getQuote().getQuote().compareToIgnoreCase(two.getQuote().getQuote());
		}
		
		return 0;
	}

}
