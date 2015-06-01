package com.nitrous.iosched.client.model;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * A tag option available to filter conference sessions
 * 
 * <pre>
 *     "THEME_DEVELOP\u0026DESIGN": {
 *       "order_in_category": 1,
 *       "tag": "THEME_DEVELOP\u0026DESIGN",
 *       "name": "Develop \u0026 Design",
 *       "category": "THEME"
 *     }
 * </pre>
 * 
 * @author nitrousdigital
 *
 */
public final class TagJSO extends JavaScriptObject {
	protected TagJSO() {
	}

	/**
	 * @return The index used to determine display order within the category of filters.
	 */
	public native int getOrderInCategory() /*-{
		return this["order_in_category"];
	}-*/;

	/**
	 * @return The tag to be matched against sessions
	 */
	public native String getTag() /*-{
		return this["tag"];
	}-*/;

	/**
	 * @return The display name for this tag
	 */
	public native String getDisplayName() /*-{
		return this["name"];
	}-*/;
	
	/**
	 * @return The category of this tag (THEME, TOPIC or TYPE)
	 */
	public native String getCategory() /*-{
		return this["category"];
	}-*/;
}