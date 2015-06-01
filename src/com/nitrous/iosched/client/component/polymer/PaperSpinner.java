package com.nitrous.iosched.client.component.polymer;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;

public class PaperSpinner extends HTML {
	private static int NEXT_ID;
	private int myId;
	public PaperSpinner() {
		myId = ++NEXT_ID;
		setHTML("<paper-spinner id=\"loading-spinner-"+myId+"\" class=\"blue\" active></paper-spinner>");
	}
	
	private Timer hideTimer = new Timer(){
		@Override
		public void run() {
			setVisible(false);
		}
	};
	
	public void show(boolean show) {
		hideTimer.cancel();
		if (show) {
			setVisible(true);
			showLoadingIndicator(myId, true);
		} else {
			showLoadingIndicator(myId, false);
			hideTimer.schedule(1000);
		}
	}
	
	private final native void showLoadingIndicator(int id, boolean show) /*-{
		var spinner = $doc.querySelector('#loading-spinner-'+id);
		if (spinner != null) {
			spinner.active = show;
		}
	}-*/;
	
}
