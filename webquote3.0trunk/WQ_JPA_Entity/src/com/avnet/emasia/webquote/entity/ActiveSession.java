package com.avnet.emasia.webquote.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.Date;
import java.math.BigDecimal;


/**
 * The persistent class for the ACTIVE_SESSION database table.
 * 
 */
@Entity
@Table(name="ACTIVE_SESSION")
@NamedQuery(name="ActiveSession.findAll", query="SELECT a FROM ActiveSession a")
public class ActiveSession implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name="ACTIVE_SESSION_ID_GENERATOR", sequenceName="WQ_SEQ",allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ACTIVE_SESSION_ID_GENERATOR")
	@Column(name="ID", unique=true, nullable=false)
	private long id;
	
	private long active;

	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LOGIN_TIME")
	private Date loginTime;
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LOGOUT_TIME")
	private Date logoutTime;
	

	@Column(name="SESSION_ID")
	private String sessionId;

	@Column(name="SEVER_NAME")
	private String severName;

	@Column(name="USER_ID")
	private String userId;
	
	
	@Column(name="CLIENT_INFO")
	private String clientInfo;

	public ActiveSession() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public long getActive() {
		return active;
	}

	public void setActive(long active) {
		this.active = active;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSeverName() {
		return this.severName;
	}

	public void setSeverName(String severName) {
		this.severName = severName;
	}

	public String getUserId() {
		return this.userId;
	}
	
	

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public Date getLogoutTime() {
		return logoutTime;
	}

	public void setLogoutTime(Date logoutTime) {
		this.logoutTime = logoutTime;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getClientInfo() {
		return clientInfo;
	}

	public void setClientInfo(String clientInfo) {
		this.clientInfo = clientInfo;
	}

}