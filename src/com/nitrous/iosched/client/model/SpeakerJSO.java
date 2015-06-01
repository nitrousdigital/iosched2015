package com.nitrous.iosched.client.model;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Describes a single speaker at the conference
 * 
 * @author nitrousdigital
 *
 */
public final class SpeakerJSO extends JavaScriptObject {
	protected SpeakerJSO() {
	}

	/**
	 * 
	 * @return the speaker id
	 */
	public native String getId() /*-{
		return this.id;
	}-*/;

	/**
	 * 
	 *  @return the speaker name
	 */
	public native String getName() /*-{
		return this.name;
	}-*/;

	/**
	 * @return the bio or null
	 */
	public native String getBio() /*-{
		return this.bio;
	}-*/;

	/**
	 * @return the company or null
	 */
	public native String getCompany() /*-{
		return this.company;
	}-*/;

	/**
	 * @return The thumbnail url or null
	 */
	public native String getThumbnailUrl() /*-{
		return this.thumbnailUrl;
	}-*/;

	/**
	 * @return The plus one url or null
	 */
	public native String getPlusoneUrl() /*-{
		return this.plusoneUrl;
	}-*/;

	/**
	 * @return The twitter url or null
	 */
	public native String getTwitterUrl() /*-{
		return this.twitterUrl;
	}-*/;
}