package com.nitrous.iosched.client.service;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nitrous.iosched.client.event.Dispatcher;
import com.nitrous.iosched.client.event.RequestLoadUserScheduleEvent;
import com.nitrous.iosched.client.event.SimpleEventHandler;
import com.nitrous.iosched.client.event.ToastEvent;
import com.nitrous.iosched.client.event.UserSessionRegisteredEvent;
import com.nitrous.iosched.client.event.UserSessionUnregisteredEvent;
import com.nitrous.iosched.client.event.UserSessionsLoadedEvent;
import com.nitrous.iosched.client.model.ConferenceDataManager;
import com.nitrous.iosched.client.model.SessionJSO;
import com.nitrous.iosched.client.model.calendar.Calendar;

/**
 * Manages retrieval and persistence of the authenticated user-defined schedule.
 * 
 * @author nitrousdigital
 *
 */
public class UserScheduleService {
	private static final Logger LOGGER = Logger.getLogger(UserScheduleService.class.getName());
	private static final String USER_SCHEDULE_COOKIE = "user-schedule";

	private static UserScheduleService INSTANCE;
	
	/** The list of session IDs for which the user is registered */
	private Set<String> userSessions = new HashSet<String>();
	
	private UserScheduleService() {
		Dispatcher.addHandler(RequestLoadUserScheduleEvent.TYPE,
				new SimpleEventHandler<RequestLoadUserScheduleEvent>() {
					@Override
					public void onEvent(RequestLoadUserScheduleEvent event) {
						loadUserSchedule();
					}
				});
	}
	
	public static UserScheduleService get() {
		if (INSTANCE == null) {
			INSTANCE = new UserScheduleService();
		}
		return INSTANCE;
	}
	
	public static void register() {
		get();
	}
	
	@SuppressWarnings("deprecation")
	private static Date getCookieExpiration() {
		Date expire = new Date();
		expire.setTime(System.currentTimeMillis());
		expire.setYear(expire.getYear()+1);
		return expire;
	}
		
	/**
	 * @param session The session
	 * @return True if the session is in the users schedule.
	 */
	public boolean isInMySchedule(SessionJSO session) {
		return isInMySchedule(session.getId());
	}
	
	/**
	 * @param sessionId The session ID
	 * @return True if the session is in the users schedule.
	 */
	public boolean isInMySchedule(String sessionId) {
		return userSessions.contains(sessionId);
	}
	
	/**
	 * Determine whether the specified session is available at another time AND that alternative session is in the users schedule.
	 * @param session The session
	 * @return True if the specified session is available at another time AND that alternative session is in the users schedule.
	 */
	public boolean isOtherInMySchedule(SessionJSO session) {		
		Calendar cal = ConferenceDataManager.get().getCalendar();
		List<SessionJSO> others = cal.getOthers(session);
		if (others != null) {
			for (SessionJSO other : others) {
				if (isInMySchedule(other.getId())) {
					return true;
				}
			}
		}
		return false;		
	}
	
	/**
	 * @return The IDs of sessions for which the user has registered or an empty set.
	 */
	public Set<String> getUserSessions() {
		return userSessions;
	}
	
	/**
	 * Import the specified sessions
	 * @param sessions The sessions to import
	 * @param callback The callback to be notified with success or failure.
	 */
	public void importSchedule(Set<SessionJSO> sessions, AsyncCallback<Set<SessionJSO>> callback) {
		Set<String> old = new HashSet<String>(userSessions);
		
		Set<String> update = new HashSet<String>();
		for (SessionJSO session : sessions) {
			update.add(session.getId());
		}
		if (saveUserSchedule(update)) {
			userSessions = update;
			// dispatch unregistration events
			for (String id : old) {
				if (!userSessions.contains(id)) {
					Dispatcher.fire(new UserSessionUnregisteredEvent(id));
				}
			}
			// dispatch registration events
			for (String id : update) {		
				if (!old.contains(id)) {
					Dispatcher.fire(new UserSessionRegisteredEvent(id));
				}
			}
		} else if (callback != null) {
			callback.onFailure(new Exception("Failed to save schedule"));
			return;
		}
		
		if (callback != null) {
			callback.onSuccess(sessions);
		}
	}
	
