package com.nitrous.iosched.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface SimpleEventHandler<T extends GwtEvent<?>> extends EventHandler {
	void onEvent(T event);
}
