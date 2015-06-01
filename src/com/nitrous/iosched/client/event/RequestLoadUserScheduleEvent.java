package com.nitrous.iosched.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fired to request loading of the user-defined schedule
 * @author nitrousdigital
 */
public class RequestLoadUserScheduleEvent extends GwtEvent<SimpleEventHandler<RequestLoadUserScheduleEvent>> {

	public RequestLoadUserScheduleEvent() {
	}

	//////// BEGIN BOILER PLATE
	public static final GwtEvent.Type<SimpleEventHandler<RequestLoadUserScheduleEvent>> TYPE = new GwtEvent.Type<SimpleEventHandler<RequestLoadUserScheduleEvent>>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SimpleEventHandler<RequestLoadUserScheduleEvent>> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SimpleEventHandler<RequestLoadUserScheduleEvent> handler) {
		handler.onEvent(this);
	}

}
