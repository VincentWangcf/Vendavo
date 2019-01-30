package com.avnet.emasia.webquote.utilites.web.common;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.annotation.WebServlet;

import com.avnet.emasia.webquote.cluster.dispatcher.CacheSynCommandClusterService;
import com.avnet.emasia.webquote.entity.User;
import com.avnet.emasia.webquote.user.ejb.ApplicationSB;
import com.avnet.emasia.webquote.web.user.UserInfo;

@ManagedBean
@ApplicationScoped
public class MenuMB implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOG =Logger.getLogger(MenuMB.class.getName());

	@EJB
	private ApplicationSB applicationSB;
	
	public boolean isAccessible(String functionName) {
		User user = UserInfo.getUser();
		
		if(user == null){
			LOG.log(Level.SEVERE, "User is null in menuMB");
		}	
		return applicationSB.isAppFunctionAccessibleByUser(user, functionName);
	}
	
}
