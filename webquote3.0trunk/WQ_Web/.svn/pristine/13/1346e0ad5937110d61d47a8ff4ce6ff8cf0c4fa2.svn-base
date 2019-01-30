package com.avnet.emasia.webquote.cluster.dispatcher;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import com.avnet.emasia.webquote.cache.ActionIndicator;

@ManagedBean
@ApplicationScoped
public class DispatcherControlMB {
	
	public String getClearAllCachePostJs() {
		return CacheSynCommandClusterService.getCallBackJs(ActionIndicator.CLEARALL);
	}
}
