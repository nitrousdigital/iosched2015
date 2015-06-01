package com.nitrous.iosched.client.service;

import com.nitrous.iosched.client.event.Dispatcher;
import com.nitrous.iosched.client.event.TagFilterChangedEvent;
import com.nitrous.iosched.client.model.TagFilter;
import com.nitrous.iosched.client.model.TagJSO;

/**
 * Manages the selected tag filter options.
 * 
 * @author nitrousdigital
 *
 */
public class TagFilterService {
	private static final TagFilterService INSTANCE = new TagFilterService();
	
	/** The set of currently enabled tag filters */
	private TagFilter tagFilter = new TagFilter();
	
	private TagFilterService() {
	}
	
	public static TagFilterService get() {
		return INSTANCE;
	}
	
	/**
	 * @param tag The tag
	 * @return True if the specified tag is currently selected as a filter.
	 */
	public boolean isEnabled(TagJSO tag) {
		return tagFilter.contains(tag);
	}
	
	/**
	 * @return The names of tags currently chosen for the active filter or an empty set if no filtering is currently enabled.
	 */
	public TagFilter getFilter() {
		return tagFilter;
	}
	
	/**
	 * Toggle the specified tag filter
	 * @param tag The name of the tag
	 * @return True if the tag filter is now enabled, false if it has become disabled.
	 */
	public boolean toggle(TagJSO tag) {
		if (tagFilter.contains(tag)) {
			tagFilter.remove(tag);
			notifyListeners();
			return false;
		} else {
			tagFilter.add(tag);
			notifyListeners();
			return true;
		}
	}
	
	/**
	 * Add or remove a tag to the filter
	 * @param tag The tag to add or remove
	 * @param filter True to add to the filter, false to remove from the filter.
	 */
	public void setFilterEnabled(TagJSO tag, boolean filter) {
		if (filter) {
			if (!tagFilter.contains(tag)) {
				tagFilter.add(tag);
				notifyListeners();
			}
		} else {
			if (tagFilter.contains(tag)) {
				tagFilter.remove(tag);
				notifyListeners();				
			}
		}
	}
	
	private void notifyListeners() {
		Dispatcher.fire(new TagFilterChangedEvent(tagFilter));
	}
	
}
