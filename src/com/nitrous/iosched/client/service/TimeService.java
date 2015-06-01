package com.nitrous.iosched.client.service;

import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.client.Window;

/**
 * A central place to obtain either the real current time or the debug time as
 * specified by the URL argument 'now' (e.g. now=201505291130-0700)
 * 
 * @author nitrousdigital
 *
 */
public class TimeService {
	/**
	 * The date time format used to parse the URL debug argument e.g.
	 * now=201505291130-0700
	 */
	private static final DateTimeFormat DEBUG_TIME_FORMAT = DateTimeFormat
			.getFormat("yyyyMMddHHmmZ");

	private TimeService() {
	}

	/**
	 * @return Returns either the actual current time or the time specified via
	 *         the 'now' URL argument (e.g. now=201505291130-0700)
	 */
	public static long getCurrentTime() {
		// check for debug time parameter
		String now = Window.Location.getParameter("now");
		if (now == null) {
			return System.currentTimeMillis();
		}
		return DEBUG_TIME_FORMAT.parse(now).getTime();
	}

}
