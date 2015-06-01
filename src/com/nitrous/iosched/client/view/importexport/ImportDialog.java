package com.nitrous.iosched.client.view.importexport;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nitrous.iosched.client.model.ConferenceDataManager;
import com.nitrous.iosched.client.model.SessionJSO;
import com.nitrous.iosched.client.service.UserScheduleService;
import com.nitrous.polygwt.client.component.HtmlRippleButton;


public class ImportDialog extends AbstractImportExportDialog {
	private static final Logger LOGGER = Logger.getLogger(ImportDialog.class.getName());
	
	private HtmlRippleButton importButton;
	private HtmlRippleButton closeButton;
	
	public ImportDialog() {
		setHeaderText("Import Schedule");
		
		importButton = createActionButton("Import");
		addButton(importButton);
		
		closeButton = addCloseButton("Cancel");
		showCancelButton();
		
		refreshContent();
	}
	
	private void showCancelButton() {
		Style style = closeButton.asWidget().getElement().getStyle();
		style.setColor("#811734");
		closeButton.getButtonHTML().setHTML("Cancel");
		getTextArea().setEnabled(true);
		getInstructions().setVisible(true);
	}
	
	private void showCloseButton() {
		Style style = closeButton.asWidget().getElement().getStyle();
		style.setColor("#008094");
		closeButton.getButtonHTML().setHTML("Close");
		importButton.asWidget().setVisible(false);
		getTextArea().setEnabled(false);
		getInstructions().setVisible(false);
	}
	
	@Override
	protected void register() {
		super.register();
		reg.add(importButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onImport();
			}
		}));
	}
	
	@Override
	protected void refreshContent() {
		Set<String> sessions = UserScheduleService.get().getUserSessions();
		if (sessions == null || sessions.size() == 0) {
			setInstructions("Paste your exported session data below and then click Import");
		} else {
			setInstructions("Paste your exported session data below and then click Import.<br/>"
					+ "<b>WARNING:</b> Performing an import will overwrite your existing schedule.");
		}
		setText("");
		showCancelButton();
	}
	
	private void onImport() {
		String text = getTextArea().getText();
		text = text.trim();
		if (text.length() == 0) {
			alert("You forgot to paste your session data into the window before clicking import!");
			return;
		}
		String[] parts = text.split(",");
		
		Set<SessionJSO> sessions = new HashSet<SessionJSO>();
		for (String part : parts) {
			SessionJSO session = ConferenceDataManager.get().getSessionById(part);
			if (session != null) {
				sessions.add(session);
			} else {
				LOGGER.severe("Import session - Unrecognized session ID: "+part);
				alert("Unrecognized session ID: "+part+". Please check your data or try clicking the reload button before attempting to import."); 
				return;
			}
		}
		
		UserScheduleService.get().importSchedule(sessions, new AsyncCallback<Set<SessionJSO>>() {
			@Override
			public void onFailure(Throwable caught) {
				LOGGER.log(Level.SEVERE, "Import failed", caught);
				alert("Failed to import schedule");
			}

			@Override
			public void onSuccess(Set<SessionJSO> result) {
				onImportComplete(result);
			}			
		});
		
	}

	private static class SessionJSOComparator implements Comparator<SessionJSO> {
		@Override
		public int compare(SessionJSO o1, SessionJSO o2) {
			if (o1 == o2) {
				return 0;
			}
			String o1DateTime = o1.getStartTimestamp();
			String o2DateTime = o2.getStartTimestamp();
			int result = o1DateTime.compareTo(o2DateTime);
			if (result == 0) {
				result = o1.getTitle().compareTo(o2.getTitle());
			}
			if (result == 0) {
				result = o1.getId().compareTo(o2.getId());
			}
			return result;
		}
	}
	
	private void onImportComplete(Set<SessionJSO> sessions) {
		StringBuilder buf = new StringBuilder("Import Success.\n\n");
		TreeSet<SessionJSO> sorted = new TreeSet<SessionJSO>(new SessionJSOComparator());
		sorted.addAll(sessions);
		for (SessionJSO session : sorted) {
			buf.append(session.getSessionDateTime()).append(" - ").append(session.getTitle()).append("\n");			
		}
		getTextArea().setText(buf.toString());
		showCloseButton();
	}
	
//	private void info(String message) {
//		Window.alert(message);
//	}
	
	private void alert(String error) {
		Window.alert(error);
	}
	
}