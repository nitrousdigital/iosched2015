package com.nitrous.iosched.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fired to request that the header tabs be hidden
 * @author nitrousdigital
 */
public class RequestHideHeaderEvent extends GwtEvent<SimpleEventHandler<RequestHideHeaderEvent>> {
	private boolean animate;
	public RequestHideHeaderEvent(boolean animate) {
		this.animate = animate;
	}
	
	/**
	 * @return True to animate hiding of the header
	 */
	public boolean isAnimate() {
		return animate;
	}

	//////// BEGIN BOILER PLATE
	public static final GwtEvent.Type<SimpleEventHandler<RequestHideHeaderEvent>> TYPE = new GwtEvent.Type<SimpleEventHandler<RequestHideHeaderEvent>>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SimpleEventHandler<RequestHideHeaderEvent>> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SimpleEventHandler<RequestHideHeaderEvent> handler) {
		handler.onEvent(this);
	}

}
