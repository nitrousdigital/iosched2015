package com.nitrous.iosched.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fired when a user unregisters for a session.
 * 
 * @author nitrousdigital
 */
public class UserSessionUnregisteredEvent extends GwtEvent<SimpleEventHandler<UserSessionUnregisteredEvent>> {
	private String sessionId;

	public UserSessionUnregisteredEvent(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}

	// ////// BEGIN BOILER PLATE
	public static final GwtEvent.Type<SimpleEventHandler<UserSessionUnregisteredEvent>> TYPE = new GwtEvent.Type<SimpleEventHandler<UserSessionUnregisteredEvent>>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SimpleEventHandler<UserSessionUnregisteredEvent>> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(
			SimpleEventHandler<UserSessionUnregisteredEvent> handler) {
		handler.onEvent(this);
	}

}
