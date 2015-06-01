package com.nitrous.iosched.client.model.calendar;

import java.util.Comparator;

class CalendarBlockComparator implements Comparator<CalendarBlock> {
	@Override
	public int compare(CalendarBlock o1, CalendarBlock o2) {
		String n1 = o1.getName();
		String n2 = o2.getName();
		String[] p1 = n1.split(" ");
		String[] p2 = n2.split(" ");
		
		// sort by AM/PM first
		int result = p1[1].compareTo(p2[1]);
		
		// sort by hour if AM/PM is a match
		if (result == 0) {
			Integer hour0 = Integer.parseInt(p1[0]) % 12;
			Integer hour1 = Integer.parseInt(p2[0]) % 12;			
			result = hour0.compareTo(hour1);
		}
		return result;
	}
}