package com.nitrous.iosched.client.model;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * The main JSON response returned by the server: https://events.google.com/io2015/api/v1/schedule
 * 
 * @author nitrousdigital
 *
 */
public final class ScheduleContainerJSO extends JavaScriptObject {
	protected ScheduleContainerJSO() {
	}
	
	public native TagsJSO getTags() /*-{
		return this.tags;
	}-*/;
	
	public native SpeakersJSO getSpeakers() /*-{
		return this.speakers;
	}-*/;
	
	public native JsArray<SessionJSO> getSessions() /*-{
		return this.sessions;
	}-*/;
	
	public static ScheduleContainerJSO parse(String json) {
		ScheduleContainerJSO jso = null;
		try {
			JSONValue jsonVal = JSONParser.parseStrict(json);
			JSONObject jsonObj = jsonVal.isObject();
			if (jsonObj != null) {
				jso = jsonObj.getJavaScriptObject().cast();
			}
		} catch (Exception ex) {			
		}
		return jso;
	}
}
