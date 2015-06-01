package com.nitrous.iosched.client.view;

import java.util.ArrayList;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.nitrous.iosched.client.event.Dispatcher;
import com.nitrous.iosched.client.event.RefreshExpiredSessionEvent;
import com.nitrous.iosched.client.event.SimpleEventHandler;
import com.nitrous.iosched.client.event.UserSessionRegisteredEvent;
import com.nitrous.iosched.client.event.UserSessionUnregisteredEvent;
import com.nitrous.iosched.client.event.UserSessionsLoadedEvent;
import com.nitrous.iosched.client.model.SessionJSO;
import com.nitrous.iosched.client.model.TagFilter;
import com.nitrous.iosched.client.resources.IOSchedResources;
import com.nitrous.iosched.client.service.TimeService;
import com.nitrous.iosched.client.service.UserScheduleService;
import com.nitrous.iosched.client.view.config.Config;
import com.nitrous.iosched.client.view.config.Layers;
import com.nitrous.polygwt.client.component.HtmlRippleButton;
import com.nitrous.polygwt.client.component.RippleButton;
import com.nitrous.polygwt.client.component.RippleCheckBox;
import com.nitrous.polygwt.client.component.RippleCheckBox.CheckHandler;

public class SessionRow extends HorizontalPanel implements RequiresContainerWidth {
	/** 
	 * The shared session detail popup dialog.
	 */
	private static SessionDetailDialog detailPopup;
	
	private SessionJSO session;
	
	private RippleCheckBox checkBox;
	private HtmlRippleButton titleButton;
	private RippleButton menuButton;
	
	private boolean visible = true;

	private static final int TOP_ROW_HEIGHT = HtmlRippleButton.DEFAULT_HEIGHT_WITH_PADDING;
	private static final String TOP_ROW_HEIGHT_PX = TOP_ROW_HEIGHT + "px";
	
	private static final int CHECKBOX_WIDTH = RippleCheckBox.SIZE 
			+ IOSchedResources.INSTANCE.css().sessionCheckBoxLeftMargin() 
			+ IOSchedResources.INSTANCE.css().sessionCheckBoxRightMargin();
	private static final String CHECKBOX_WIDTH_PX = CHECKBOX_WIDTH + "px";
	
	private ArrayList<HandlerRegistration> reg = new ArrayList<HandlerRegistration>();
	
