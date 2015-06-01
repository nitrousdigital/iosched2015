package com.nitrous.iosched.client.view;

import java.util.Map;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.nitrous.iosched.client.event.Dispatcher;
import com.nitrous.iosched.client.event.RequestScrollToTimeBlockEvent;
import com.nitrous.iosched.client.event.SimpleEventHandler;
import com.nitrous.iosched.client.event.UserSessionRegisteredEvent;
import com.nitrous.iosched.client.event.UserSessionUnregisteredEvent;
import com.nitrous.iosched.client.event.UserSessionsLoadedEvent;
import com.nitrous.iosched.client.model.SessionJSO;
import com.nitrous.iosched.client.model.calendar.CalendarBlock;
import com.nitrous.iosched.client.resources.IOSchedResources;
import com.nitrous.iosched.client.service.UserScheduleService;
import com.nitrous.polygwt.client.component.HtmlRippleButton;

/**
 * Displays a time block in My Schedule
 * @author nitrousdigital
 *
 */
public class MyTimeBlockCard extends TimeBlockCard {
	private HTML addSessionsButton;
	HorizontalPanel buttonContainer;
	
	public MyTimeBlockCard(CalendarBlock block) {
		super(block);
		Dispatcher.addHandler(UserSessionRegisteredEvent.TYPE,  new SimpleEventHandler<UserSessionRegisteredEvent>(){
			@Override
			public void onEvent(UserSessionRegisteredEvent event) {
				showSession(event.getSessionId(), true);
			}
		});
		Dispatcher.addHandler(UserSessionUnregisteredEvent.TYPE,  new SimpleEventHandler<UserSessionUnregisteredEvent>(){
			@Override
			public void onEvent(UserSessionUnregisteredEvent event) {
				showSession(event.getSessionId(), false);
			}
		});
		Dispatcher.addHandler(UserSessionsLoadedEvent.TYPE,  new SimpleEventHandler<UserSessionsLoadedEvent>(){
			@Override
			public void onEvent(UserSessionsLoadedEvent event) {
				refresh();
			}
		});
		
		addSessionsButton = new HTML("Browse Sessions...");
		addSessionsButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Dispatcher.fire(new RequestScrollToTimeBlockEvent(MyTimeBlockCard.this.block));
			}
		});
		addSessionsButton.asWidget().setStyleName(IOSchedResources.INSTANCE.css().browseSessionsButton());
		layout.add(addSessionsButton);
		layout.setCellHeight(addSessionsButton, HtmlRippleButton.DEFAULT_HEIGHT + "px");
		
		onResize();
	}

	
	/**
	 * Update the expired state of this card
	 * @param isExpired True if the time block is now expired
	 */
	@Override
	protected void setExpired(boolean isExpired) {
		super.setExpired(isExpired);
		if (isExpired) {
			sessionRowsVerticalLayout.asWidget().getElement().getStyle().setBackgroundColor("#eee");
		} else {
			sessionRowsVerticalLayout.asWidget().getElement().getStyle().clearBackgroundColor();
		}
	}
	
	/**
	 * Register a listener for tag filters
	 */
	@Override
	protected void initFilterListener() {
		// no filtering on my schedule
	}
	
	/**
	 * Apply the current filter state
	 */
	@Override
	protected void applyFilters() {
		// no filtering on my schedule
	}
	
	@Override
	public void onResize() {
		onResize(Window.getClientWidth());
	}
	
	@Override
	public void onResize(int parentWidth) {
		super.onResize(parentWidth);
		int width = parentWidth - LEFT_RIGHT_MARGIN;
		if (buttonContainer != null) {
			buttonContainer.setWidth(width + "px");
		}
	}
	
	/**
	 * Refresh the visibility of session rows based on current session registrations. 
	 */
	private void refresh() {
		Set<String> registered = UserScheduleService.get().getUserSessions();
		for (Map.Entry<String, SessionRow> entry : sessionRowsById.entrySet()) {
			showSession(entry.getKey(), registered.contains(entry.getKey()));
		}
	}
	
	@Override
	protected boolean shouldBeVisible(SessionJSO session) {
		return UserScheduleService.get().isInMySchedule(session);
	}
}
