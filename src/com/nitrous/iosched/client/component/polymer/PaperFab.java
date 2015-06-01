package com.nitrous.iosched.client.component.polymer;


/**
 * A wrapper for polymer paper-fab
 * @author nitrousdigital
 *
 */
public class PaperFab extends AbstractClickableWrapper {
	
	public PaperFab(String icon, boolean mini) {
		this(icon, mini, null);
	}
	
	public PaperFab(String icon, boolean mini, String style) {
		setHTML(buildElement(icon, mini, style, myId));
	}

	@Override
	protected String getIdPrefix() {
		return "paper-fab";
	}
	
	private static String buildElement(String icon, boolean mini, String style, String id) {
		StringBuilder buf = new StringBuilder("<paper-fab");
		buf.append(" id=\"").append(id).append("\"");
		buf.append(" icon=\"").append(icon).append("\"");
		if (mini) {
			buf.append(" mini");
		}
		style = formatStyleAttribute(style);
		if (style != null) {
			buf.append(style);
		}
		buf.append("></paper-fab>");
		return buf.toString();
	}
}
