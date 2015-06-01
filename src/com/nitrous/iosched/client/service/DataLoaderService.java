package com.nitrous.iosched.client.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.nitrous.iosched.client.event.DataLoadingEvent;
import com.nitrous.iosched.client.event.Dispatcher;
import com.nitrous.iosched.client.event.RequestReloadDataEvent;
import com.nitrous.iosched.client.event.SimpleEventHandler;
import com.nitrous.iosched.client.event.ToastEvent;
import com.nitrous.iosched.client.model.ConferenceDataManager;
import com.nitrous.iosched.client.model.ScheduleContainerJSO;
import com.nitrous.iosched.client.rpc.RpcService;
import com.nitrous.iosched.client.rpc.RpcServiceAsync;

/**
 * Responsible for loading the conference schedule data.
 * 
 * @author nitrousdigital
 *
 */
public class DataLoaderService {
	private static final Logger LOGGER = Logger.getLogger(DataLoaderService.class.getName());
	
	private static final String KEY_CONFERENCE_DATA = "conference_data";
	
	private static DataLoaderService INSTANCE;
	private Storage storage;
	
	private boolean isLoading = false;
	
	private DataLoaderService() {
		storage = Storage.getLocalStorageIfSupported();
		
		Dispatcher.addHandler(RequestReloadDataEvent.TYPE,  new SimpleEventHandler<RequestReloadDataEvent>(){
			@Override
			public void onEvent(RequestReloadDataEvent event) {
				onRequestReload();
			}
		});
	}

	public static DataLoaderService get() {
		if (INSTANCE == null) {
			INSTANCE = new DataLoaderService();
		}
		return INSTANCE;
	}

	/**
	 * 
	 * @param refresh
	 *            True to force a reload from the server, false to obtain from
	 *            the cache if available
	 * @param callback
	 *            The callback to be notified with the result
	 */
	public void loadData(boolean refresh, final AsyncCallback<ScheduleContainerJSO> callback) {
		if (!refresh) {
			loadFromCache(callback);
		} else {
			loadFromServer(callback);
		}
	}
	
	private void saveToCache(String data) {
		if (storage != null) {
			storage.setItem(KEY_CONFERENCE_DATA, data);
		}		
	}
	
	private void loadFromCache(final AsyncCallback<ScheduleContainerJSO> callback) {
		LOGGER.fine("Loading from cache...");
		ScheduleContainerJSO sessionList = null;
		if (storage != null) {
			String data = storage.getItem(KEY_CONFERENCE_DATA);
			if (data != null) {
				sessionList = ScheduleContainerJSO.parse(data);
				if (sessionList == null) {
					// remove invalid data from the Local Storage
					storage.removeItem(KEY_CONFERENCE_DATA);
					LOGGER.log(Level.WARNING, "Removing invalid data from the cache.");
				}
			}
		}
		
		if (sessionList != null) {
			LOGGER.fine("Data loaded from the cache");
			ConferenceDataManager.get().onScheduleLoaded(sessionList);
			callback.onSuccess(sessionList);
		} else {
			LOGGER.fine("Data not found in cache... Loading from server...");
			loadFromServer(callback);
		}
	}
	
	private void onRequestReload() {
		if (isLoading) {
			return;
		}
		loadFromServer(new AsyncCallback<ScheduleContainerJSO>(){
			@Override
			public void onFailure(Throwable caught) {
				Dispatcher.fire(new ToastEvent("Failed to load data."));
			}

			@Override
			public void onSuccess(ScheduleContainerJSO result) {
			}
		});
	}

	/**
	 * Load demo conference data instead of live conference data.
	 * @param callback The calkback to be notified with the result
	 */
	private void loadDemoData(final AsyncCallback<ScheduleContainerJSO> callback) {
		LOGGER.fine("Loading demo data from the server.");
		isLoading = true;
		Dispatcher.fire(new DataLoadingEvent(true));
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, GWT.getHostPageBaseURL() + "offline.json");
		RequestCallback cb = new RequestCallback() {			
			@Override
			public void onResponseReceived(Request request, Response response) {
				LOGGER.fine("Data received from the server.");
				isLoading = false;
				onScheduleReceived(response.getText(), callback);
				Dispatcher.fire(new DataLoadingEvent(false));
			}
			
			@Override
			public void onError(Request request, Throwable exception) {
				LOGGER.log(Level.SEVERE, "Failed to load demo data from the server", exception);
				isLoading = false;
				Dispatcher.fire(new DataLoadingEvent(false));
				onScheduleLoadFailed(new Exception("Failed to load schedule", exception), callback);
			}
		};
		builder.setCallback(cb);
		
		try {
			builder.send();
		} catch (RequestException exception) {
			cb.onError(null, exception);
		}
	}
	
	private void loadFromServer(final AsyncCallback<ScheduleContainerJSO> callback) {
		if ("1".equals(Window.Location.getParameter("demo"))) {
			loadDemoData(callback);
			return;
		}
		
		LOGGER.fine("Loading conference data from the server.");
		isLoading = true;
		Dispatcher.fire(new DataLoadingEvent(true));
		RpcServiceAsync RPC = GWT.create(RpcService.class);
		RPC.getSchedule(new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				LOGGER.fine("Data received from the server.");
				isLoading = false;
				onScheduleReceived(result, callback);
				Dispatcher.fire(new DataLoadingEvent(false));
			}

			@Override
			public void onFailure(Throwable caught) {
				LOGGER.log(Level.SEVERE, "Failed to load data from the server", caught);
				isLoading = false;
				Dispatcher.fire(new DataLoadingEvent(false));
				onScheduleLoadFailed(new Exception("Failed to load schedule", caught), callback);
			}
		});
	}

	private void onScheduleLoadFailed(Throwable cause, AsyncCallback<ScheduleContainerJSO> callback) {
		callback.onFailure(cause);
	}
	
	private void onScheduleReceived(String body, final AsyncCallback<ScheduleContainerJSO> callback) {
		ScheduleContainerJSO sessionList = ScheduleContainerJSO.parse(body);
		if (sessionList == null) {
			onScheduleLoadFailed(new Exception("Failed to load schedule.", new Exception("Unable to parse schedule data")), callback);
		} else {
			saveToCache(body);
			// make the schedule available to the UI components.
			ConferenceDataManager.get().onScheduleLoaded(sessionList);
			callback.onSuccess(sessionList);
		}
	}
}
