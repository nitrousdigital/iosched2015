package com.nitrous.iosched.client.component.polymer;


public class PaperIconButton extends AbstractClickableWrapper {
	
	public PaperIconButton(String icon) {
		setHTML("<paper-icon-button id=\""+getMyId()+"\" icon=\"" + icon + "\" role=\"button\" tabindex=\"0\" aria-label=\"more-vert\"></paper-icon-button>");
	}
	
	@Override
	protected String getIdPrefix() {
		return "paper-icon-button";
	}
}
