package com.nitrous.iosched.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RpcServiceAsync {
	void getSchedule(AsyncCallback<String> callback);

	void getUserSchedule(String authToken, AsyncCallback<String> callback);
}
