package com.avnet.emasia.webquote.web.quote;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.avnet.emasia.webquote.utilites.web.schedule.RefreshCacheTask;

@ManagedBean
@SessionScoped
public class CacheMB implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 757006090908911445L;
	@EJB
	RefreshCacheTask refreshCacheTask;
	
	public void refreshCache(){
		refreshCacheTask.refresh();
	}
	
	
}