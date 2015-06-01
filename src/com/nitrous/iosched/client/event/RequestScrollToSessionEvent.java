package com.nitrous.iosched.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.nitrous.iosched.client.model.SessionJSO;

/**
 * Fired to request a session to be scrolled into view
 * @author nitrousdigital
 */
public class RequestScrollToSessionEvent extends GwtEvent<SimpleEventHandler<RequestScrollToSessionEvent>> {
	private SessionJSO session;
	public RequestScrollToSessionEvent(SessionJSO session) {
		this.session = session;
	}
	
	/**
	 * @return The element to be scrolled into view.
	 */
	public SessionJSO getSession() {
		return session;
	}
	
	//////// BEGIN BOILER PLATE
	public static final GwtEvent.Type<SimpleEventHandler<RequestScrollToSessionEvent>> TYPE = new GwtEvent.Type<SimpleEventHandler<RequestScrollToSessionEvent>>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SimpleEventHandler<RequestScrollToSessionEvent>> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SimpleEventHandler<RequestScrollToSessionEvent> handler) {
		handler.onEvent(this);
	}

}
