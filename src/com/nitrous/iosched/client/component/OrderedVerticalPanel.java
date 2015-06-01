package com.nitrous.iosched.client.component;

import java.util.Comparator;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A VerticalPanel whose contents are sorted by a specified Comparator.
 * 
 * @author nitrousdigital
 *
 * @param <T> The type of widget that is sorted by the Comparator.
 */
public class OrderedVerticalPanel<T extends IsWidget> implements IsWidget {
	private Comparator<T> comparator;
	private VerticalPanel layout;
	
	public OrderedVerticalPanel(Comparator<T> comparator) {
		this.comparator = comparator;
		this.layout = new VerticalPanel();
	}
	
	/**
	 * Add a widget to its sorted position in this panel
	 * @param widget The widget to add
	 * @return The index where the widget was inserted
	 */
	public int add(T widget) {		
		int insertionIndex = -1;
		int len = layout.getWidgetCount();
		if (len > 0) {
			for (insertionIndex = 0; insertionIndex < len; insertionIndex++) {
				@SuppressWarnings("unchecked")
				T w = (T)layout.getWidget(insertionIndex);
				int result = comparator.compare(widget, w);
				if (result <= 0) {
					break;
				}
			}
		}
		
		if (insertionIndex == -1) {
			// append to end of list (or insert into empty list)
			insertionIndex = len;
			layout.add(widget);
		} else {
			layout.insert(widget, insertionIndex);
		}
		return insertionIndex;
	}
	
	public void remove(T widget) {
		layout.remove(widget);
	}

	@Override
	public Widget asWidget() {
		return layout;
	}
}
