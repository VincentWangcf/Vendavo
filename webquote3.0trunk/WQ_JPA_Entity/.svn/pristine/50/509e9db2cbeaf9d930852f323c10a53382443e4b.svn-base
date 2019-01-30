package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "LOCALE_MASTER")
public class LocaleMaster implements Serializable{

	@Id
	@Column(name="LOCALE_ID", unique=true)
	private String localId;

	
	@Column(name = "LOCALE_NAME", unique = false, nullable = true)
	private String localName;

	// bi-directional many-to-one association to User
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "localeMaster")
	private List<AppMessages> messageList = new ArrayList<AppMessages>();
	
	
	// bi-directional many-to-one association to User
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "localeMaster")
	private List<AppLabel> messageLabelList = new ArrayList<AppLabel>();

	/**
	 * @return APP MESSAGES
	 */
	public List<AppMessages> getMessageList() {
		return messageList;
	}

	/**
	 * @param messageList
	 * Set APP MESSAGE
	 */
	public void setMessageList(List<AppMessages> messageList) {
		this.messageList = messageList;
	}
	
	/**
	 * This return Locale Name
	 * @return Locale Id
	 */
	public String getLocalId() {
		return localId;
	}

	/**
	 * @param localId
	 */
	public void setLocalId(String pLocalId) {
		this.localId = pLocalId;
	}

	/**
	 * This return locale description
	 * @return Locale Name
	 */
	public String getLocalName() {
		return localName;
	}

	/**
	 * @param localName
	 */
	public void setLocalName(String pLocalName) {
		this.localName = pLocalName;
	}

	/**
	 * @return List Message Label
	 */
	public List<AppLabel> getMessageLabelList() {
		return messageLabelList;
	}

	/**
	 * @param messageLabelList
	 * Set Message Labels
	 */
	public void setMessageLabelList(List<AppLabel> messageLabelList) {
		this.messageLabelList = messageLabelList;
	}

	
}
