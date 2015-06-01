package com.nitrous.iosched.shared;

import java.io.Serializable;

public class RpcException extends Exception implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public RpcException() {
		super();
	}
	public RpcException(String message) {
		super(message);
	}
}
