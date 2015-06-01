package com.nitrous.iosched.client.view;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nitrous.iosched.client.component.DeferredWindowResizeHandler;
import com.nitrous.iosched.client.component.OrderedVerticalPanel;
import com.nitrous.iosched.client.event.Dispatcher;
import com.nitrous.iosched.client.event.RefreshExpiredSessionEvent;
import com.nitrous.iosched.client.event.SimpleEventHandler;
import com.nitrous.iosched.client.event.TagFilterChangedEvent;
import com.nitrous.iosched.client.model.SessionJSO;
import com.nitrous.iosched.client.model.TagFilter;
import com.nitrous.iosched.client.model.calendar.CalendarBlock;
import com.nitrous.iosched.client.model.calendar.CalendarTimeSlot;
import com.nitrous.iosched.client.resources.IOSchedResources;
import com.nitrous.iosched.client.service.TagFilterService;
import com.nitrous.iosched.client.service.TimeService;

public class TimeBlockCard implements IsWidget, RequiresContainerWidth, SimpleEventHandler<TagFilterChangedEvent> {
	private static final Logger LOGGER = Logger.getLogger(TimeBlockCard.class.getName());
	
	protected VerticalPanel layout;
	
	protected Map<String, SessionRow> sessionRowsById;
	
	private TimeBlockCardTitle title;
	protected OrderedVerticalPanel<SessionRow> sessionRowsVerticalLayout;
	private HTML noSessionsMessage;
	
	protected static int LEFT_RIGHT_MARGIN = (IOSchedResources.INSTANCE.css().cardLeftRightMargin() * 2);
	protected CalendarBlock block;
	
	private boolean visible;
	
	/* The sorted list of IDs of currently visible rows */
	private Vector<String> visibleRows = new Vector<String>();
	
	private static final SessionRowComparator SESSION_COMPARATOR = new SessionRowComparator();
	
