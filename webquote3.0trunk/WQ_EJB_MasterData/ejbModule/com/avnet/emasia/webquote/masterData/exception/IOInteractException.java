package com.avnet.emasia.webquote.masterData.exception;

import com.avnet.emasia.webquote.exception.WebQuoteRuntimeException;

/**
 * @author Tonmy,Li(906893)
 * @Created on 2013-01-22
 * 
 */
public class IOInteractException extends WebQuoteRuntimeException {

	public IOInteractException(String errorCode, Exception causedBy) {
		super(errorCode, causedBy);
	}

	private static final long serialVersionUID = 1L;

	
}
