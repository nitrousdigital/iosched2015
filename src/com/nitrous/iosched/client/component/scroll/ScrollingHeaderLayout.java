package com.nitrous.iosched.client.component.scroll;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public interface ScrollingHeaderLayout extends IsWidget {
	ScrollPanel getScrollPanel();

	/**
	 * Get the scrolling container
	 * 
	 * @return The container to which scrollable content should be added
	 */
	VerticalPanel getScrollContainer();

	void scrollToWidget(IsWidget widget, AnimationCompletionCallback callback);
	
	int getVerticalScrollPosition();
	void setVerticalScrollPosition(int position);
}
