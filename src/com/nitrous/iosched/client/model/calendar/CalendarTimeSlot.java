package com.nitrous.iosched.client.model.calendar;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.nitrous.iosched.client.model.SessionJSO;

public class CalendarTimeSlot {
	private String startTime;
	private Map<String, SessionJSO> sessionsById = new HashMap<String, SessionJSO>();
	
	private CalendarBlock timeBlock;
	
	/**
	 * @param timeBlock The time block within the day that holds this slot
	 * @param startTime The start time (e.g. "9:30 AM")
	 */	
	public CalendarTimeSlot(CalendarBlock timeBlock, String startTime) {
		this.startTime = startTime;
		this.timeBlock = timeBlock;
	}
	
	/**
	 * @return The CalendarBlock that contains this slot
	 */
	public CalendarBlock getTimeBlock() {
		return this.timeBlock;
	}
	
	/**
	 * 
	 * @return The CalendarDay that holds the CalendarBlock that holds this slot.
	 */
	public CalendarDay getDay() {
		return this.timeBlock.getDay();
	}
	
	/**
	 * @return The start time (e.g. "9:30 AM")
	 */
	public String getStartTime() {
		return startTime;
	}
	
	public void add(SessionJSO session) {
		sessionsById.put(session.getId(), session);
	}
	
	public Collection<SessionJSO> getSessions() {
		return sessionsById.values();
	}
}
