package com.nitrous.iosched.client.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.JsArray;
import com.nitrous.iosched.client.event.ConferenceDataUpdatedEvent;
import com.nitrous.iosched.client.event.Dispatcher;
import com.nitrous.iosched.client.model.calendar.Calendar;

public class ConferenceDataManager {
	private Calendar calendar;
	private ScheduleContainerJSO scheduleData;
	private TagCategory[] tagCategories;
	
	private static ConferenceDataManager INSTANCE;
	
	private ConferenceDataManager() {
	}
	
	public static ConferenceDataManager get() {
		if (INSTANCE == null) {
			INSTANCE = new ConferenceDataManager();
		}
		return INSTANCE;
	}
	
	/**
	 * Find a session by id
	 * @param sessionId The ID of the session
	 * @return The session if found otherwise null.
	 */
	public SessionJSO getSessionById(String sessionId) {
		if (scheduleData != null) {
			JsArray<SessionJSO> sessions = scheduleData.getSessions();
			if (sessions != null) {
				for (int i = 0, len = sessions.length(); i < len; i++) {
					SessionJSO session = sessions.get(i);
					if (session.getId().equals(sessionId)) {
						return session;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * @return The calendar that was loaded or null
	 */
	public Calendar getCalendar() {
		return calendar;
	}

	/**
	 * Populate this manager with the schedule that was loaded.
	 * @param schedule
	 */
	public void onScheduleLoaded(ScheduleContainerJSO scheduleData) {
		this.scheduleData = scheduleData;
		
		// calendar
		this.calendar = new Calendar(scheduleData);
		
		Dispatcher.fire(new ConferenceDataUpdatedEvent());
	}
	
	/**
	 * @return The sorted array of tag categories or null if not yet loaded.
	 */
	public TagCategory[] getFilterTags() {
		if (tagCategories == null) {
			if (scheduleData != null) {
				TagsJSO tagsJso = scheduleData.getTags();
				if (tagsJso != null){
					// lookup from category name to list of tags
					Map<String, List<TagJSO>> map = tagsJso.getTags();
					if (map != null) {
						ArrayList<TagCategory> categories = new ArrayList<TagCategory>();
						for (Map.Entry<String, List<TagJSO>> entry : map.entrySet()) {
							categories.add(new TagCategory(entry.getKey(), entry.getValue()));							
						}
						tagCategories = categories.toArray(new TagCategory[categories.size()]);
						Arrays.sort(tagCategories, new TagCategoryComparator());
					}
				}
			}
		}
		return tagCategories;
	}
	
	/**
	 * 
	 * @param id The id of the speaker
	 * @return The speaker details or null if not yet loaded or unavailable.
	 */
	public SpeakerJSO getSpeaker(String id) {
		if (scheduleData != null) {
			SpeakersJSO speakers = scheduleData.getSpeakers();
			if (speakers != null) {
				return speakers.getSpeaker(id);
			}
		} 
		return null;
	}
	
}
