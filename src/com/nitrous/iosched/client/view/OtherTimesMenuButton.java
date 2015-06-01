package com.nitrous.iosched.client.view;

import java.util.List;

import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.AnimationType;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.nitrous.iosched.client.component.PopupMenu;
import com.nitrous.iosched.client.component.polymer.CoreIcon;
import com.nitrous.iosched.client.event.Dispatcher;
import com.nitrous.iosched.client.event.RequestScrollToSessionEvent;
import com.nitrous.iosched.client.event.SimpleEventHandler;
import com.nitrous.iosched.client.event.WindowScrolledEvent;
import com.nitrous.iosched.client.model.ConferenceDataManager;
import com.nitrous.iosched.client.model.SessionJSO;
import com.nitrous.iosched.client.model.calendar.Calendar;
import com.nitrous.iosched.client.resources.IOSchedResources;
import com.nitrous.iosched.client.service.TimeService;
import com.nitrous.iosched.client.view.config.Layers;
import com.nitrous.polygwt.client.component.HtmlRippleButton;
import com.nitrous.polygwt.client.component.RippleButton;

/**
 * A button that displays a popup/dropdown menu listing alternate times for a session.
 * @author nitrousdigital
 *
 */
public class OtherTimesMenuButton extends HtmlRippleButton {
	private PopupMenu popupMenu;
	private HandlerRegistration popupMenuHideReg;
	private SessionJSO currentSession;
	public static final int DEFAULT_WIDTH = 24;
	public static final int DEFAULT_HEIGHT = 24;
	
