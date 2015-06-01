package com.nitrous.iosched.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fired when the ConferenceDataManager has new data.
 * @author nitrousdigital
 */
public class ConferenceDataUpdatedEvent extends GwtEvent<SimpleEventHandler<ConferenceDataUpdatedEvent>> {
	public ConferenceDataUpdatedEvent() {
	}

	//////// BEGIN BOILER PLATE
	public static final GwtEvent.Type<SimpleEventHandler<ConferenceDataUpdatedEvent>> TYPE = new GwtEvent.Type<SimpleEventHandler<ConferenceDataUpdatedEvent>>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SimpleEventHandler<ConferenceDataUpdatedEvent>> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SimpleEventHandler<ConferenceDataUpdatedEvent> handler) {
		handler.onEvent(this);
	}

}
