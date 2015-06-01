package com.nitrous.iosched.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fired to request the export dialog to be displayed
 * @author nitrousdigital
 */
public class RequestExportEvent extends GwtEvent<SimpleEventHandler<RequestExportEvent>> {
	public RequestExportEvent() {
	}
	
	//////// BEGIN BOILER PLATE
	public static final GwtEvent.Type<SimpleEventHandler<RequestExportEvent>> TYPE = new GwtEvent.Type<SimpleEventHandler<RequestExportEvent>>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SimpleEventHandler<RequestExportEvent>> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SimpleEventHandler<RequestExportEvent> handler) {
		handler.onEvent(this);
	}

}
