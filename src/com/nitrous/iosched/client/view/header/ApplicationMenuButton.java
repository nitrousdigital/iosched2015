package com.nitrous.iosched.client.view.header;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.PopupPanel.AnimationType;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.nitrous.iosched.client.component.PopupMenu;
import com.nitrous.iosched.client.component.polymer.PaperIconButton;
import com.nitrous.iosched.client.event.Dispatcher;
import com.nitrous.iosched.client.event.RequestExportEvent;
import com.nitrous.iosched.client.event.RequestImportEvent;
import com.nitrous.iosched.client.resources.IOSchedResources;
import com.nitrous.iosched.client.view.config.Layers;
import com.nitrous.iosched.client.view.importexport.ImportExportService;
import com.nitrous.polygwt.client.component.HtmlRippleButton;

public class ApplicationMenuButton extends PaperIconButton {
	private PopupMenu popupMenu;

	public ApplicationMenuButton() {
		super("more-vert");
		setPixelSize(40, 40);
		this.asWidget().setTitle("Click to see the menu.");
		this.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onButtonClicked();
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
		
		// setup the service to handle the import/export actions
		ImportExportService.register();
	}
	
	private void onButtonClicked() {
		if (popupMenu == null) {
			// popup menu not showing
			popupMenu = buildMenu();
		}
		
		if (popupMenu.isShowing()) {
			// popup menu showing, so hide
			hideMenu();
		} else {
			// popup menu not showing, so move/show
			popupMenu.showRelativeTo(getEl());
		}
	}
	
	private void hideMenu() {
		if (popupMenu != null) {
			popupMenu.hide();
		}
	}

	private void onExportClicked() {
		delayedHide(new RequestExportEvent());
	}
	
	private void onImportClicked() {
		delayedHide(new RequestImportEvent());
	}
	
	private void delayedHide(final GwtEvent<?> event) {
		new Timer(){
			@Override
			public void run() {
				hideMenu();
				Dispatcher.fire(event);
				
			}
		}.schedule(500);
	}
	
	private static final String POPUP_CONTENT_WIDTH = "115px";
	private static final String BUTTON_WIDTH = "95px";
	private static final String BUTTON_HEIGHT = "25px";
	private static final String BUTTON_HEIGHT_WITH_PADDING = "35px";
//	private static final String BUTTON_TEXT_COLOR = "rgb(102, 102, 102)";
	
	private PopupMenu buildMenu() {
		
		HtmlRippleButton exportButton = new HtmlRippleButton("Export Schedule", null);
		exportButton.getRippleCanvas().getElement().getStyle().setZIndex(Layers.POPUP_RIPPLE_Z_INDEX);
		exportButton.asWidget().getElement().getStyle().setZIndex(Layers.POPUP_RIPPLE_Z_INDEX + 1);
		exportButton.asWidget().setStyleName(IOSchedResources.INSTANCE.css().applicationMenuItem());
		exportButton.asWidget().setWidth(BUTTON_WIDTH);
//		exportButton.asWidget().getElement().getStyle().setColor(BUTTON_TEXT_COLOR);
		exportButton.asWidget().setHeight(BUTTON_HEIGHT);
		exportButton.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onExportClicked();
			}
		});
		
		HtmlRippleButton importButton = new HtmlRippleButton("Import Schedule", null);
		importButton.asWidget().setStyleName(IOSchedResources.INSTANCE.css().applicationMenuItem());
		importButton.getRippleCanvas().getElement().getStyle().setZIndex(Layers.POPUP_RIPPLE_Z_INDEX);
		importButton.asWidget().getElement().getStyle().setZIndex(Layers.POPUP_RIPPLE_Z_INDEX + 1);
		importButton.asWidget().setWidth(BUTTON_WIDTH);
//		importButton.asWidget().getElement().getStyle().setColor(BUTTON_TEXT_COLOR);
		importButton.asWidget().setHeight(BUTTON_HEIGHT);
		importButton.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onImportClicked();
			}
		});
		
		PopupMenu popupMenu = new PopupMenu();
		
		VerticalPanel popupContent = new VerticalPanel();
		popupContent.setWidth(POPUP_CONTENT_WIDTH);
		popupContent.getElement().getStyle().setProperty("borderCollapse",  "collapse");
		popupContent.add(exportButton);
		popupContent.setCellHeight(exportButton, BUTTON_HEIGHT_WITH_PADDING);
		popupContent.add(importButton);
		popupContent.setCellHeight(importButton, BUTTON_HEIGHT_WITH_PADDING);
		popupMenu.getElement().getStyle().setZIndex(Layers.POPUP_Z_INDEX);
		popupMenu.setAnimationEnabled(true);
		popupMenu.setAutoHideEnabled(true);
		popupMenu.setAnimationType(AnimationType.ROLL_DOWN);
		popupMenu.add(popupContent);
		
		return popupMenu;
	}
	
}
