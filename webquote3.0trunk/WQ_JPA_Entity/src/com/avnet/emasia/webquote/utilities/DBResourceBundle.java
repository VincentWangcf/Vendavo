package com.avnet.emasia.webquote.utilities;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
/**
 * The class DBResourceBundle is created for providing the internationalization support. 
 * @author NEC
 *
 */
public class DBResourceBundle extends ResourceBundle{


	private static final String BUNDLE_NAME = DBResourceBundle.class.getName();


	public DBResourceBundle() {
		this(Locale.ENGLISH);
	}

	public DBResourceBundle(Locale locale){
		setParent(ResourceBundle.getBundle(BUNDLE_NAME,locale,new DBControl()));
	}

	@Override
	protected Object handleGetObject(String key) {
		return parent.getObject(key);
	}


	@Override
	public Enumeration<String> getKeys() {
		return parent.getKeys();
	}

	

	
}
