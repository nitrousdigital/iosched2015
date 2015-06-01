package com.nitrous.iosched.client.view;

import com.nitrous.iosched.client.model.calendar.CalendarDay;

public interface ScheduleDayFilter {
	boolean accept(CalendarDay day);
}
