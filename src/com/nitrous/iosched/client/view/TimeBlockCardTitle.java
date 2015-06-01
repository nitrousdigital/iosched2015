package com.nitrous.iosched.client.view;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.nitrous.iosched.client.model.calendar.CalendarBlock;
import com.nitrous.iosched.client.resources.IOSchedResources;
import com.nitrous.iosched.client.view.config.Config;
import com.nitrous.iosched.client.view.config.Layers;
import com.nitrous.polygwt.client.component.HtmlRippleButton;

class TimeBlockCardTitle extends HorizontalPanel {
	public static final String HEIGHT_PX = "48px";
	private HtmlRippleButton cardTitleButton;
	TimeBlockCardTitle(CalendarBlock block) {
		String title = block.getDay().getMonthDay() + ", " + block.getName();
		
		setStyleName(IOSchedResources.INSTANCE.css().cardTitleRow());
		cardTitleButton = new HtmlRippleButton(title, null);
		cardTitleButton.getRippleCanvas().getElement().getStyle().setZIndex(Layers.SESSION_RIPPLE_Z_INDEX);
		cardTitleButton.setClickEnabled(false);
		cardTitleButton.getButtonHTML().setStyleName(IOSchedResources.INSTANCE.css().cardTitleButton());
		add(cardTitleButton);
		setCellHeight(cardTitleButton, HEIGHT_PX);
		setHeight(HEIGHT_PX);
		setWidth("100%");
		setStyleName(IOSchedResources.INSTANCE.css().cardTitleRow());
	}
	
	public void setWidth(int width) {
		super.setWidth(width + "px");
		cardTitleButton.asWidget().setWidth(width - IOSchedResources.INSTANCE.css().cardTitleButtonHorizontalPadding() + "px");
	}
	
	public void animateRipple() {
		cardTitleButton.animateRipple(Config.ROW_RIPPLE_HIGHLIGHT_COLOR);
	}
}