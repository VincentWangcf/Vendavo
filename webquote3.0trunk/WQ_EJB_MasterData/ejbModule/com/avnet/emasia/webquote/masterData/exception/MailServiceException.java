package com.avnet.emasia.webquote.masterData.exception;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-22
 * 
 */

public class MailServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MailServiceException() {
	}

	public MailServiceException(Throwable e) {
		super(e);
	}

	public MailServiceException(String s) {
		super(s);
	}

	public MailServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
