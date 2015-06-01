package com.nitrous.iosched.client.view.header;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nitrous.iosched.client.component.polymer.PaperSpinner;
import com.nitrous.iosched.client.event.DataLoadingEvent;
import com.nitrous.iosched.client.event.Dispatcher;
import com.nitrous.iosched.client.event.SimpleEventHandler;
import com.nitrous.iosched.client.resources.IOSchedResources;
import com.nitrous.iosched.client.view.config.Layers;
import com.nitrous.polygwt.client.component.HtmlRippleButton;
import com.nitrous.polygwt.client.component.RippleTabs;

public class Header implements IsWidget {
	
	private VerticalPanel header;
	
	private int height;
	private int toolbarHeight = 42;
	private RippleTabs tabs;
	private PaperSpinner spinner;
	
	public Header(String[] tabLabels) {
		header = new VerticalPanel();
		header.setWidth("100%");
		header.setStyleName(IOSchedResources.INSTANCE.css().header());
		
		HorizontalPanel topToolbar = new HorizontalPanel();
		topToolbar.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		topToolbar.setSize("100%", toolbarHeight+"px");
		topToolbar.setStyleName(IOSchedResources.INSTANCE.css().topToolbar());
		topToolbar.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		
		Image ioIcon = new Image(IOSchedResources.INSTANCE.io15headerIcon());
		ioIcon.setStyleName(IOSchedResources.INSTANCE.css().topToolbarIOIcon());
		topToolbar.add(ioIcon);
		topToolbar.setCellWidth(ioIcon, ioIcon.getWidth() + (IOSchedResources.INSTANCE.css().topToolbarIOIconLeftRightPadding() * 2)+"px");
		
		topToolbar.add(spinner = new PaperSpinner());
		
		HorizontalPanel rightButtons = new HorizontalPanel();
		rightButtons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		rightButtons.add(new RefreshButton());
		rightButtons.add(new FilterButton());
		rightButtons.add(new ApplicationMenuButton());
		topToolbar.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		topToolbar.add(rightButtons);
		
		header.add(topToolbar);
		
		tabs = new RippleTabs(Window.getClientWidth(), tabLabels);
		tabs.asWidget().setStyleName(IOSchedResources.INSTANCE.css().headerTabs());
		HtmlRippleButton[] buttons = tabs.getButtons();
		for (HtmlRippleButton button : buttons) {
			button.asWidget().getElement().getStyle().setPadding(0, Unit.PX);
			button.getRippleCanvas().getElement().getStyle().setZIndex(Layers.HEADER_Z_INDEX + 1);
		}
		
		header.add(tabs);
		
		this.height = toolbarHeight + tabs.getHeight() + IOSchedResources.INSTANCE.css().headerBottomBorderWidth();
		
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				tabs.setWidth(Window.getClientWidth());
			}
		});
		
		Dispatcher.addHandler(DataLoadingEvent.TYPE, new SimpleEventHandler<DataLoadingEvent>(){
			@Override
			public void onEvent(DataLoadingEvent event) {
				showLoadingIndicator(event.isLoading());
			}
		});
		
		showLoadingIndicator(false);
	}
	
	/**
	 * Cancel any ripple animation that might be playing on the tabs
	 */
	public void cancelRippleAnimation() {
		for(int i = 0, len = tabs.getTabCount(); i < len; i++) {
			tabs.getTabButton(i).cancelAnimation();
		}
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public RippleTabs getTabs() {
		return tabs;
	}
	
	@Override
	public Widget asWidget() {
		return header;
	}
	
	private void showLoadingIndicator(boolean show) {
		spinner.show(show);
	}
}
