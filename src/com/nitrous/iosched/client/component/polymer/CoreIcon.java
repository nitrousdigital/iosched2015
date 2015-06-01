package com.nitrous.iosched.client.component.polymer;

import com.google.gwt.user.client.ui.HTML;

/**
 * A wrapper for a polymer core-icon
 * @author nitrousdigital
 *
 */
public class CoreIcon extends HTML {
	public CoreIcon() {
	}
	
	public CoreIcon(String icon) {
		this(icon, null);
	}
	
	public CoreIcon(String icon, String style) {
		setIcon(icon, style);
		setHTML(getHTML(icon, style));
	}
	
	public static String getHTML(String icon) {
		return getHTML(icon, null);
	}
	
	public void setIcon(String icon, String style) {
		setHTML(getHTML(icon, style));
	}
	
	public static String getHTML(String icon, String style) {
		StringBuilder buf = new StringBuilder("<core-icon icon=\"").append(icon).append("\"");
		if (style != null) {
			boolean wrap = false;
			buf.append(" ");
			if (!style.trim().toLowerCase().startsWith("style")) {
				wrap = true;
				buf.append("style='");
			}
			buf.append(style);
			if (wrap) {
				buf.append("'");
			}
		}
		buf.append("></core-icon>");
		return buf.toString();
	}
}
