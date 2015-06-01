package com.nitrous.iosched.client.view.importexport;

import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.nitrous.iosched.client.service.UserScheduleService;
import com.nitrous.polygwt.client.component.HtmlRippleButton;


public class ExportDialog extends AbstractImportExportDialog {
	private HtmlRippleButton emailButton;
	public ExportDialog() {
		setHeaderText("Export Schedule");
		emailButton = createActionButton("Send by Email");
		addButton(emailButton);
		addCloseButton();
		refreshContent();
		getTextArea().setEnabled(false);
	}
	@Override
	protected void register() {
		// TODO Auto-generated method stub
		super.register();
		reg.add(emailButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSendEmail();
			}
		}));
	}
	
	private void onSendEmail() {
		String export = getTextArea().getText();
		if (export.trim().length() > 0) {
			StringBuilder buf = new StringBuilder("Here is your exported schedule. To import the schedule, visit http://io2015schedule.appspot.com/#import and copy the following text into the import dialog:\n\n");
			buf.append(export);
			String body = URL.encode(buf.toString());
			Window.open("mailto:?subject=My Google IO Schedule&body=" + body, "_new", "");
		} else {
			Window.alert("Nothing to export.");
		}
	}
	
	@Override
	protected void refreshContent() {
		Set<String> sessions = UserScheduleService.get().getUserSessions();
		if (sessions == null || sessions.size() == 0) {
			setInstructions("Nothing to export. Your schedule is currently empty.");
			setText("");
		} else {		
			String[] arr = sessions.toArray(new String[sessions.size()]);
			setInstructions(
					"To transfer your schedule to another device, copy the text below to the "
					+ "import dialog on your other device.");

			StringBuilder buf = new StringBuilder();
			for (int i = 0 ; i < arr.length; i++) {
				if (i > 0) {
					buf.append(",");
				}
				buf.append(arr[i]);
			}
			setText(buf.toString());
		}

	}
	
}