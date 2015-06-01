package com.nitrous.iosched.client.model.calendar;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.nitrous.iosched.client.model.SessionJSO;

/**
 * Describes the blocks of session times for a single day of the conference.
 * 
 * @author nitrousdigital
 *
 */
public class CalendarDay {
	private TreeSet<CalendarBlock> blocks = new TreeSet<CalendarBlock>(new CalendarBlockComparator());
	private Map<String, CalendarBlock> blocksByName = new HashMap<String, CalendarBlock>();
	
	private int day;
	private Calendar calendar;
	
	public CalendarDay(Calendar calendar, int day) {
		this.calendar = calendar;
		this.day = day;
	}
	
	public String getYear() {
		return this.calendar.getYear();
	}
	
	public String getMonthDay() {
		return this.calendar.getMonth() + " " + day;
	}
	
	public String getMonth() {
		return this.calendar.getMonth();				
	}
	
	/**
	 * @return The Calendar that holds this day
	 */
	public Calendar getCalendar() {
		return this.calendar;
	}
	
	public int getDay() {
		return day;
	}
	
	public void add(SessionJSO session) {
		String block = session.getBlock(); // eg 9 AM
		CalendarBlock cb = blocksByName.get(block);
		if (cb == null) {
			cb = new CalendarBlock(this, block);
			blocksByName.put(block, cb);
			blocks.add(cb);
		}
		cb.add(session);
	}
	
	public Set<CalendarBlock> getTimeBlocks() {
		return blocks;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + day;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CalendarDay other = (CalendarDay) obj;
		if (day != other.day) {
			return false;
		}
		return true;
	}
	
	
}