	public SessionRow(SessionJSO session) {
		this.session = session;
		
		getElement().setId("session-"+session.getId());

		checkBox = new RippleCheckBox();
		checkBox.setRippleEnabled(false);
		checkBox.asWidget().setStyleName(IOSchedResources.INSTANCE.css().sessionCheckBox());
		
		String titleText = session.getTitle();
		titleButton = new HtmlRippleButton(titleText);
		titleButton.getRippleCanvas().getElement().getStyle().setZIndex(Layers.SESSION_RIPPLE_Z_INDEX);
		titleButton.getRippleCanvas().getElement().getStyle().setCursor(Cursor.POINTER);
		titleButton.asWidget().setStyleName(IOSchedResources.INSTANCE.css().sessionTitle());
		titleButton.asWidget().getElement().getStyle().setZIndex(Layers.SESSION_RIPPLE_Z_INDEX + 1);
		titleButton.asWidget().setTitle(titleText);			
		
		// session time
		HorizontalPanel sessionTime = new HorizontalPanel();
		sessionTime.setStyleName(IOSchedResources.INSTANCE.css().sessionTimeRow());
		HTML time = new HTML(session.getSessionTime());
		time.setStyleName(IOSchedResources.INSTANCE.css().sessionTimeText());
		sessionTime.add(time);
		// show alternative show menu button
		menuButton = OtherTimesMenuButton.create(session);
		if (menuButton != null) {
			menuButton.asWidget().getElement().getStyle().setMarginTop(-4, Unit.PX);
			sessionTime.add(menuButton);
			sessionTime.setCellWidth(menuButton, OtherTimesMenuButton.DEFAULT_WIDTH + 3 +"px");
		}
		// show a status icon if the session appears at another time in the users schedule
		OtherTimeMarkerImage otherTimeStatusIcon = OtherTimeMarkerImage.create(session);
		if (otherTimeStatusIcon != null) {
			otherTimeStatusIcon.asWidget().getElement().getStyle().setMarginTop(-8, Unit.PX);
			sessionTime.add(otherTimeStatusIcon);
			sessionTime.setCellWidth(otherTimeStatusIcon, OtherTimeMarkerImage.DEFAULT_WIDTH + "px");
		}
		
		this.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		this.add(checkBox);
		
		VerticalPanel right = new VerticalPanel();
		right.add(titleButton);
		right.add(sessionTime);
		this.add(right);
		this.setWidth("100%");
		this.setCellWidth(checkBox, CHECKBOX_WIDTH_PX);
		right.setCellHeight(titleButton, TOP_ROW_HEIGHT_PX);
		right.setCellHeight(sessionTime, TOP_ROW_HEIGHT_PX);
		
		this.addAttachHandler(new Handler() {			
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached()) {
					register();
				} else {
					unregister();
				}
			}
		});
	}
	
	private void unregister() {
		for (HandlerRegistration r : reg) {
			r.removeHandler();
		}
		reg.clear();
	}
	
	private void register() {
		unregister();
		
		// show registration state
		reg.add(Dispatcher.addHandler(UserSessionsLoadedEvent.TYPE, new SimpleEventHandler<UserSessionsLoadedEvent>(){
			@Override
			public void onEvent(UserSessionsLoadedEvent event) {
				updateSessionSelectionState();
			}
		}));
		reg.add(Dispatcher.addHandler(UserSessionRegisteredEvent.TYPE, new SimpleEventHandler<UserSessionRegisteredEvent>(){
			@Override
			public void onEvent(UserSessionRegisteredEvent event) {
				if (event.getSessionId().equals(SessionRow.this.session.getId())) {
					checkBox.setChecked(true);
				}
			}
		}));
		reg.add(Dispatcher.addHandler(UserSessionUnregisteredEvent.TYPE, new SimpleEventHandler<UserSessionUnregisteredEvent>(){
			@Override
			public void onEvent(UserSessionUnregisteredEvent event) {
				if (event.getSessionId().equals(SessionRow.this.session.getId())) {
					checkBox.setChecked(false);
				}
			}
		}));
		
		// change the background color when the end of the session is in the past.
		reg.add(Dispatcher.addHandler(RefreshExpiredSessionEvent.TYPE, new SimpleEventHandler<RefreshExpiredSessionEvent>(){
			@Override
			public void onEvent(RefreshExpiredSessionEvent event) {
				updateExpirationState(event.getCurrentTime());
			}
		}));
		
		// update session registration state when the checkbox state is changed
		reg.add(checkBox.addCheckHandler(new CheckHandler() {
			@Override
			public void onCheckChange(RippleCheckBox checkbox, boolean isChecked) {
				onCheckboxChecked(isChecked);
			}
		}));
		
		// Launch the session detail dialog when the session title is clicked 
		reg.add(titleButton.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSessionTitleClicked();
			}
		}));
		
		// initalize the UI with the initial session registration and expiration state. 
		updateSessionSelectionState();
		updateExpirationState(TimeService.getCurrentTime());
	}
	
	
	/**
	 * Animate a ripple on the session title button
	 */
	public void animateRipple() {
		titleButton.animateRipple(Config.ROW_RIPPLE_HIGHLIGHT_COLOR);
	}
	
	/**
	 * Load the user schedule registration state
	 */
	private void updateSessionSelectionState() {
		checkBox.setChecked(UserScheduleService.get().isInMySchedule(session.getId()));
	}
	
	/**
	 * Update the style when the session is expired.
	 * @param currentTime The current time used to determine whether the session has expired.
	 */
	private void updateExpirationState(long currentTime) {
		if (session.isExpired(currentTime)) {
			setStyleName(IOSchedResources.INSTANCE.css().sessionRowExpired());
		} else {
			setStyleName(IOSchedResources.INSTANCE.css().sessionRow());
		}		
	}
	
	/**
	 * @return The session rendered by this row
	 */
	public SessionJSO getSession() {
		return session;
	}
	
	/**
	 * Display the details of this session when the title is clicked
	 */
	private void onSessionTitleClicked() {
		if (detailPopup == null) {
			detailPopup = new SessionDetailDialog();
		}
		detailPopup.show(session);
	}
	
	private void onCheckboxChecked(boolean isChecked) {
		if (isChecked) {
			// register
			UserScheduleService.get().registerSession(session.getId(), new AsyncCallback<String>() {
				@Override
				public void onSuccess(String result) {
				}
				
				@Override
				public void onFailure(Throwable caught) {
					// revert checkbox state
					checkBox.setChecked(false);
				}
			});
		} else {
			// unregister
			UserScheduleService.get().unregisterSession(session.getId(), new AsyncCallback<String>() {
				@Override
				public void onSuccess(String result) {
				}
				
				@Override
				public void onFailure(Throwable caught) {
					// revert checkbox state
					checkBox.setChecked(true);
				}
			});
		}
	}

	@Override
	public void onResize(int parentWidth) {
		int rowWidth = parentWidth;
		this.setWidth(rowWidth + "px");
		this.titleButton.setWidth(rowWidth - (CHECKBOX_WIDTH + (IOSchedResources.INSTANCE.css().sessionTitleHorizontalMargin() * 2)));
	}

	/**
	 * @param filter The active filter
	 * @return True if the session should be visible given the specified filter
	 */
	public boolean shouldDisplay(TagFilter filter) {
		return session.shouldDisplay(filter);
	}
	
	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible != this.visible) {
			this.visible = visible;
			super.setVisible(visible);
		}
	}
}

