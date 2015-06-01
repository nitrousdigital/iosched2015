package com.nitrous.iosched.client.view;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nitrous.iosched.client.component.PopupMenu;
import com.nitrous.iosched.client.component.polymer.CoreIcon;
import com.nitrous.iosched.client.event.Dispatcher;
import com.nitrous.iosched.client.event.SimpleEventHandler;
import com.nitrous.iosched.client.event.UserSessionRegisteredEvent;
import com.nitrous.iosched.client.event.UserSessionUnregisteredEvent;
import com.nitrous.iosched.client.event.UserSessionsLoadedEvent;
import com.nitrous.iosched.client.model.ConferenceDataManager;
import com.nitrous.iosched.client.model.SessionJSO;
import com.nitrous.iosched.client.model.calendar.Calendar;
import com.nitrous.iosched.client.resources.IOSchedResources;
import com.nitrous.iosched.client.service.UserScheduleService;
import com.nitrous.iosched.client.view.config.Layers;
import com.nitrous.polygwt.client.component.HtmlRippleButton;

/**
 * An image that becomes visible when a session is present in the users schedule
 * at a different time.
 * 
 * @author nitrousdigital
 *
 */
public class OtherTimeMarkerImage extends HtmlRippleButton {
	public static final int DEFAULT_WIDTH = 24;
	public static final int DEFAULT_HEIGHT = 24;
	
	private SessionJSO session;
	public OtherTimeMarkerImage(SessionJSO session) {
		super(CoreIcon.getHTML("check-circle"));
		setPixelSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		
		this.session = session;
		
		Widget w = this.asWidget();
		w.setStyleName(IOSchedResources.INSTANCE.css().sessionConflictStatusIcon());
		w.addAttachHandler(new Handler() {			
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached()) {
					updateState();
					register();
				} else {
					unregister();
				}
			}
		});
	}

	/**
	 * Create and return a OtherTimeMarkerImage for the specified session that
	 * will be used to indicate whether the specified session appears at other
	 * time(s) in the users schedule.
	 * 
	 * @param session
	 *            The session
	 * @return The OtherTimeMarkerImage or null if the specified session doesn't
	 *         occur more than once during the conference.
	 */
	public static OtherTimeMarkerImage create(SessionJSO session) {
		Calendar cal = ConferenceDataManager.get().getCalendar();
		List<SessionJSO> others = cal.getOthers(session);
		boolean hasOthers = others != null && others.size() > 0;

		OtherTimeMarkerImage menuButton = null;
		if (hasOthers) {
			menuButton = new OtherTimeMarkerImage(session);
		}
		return menuButton;
	}
	
	private ArrayList<HandlerRegistration> reg = new ArrayList<HandlerRegistration>();
	
	private void register() {
		reg.add(Dispatcher.addHandler(UserSessionsLoadedEvent.TYPE, new SimpleEventHandler<UserSessionsLoadedEvent>() {
			@Override
			public void onEvent(UserSessionsLoadedEvent event) {
				updateState();
			}
		}));
		reg.add(Dispatcher.addHandler(UserSessionRegisteredEvent.TYPE, new SimpleEventHandler<UserSessionRegisteredEvent>() {
			@Override
			public void onEvent(UserSessionRegisteredEvent event) {
				updateState();
			}
		}));
		reg.add(Dispatcher.addHandler(UserSessionUnregisteredEvent.TYPE, new SimpleEventHandler<UserSessionUnregisteredEvent>() {
			@Override
			public void onEvent(UserSessionUnregisteredEvent event) {
				updateState();
			}
		}));
		
		reg.add(this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showPopup();
			}
		}));
		
		// hide the popup menu on escape
		reg.add(RootPanel.get().addDomHandler(new KeyDownHandler(){
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					hidePopup();
				}
			}}, KeyDownEvent.getType()));
		
	}
	
	
	private PopupMenu popup;
	private void hidePopup() {
		if (popup != null) {
			popup.hide();
		}
	}
	
	private Timer hideTimer = new Timer() {
		@Override
		public void run() {
			hidePopup();
		}
	};
	
	private void showPopup() {
		hidePopup();
		if (message != null && asWidget().isAttached()) {
			popup = new PopupMenu();
			popup.add(new HTML(message));
			popup.setAutoHideEnabled(true);
			popup.showRelativeTo(this.asWidget());
			popup.getElement().getStyle().setZIndex(Layers.POPUP_Z_INDEX + 1);
			if (hideTimer == null) {
				hideTimer = new Timer() {
					@Override
					public void run() {
						hidePopup();
					}
				};
			}
			hideTimer.cancel();
			hideTimer.schedule(3000);
		}
	}
	
	private void unregister() {
		for (HandlerRegistration r : reg) {
			r.removeHandler();
		}
		reg.clear();
	}
	
	private String message;
	
	private void updateState() {
		boolean scheduleAtOtherTime = UserScheduleService.get().isOtherInMySchedule(session);
		boolean isSessionInSchedule = UserScheduleService.get().isInMySchedule(session);
		String icon = null;
		String style = null;
		this.message = null;
		if (scheduleAtOtherTime) {
			if (isSessionInSchedule) {
				// conflict - red
				icon = "error";
				style = "color:red;cursor:pointer;";
				message = "This session is in your schedule multiple times."; 
				this.asWidget().setTitle(message);
			} else {
				// only schedule at other time - gray
				icon = "check-circle";
				style = "color:#ddd;cursor:pointer;";
				message = "This session is in your schedule at another time."; 
				this.asWidget().setTitle(message);
			}
		} 
		
		if (icon != null) {
			this.getButtonHTML().setHTML(CoreIcon.getHTML(icon,  style));
			this.asWidget().setVisible(true);
		} else {
			this.asWidget().setVisible(false);
		}
	}
}
