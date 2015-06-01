package com.nitrous.iosched.client.view;

import com.nitrous.iosched.client.model.calendar.CalendarBlock;

public class MyScheduleView extends ScheduleExplorerView {
	
	@Override
	protected TimeBlockCard constructTimeBlockCard(CalendarBlock block) {
		return new MyTimeBlockCard(block);
	}

}
