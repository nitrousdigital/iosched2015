package com.nitrous.iosched.client.event;

import java.util.Set;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fired when the list of user session IDs is loaded.
 * 
 * @author nitrousdigital
 */
public class UserSessionsLoadedEvent extends GwtEvent<SimpleEventHandler<UserSessionsLoadedEvent>> {
	private Set<String> userSessions;
	
	public UserSessionsLoadedEvent(Set<String> userSessions) {
		this.userSessions = userSessions;
	}
	
	public Set<String> getUserSessions() {
		return userSessions;
	}

	//////// BEGIN BOILER PLATE
	public static final GwtEvent.Type<SimpleEventHandler<UserSessionsLoadedEvent>> TYPE = new GwtEvent.Type<SimpleEventHandler<UserSessionsLoadedEvent>>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SimpleEventHandler<UserSessionsLoadedEvent>> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SimpleEventHandler<UserSessionsLoadedEvent> handler) {
		handler.onEvent(this);
	}

}
