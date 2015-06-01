package com.nitrous.iosched.client.model.calendar;

import java.util.LinkedHashSet;
import java.util.TreeMap;

import com.google.gwt.i18n.shared.DateTimeFormat;
import com.nitrous.iosched.client.model.SessionJSO;

/**
 * Describes a single block of time for a single day.
 * Each time block represents a single starting hour in the schedule.
 * 
 * @author nitrousdigital
 *
 */
public class CalendarBlock {
	private static final DateTimeFormat DTF = DateTimeFormat.getFormat("MMM dd yyyy HH aa Z");
	private static final long HOUR = 1000 * 60 * 60;
	
	private CalendarDay day;
	private String name;
	private TreeMap<String, CalendarTimeSlot> slots = new TreeMap<String, CalendarTimeSlot>();
	private Long parsedTimestamp;
	
	/**
	 * @param name The block name e.g. 9 AM
	 * @param day The day that holds this time block
	 */
	public CalendarBlock(CalendarDay day, String name) {
		this.name = name;
		this.day = day;
	}

	public CalendarDay getDay() {
		return day;
	}
	
	private long getBlockTimestamp() {		
		if (parsedTimestamp == null) {
			String blockTime = day.getMonth() + " " + day.getDay() + " " + day.getYear() + " " + name + " -0700";  
			parsedTimestamp = DTF.parse(blockTime).getTime();
		} 
		return parsedTimestamp;
	}
	
	public boolean isExpired(long currentTime) {
		// block is not expired until 1 hour later
		return getBlockTimestamp() + HOUR <= currentTime;
	}
	
	/**
	 * 
	 * @return The block name e.g. 9 AM
	 */
	public String getName() {
		return name;
	}
	
	public void add(SessionJSO session) {
		String slotStart = session.getStart();
		CalendarTimeSlot slot = slots.get(slotStart);
		if (slot == null) {
			slot = new CalendarTimeSlot(this, slotStart);
			slots.put(slotStart, slot);
		}
		slot.add(session);
	}
	
	public LinkedHashSet<CalendarTimeSlot> getTimeSlots() {
		return new LinkedHashSet<CalendarTimeSlot>(slots.values());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((day == null) ? 0 : day.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		CalendarBlock other = (CalendarBlock) obj;
		if (day == null) {
			if (other.day != null) {
				return false;
			}
		} else if (!day.equals(other.day)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}


}	
