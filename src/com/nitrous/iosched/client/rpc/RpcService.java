package com.nitrous.iosched.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.nitrous.iosched.shared.RpcException;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("rpc")
public interface RpcService extends RemoteService {
	/**
	 * Retrieve the public schedule
	 * @return the public schedule
	 * @throws RpcException
	 */
	String getSchedule() throws RpcException;
	
	/**
	 * Retrieve the user-defined schedule for the authenticated user.
	 * @param authToken The authenticated user token
	 * @return The JSON list of sessions IDs for the authenticated user
	 * @throws RpcException
	 */
	String getUserSchedule(String authToken) throws RpcException;
}