	public TimeBlockCard(CalendarBlock block) {
		this.block = block;
		this.visible = true;
		
		this.layout = new VerticalPanel();
		this.layout.add(title = new TimeBlockCardTitle(block));
		this.layout.setCellHeight(title, TimeBlockCardTitle.HEIGHT_PX);
		this.layout.setCellWidth(title, "100%");
		this.layout.setWidth("100%");
		
		this.sessionRowsVerticalLayout = new OrderedVerticalPanel<SessionRow>(SESSION_COMPARATOR);
		this.layout.add(sessionRowsVerticalLayout);
		
		this.sessionRowsById = new HashMap<String, SessionRow>();
		LinkedHashSet<CalendarTimeSlot> slots = block.getTimeSlots();
		for (CalendarTimeSlot slot : slots) {
			Collection<SessionJSO> sessions = slot.getSessions();
			if (sessions != null && sessions.size() > 0) {
				for (SessionJSO session : sessions) {
					SessionRow row = new SessionRow(session);
					this.sessionRowsById.put(session.getId(), row);
					maybeAddToUI(row);
				}
			}
		}

		new DeferredWindowResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				TimeBlockCard.this.onResize();
			}
		});
		Dispatcher.addHandler(RefreshExpiredSessionEvent.TYPE, new SimpleEventHandler<RefreshExpiredSessionEvent>(){
			@Override
			public void onEvent(RefreshExpiredSessionEvent event) {
				setExpired(TimeBlockCard.this.block.isExpired(event.getCurrentTime()));
			}
		});

		onResize();
		
		initFilterListener();
		applyFilters();
		
		setExpired(block.isExpired(TimeService.getCurrentTime()));
	}
	
	/** 
	 * Add the specified row to the UI
	 * @param row The row to add to the UI
	 */
	protected void maybeAddToUI(SessionRow row) {
		SessionJSO session = row.getSession();
		String id = session.getId();
		if (shouldBeVisible(row.getSession())) {
			visibleRows.add(id);
			sessionRowsVerticalLayout.add(row);
		}
	}
	
	protected boolean shouldBeVisible(SessionJSO session) {
		return true;
	}
	
	protected void showSession(String id, boolean show) {		
		if (show) {
			showSession(id);
		} else {
			hideSession(id);			
		}
	}
	
	private void hideSession(String id) {
		if (!visibleRows.contains(id)) {
			// already hidden
			return;
		}
		
		// remove from visible state list
		visibleRows.remove(id);
		// remove row widget from UI
		SessionRow row = sessionRowsById.get(id);
		sessionRowsVerticalLayout.remove(row);
	}
	
	private void showSession(String id) {
		if (visibleRows.contains(id)) {
			// already visible
			return;
		}
		
		SessionRow row = sessionRowsById.get(id);
		if (row == null) {
			LOGGER.fine("showSession("+id+") - row widget for session not found in block "+block.getName());
			return;
		}
		sessionRowsVerticalLayout.add(row);
		visibleRows.add(id);
	}	
	
	/**
	 * Change the style of this card when the entire time block is expired
	 * @param isExpired True if the time period represented by this card is in the past.
	 */
	protected void setExpired(boolean isExpired) {
		if (isExpired) {
			layout.setStyleName(IOSchedResources.INSTANCE.css().cardExpired());
		} else {
			layout.setStyleName(IOSchedResources.INSTANCE.css().card());
		}
	}
	
	/**
	 * Register a listener for tag filters
	 */
	protected void initFilterListener() {
		Dispatcher.addHandler(TagFilterChangedEvent.TYPE, this);
		String message = "No sessions match the selected filter criteria for this time slot.";
		noSessionsMessage = new HTML(message);
		noSessionsMessage.setTitle(message);
		noSessionsMessage.setStyleName(IOSchedResources.INSTANCE.css().noSessionsMatchFilterMessage());
		noSessionsMessage.setVisible(false);
		layout.add(noSessionsMessage);
		this.asWidget().addAttachHandler(new Handler() {
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached()) {
					onResize();
				}
			}
		});
	}
	
	/**
	 * Apply the current filter state
	 */
	protected void applyFilters() {
		int visibleCount = 0;
		TagFilter filter = TagFilterService.get().getFilter();
		if (filter.isEmpty()) {
			// show all
			for (SessionRow row : sessionRowsById.values()) {
				row.setVisible(true);
			}
			visibleCount = sessionRowsById.size();
		} else {
			// apply filter
			for (SessionRow row : sessionRowsById.values()) {
				boolean visible = row.shouldDisplay(filter);
				row.setVisible(visible);
				if (visible) {
					visibleCount++;
				}
			}
		}
		
		if (noSessionsMessage != null) {
			noSessionsMessage.setVisible(visibleCount == 0);
		}
	}
	
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		if (visible != this.visible) {
			this.visible = visible;
			this.asWidget().setVisible(visible);
		}
	}

	/**
	 * Play animation to focus the users attention on this card
	 */
	public void animateRipple() {
		title.animateRipple();
	}
	
	/**
	 * Find the SessionRow that displays the identified session
	 * @param sessionId The ID of the session to find
	 * @return The SessionRow if found otherwise returns null.
	 */
	public SessionRow findSessionRow(String sessionId) {
		return sessionRowsById.get(sessionId);
	}
	
	@Override
	public Widget asWidget() {
		return layout;
	}
	
	public void onResize() {
		onResize(Window.getClientWidth());
	}
	
	@Override
	public void onResize(int parentWidth) {
		int width = parentWidth - LEFT_RIGHT_MARGIN;
		layout.setWidth(width + "px");
		title.setWidth(width);
		
		for (SessionRow session : sessionRowsById.values()) {
			session.onResize(width);
		}
		if (noSessionsMessage != null) {
			noSessionsMessage.setWidth((width - 25) + "px");
		}
	}
	
	public CalendarBlock getCalendarBlock() {
		return block;
	}

	@Override
	public void onEvent(TagFilterChangedEvent event) {
		applyFilters();
	}

}
