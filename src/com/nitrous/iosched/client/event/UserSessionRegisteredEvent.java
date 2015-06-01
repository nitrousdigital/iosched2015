package com.nitrous.iosched.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fired when a user registers for a session.
 * 
 * @author nitrousdigital
 */
public class UserSessionRegisteredEvent extends GwtEvent<SimpleEventHandler<UserSessionRegisteredEvent>> {
	private String sessionId;

	public UserSessionRegisteredEvent(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}

	// ////// BEGIN BOILER PLATE
	public static final GwtEvent.Type<SimpleEventHandler<UserSessionRegisteredEvent>> TYPE = new GwtEvent.Type<SimpleEventHandler<UserSessionRegisteredEvent>>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SimpleEventHandler<UserSessionRegisteredEvent>> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(
			SimpleEventHandler<UserSessionRegisteredEvent> handler) {
		handler.onEvent(this);
	}

}
