package com.nitrous.iosched.client.component;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;

public class PopupMenu extends PopupPanel {
	public void showRelativeTo(final Element target) {
		// Set the position of the popup right before it is shown.
		setPopupPositionAndShow(new PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				position(target, offsetWidth, offsetHeight);
			}
		});
	}

	/**
	 * Positions the popup, called after the offset width and height of the
	 * popup are known.
	 *
	 * @param buttonObject
	 *            the ui object to position relative to
	 * @param popupWidth
	 *            the drop down's offset width
	 * @param popupHeight
	 *            the drop down's offset height
	 */
	private void position(final Element buttonObject, int popupWidth, int popupHeight) {
		// Calculate left position for the popup. 
		int buttonWidth = buttonObject.getOffsetWidth();
		int buttonLeft = buttonObject.getAbsoluteLeft(); 
		int right = buttonLeft + buttonWidth;
		// default to placing the popup to the right of the button
		int left = right;

		// try and place the popup to the right of the anchor button
		
		// Make sure scrolling is taken into account, since
		// box.getAbsoluteLeft() takes scrolling into account.
		int windowLeft = Window.getScrollLeft();
		int windowRight = Window.getClientWidth() + Window.getScrollLeft();
		
		// Distance from the right edge of the button to the right edge of the window
		int distanceToWindowRight = windowRight - right;
		int distanceToWindowLeft = buttonLeft - windowLeft; 
		// if there isn't enough space to place the popup to the right of the button, shift to the left
		if (distanceToWindowRight < popupWidth) {
			if (distanceToWindowLeft > popupWidth) {
				// place to the left of the button
				left = buttonLeft - popupWidth;
			} else {
				// not enough space to the left or right, so just place at the right edge of the window.
				left = windowRight - popupWidth;
			}
		}

		// Calculate top position for the popup
		int top = buttonObject.getAbsoluteTop();

		// Make sure scrolling is taken into account, since
		// box.getAbsoluteTop() takes scrolling into account.
		int windowBottom = Window.getScrollTop() + Window.getClientHeight();

		// Distance from the bottom edge of the window to the top edge of the text box
		int distanceToWindowBottom = windowBottom - top;

		// stick the popup to the bottom of the screen if not enough space below the button to place the popup
		if (distanceToWindowBottom < popupHeight) {
			top = windowBottom - popupHeight;
		}
		setPopupPosition(left, top);
	}
}
