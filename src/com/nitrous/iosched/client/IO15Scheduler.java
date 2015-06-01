package com.nitrous.iosched.client;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.nitrous.iosched.client.component.scroll.AnimationCompletionCallback;
import com.nitrous.iosched.client.component.scroll.ScrollHeaderPanel;
import com.nitrous.iosched.client.component.scroll.ScrollingHeaderLayout;
import com.nitrous.iosched.client.event.Dispatcher;
import com.nitrous.iosched.client.event.RefreshExpiredSessionEvent;
import com.nitrous.iosched.client.event.RequestImportEvent;
import com.nitrous.iosched.client.event.RequestLoadUserScheduleEvent;
import com.nitrous.iosched.client.event.RequestScrollToSessionEvent;
import com.nitrous.iosched.client.event.RequestScrollToTimeBlockEvent;
import com.nitrous.iosched.client.event.SimpleEventHandler;
import com.nitrous.iosched.client.model.ConferenceDataManager;
import com.nitrous.iosched.client.model.ScheduleContainerJSO;
import com.nitrous.iosched.client.model.SessionJSO;
import com.nitrous.iosched.client.model.calendar.CalendarDay;
import com.nitrous.iosched.client.resources.IOSchedResources;
import com.nitrous.iosched.client.service.DataLoaderService;
import com.nitrous.iosched.client.service.TimeService;
import com.nitrous.iosched.client.service.ToastService;
import com.nitrous.iosched.client.service.UserScheduleService;
import com.nitrous.iosched.client.view.MyScheduleView;
import com.nitrous.iosched.client.view.ScheduleDayFilter;
import com.nitrous.iosched.client.view.ScheduleExplorerView;
import com.nitrous.iosched.client.view.SessionRow;
import com.nitrous.iosched.client.view.TimeBlockCard;
import com.nitrous.iosched.client.view.header.Header;
import com.nitrous.polygwt.client.component.RippleTabs;
import com.nitrous.polygwt.client.component.TabSelectionHandler;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class IO15Scheduler implements EntryPoint, TabSelectionHandler {
	private static final Logger LOGGER = Logger.getLogger(IO15Scheduler.class.getName());

	public static final int TAB_MY_SCHEDULE = 0;
	public static final int TAB_EXPLORE_DAY_1 = 1;
	public static final int TAB_EXPLORE_DAY_2 = 2;
	
	private static String[] TAB_LABELS = {
		"MY SCHEDULE", 
		"DAY 1",
		"DAY 2",
	};
	
	private ScheduleExplorerView scheduleExplorerDay1View;
	private ScheduleExplorerView scheduleExplorerDay2View;
	private MyScheduleView myScheduleView;
	private HTML status;
	private Header header;
	private ScrollingHeaderLayout scroll;
	private Timer expirationTimer;
	
	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {		
		LOGGER.info("Startup..");
		GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void onUncaughtException(Throwable e) {
				if (e instanceof UmbrellaException) {
					Set<Throwable> causes = ((UmbrellaException) e).getCauses();
					for (Throwable t : causes) {
						onUncaughtException(t);
					}
				} else {
					LOGGER.log(Level.SEVERE, "Uncaught error", e);				
				}
			}
		});
		
		IOSchedResources.INSTANCE.css().ensureInjected();
		
		header = new Header(TAB_LABELS);		
		int headerHeight = header.getHeight();
		header.asWidget().setHeight(headerHeight+"px");
		scroll = new ScrollHeaderPanel(header, headerHeight);
		RootLayoutPanel.get().add(scroll);
		
		// this causes non-smooth scrolling on iOS
		// fire an even when the main view is scrolled - to hide popup menus
