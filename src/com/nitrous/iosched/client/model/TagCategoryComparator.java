package com.nitrous.iosched.client.model;

import java.util.Comparator;

class TagCategoryComparator implements Comparator<TagCategory> {

	@Override
	public int compare(TagCategory cat1, TagCategory cat2) {
		String name1 = cat1.getCategoryName();
		String name2 = cat2.getCategoryName();
		Integer o1 = toOrder(name1);
		Integer o2 = toOrder(name2);
		int result = o1.compareTo(o2);
		if (result == 0) {
			result = name1.compareTo(name2);
		}
		return result;
	}
	
	private static int toOrder(String categoryName) {
		if ("THEME".equals(categoryName)) {
			return 0;
		}
		if ("TYPE".equals(categoryName)) {
			return 1;
		}
		if ("TOPIC".equals(categoryName)) {
			return 2;
		}
		return 3;
	}
}