package com.avnet.emasia.webquote.web.stm.util;

public class FTPClientException extends Exception {

	private static final long serialVersionUID = 7976191025215044270L;

	public FTPClientException() {
	}

	public FTPClientException(String message) {
		super(message);
	}

	public FTPClientException(String message, Exception e) {
		super(message,e);
	}
}
