package com.nitrous.iosched.client.service;

import com.nitrous.iosched.client.component.polymer.PaperToast;
import com.nitrous.iosched.client.event.Dispatcher;
import com.nitrous.iosched.client.event.SimpleEventHandler;
import com.nitrous.iosched.client.event.ToastEvent;

/**
 * Display a warning to the user if cookies are disabled when we attempt to
 * load/save state. The warning will only be displayed once per user session.
 * 
 * @author nitrousdigital
 *
 */
public class ToastService {
	private static ToastService INSTANCE;

	private ToastService() {
		Dispatcher.addHandler(ToastEvent.TYPE,
				new SimpleEventHandler<ToastEvent>() {
					@Override
					public void onEvent(ToastEvent event) {
						showToast(event.getMessage());
					}
				});
	}

	private void showToast(String message) {
		PaperToast.show(message);
	}
	
	
	public static void register() {
		get();
	}
	
	public static ToastService get() {
		if (INSTANCE == null) {
			INSTANCE = new ToastService();
		}
		return INSTANCE;
	}
}
