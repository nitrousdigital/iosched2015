package com.nitrous.iosched.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fired to display a toast message
 * 
 * @author nitrousdigital
 */
public class ToastEvent extends GwtEvent<SimpleEventHandler<ToastEvent>> {

	private String message;
	public ToastEvent(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}

	//////// BEGIN BOILER PLATE
	public static final GwtEvent.Type<SimpleEventHandler<ToastEvent>> TYPE = new GwtEvent.Type<SimpleEventHandler<ToastEvent>>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SimpleEventHandler<ToastEvent>> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SimpleEventHandler<ToastEvent> handler) {
		handler.onEvent(this);
	}

}
