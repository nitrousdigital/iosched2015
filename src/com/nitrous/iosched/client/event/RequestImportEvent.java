package com.nitrous.iosched.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fired to request the import dialog to be displayed
 * @author nitrousdigital
 */
public class RequestImportEvent extends GwtEvent<SimpleEventHandler<RequestImportEvent>> {
	public RequestImportEvent() {
	}
	
	//////// BEGIN BOILER PLATE
	public static final GwtEvent.Type<SimpleEventHandler<RequestImportEvent>> TYPE = new GwtEvent.Type<SimpleEventHandler<RequestImportEvent>>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SimpleEventHandler<RequestImportEvent>> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SimpleEventHandler<RequestImportEvent> handler) {
		handler.onEvent(this);
	}

}