	/**
	 * Register the user for a session
	 * @param sessionId The ID of the session to added to the user's schedule
	 * @param callback The callback to be notified when the registration is completed, or null.
	 */
	public void registerSession(String sessionId, AsyncCallback<String> callback) {
		// update
		if (!userSessions.contains(sessionId)) {
			Set<String> update = new HashSet<String>(userSessions);
			update.add(sessionId);
			if (saveUserSchedule(update)) {
				userSessions = update;
				Dispatcher.fire(new UserSessionRegisteredEvent(sessionId));
			} else if (callback != null) {
				callback.onFailure(new Exception("Failed to save schedule"));
				return;
			}
		}
		
		if (callback != null) {
			callback.onSuccess(sessionId);
		}
	}
	
	/**
	 * Unregister the user from a session
	 * @param sessionId The ID of the session to be removed from the user's schedule
	 * @param callback The callback to be notified when the operation is completed, or null.
	 */
	public void unregisterSession(String sessionId, AsyncCallback<String> callback) {
		if (userSessions.contains(sessionId)) {
			Set<String> update = new HashSet<String>(userSessions);
			update.remove(sessionId);
			if (saveUserSchedule(update)) {
				userSessions = update;
				Dispatcher.fire(new UserSessionUnregisteredEvent(sessionId));
			} else if (callback != null) {
				callback.onFailure(new Exception("Failed to save schedule"));
				return;
			}
		}
		if (callback != null) {
			callback.onSuccess(sessionId);
		}
	}
	
	/**
	 * Save the user schedule
	 * @return True if the save was successful, otherwise false.
	 */
	private boolean saveUserSchedule(Set<String> sessions) {
		if (!checkCookieSupport(true)) {
			LOGGER.warning("Cookies not enabled - unable to save user schedule");
			return false;
		}

		JSONArray arr = new JSONArray();
		if (sessions != null) {
			for (String id : sessions) {
				arr.set(arr.size(), new JSONString(id));
			}
		}
		if (arr.size() > 0) {
			Cookies.setCookie(USER_SCHEDULE_COOKIE, arr.toString(), getCookieExpiration());
		} else {
			Cookies.removeCookie(USER_SCHEDULE_COOKIE);
		}
		
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("Saved schedule: "+arr.toString());
		}
		return true;
	}
	
	/**
	 * Check for cookie support.
	 * @param fireEvent True to fire a CookiesDisabledEvent if cookies are not enabled
	 * @return True if cookies are enabled otherwise false.
	 */
	private boolean checkCookieSupport(boolean fireEvent) {
		if (!Cookies.isCookieEnabled()) {
			Dispatcher.fire(new ToastEvent("Cookies are disabled. In order to save your schedule, please enable Cookies."));
			return false;
		}
		return true;
	}
	
	private void loadUserSchedule() {
		if (!checkCookieSupport(true)) {
			LOGGER.warning("Cookies not enabled - unable to load user schedule");
			return;
		}
		
		String sessionsJson = Cookies.getCookie(USER_SCHEDULE_COOKIE);
		if (sessionsJson != null) {
			JSONValue val = JSONParser.parseStrict(sessionsJson);
			if (val != null) {
				JSONArray arr = val.isArray();
				if (arr != null) {
					Set<String> sessions = new HashSet<String>();
					for (int i = 0, len = arr.size(); i < len; i++) {
						JSONValue sessionVal = arr.get(i);
						if (sessionVal != null) {
							JSONString str = sessionVal.isString();
							if (str != null) {
								String sessionId = str.stringValue();
								if (sessionId != null && sessionId.trim().length() > 0) {
									sessions.add(sessionId);
								}
							}
						}
					}			
					if (sessions.size() > 0) {
						onUserScheduleLoaded(sessions);
					}
				}
			}
		}
	}
	
	private void onUserScheduleLoaded(Set<String> sessions) {
		this.userSessions.clear();
		if (sessions != null) {
			this.userSessions.addAll(sessions);
		}
		Dispatcher.fire(new UserSessionsLoadedEvent(this.userSessions));
	}
}
