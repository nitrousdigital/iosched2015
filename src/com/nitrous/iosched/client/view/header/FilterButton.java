package com.nitrous.iosched.client.view.header;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.AnimationType;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.nitrous.iosched.client.component.PopupMenu;
import com.nitrous.iosched.client.component.polymer.PaperIconButton;
import com.nitrous.iosched.client.component.polymer.PaperToast;
import com.nitrous.iosched.client.event.Dispatcher;
import com.nitrous.iosched.client.event.SimpleEventHandler;
import com.nitrous.iosched.client.event.TagFilterChangedEvent;
import com.nitrous.iosched.client.model.ConferenceDataManager;
import com.nitrous.iosched.client.model.TagCategory;
import com.nitrous.iosched.client.model.TagFilter;
import com.nitrous.iosched.client.model.TagJSO;
import com.nitrous.iosched.client.resources.IOSchedResources;
import com.nitrous.iosched.client.service.TagFilterService;
import com.nitrous.iosched.client.view.config.Layers;
import com.nitrous.polygwt.client.component.HtmlRippleButton;
import com.nitrous.polygwt.client.component.RippleButton;
import com.nitrous.polygwt.client.component.RippleCheckBox;
import com.nitrous.polygwt.client.component.RippleCheckBox.CheckHandler;

public class FilterButton extends PaperIconButton {
	private PopupMenu popupMenu;
	
