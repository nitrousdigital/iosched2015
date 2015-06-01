package com.nitrous.iosched.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.nitrous.iosched.client.model.calendar.CalendarBlock;

/**
 * Fired to request a time block scrolled into view on the explorer view to allow the user to find content to add to his/her schedule.
 * 
 * @author nitrousdigital
 */
public class RequestScrollToTimeBlockEvent extends GwtEvent<SimpleEventHandler<RequestScrollToTimeBlockEvent>> {
	private CalendarBlock calendarBlock;
	public RequestScrollToTimeBlockEvent(CalendarBlock block) {
		this.calendarBlock = block;
	}
	
	/**
	 * @return The calendar blockto be scrolled into view.
	 */
	public CalendarBlock getCalendarBlock() {
		return calendarBlock;
	}
	
	//////// BEGIN BOILER PLATE
	public static final GwtEvent.Type<SimpleEventHandler<RequestScrollToTimeBlockEvent>> TYPE = new GwtEvent.Type<SimpleEventHandler<RequestScrollToTimeBlockEvent>>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SimpleEventHandler<RequestScrollToTimeBlockEvent>> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SimpleEventHandler<RequestScrollToTimeBlockEvent> handler) {
		handler.onEvent(this);
	}

}
