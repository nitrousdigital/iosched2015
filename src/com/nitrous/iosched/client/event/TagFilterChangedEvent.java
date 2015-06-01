package com.nitrous.iosched.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.nitrous.iosched.client.model.TagFilter;

/**
 * Fired when the tag filter is changed (i.e. a user changes the tag selections in the filter drop-down)
 * 
 * @author nitrousdigital
 */
public class TagFilterChangedEvent extends GwtEvent<SimpleEventHandler<TagFilterChangedEvent>> {
	private TagFilter filter;
	
	public TagFilterChangedEvent(TagFilter filter) {
		this.filter = filter;
	}

	/**
	 * @return The current tag filter
	 */
	public TagFilter getFilter() {
		return filter;
	}
	
	//////// BEGIN BOILER PLATE
	public static final GwtEvent.Type<SimpleEventHandler<TagFilterChangedEvent>> TYPE = new GwtEvent.Type<SimpleEventHandler<TagFilterChangedEvent>>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SimpleEventHandler<TagFilterChangedEvent>> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SimpleEventHandler<TagFilterChangedEvent> handler) {
		handler.onEvent(this);
	}

}
