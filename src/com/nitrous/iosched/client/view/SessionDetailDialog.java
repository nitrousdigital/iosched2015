package com.nitrous.iosched.client.view;

import java.util.ArrayList;
import java.util.Map;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.nitrous.iosched.client.component.polymer.CoreImage;
import com.nitrous.iosched.client.component.polymer.CoreImage.Sizing;
import com.nitrous.iosched.client.component.polymer.PaperFab;
import com.nitrous.iosched.client.event.Dispatcher;
import com.nitrous.iosched.client.event.RequestScrollToSessionEvent;
import com.nitrous.iosched.client.event.SimpleEventHandler;
import com.nitrous.iosched.client.model.SessionFilterJSO;
import com.nitrous.iosched.client.model.SessionJSO;
import com.nitrous.iosched.client.model.SpeakerJSO;
import com.nitrous.iosched.client.resources.IOSchedResources;
import com.nitrous.iosched.client.service.UserScheduleService;
import com.nitrous.polygwt.client.component.ImageRippleButton;

public class SessionDetailDialog extends PopupPanel {
	// placeholder image
	// https://events.google.com/io2015/images/schedule/session_placeholder.jpg
	
	// io15 
	// https://events.google.com/io2015/images/io15-color.png
	
	private VerticalPanel layout;
	
	private HTML image;
	private HTML sessionTitle;
	private HorizontalPanel sessionTimeRow;
	private HTML sessionTime;
	private HTML sessionTags;
	private HTML sessionDescription;
	private HTML speakersHeader;
	private VerticalPanel speakers;
	
	private ScrollPanel scroll;
	private VerticalPanel textContent;
	
	private ArrayList<HandlerRegistration> reg = new ArrayList<HandlerRegistration>();
	private HandlerRegistration actionReg;
		
	private PaperFab actionButton;
	private SessionJSO session;
	
	public SessionDetailDialog() {
		this.setStyleName(IOSchedResources.INSTANCE.css().popupDialog());
		setAnimationEnabled(true);
		setAnimationType(AnimationType.CENTER);
		setAutoHideEnabled(false);
		setGlassEnabled(true);
		
		this.layout = new VerticalPanel();
		this.layout.setWidth("100%");
		this.scroll = new ScrollPanel(layout);
		this.scroll.setSize("100%", "100%");
		this.add(scroll);

		ImageRippleButton backButton = new ImageRippleButton(IOSchedResources.INSTANCE.arrowBack());
		this.layout.add(backButton);
		Style backStyle = backButton.asWidget().getElement().getStyle();
		backStyle.setPosition(Position.ABSOLUTE);
		backStyle.setLeft(16, Unit.PX);
		backStyle.setTop(16, Unit.PX);
		backStyle.setZIndex(10);
		backButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		image = new HTML();
		image.getElement().setId("banner-image");
		image.setSize("100%", "100%");
		this.layout.add(image);
		
		this.textContent = new VerticalPanel();
		this.textContent.setStyleName(IOSchedResources.INSTANCE.css().sessionDetailTextContentPanel());
		this.layout.add(textContent);
		
		this.sessionTitle = new HTML();
		this.sessionTitle.setStyleName(IOSchedResources.INSTANCE.css().sessionDetailTitle());
		this.textContent.add(this.sessionTitle);
		
		this.sessionTimeRow = new HorizontalPanel();
		this.sessionTimeRow.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		this.sessionTime = new HTML();
		this.sessionTime.setStyleName(IOSchedResources.INSTANCE.css().sessionDetailTime());
		this.sessionTimeRow.add(this.sessionTime);
		this.textContent.add(this.sessionTimeRow);
		

		this.sessionTags = new HTML();
		this.sessionTags.setStyleName(IOSchedResources.INSTANCE.css().sessionDetailTags());
		this.textContent.add(this.sessionTags);
		
		this.sessionDescription = new HTML();
		this.sessionDescription.setStyleName(IOSchedResources.INSTANCE.css().sessionDetailDescription());
		this.textContent.add(this.sessionDescription);
		
		// speakers
		this.speakersHeader = new HTML();
		this.speakersHeader.setStyleName(IOSchedResources.INSTANCE.css().sessionDetailSpeakersHeader());
		this.speakersHeader.setHTML("Speakers");
		this.layout.add(this.speakersHeader);
		
		this.speakers = new VerticalPanel();
		this.speakers.setWidth("100%");
		this.speakers.setStyleName(IOSchedResources.INSTANCE.css().sessionDetailSpeakersList());
		this.layout.add(speakers);
	}
	
