package com.avnet.emasia.webquote.utilites.web.common;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import com.avnet.emasia.webquote.utilites.resources.ResourceMB;

@ManagedBean(name="AppVersionMB")
@RequestScoped
public class AppVersionMB implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(AppVersionMB.class.getName());
	
	public AppVersionMB() {
	}
	
	@PostConstruct
	public void postContruct() {
	}
	
	public String currentVersion() {
		String version = ResourceMB.getText("wq.title.webQuoteVersion");
		return version;
	}

	
}