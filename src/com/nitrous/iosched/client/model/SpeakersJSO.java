package com.nitrous.iosched.client.model;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Describes a list of speakers at the conference
 * 
 * @author nitrousdigital
 *
 */
public final class SpeakersJSO extends JavaScriptObject {
	protected SpeakersJSO() {
	}

	public native SpeakerJSO getSpeaker(String id) /*-{
		return this[id];
	}-*/;
}