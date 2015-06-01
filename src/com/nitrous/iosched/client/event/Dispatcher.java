package com.nitrous.iosched.client.event;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.SimpleEventBus;

/**
 * A singleton that provides shared access to the client-side event bus.
 * 
 * @author nitrousdigital
 */
public class Dispatcher {
	private static Dispatcher INSTANCE = new Dispatcher();
	private EventBus bus;

	private Dispatcher() {
		bus = new SimpleEventBus();
	}

	public static void fire(GwtEvent<?> event) {
		INSTANCE.bus.fireEvent(event);
	}

	public static <H extends EventHandler> HandlerRegistration addHandler(GwtEvent.Type<H> type, H handler) {
		return INSTANCE.bus.addHandler(type, handler);
	}

	public static EventBus getBus() {
		return INSTANCE.bus;
	}

	public static Dispatcher get() {
		return INSTANCE;
	}
}
