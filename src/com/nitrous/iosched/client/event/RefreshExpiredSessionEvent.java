package com.nitrous.iosched.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fired to refresh rendered state of expired sessions
 * 
 * @author nitrousdigital
 */
public class RefreshExpiredSessionEvent extends
		GwtEvent<SimpleEventHandler<RefreshExpiredSessionEvent>> {
	private long currentTime;

	public RefreshExpiredSessionEvent(long currentTime) {
		this.currentTime = currentTime;
	}

	public long getCurrentTime() {
		return currentTime;
	}

	// ////// BEGIN BOILER PLATE
	public static final GwtEvent.Type<SimpleEventHandler<RefreshExpiredSessionEvent>> TYPE = new GwtEvent.Type<SimpleEventHandler<RefreshExpiredSessionEvent>>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SimpleEventHandler<RefreshExpiredSessionEvent>> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(
			SimpleEventHandler<RefreshExpiredSessionEvent> handler) {
		handler.onEvent(this);
	}

}
