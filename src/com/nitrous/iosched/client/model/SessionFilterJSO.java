package com.nitrous.iosched.client.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * A filters associated with session
 * 
 * @author nitrousdigital
 *
 */
public final class SessionFilterJSO extends JavaScriptObject {
	
	protected SessionFilterJSO() {
	}

	// some filter keys are UUIDs and these are not displayed in the UI so
	// use the length of a UUID as a filter
	private static final int UUID_LEN = "67fa8734-8be4-e411-b87f-00155d5066d7".length();
	
	private static boolean isDisplayFilter(String key) {
		if (key == null) {
			return false;
		}
		if (key.length() == UUID_LEN 
				&& key.charAt(8) == '-' 
				&& key.charAt(13) == '-'
				&& key.charAt(18) == '-'
				&& key.charAt(23) == '-') {
			// looks like a UUID
			return false;
		}
		return true;
	}
	
	public Map<String, Boolean> getFilters() {
		JsArrayString keys = getKeysNative();
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		for (int i = 0, len = keys.length(); i < len; i++) {
			String key = keys.get(i);
			if (isDisplayFilter(key)) {
				map.put(key, getValue(key));
			}
		}
		return map;
	}

	private native boolean getValue(String key) /*-{
		return this[key] === true;
	}-*/;
	
	public Set<String> getKeys() {
		return getFilters().keySet();
	}
	
	private native JsArrayString getKeysNative() /*-{
		var arr = new Array();
		for (var key in this) {
		  if (this.hasOwnProperty(key)) {
		  	arr.push(key);
		  }
		}	
		return arr;	 
	}-*/; 
}