	public FilterButton() {
		super("filter-list");
		setPixelSize(40, 40);
		this.asWidget().setTitle("Click to select filter options.");
		this.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onButtonClicked();
			}
		});		
		Dispatcher.addHandler(TagFilterChangedEvent.TYPE,  new SimpleEventHandler<TagFilterChangedEvent>(){
			@Override
			public void onEvent(TagFilterChangedEvent event) {
				refreshSelections();
			}
		});
		
		// hide the popup menu on escape
		RootPanel.get().addDomHandler(new KeyDownHandler(){
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					hideMenu();
				}
			}}, KeyDownEvent.getType());
		
	}
	
	private boolean ignoreCheckEvents = false;
	private void refreshSelections() {		
		if (filterCheckMarks.size() > 0) {
			ignoreCheckEvents = true;
			TagFilter filter = TagFilterService.get().getFilter();
			for (Map.Entry<TagJSO, RippleCheckBox> entry : filterCheckMarks.entrySet()) {
				entry.getValue().setChecked(filter.contains(entry.getKey()));
			}
			ignoreCheckEvents = false;
		}
	}
	
	private void onButtonClicked() {
		if (popupMenu == null) {
			// popup menu not showing
			buildMenu();
		}
		
		if (popupMenu == null) {
			// unable to build the menu at this time
			PaperToast.show("Filter options not loaded. Please wait or try reloading the page.");
		} else {
			if (popupMenu.isShowing()) {
				// popup menu showing, so hide
				hideMenu();
			} else {
				// popup menu not showing, so move/show
				popupMenu.showRelativeTo(getEl());
			}
		}
	}
	
	private void hideMenu() {
		if (popupMenu != null) {
			popupMenu.hide();
		}
	}
		
	private void buildMenu() {
		TagCategory[] categories = ConferenceDataManager.get().getFilterTags();
		if (categories != null && categories.length > 0) {
			// tag list available, build the filter menu content
			popupMenu = initTagMenu(categories);
		}
	}
	
	private Map<TagJSO, RippleCheckBox> filterCheckMarks = new HashMap<TagJSO, RippleCheckBox>();
	
	private HandlerRegistration popupCloseReg; 

	private PopupMenu initTagMenu(TagCategory[] categories) {
		final VerticalPanel popupContent = new VerticalPanel();
		popupContent.getElement().getStyle().setProperty("borderCollapse",  "collapse");
		
		final HtmlRippleButton closeButton = new HtmlRippleButton("Close");
		closeButton.asWidget().setWidth("200px");
		closeButton.getRippleCanvas().getElement().getStyle().setZIndex(Layers.POPUP_RIPPLE_Z_INDEX);
		closeButton.asWidget().setStyleName(IOSchedResources.INSTANCE.css().popupMenuCloseButton());
		closeButton.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				new Timer(){
					@Override
					public void run() {
						hideMenu();
					}
				}.schedule(500);
			}
		});
		final HTML header = new HTML("FILTERS");
		header.setStyleName(IOSchedResources.INSTANCE.css().filterPopupMenuHeader());
		
		VerticalPanel scrollingContent = new VerticalPanel();
		scrollingContent.getElement().getStyle().setProperty("borderCollapse",  "collapse");
		final ScrollPanel scroll = new ScrollPanel(scrollingContent);
		final PopupMenu popupMenu = new PopupMenu() {
			@Override
			public void show() {
				super.show();
				int closeButtonHeight = RippleButton.DEFAULT_HEIGHT_WITH_PADDING;//closeButton.asWidget().getOffsetHeight();
				int headerHeight = header.asWidget().getOffsetHeight();
				int contentHeight = headerHeight + closeButtonHeight + scroll.getOffsetHeight();
				int maxHeight = Math.min(Window.getClientHeight() - 30, contentHeight);
				int scrollHeight = maxHeight - (headerHeight + closeButtonHeight);
				scroll.setHeight(scrollHeight + "px");
			}
		};
		
		// force the popup to dispose on close so that ripple canvases are removed from the DOM
		popupCloseReg = popupMenu.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				popupMenu.clear();
				FilterButton.this.popupMenu = null;
				popupCloseReg.removeHandler();
				popupCloseReg = null;
			}
		});
		popupContent.add(header);
		popupContent.add(scroll);
		popupContent.add(closeButton);
		popupContent.setCellHeight(closeButton,  RippleButton.DEFAULT_HEIGHT_WITH_PADDING + "px");
		//popupContent.setCellWidth(closeButton,  "100%");
		closeButton.asWidget().setWidth("172px");		
		popupMenu.getElement().getStyle().setZIndex(Layers.POPUP_Z_INDEX);
		popupMenu.setAnimationEnabled(true);
		popupMenu.setAutoHideEnabled(true);
		popupMenu.setAnimationType(AnimationType.ROLL_DOWN);
		popupMenu.add(popupContent);
		
		for (int i = 0 ; i < categories.length; i++) {
			if (i > 0) {
				HTML separator = new HTML();
				separator.setStyleName(IOSchedResources.INSTANCE.css().popupMenuSeparator());
				scrollingContent.add(separator);
			}				
			TagCategory category = categories[i];
			TagJSO[] tags = category.getTags();
			for (TagJSO tag : tags) {
				final TagJSO curTag = tag;
				String displayName = tag.getDisplayName();
				HorizontalPanel row = new HorizontalPanel();
				row.setStyleName(IOSchedResources.INSTANCE.css().popupMenuItem());
				row.setWidth("100%");
				RippleCheckBox cb = new RippleCheckBox();
				cb.addCheckHandler(new CheckHandler() {
					@Override
					public void onCheckChange(RippleCheckBox checkbox, boolean isChecked) {
						if (ignoreCheckEvents) {
							return;
						}
						TagFilterService.get().setFilterEnabled(curTag, isChecked);
					}
				});
				filterCheckMarks.put(curTag, cb);
				row.add(cb);
				row.setCellWidth(cb, RippleCheckBox.SIZE + "px");
				
				final HtmlRippleButton tagButton = new HtmlRippleButton(displayName);
				tagButton.getRippleCanvas().getElement().getStyle().setZIndex(Layers.POPUP_RIPPLE_Z_INDEX);
				tagButton.asWidget().getElement().getStyle().setZIndex(Layers.POPUP_RIPPLE_Z_INDEX + 1);
				tagButton.asWidget().getElement().getStyle().setColor("rgb(102, 102, 102)");
				tagButton.asWidget().setHeight("25px");
				tagButton.setWidth(140);
				row.add(tagButton);
				row.setCellHeight(tagButton, RippleButton.DEFAULT_HEIGHT + "px");
				row.setCellWidth(tagButton, "150px");
				scrollingContent.add(row);
				tagButton.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler(){
					@Override
					public void onClick(ClickEvent event) {
						TagFilterService.get().toggle(curTag);
					}
				});
			}				
		}
		
		refreshSelections();
		return popupMenu;
	}
	
}
