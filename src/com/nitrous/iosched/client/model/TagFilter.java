package com.nitrous.iosched.client.model;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.JsArrayString;

public class TagFilter {
	private Set<String> tagNames;
	private Set<String> tagDisplayNames;
	
	public TagFilter() {
		this.tagNames = new HashSet<String>();
		this.tagDisplayNames = new HashSet<String>();		
	}
	
	public void add(TagJSO tag) {
		this.tagNames.add(tag.getTag());
		this.tagDisplayNames.add(tag.getDisplayName());
	}
	
	public void remove(TagJSO tag) {
		this.tagNames.remove(tag.getTag());
		this.tagDisplayNames.remove(tag.getDisplayName());
	}
	
	public boolean isEmpty() {
		return this.tagNames.isEmpty();
	}
	
	public boolean contains(TagJSO tag) {
		return this.tagNames.contains(tag.getTag());
	}
	
	public boolean accept(SessionJSO session) {
		if (isEmpty()) {
			return true;
		}
		JsArrayString sessionTags = session.getTags();
		for (int i = 0, len = sessionTags.length(); i < len; i++) {
			if (tagNames.contains(sessionTags.get(i))) {
				return true;
			}
		}
		return false;
	}
		
	
}
