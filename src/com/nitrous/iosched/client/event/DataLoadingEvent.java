package com.nitrous.iosched.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fired to hide or show the loading spinner
 * @author nitrousdigital
 */
public class DataLoadingEvent extends GwtEvent<SimpleEventHandler<DataLoadingEvent>> {
	private boolean isLoading;
	public DataLoadingEvent(boolean isLoading) {
		this.isLoading = isLoading;
	}
	
	/**
	 * 
	 * @return True if currently loading, otherwise false.
	 */
	public boolean isLoading() {
		return isLoading;
	}

	//////// BEGIN BOILER PLATE
	public static final GwtEvent.Type<SimpleEventHandler<DataLoadingEvent>> TYPE = new GwtEvent.Type<SimpleEventHandler<DataLoadingEvent>>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SimpleEventHandler<DataLoadingEvent>> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SimpleEventHandler<DataLoadingEvent> handler) {
		handler.onEvent(this);
	}

}
