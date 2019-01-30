package com.avnet.emasia.webquote.utilities;

public class SimpleDateFormat extends java.text.SimpleDateFormat {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 988497417423939665L;

	public SimpleDateFormat() {
		super();
		setLenient(false);
	}
	
	public SimpleDateFormat(String pattern) {
		super(pattern);
		setLenient(false);
	}
}