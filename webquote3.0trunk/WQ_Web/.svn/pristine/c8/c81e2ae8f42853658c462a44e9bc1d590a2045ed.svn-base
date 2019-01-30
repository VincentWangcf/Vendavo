package com.avnet.emasia.webquote.web.user;

import java.io.IOException;
import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;
import javax.faces.context.FacesContext;

import com.avnet.emasia.webquote.utilities.common.SysConfigSB;

@ManagedBean
@ApplicationScoped
public class WebSealLogoutMB implements Serializable {

	private static final String LOGOUT_URL = "SECURITY_LOGOUT_URL";
	private static final long serialVersionUID = 1205370671614446647L;
	@EJB
	transient SysConfigSB sysConfigSB;

	public String getLogoutURL(){
		return sysConfigSB.getProperyValue(LOGOUT_URL);
	}
	
}
