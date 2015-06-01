package com.nitrous.iosched.client.view;

import java.util.ArrayList;
import java.util.Set;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nitrous.iosched.client.event.ConferenceDataUpdatedEvent;
import com.nitrous.iosched.client.event.Dispatcher;
import com.nitrous.iosched.client.event.SimpleEventHandler;
import com.nitrous.iosched.client.model.ConferenceDataManager;
import com.nitrous.iosched.client.model.calendar.Calendar;
import com.nitrous.iosched.client.model.calendar.CalendarBlock;
import com.nitrous.iosched.client.model.calendar.CalendarDay;
import com.nitrous.iosched.client.resources.IOSchedResources;

/**
 * Displays the full list of available sessions for a single day of the conference
 * 
 * @author nitrousdigital
 */
public class ScheduleExplorerView implements IsWidget, RequiresResize {
	private static int NEXT_ID;
	private int myId;
	private VerticalPanel layout;
	
	private ArrayList<TimeBlockCard> timeBlockCards;
	private ScheduleDayFilter dayFilter;
	
	public ScheduleExplorerView() {
		this(null);
	}
	
	public ScheduleExplorerView(ScheduleDayFilter dayFilter) {
		this.dayFilter = dayFilter;
		layout = new VerticalPanel();
		this.myId = ++NEXT_ID;
		layout.setStyleName(IOSchedResources.INSTANCE.css().scheduleExplorerGrid());
		layout.getElement().setId("schedule-explorer-"+myId);
		
		paintSchedule();
		layout.addAttachHandler(new Handler() {
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached()) {
					onResize();
				}
			}
		});
		
		Dispatcher.addHandler(ConferenceDataUpdatedEvent.TYPE,  new SimpleEventHandler<ConferenceDataUpdatedEvent>(){
			@Override
			public void onEvent(ConferenceDataUpdatedEvent event) {
				repaint();				
			}
		});
	}
	
	private void repaint() {
		layout.clear();
		paintSchedule();
	}
	
	/**
	 * Find the TimeBlockCard that displays the specified CalendarBlock
	 * @param block The TimeBlock to find
	 * @return The TimeBlockCard if found, otherwise returns null.
	 */
	public TimeBlockCard findTimeBlock(CalendarBlock block) {
		if (timeBlockCards != null) {
			for (TimeBlockCard card : timeBlockCards) {
				if (card.getCalendarBlock().equals(block)) {
					return card;
				}
			}
		}
		return null;
	}
	
	/**
	 * Find the SessionRow that displays the identified session
	 * @param sessionId The ID of the session to find
	 * @return The SessionRow if found otherwise returns null.
	 */
	public SessionRow findSessionRow(String sessionId) {
		if (timeBlockCards != null) {
			for (TimeBlockCard card : timeBlockCards) {
				SessionRow row = card.findSessionRow(sessionId);
				if (row != null) {
					return row;
				}
			}
		}
		return null;
	}
	
	private void paintSchedule() {
		this.timeBlockCards = new ArrayList<TimeBlockCard>();
		Calendar cal = ConferenceDataManager.get().getCalendar();
		if (cal == null) {
			return;
		}

		Set<CalendarDay> days = cal.getDays();
		for (CalendarDay day : days) {
			if (dayFilter != null && !dayFilter.accept(day)) {
				continue;
			}
			
			Set<CalendarBlock> dayTimeBlocks = day.getTimeBlocks();
			for(CalendarBlock dayTimeBlock : dayTimeBlocks) {
				TimeBlockCard timeBlockCard = constructTimeBlockCard(dayTimeBlock);
				if (timeBlockCard != null) {
					timeBlockCards.add(timeBlockCard);
					layout.add(timeBlockCard);
				}
			}
		}
	}
	
	protected TimeBlockCard constructTimeBlockCard(CalendarBlock block) {
		return new TimeBlockCard(block);
	}
	
	@Override
	public Widget asWidget() {
		return layout;
	}

	@Override
	public void onResize() {
		if (timeBlockCards != null) {
			int width = Window.getClientWidth();
			for (TimeBlockCard card : timeBlockCards) {
				card.onResize(width);
			}
		}
	}
}
