package com.nitrous.iosched.client.component;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;

/**
 * Batch up and ignore intermediate window resize events to prevent layout thrashing.
 * 
 * @author nitrousdigital
 */
public class DeferredWindowResizeHandler implements ResizeHandler {
	private ResizeHandler handler;
	private DelayedTask delay;
	private ResizeEvent lastEvent;
	
	public DeferredWindowResizeHandler(ResizeHandler handler) {
		this.handler = handler;
		this.delay = new DelayedTask() {
			@Override
			public void run() {
				dispatchEventNow();
			}
		};
		Window.addResizeHandler(this);
	}
	
	private void dispatchEventNow() {
		handler.onResize(lastEvent);
	}

	@Override
	public void onResize(ResizeEvent event) {
		this.lastEvent = event;
		delay.schedule(200);
	}
}
