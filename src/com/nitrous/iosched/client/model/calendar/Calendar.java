package com.nitrous.iosched.client.model.calendar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.core.client.JsArray;
import com.nitrous.iosched.client.model.ScheduleContainerJSO;
import com.nitrous.iosched.client.model.SessionJSO;

/**
 * The full calendar of all sessions for the duration of the conference.
 * 
 * @author nitrousdigital
 *
 */
public class Calendar {
	private static final String CODE_LABS = "Self-paced Code Labs";
	
	// lookup from month day to CalendarDay object that holds the sessions for that day
	private TreeMap<Integer, CalendarDay> days = new TreeMap<Integer, CalendarDay>();
	
	private int firstDay = Integer.MAX_VALUE;
	private int lastDay = -1;
	
	// lookup to duplicate sessions by title
	private Map<String, List<SessionJSO>> links = new HashMap<String, List<SessionJSO>>();
	
	/**
	 * @return The earliest date of the conference
	 */
	public int getFirstDay() {
		return firstDay;
	}

	/**
	 * @return The latest date of the conference
	 */
	public int getLastDay() {
		return lastDay;
	}
	
	/**
	 * @return The year of the conference
	 */
	public String getYear() {
		return "2015";
	}
	
	/**
	 * @return The month of the conference
	 */
	public String getMonth() {
		return "May";
	}
	
	public Calendar(ScheduleContainerJSO sessionList) {
		JsArray<SessionJSO> sessions = sessionList.getSessions();
		if (sessions != null) {
			int numSessions = sessions.length();
			for (int i = 0 ; i < numSessions; i++) {
				SessionJSO session = sessions.get(i);
				add(session);
				
				// keep track of duplicates by title
				List<SessionJSO> others = links.get(session.getTitle());
				if (others == null) {
					others = new ArrayList<SessionJSO>();
					links.put(session.getTitle(), others);
				}
				others.add(session);
			}
		}
	}
	
	/**
	 * 
	 * @param session The session
	 * @return True if the specified session is duplicate elsewhere.
	 */
	public boolean isDuplicateTitle(SessionJSO session) {
		List<SessionJSO> dupes = links.get(session.getTitle());
		return dupes != null && dupes.size() > 1;
	}
	
	/**
	 * Find alternate times for the specified session
	 * @param session The session for which alternates are to be found
	 * @return The other times that the specified session is available or an empty list if the session isnt offered at other times.
	 */
	public List<SessionJSO> getOthers(SessionJSO session) {
		// dont show duplicates for code labs
		if (CODE_LABS.equals(session.getTitle())) {
			return Arrays.asList();
		}
		
		List<SessionJSO> dupes = new ArrayList<SessionJSO>(links.get(session.getTitle()));
		dupes.remove(session);
		return dupes;
	}
	
	private void add(SessionJSO session) {
		int day = session.getDay();
		this.firstDay = Math.min(day, firstDay);
		this.lastDay = Math.max(day, lastDay);
		
		CalendarDay calDay = days.get(day);
		if (calDay == null) {
			calDay = new CalendarDay(this, day);
			days.put(day, calDay);
		}
		calDay.add(session);
	}
	
	public LinkedHashSet<CalendarDay> getDays() {
		return new LinkedHashSet<CalendarDay>(days.values());
	}
}
