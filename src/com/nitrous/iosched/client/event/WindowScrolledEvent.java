package com.nitrous.iosched.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fired when the scroll pane is scrolled.
 * 
 * @author nitrousdigital
 */
public class WindowScrolledEvent extends GwtEvent<SimpleEventHandler<WindowScrolledEvent>> {

	public WindowScrolledEvent() {
	}

	//////// BEGIN BOILER PLATE
	public static final GwtEvent.Type<SimpleEventHandler<WindowScrolledEvent>> TYPE = new GwtEvent.Type<SimpleEventHandler<WindowScrolledEvent>>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SimpleEventHandler<WindowScrolledEvent>> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SimpleEventHandler<WindowScrolledEvent> handler) {
		handler.onEvent(this);
	}

}
