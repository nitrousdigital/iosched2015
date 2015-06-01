package com.nitrous.iosched.server;


public class Config {
	public static final String[] AUTH_SCOPES = { 
		//CalendarAuthScope.CALENDAR.getScope(),
		"https://www.googleapis.com/auth/calendar.readonly"
//		"https://www.googleapis.com/auth/plus.login", 
//		"https://www.googleapis.com/auth/drive.appdata", 
//		"https://www.googleapis.com/auth/userinfo.email" 
	};
    

	/** The URL used to retrieve the authenticated user's schedule */
	//private static final String SESSIONS_GET_URL = "https://events.google.com/io2015/api/v1/user/schedule/__keynote__";
	private static final String SESSIONS_GET_URL = "https://events.google.com/io2015/api/v1/user/schedule";

	/** The URL used to retrieve the full public schedule */
	private static final String SCHEDULE_URL = "https://events.google.com/io2015/api/v1/schedule";

	// the manifest points to the active versions of available resources
	// http://storage.googleapis.com/io2015-data.appspot.com/manifest_v1.json
	/*
	 * {"format":"iosched-json-v1","data_files":["past_io_videolibrary_v4.json","blocks_v2.json","map_v3.json","keynote_v1.json","session_data_v1.42.json"]}
	 */
	
	// the data_files each identify a json file:
	// http://storage.googleapis.com/io2015-data.appspot.com/session_data_v1.42.json
	
	private Config() {
	}

	/**
	 * 
	 * @return The URL used to retrieve the full public schedule
	 */
	public static String getScheduleURL() {
		return SCHEDULE_URL;
	}

	/**
	 * 
	 * @return The URL used to retrieve the authenticated user's schedule
	 */
	public static String getUserScheduleURL() {
		return SESSIONS_GET_URL;
	}
}