	private void setImage(String url) {
		image.setHTML(CoreImage.toHTML("100%", Window.getClientHeight() * 0.5 + "px", url, IOSchedResources.INSTANCE.io15billBoard(), CoreImage.Sizing.COVER, true,  true));
	}
	
	// 
	public void show(SessionJSO session) {
		this.session = session;
		
		// header image
		setImage(session.getPhotoUrl());
		
		// title
		sessionTitle.setHTML(session.getTitle());
		
		// time and place
		StringBuilder timeAndPlace = new StringBuilder();
		timeAndPlace.append(session.getMonth()).append(" ").append(session.getDay())
			.append(" / ").append(session.getSessionTime())
			.append(" / ").append(session.getRoom());
		sessionTime.setHTML(timeAndPlace.toString());
		// other showtimes
		sessionTimeRow.clear();
		sessionTimeRow.add(sessionTime);
		OtherTimesMenuButton otherTimes = OtherTimesMenuButton.create(session);		
		if (otherTimes != null) {
			otherTimes.asWidget().getElement().getStyle().setMarginTop(4, Unit.PX);
			sessionTimeRow.add(otherTimes);
			sessionTimeRow.setCellWidth(otherTimes, OtherTimesMenuButton.DEFAULT_WIDTH + "px");
		}
		// show a status icon if the session appears at another time in the users schedule
		OtherTimeMarkerImage otherTimeStatusIcon = OtherTimeMarkerImage.create(session);
		if (otherTimeStatusIcon != null) {
			sessionTimeRow.add(otherTimeStatusIcon);
			sessionTimeRow.setCellWidth(otherTimeStatusIcon, OtherTimeMarkerImage.DEFAULT_WIDTH + "px");
		}
		
		// add/remove action button
		updateActionButton();
		
		// filters
		SessionFilterJSO filters = session.getFilters();
		Map<String, Boolean> filterMap = filters.getFilters();
		StringBuilder filterHtml = new StringBuilder();
		boolean first = true;
		for (Map.Entry<String, Boolean> entry : filterMap.entrySet()) {
			if (Boolean.TRUE.equals(entry.getValue())) {
				if (!first) {
					filterHtml.append(", ");
				}
				first = false;
				filterHtml.append(entry.getKey());
			}
		}
		sessionTags.setHTML(filterHtml.toString());
				
		// description
		sessionDescription.setHTML(session.getDescription());
		
		// speakers
		ArrayList<SpeakerJSO> speakers = session.getSpeakers();
		if (speakers.size() > 0) {
			this.speakers.setVisible(true);
			this.speakersHeader.setVisible(true);
			this.speakers.clear();
			for (SpeakerJSO speaker : speakers) {
				HorizontalPanel speakerRow = new HorizontalPanel();
				speakerRow.setStyleName(IOSchedResources.INSTANCE.css().sessionDetailSpeakersCard());
				speakerRow.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
				this.speakers.add(speakerRow);
				
				String faceUrl = speaker.getThumbnailUrl();
				CoreImage faceImg = new CoreImage(
						"width:56px;height:56px;border-radius:50%;", 
						faceUrl, 
						IOSchedResources.INSTANCE.profilePlaceholder(),
						Sizing.CONTAIN,
						true, 
						true, 
						faceUrl != null);
				faceImg.setStyleName(IOSchedResources.INSTANCE.css().sessionDetailSpeakersFace());
				speakerRow.add(faceImg);
				
				// text rows
				VerticalPanel speakerTextLayout = new VerticalPanel();
				speakerRow.add(speakerTextLayout);
				
				// speaker name
				HTML name = new HTML(speaker.getName());
				name.setStyleName(IOSchedResources.INSTANCE.css().sessionDetailSpeakersName());
				speakerTextLayout.add(name);
				
				// company
				String company = speaker.getCompany();				
				if (company != null && company.length() > 0) {
					HTML companyHtml = new HTML(company);
					companyHtml.setStyleName(IOSchedResources.INSTANCE.css().sessionDetailSpeakersCompany());
					speakerTextLayout.add(companyHtml);
				}
				
				// bio
				String bio = speaker.getBio();				
				if (bio != null && bio.length() > 0) {
					HTML bioHtml = new HTML(bio);
					bioHtml.setStyleName(IOSchedResources.INSTANCE.css().sessionDetailSpeakersBio());
					speakerTextLayout.add(bioHtml);
				}				
			}
		} else {
			this.speakers.setVisible(false);
			this.speakersHeader.setVisible(false);
		}
		
		setSize("100%", "100%");
		center();
	}
	
