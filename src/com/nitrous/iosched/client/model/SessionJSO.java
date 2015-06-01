package com.nitrous.iosched.client.model;

import java.util.ArrayList;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.nitrous.iosched.client.service.UserScheduleService;

/**
 * Describes a single session within the schedule
 * 
 * @author nitrousdigital
 *
 */
public final class SessionJSO extends JavaScriptObject {
	private static DateTimeFormat TIMESTAMP_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
	protected SessionJSO() {
	}

	/**
	 * @return True if this session is in the users schedule
	 */
	public boolean isInMySchedule() {
		return UserScheduleService.get().isInMySchedule(getId());
	}
	
	/**
	 * Cache a parsed copy of the end time in milliseconds
	 * @param endTime The end time in milliseconds
	 */
	private native void setEndTimeMillis(double endTime) /*-{
		this.endTimeMillis = endTime;
	}-*/;
	
	/**
	 * @return The end time in milliseconds if already parsed and set via setEndTimeMillis
	 */
	private native double getEndTimeMillis() /*-{
		return typeof (this.endTimeMillis) != "undefined" ? this.endTimeMillis : 0;
	}-*/;
	
	/**
	 * @return The session end time in milliseconds
	 */
	public long getEndTime() {
		double d = getEndTimeMillis();
		long endTime;
		if (d == 0) {
			// 2015-05-28T16:30:00Z
			String endTimeStr = getEndTimestamp();
			endTime = TIMESTAMP_FORMAT.parse(endTimeStr).getTime();
			setEndTimeMillis(endTime);
		} else {
			endTime = (long)d;
		}
		return endTime;
	}
	
	public boolean isExpired(long currentTime) {
		return getEndTime() <= currentTime;
	}
	
	/**
	 * 
	 * @return The session date and time e.g. MAY 3, 9:30 AM - 10:30 AM
	 */
	public String getSessionDateTime() {
		StringBuilder text = new StringBuilder()
			.append(ConferenceDataManager.get().getCalendar().getMonth()).append(" ")
			.append(getDay()).append(", ")
			.append(getSessionTime());
		return text.toString();
	}
	/**
	 * @return the time of this session e.g. 9:30 AM - 10:10 AM
	 */
	public String getSessionTime() {
		return getStart() + " - " +getEnd();
	}
	
	public native String getId() /*-{
		return this.id;
	}-*/;

	public native String getTitle() /*-{
		return this.title;
	}-*/;

	public native String getDescription() /*-{
		return this.description;
	}-*/;

	/**
	 * Cache a parsed copy of the start time in milliseconds
	 * @param startTime The start time in milliseconds
	 */
	private native void setStartTimeMillis(double startTime) /*-{
		this.startTimeMillis = startTime;
	}-*/;
	
	/**
	 * @return The start time in milliseconds if already parsed and set via setStartTimeMillis
	 */
	private native double getStartTimeMillis() /*-{
		return typeof (this.startTimeMillis) != "undefined" ? this.startTimeMillis : 0;
	}-*/;
	
	/**
	 * @return The start time in milliseconds
	 */
	public long getStartTime() {
		double d = getStartTimeMillis();
		long startTime;
		if (d == 0) {
			// 2015-05-28T16:30:00Z
			String startTimeStr = getStartTimestamp();
			startTime = TIMESTAMP_FORMAT.parse(startTimeStr).getTime();
			setStartTimeMillis(startTime);
		} else {
			startTime = (long)d;
		}
		return startTime;
	}
	
	/**
	 * @return e.g. "2015-05-28T16:30:00Z"
	 */
	public native String getStartTimestamp() /*-{
		return this.startTimestamp;
	}-*/;

	public native String getEndTimestamp() /*-{
		return this.endTimestamp;
	}-*/;

	public native boolean isLivestream() /*-{
		return this.isLivestream;
	}-*/;

	public native SessionFilterJSO getFilters() /*-{
		return this.filters;
	}-*/;
	
	public native JsArrayString getTags() /*-{
		return this.tags;
	}-*/;

	/**
	 * @param filter The active filter
	 * @return True if the session should be visible given the specified filter
	 */
	public boolean shouldDisplay(TagFilter filter) {
		return filter.accept(this);
	}
	
	/**
	 * @return a list of speaker IDs or null.
	 */
	public native JsArrayString getSpeakerIds() /*-{
		return this.speakers;
	}-*/;

	/**
	 * @return The speakers for this session or an empty list
	 */
	public ArrayList<SpeakerJSO> getSpeakers() {
		ArrayList<SpeakerJSO> speakers = new ArrayList<SpeakerJSO>();
		JsArrayString speakerIds = getSpeakerIds();
		if (speakerIds != null && speakerIds.length() > 0) {
			for (int i = 0, len = speakerIds.length(); i < len; i++) {
				String id = speakerIds.get(i);
				SpeakerJSO speaker = ConferenceDataManager.get().getSpeaker(id);
				if (speaker != null) {
					speakers.add(speaker);
				}
			}
		}
		return speakers;
	}
	
	public native String getRoom() /*-{
		return this.room;
	}-*/;

	public native String getPhotoUrl() /*-{
		return this.photoUrl;
	}-*/;

	/**
	 * 
	 * @return a youtube video ID, e.g. "wtLJPvx7-ys"
	 */
	public native String getYouTubeUrl() /*-{
		return this.youtubeUrl;
	}-*/;

	public native boolean hasRelated() /*-{
		return this.hasRelated;
	}-*/;

	/**
	 * 
	 * @return the day of the month of this session (e.g. 28)
	 */
	public native int getDay() /*-{
		return this.day;
	}-*/;

	/**
	 * @return The month of this session (e.g. May)
	 */
	public String getMonth() {
		return ConferenceDataManager.get().getCalendar().getMonth();
	}
	
	/**
	 * 
	 * @return the time block of this session (e.g. "9 AM")
	 */
	public native String getBlock() /*-{
		return this.block;
	}-*/;

	/**
	 * 
	 * @return The start time (e.g. "9:30 AM")
	 */
	public native String getStart() /*-{
		return this.start;
	}-*/;

	/**
	 * 
	 * @return The end time (e.g. "11:30 AM")
	 */
	public native String getEnd() /*-{
		return this.end;
	}-*/;

}