package com.nitrous.iosched.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * The tags options available to filter conference session
 * 
 * <pre>
 * "tags": {
 *     "THEME_DEVELOP\u0026DESIGN": {
 *       "order_in_category": 1,
 *       "tag": "THEME_DEVELOP\u0026DESIGN",
 *       "name": "Develop \u0026 Design",
 *       "category": "THEME"
 *     },
 *     "THEME_ENGAGE\u0026EARN": {
 *       "order_in_category": 2,
 *       "tag": "THEME_ENGAGE\u0026EARN",
 *       "name": "Engage \u0026 Earn",
 *       "category": "THEME"
 *     },
 * </pre>
 * 
 * @author nitrousdigital
 *
 */
public final class TagsJSO extends JavaScriptObject {
	protected TagsJSO() {
	}

	/**
	 * @return a lookup from category name to list of tags for that category
	 */
	public Map<String, List<TagJSO>> getTags() {
		HashMap<String, List<TagJSO>> map = new HashMap<String, List<TagJSO>>(); 
		JsArrayString tagNames = getTagNames();		
		if (tagNames != null) {
			for (int i = 0, len = tagNames.length(); i < len; i++) {
				String name = tagNames.get(i);
				TagJSO tag = getTag(name);
				String category = tag.getCategory();
				List<TagJSO> tagsForCat = map.get(category);
				if (tagsForCat == null) {
					tagsForCat = new ArrayList<TagJSO>();
					map.put(category, tagsForCat);
				}
				tagsForCat.add(tag);
			}
		}
		return map;
	}

	public native TagJSO getTag(String name) /*-{
		return this[name];
	}-*/;
	
	private native JsArrayString getTagNames() /*-{
		var arr = new Array();
       	for(var property in this){
       		if (this.hasOwnProperty(property)) {
       			arr.push(property);
       		}
       	}
       	return arr;
	}-*/;
	
}