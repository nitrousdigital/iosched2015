package com.nitrous.iosched.client.component.polymer;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

public class PaperToast extends HTML {
	private static int NEXT_ID;
	private int myId;
	private JavaScriptObject toast;

	private PaperToast(String message) {
		myId = NEXT_ID++;
		StringBuilder html = new StringBuilder(
				"<paper-toast id='toast-message-").append(myId)
				.append("' text='").append(message).append("'></paper-toast>");
		setHTML(html.toString());
	}

	private void show() {
		showToast(find(myId));
	}

	private native JavaScriptObject find(int id) /*-{
		return $doc.querySelector('#toast-message-'+id);
	}-*/;

	private native void showToast(JavaScriptObject toast) /*-{
		toast.show();
	}-*/;

	public static void show(String message) {
		PaperToast toast = new PaperToast(message);
		RootPanel.get().add(toast);
		toast.show();
	}
}
