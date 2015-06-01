package com.nitrous.iosched.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fired to request that auto-hiding of the header be enabled/disabled.
 * @author nitrousdigital
 */
public class RequestEnableHeaderAutoHideEvent extends GwtEvent<SimpleEventHandler<RequestEnableHeaderAutoHideEvent>> {
	private boolean enable;
	public RequestEnableHeaderAutoHideEvent(boolean enable) {
		this.enable = enable;
	}
	
	/**
	 * 
	 * @return True to enable auto-hiding of the header in response to scroll events, False to disable.
	 */
	public boolean isEnableAutoHide() {
		return enable;
	}

	//////// BEGIN BOILER PLATE
	public static final GwtEvent.Type<SimpleEventHandler<RequestEnableHeaderAutoHideEvent>> TYPE = new GwtEvent.Type<SimpleEventHandler<RequestEnableHeaderAutoHideEvent>>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SimpleEventHandler<RequestEnableHeaderAutoHideEvent>> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SimpleEventHandler<RequestEnableHeaderAutoHideEvent> handler) {
		handler.onEvent(this);
	}

}
