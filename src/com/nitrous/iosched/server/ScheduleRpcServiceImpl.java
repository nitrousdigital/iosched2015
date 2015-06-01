package com.nitrous.iosched.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.nitrous.iosched.client.rpc.RpcService;
import com.nitrous.iosched.shared.RpcException;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ScheduleRpcServiceImpl extends RemoteServiceServlet implements RpcService {
	
	@Override
	public String getSchedule() throws RpcException {
		try {
			return readURL(Config.getScheduleURL());
		} catch (InternalServerException ex) {
			String err = "Server Error. Unable to retrieve schedule";
			log(err, ex);
			throw new RpcException(err);
		}
	}
	
	@Override
	public String getUserSchedule(String authToken) throws RpcException {
		try {
			URL theURL = new URL(Config.getUserScheduleURL());
			
			HttpURLConnection myURLConnection = (HttpURLConnection)theURL.openConnection();
			myURLConnection.setRequestProperty ("Authorization", "Bearer "+authToken);
			myURLConnection.setRequestProperty("Content-Type", "application/json");
			myURLConnection.setUseCaches(false);
			myURLConnection.setDoInput(true);
			myURLConnection.setDoOutput(true);
			return readConnection(myURLConnection);
		} catch (Exception ex) {
			String err = "Server Error. Unable to retrieve user schedule";
			log(err, ex);
			throw new RpcException(err);
		}
	}

	private static final Charset UTF8 = Charset.forName("utf-8");
	private static String readConnection(URLConnection connection) throws IOException {
		StringBuilder result = new StringBuilder();
		InputStream is = null;
		try {
			is = connection.getInputStream();
			result = new StringBuilder();
			byte[] buf = new byte[1024];
			int bytesRead = 0;
			while((bytesRead = is.read(buf, 0, buf.length)) > 0) {
				byte[] chunk = new byte[bytesRead];
				System.arraycopy(buf, 0, chunk, 0, bytesRead);
				result.append(new String(chunk, UTF8));
			}
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		return result.toString();
	}
	
	private static String readURL(String url) throws InternalServerException {
		try {
			URL theURL = new URL(url);
			return readConnection(theURL.openConnection());
		} catch (Exception e) {			
			throw new InternalServerException("Server Error. Unable to read URL: "+url, e);
		}
	}
	

}