	public OtherTimesMenuButton(SessionJSO currentSession, List<SessionJSO> others, String style) {
		super(CoreIcon.getHTML("menu", style));		
		setPixelSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		this.asWidget().getElement().getStyle().setPadding(0, Unit.PX);		
		this.getRippleCanvas().getElement().getStyle().setZIndex(Layers.POPUP_RIPPLE_Z_INDEX);
		this.currentSession = currentSession;
		this.asWidget().setTitle("Click to see other time(s) for this session.");
		this.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onTimeMenuButtonClicked();
			}
		});

		// hide the popup when the view is scrolled
		Dispatcher.addHandler(WindowScrolledEvent.TYPE,
				new SimpleEventHandler<WindowScrolledEvent>() {
					@Override
					public void onEvent(WindowScrolledEvent event) {
						hideTimeMenu();
					}
				});
		
		// hide the popup menu on escape
		RootPanel.get().addDomHandler(new KeyDownHandler(){
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					hideTimeMenu();
				}
			}}, KeyDownEvent.getType());
		
	}

	private void onTimeMenuButtonClicked() {
		if (popupMenu == null) {
			// popup menu not showing
			buildTimeMenu();
			popupMenu.showRelativeTo(this.asWidget().getElement());
		} else {
			if (popupMenu.isShowing()) {
				// popup menu showing, so hide
				hideTimeMenu();
			} else {
				// popup menu not showing, so move
				popupMenu.showRelativeTo(this.asWidget().getElement());
			}
		}
	}
	
	private void hideTimeMenu() {
		if (popupMenu != null) {
			popupMenu.hide();
			popupMenu = null;
		}
		if (popupMenuHideReg != null) {
			popupMenuHideReg.removeHandler();
			popupMenuHideReg = null;
		}
	}
		
	private void buildTimeMenu() {
		hideTimeMenu();
		
		popupMenu = new PopupMenu();
		popupMenu.getElement().getStyle().setZIndex(Layers.POPUP_Z_INDEX);
		VerticalPanel content = new VerticalPanel();
		popupMenu.add(content);
		popupMenu.setAnimationEnabled(true);
		popupMenu.setAutoHideEnabled(true);
		popupMenu.setAnimationType(AnimationType.ONE_WAY_CORNER);
		
		Calendar cal = ConferenceDataManager.get().getCalendar();
		List<SessionJSO> others = cal.getOthers(currentSession);
		StringBuilder otherTimes = new StringBuilder("<span class='")
				.append(IOSchedResources.INSTANCE.css().sessionMenuHeaderText())
				.append("'>Also showing at the following time");
		if (others.size() > 1) {
			otherTimes.append("s");
		}
		otherTimes.append(":</span>");
		content.add(new HTML(otherTimes.toString()));
		
		long now = TimeService.getCurrentTime();		
		
		// menu option list
		for (SessionJSO other : others) {
			boolean inSchedule = other.isInMySchedule();

			final HtmlRippleButton menuItemButton = new HtmlRippleButton(other.getSessionDateTime());
			menuItemButton.getButtonHTML().getElement().getStyle().setColor("rgb(102, 102, 102)");
			
			// mark expired sessions
			if (other.isExpired(now)) {
				menuItemButton.getButtonHTML().getElement().getStyle().setTextDecoration(TextDecoration.LINE_THROUGH);
			}
			
			menuItemButton.setHeight(25);
			menuItemButton.getRippleCanvas().getElement().getStyle().setZIndex(Layers.POPUP_RIPPLE_Z_INDEX);
			menuItemButton.asWidget().getElement().getStyle().setZIndex(Layers.POPUP_RIPPLE_Z_INDEX + 1);
			
			HorizontalPanel row = new HorizontalPanel();
			row.getElement().getStyle().setZIndex(Layers.POPUP_RIPPLE_Z_INDEX + 1);
			row.add(menuItemButton);
			row.setStyleName(IOSchedResources.INSTANCE.css().popupMenuItem());
			row.setWidth("100%");
			row.setCellWidth(menuItemButton, "200px");
			row.setCellHeight(menuItemButton, RippleButton.DEFAULT_HEIGHT + "px");
			if (inSchedule) {
				row.setTitle("This session is in your schedule.");
				CoreIcon ico = new CoreIcon("check-circle", "color:#33c6d9;");
				row.add(ico);
			}			
			
			final SessionJSO otherItem = other;
			menuItemButton.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {				
				@Override
				public void onClick(ClickEvent event) {
					event.preventDefault();
					event.stopPropagation();
					// allow the ripple some time to animate before hiding the menu
					Timer t = new Timer() {
						@Override
						public void run() {
							hideTimeMenu();
							scrollToSession(otherItem);
						}
					};
					t.schedule(500);
				}
			});
			content.add(row);
			content.setCellHeight(row, "25px");
		}
		
		popupMenuHideReg = popupMenu.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				hideTimeMenu();
			}
		});
	}
	
	/**
	 * Create an OtherTimesMenuButton if the specified session has other show
	 * times.
	 * 
	 * @param session
	 *            The current session to be checked for other scheduled times
	 * @return The OtherTimesMenuButton that can be used to launch a popup menu
	 *         to navigate to other showtimes or null if the session does not
	 *         have any other scheduled times.
	 */
	public static OtherTimesMenuButton create(SessionJSO session) {
		return create(session, null);
	}
	
	/**
	 * Create an OtherTimesMenuButton if the specified session has other show
	 * times.
	 * 
	 * @param session
	 *            The current session to be checked for other scheduled times
	 * @param style The style to be applied to the icon or null
	 * @return The OtherTimesMenuButton that can be used to launch a popup menu
	 *         to navigate to other showtimes or null if the session does not
	 *         have any other scheduled times.
	 */
	public static OtherTimesMenuButton create(SessionJSO session, String style) {
		Calendar cal = ConferenceDataManager.get().getCalendar();
		List<SessionJSO> others = cal.getOthers(session);
		boolean hasOthers = others != null && others.size() > 0;

		OtherTimesMenuButton menuButton = null;
		if (hasOthers) {
			menuButton = new OtherTimesMenuButton(session, others, style);
		}
		return menuButton;
	}
	
	private void scrollToSession(SessionJSO session) {
		Dispatcher.fire(new RequestScrollToSessionEvent(session));
	}

}
