package com.avnet.emasia.webquote.utilities;

import java.io.Serializable;

import com.sun.faces.application.ApplicationAssociate;

public class DBResourceBundleReloadEvent implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private ApplicationAssociate applicationResourceBundle;

	/**
	 * @return the applicationResourceBundle
	 */
	public ApplicationAssociate getApplicationResourceBundle() {
		return applicationResourceBundle;
	}

	/**
	 * @param applicationResourceBundle the applicationResourceBundle to set
	 */
	public void setApplicationResourceBundle(ApplicationAssociate applicationResourceBundle) {
		this.applicationResourceBundle = applicationResourceBundle;
	}

	public DBResourceBundleReloadEvent(ApplicationAssociate applicationResourceBundle) {
		super();
		this.applicationResourceBundle = applicationResourceBundle;
	}

		
	
}