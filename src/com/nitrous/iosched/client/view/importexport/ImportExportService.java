package com.nitrous.iosched.client.view.importexport;

import com.nitrous.iosched.client.event.Dispatcher;
import com.nitrous.iosched.client.event.RequestExportEvent;
import com.nitrous.iosched.client.event.RequestImportEvent;
import com.nitrous.iosched.client.event.SimpleEventHandler;

public class ImportExportService {
	private static ImportExportService INSTANCE;

	private ImportExportService() {
		Dispatcher.addHandler(RequestImportEvent.TYPE, new SimpleEventHandler<RequestImportEvent>(){
			@Override
			public void onEvent(RequestImportEvent event) {
				showImportDialog();
			}
		});
		Dispatcher.addHandler(RequestExportEvent.TYPE, new SimpleEventHandler<RequestExportEvent>(){
			@Override
			public void onEvent(RequestExportEvent event) {
				showExportDialog();
			}
		});
	}

	private void showImportDialog() {
		new ImportDialog().showDialog();
	}
	
	private void showExportDialog() {
		new ExportDialog().showDialog();
	}
	
	public static void register() {
		get();
	}
	
	public static ImportExportService get() {
		if (INSTANCE == null) {
			INSTANCE = new ImportExportService();
		}
		return INSTANCE;
	}
}