//		scroll.getScrollPanel().addScrollHandler(new ScrollHandler() {
//			@Override
//			public void onScroll(ScrollEvent event) {
//				Dispatcher.fire(new WindowScrolledEvent());
//			}
//		});
				
		// show the schedule explorer tab and scroll to a requested time block
		Dispatcher.addHandler(RequestScrollToTimeBlockEvent.TYPE, new SimpleEventHandler<RequestScrollToTimeBlockEvent>(){
			@Override
			public void onEvent(RequestScrollToTimeBlockEvent event) {
				final TimeBlockCard card;
				if (event.getCalendarBlock().getDay().getDay() == ConferenceDataManager.get().getCalendar().getFirstDay()) {
					setSelectedTab(TAB_EXPLORE_DAY_1);
					card = scheduleExplorerDay1View.findTimeBlock(event.getCalendarBlock());
				} else {
					setSelectedTab(TAB_EXPLORE_DAY_2);
					card = scheduleExplorerDay2View.findTimeBlock(event.getCalendarBlock());
				}
				
				scroll.scrollToWidget(card, new AnimationCompletionCallback(){
					@Override
					public void onAnimationComplete() {
						card.animateRipple();
					}
				});
			}
		});
		
		Dispatcher.addHandler(RequestScrollToSessionEvent.TYPE,  new SimpleEventHandler<RequestScrollToSessionEvent>() {
			@Override
			public void onEvent(RequestScrollToSessionEvent event) {
				SessionJSO session = event.getSession();
				final SessionRow row;
				if (event.getSession().getDay() == ConferenceDataManager.get().getCalendar().getFirstDay()) {
					setSelectedTab(TAB_EXPLORE_DAY_1);
					row = scheduleExplorerDay1View.findSessionRow(session.getId());
				} else {
					setSelectedTab(TAB_EXPLORE_DAY_2);
					row = scheduleExplorerDay2View.findSessionRow(session.getId());
				}
				
				scroll.scrollToWidget(row, new AnimationCompletionCallback(){
					@Override
					public void onAnimationComplete() {
						row.animateRipple();
					}
				});
			}
		});
		
		status = new HTML();
		Style statusStyle = status.getElement().getStyle();
		statusStyle.setPosition(Position.ABSOLUTE);
		statusStyle.setBottom(5, Unit.PX);
		statusStyle.setLeft(5, Unit.PX);
		RootPanel.get().add(status);
		loadSchedule();
	}

	/**
	 * Load the schedule data
	 */
	private void loadSchedule() {
		showInfoStatus("Loading...");
		DataLoaderService.get().loadData(false,  new AsyncCallback<ScheduleContainerJSO>(){
			@Override
			public void onFailure(Throwable caught) {
				showErrorStatus(caught.getMessage());
			}

			@Override
			public void onSuccess(ScheduleContainerJSO result) {
				showUI(result);
			}
		});
	}
	
	private void showUI(ScheduleContainerJSO sessionList) {
		showInfoStatus("Initializing...");
		
		// init the views
		scheduleExplorerDay1View = new ScheduleExplorerView(new ScheduleDayFilter() {
			@Override
			public boolean accept(CalendarDay day) {
				return ConferenceDataManager.get().getCalendar().getFirstDay() == day.getDay();
			}
		});
		scheduleExplorerDay2View = new ScheduleExplorerView(new ScheduleDayFilter() {
			@Override
			public boolean accept(CalendarDay day) {
				return ConferenceDataManager.get().getCalendar().getLastDay() == day.getDay();
			}
		});
		myScheduleView = new MyScheduleView();

		scheduleExplorerDay1View.asWidget().setVisible(false);
		scroll.getScrollContainer().add(scheduleExplorerDay1View);
		scheduleExplorerDay2View.asWidget().setVisible(false);
		scroll.getScrollContainer().add(scheduleExplorerDay2View);
		myScheduleView.asWidget().setVisible(false);
		scroll.getScrollContainer().add(myScheduleView);
		
		// hide the loading status
		RootPanel.get().remove(status);
		
		// listen for tab change events 
		header.getTabs().addTabSelectionHandler(this);
		
		// register the user schedule service and load the user-schedule
		startServices();
		Dispatcher.fire(new RequestLoadUserScheduleEvent());
		
		// check bookmark for initial view
		loadBookmark();
		
		// refresh expired session state and start updating expired state periodically
		refreshExpiredSessions();
	}
	
	/**
	 * Check for expired sessions once every minute
	 */
	private void refreshExpiredSessions() {
		if (expirationTimer != null) {
			expirationTimer.cancel();
		}
		
		Dispatcher.fire(new RefreshExpiredSessionEvent(TimeService.getCurrentTime()));
		expirationTimer = new Timer(){
			@Override
			public void run() {
				refreshExpiredSessions();
			}
		};
		expirationTimer.schedule(60000);
	}
	
	private void loadBookmark() {
		String token = History.getToken();
		navToBookmark(token);
	}
	
	private void navToBookmark(String token) {
		int tabIdx = TAB_MY_SCHEDULE;
		boolean doImport = false;
		if (token != null) {
			String[] parts = token.split(";");
			for (String part : parts) {
				if ("import".equalsIgnoreCase(part)) {
					doImport = true;
				} else {
					if (part.length() > 2) {
						int splitIdx = part.indexOf('=');
						if (splitIdx > 0) {
							String[] keyValuePair = part.split("=");
							if (keyValuePair.length == 2) {
								String key = keyValuePair[0];
								String value = keyValuePair[1];
								if ("tab".equalsIgnoreCase(key)) {
									if ("myschedule".equals(value)) {
										tabIdx = TAB_MY_SCHEDULE;
									} else if ("day1".equals(value)) {
										tabIdx = TAB_EXPLORE_DAY_1;
									} else if ("day2".equals(value)) {
										tabIdx = TAB_EXPLORE_DAY_2;
									}
								}
							}
						}
					}
				}
			}
		}
		
		// select and show the initial tab content
		setSelectedTab(tabIdx);
		
		// bookmark requests display of import dialog
		if (doImport) {
			Dispatcher.fire(new RequestImportEvent());
		}		
	}
	
	private void setSelectedTab(int index) {
		header.getTabs().setSelectedIndex(index);
	}
	
	private void startServices() {
		UserScheduleService.register();
		ToastService.register();
	}
	
	private void showInfoStatus(String message) {
		status.setHTML("<span style='font-family: monospace;color:blue;'>"
				+ message + "</span>");
	}

	private void showErrorStatus(String err) {
		status.setHTML("<span style='color:red;'>" + err + "</span>");
	}

	private void showExplorerDay1Tab(boolean show) {
		scheduleExplorerDay1View.asWidget().setVisible(show);
	}
	
	private void showExplorerDay2Tab(boolean show) {
		scheduleExplorerDay2View.asWidget().setVisible(show);
	}
	
	private void showMyScheduleTab(boolean show) {
		myScheduleView.asWidget().setVisible(show);
	}	
	
	@Override
	public void onTabSelected(RippleTabs tabs, int oldSelection, int selectedIndex) {
		switch (oldSelection) {
		case TAB_EXPLORE_DAY_2:
			showExplorerDay2Tab(false);
			break;
			
		case TAB_EXPLORE_DAY_1:
			showExplorerDay1Tab(false);
			break;
			
		case TAB_MY_SCHEDULE:
			showMyScheduleTab(false);
			break;
		}
		
		
		switch (selectedIndex) {
		case TAB_EXPLORE_DAY_2:
			showExplorerDay2Tab(true);
			break;
			
		case TAB_EXPLORE_DAY_1:
			showExplorerDay1Tab(true);
			break;
			
		case TAB_MY_SCHEDULE:
			showMyScheduleTab(true);
			break;
		}
		
		scroll.setVerticalScrollPosition(0);
	}
}
