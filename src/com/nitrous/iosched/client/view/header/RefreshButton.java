package com.nitrous.iosched.client.view.header;

import com.google.gwt.event.dom.client.ClickEvent;
import com.nitrous.iosched.client.component.polymer.PaperIconButton;
import com.nitrous.iosched.client.event.Dispatcher;
import com.nitrous.iosched.client.event.RequestReloadDataEvent;

public class RefreshButton extends PaperIconButton {
	public RefreshButton() {
		super("refresh");
		setPixelSize(40, 40);
		this.asWidget().setTitle("Click to reload schedule data.");
		this.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onButtonClicked();
			}
		});		
	}
	
	private void onButtonClicked() {
		Dispatcher.fire(new RequestReloadDataEvent());
	}
}
