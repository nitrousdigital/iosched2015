package com.nitrous.iosched.client.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TagCategory {
	private String name;
	private TagJSO[] tags;
	
	public TagCategory(String name, List<TagJSO> tags) {
		this.name = name;
		this.tags = tags.toArray(new TagJSO[tags.size()]);
		Arrays.sort(this.tags, TAG_COMPARATOR);
	}	
	
	public String getCategoryName() {
		return name;
	}
	
	/**
	 * @return The sorted array of tags in this category
	 */
	public TagJSO[] getTags() {
		return tags;
	}
	
	private static final Comparator<TagJSO> TAG_COMPARATOR = new Comparator <TagJSO>(){
		@Override
		public int compare(TagJSO o1, TagJSO o2) {
			Integer order1 = o1.getOrderInCategory();
			Integer order2 = o2.getOrderInCategory();
			int result = order1.compareTo(order2);
			if (result == 0) {
				result = o1.getDisplayName().compareTo(o2.getDisplayName());
			}
			if (result == 0) {
				result = o1.getTag().compareTo(o2.getTag());
			}
			return result;
		}
	};
	
	
}
