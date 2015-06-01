package com.nitrous.iosched.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fired to request a reload of the conference data from the server.
 * @author nitrousdigital
 */
public class RequestReloadDataEvent extends GwtEvent<SimpleEventHandler<RequestReloadDataEvent>> {
	public RequestReloadDataEvent() {
	}
	
	//////// BEGIN BOILER PLATE
	public static final GwtEvent.Type<SimpleEventHandler<RequestReloadDataEvent>> TYPE = new GwtEvent.Type<SimpleEventHandler<RequestReloadDataEvent>>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SimpleEventHandler<RequestReloadDataEvent>> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SimpleEventHandler<RequestReloadDataEvent> handler) {
		handler.onEvent(this);
	}

}