	private void unregister() {
		if (reg != null) {
			for (HandlerRegistration r : reg) {
				r.removeHandler();
			}
			reg.clear();
			actionReg = null;
		}
	}
	
	private void register() {
		unregister();
		
		reg.add(this.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				unregister();
			}
		}));
		
		reg.add(RootPanel.get().addDomHandler(new KeyDownHandler(){

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					hide();
				}
			}}, KeyDownEvent.getType()));
		
		reg.add(Dispatcher.addHandler(RequestScrollToSessionEvent.TYPE,  new SimpleEventHandler<RequestScrollToSessionEvent>() {
			@Override
			public void onEvent(RequestScrollToSessionEvent event) {
				hide();
			}
		}));
		
		registerActionButton();
	}
	
	private void registerActionButton() {
		unregisterActionButton();
		reg.add(actionReg = actionButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				toggleSessionRegistration();
			}
		}));
	}
	
	private void unregisterActionButton() {
		if (actionReg != null) {
			actionReg.removeHandler();
			reg.remove(actionReg);
			actionReg = null;
		}
	}
	
	private void toggleSessionRegistration() {
		if (session != null) {
			String sessionId = session.getId();
			boolean isRegistered = UserScheduleService.get().isInMySchedule(sessionId);
			if (isRegistered) {
				UserScheduleService.get().unregisterSession(sessionId, new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {
						updateActionButton();
					}
					
					@Override
					public void onFailure(Throwable caught) {
					}
				});
			} else {
				UserScheduleService.get().registerSession(sessionId, new AsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {
						updateActionButton();
					}
					
					@Override
					public void onFailure(Throwable caught) {
					}
				});
			}
		}
	}
	
	private void updateActionButton() {
		if (actionButton != null) {
			actionButton.removeFromParent();
			actionButton = null;
		}
		
		boolean isInMySchedule = UserScheduleService.get().isInMySchedule(session.getId());
		if (isInMySchedule) {
			actionButton = new PaperFab("check", false, "background: #26C6DA;");
		} else {
			actionButton = new PaperFab("add", false, "color: black; background: white;");
		}
		Style actionStyle = actionButton.getElement().getStyle();
		actionStyle.setPosition(Position.ABSOLUTE);
		actionStyle.setRight(24, Unit.PX);
		actionStyle.setTop((Window.getClientHeight() * 0.5) - (56 / 2), Unit.PX);
		layout.add(actionButton);
		registerActionButton();
	}
	
	@Override
	public void show() {
		super.show();
		register();
	}
	
	
}
