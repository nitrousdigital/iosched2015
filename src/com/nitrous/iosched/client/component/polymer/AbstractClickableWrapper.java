package com.nitrous.iosched.client.component.polymer;

import java.util.ArrayList;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HTML;

/**
 * 
 * @author nitrousdigital
 *
 * @param <T> The type that extends this class.
 */
public abstract class AbstractClickableWrapper extends HTML {
	
	public interface ClickHandler {
		void onClick(AbstractClickableWrapper fab);
	}

	private JavaScriptObject clickHandler;

	private ArrayList<ClickHandler> clickHandlers;

	private static int NEXT_ID;
	protected final String myId;

	protected AbstractClickableWrapper() {
		this(null);
	}
	
	protected AbstractClickableWrapper(String html) {
		if (html != null){
			setHTML(html);
		}
		int id = nextId();
		myId = getIdPrefix() + "-" + id;

		this.addAttachHandler(new Handler() {
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached()) {
					register();
				} else {
					unregister();
				}
			}
		});
	}

	private static native void removeClickHandler(Element el, JavaScriptObject handler) /*-{
		el.removeEventListener("click", handler);
	}-*/;

	private static native JavaScriptObject addClickHandler(Element el, AbstractClickableWrapper fab) /*-{
		var fun = function(event) {
			$entry(fab.@com.nitrous.iosched.client.component.polymer.AbstractClickableWrapper::handleClick()());
		}
		el.addEventListener("click", fun);
		return fun;
	}-*/;

	public HandlerRegistration addClickHandler(final ClickHandler handler) {
		if (clickHandlers == null) {
			clickHandlers = new ArrayList<ClickHandler>();
		}
		clickHandlers.add(handler);
		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				removeClickHandler(handler);
			}
		};
	}

	public void removeClickHandler(ClickHandler handler) {
		if (clickHandlers != null) {
			clickHandlers.remove(handler);
		}
	}

	private void register() {
		unregister();
		clickHandler = addClickHandler(getEl(), this);
	}

	private void unregister() {
		if (clickHandler != null) {
			removeClickHandler(getEl(), clickHandler);
			clickHandler = null;
		}
	}

	private void handleClick() {
		if (clickHandlers != null) {
			ClickHandler[] arr = clickHandlers.toArray(new ClickHandler[clickHandlers.size()]);
			for (ClickHandler h : arr) {
				h.onClick(this);
			}
		}
	}

	/**
	 * Find the element of this widget
	 * @return The Element of this widget or null if not found.
	 */
	protected Element getEl() {
		return Document.get().getElementById(getMyId());
	}

	/**
	 * @return The ID of this AbstractClickableWrapper instance
	 */
	protected String getMyId() {
		return myId;
	}

	/**
	 * @return The ID prefix to be assigned to this widget, e.g. "my-widget". This will be appended with a unique ID to result in e.g. "my-widget-10" and returned by getMyId()
	 */
	protected abstract String getIdPrefix();
	
	/**
	 * Issue the next numeric ID
	 * @return The next numeric ID to be assigned to an AbstractClickableWrapper
	 */
	protected static int nextId() {
		return ++NEXT_ID;
	}
	
	/**
	 * Format the style attribute
	 * 
	 * @param style
	 *            The style attributes, either as "color:red;" or
	 *            "style=\"color:red;\"
	 * @return The normalized style attribute that can be appended directly to
	 *         the html element, e.g. "style=\"color:red;\" or null if the style
	 *         parameter is null or empty.
	 */
	protected static String formatStyleAttribute(String style) {
		if (style == null || style.trim().length() == 0) {
			return null;
		}
		StringBuilder buf = new StringBuilder();
		boolean wrap = false;
		if (!style.trim().toLowerCase().startsWith("style")) {
			buf.append("style=\"");
			wrap = true;
		}
		buf.append(style);
		if (wrap) {
			buf.append("\"");
		}
		return buf.toString();
	}
	
}
