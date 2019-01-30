package com.avnet.emasia.webquote.utilites.web.schedule;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import com.avnet.emasia.webquote.entity.ActiveSession;
import com.avnet.emasia.webquote.user.ejb.ActiveSessionSB;

@ManagedBean
@RequestScoped
public class ActiveSessionMB  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2204929166936508628L;
	private static final Logger LOG = Logger.getLogger(ActiveSessionMB.class.getName());
	
	@EJB 
	ActiveSessionSB activeSessionSB;
	
	private List<ActiveSession> sessionList = null;
	
	@PostConstruct
	public void initialize() {
		sessionList = activeSessionSB.findActiveSession();
		LOG.info("sessionList.size===>> " + sessionList.size());
	}
	
	public void refreshSession(){
		sessionList = activeSessionSB.findActiveSession();
		LOG.info("sessionList.size===>> " + sessionList.size());
	}

	public List<ActiveSession> getSessionList() {
		return sessionList;
	}

	public void setSessionList(List<ActiveSession> sessionList) {
		this.sessionList = sessionList;
	}
	
	
	

}
