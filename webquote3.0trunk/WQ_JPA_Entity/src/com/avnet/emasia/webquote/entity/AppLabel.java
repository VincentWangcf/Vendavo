package com.avnet.emasia.webquote.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "APP_LABEL")
public class AppLabel implements Serializable
{

	@Id
	@SequenceGenerator(name = "APP_LABEL_ID_GENERATOR", sequenceName = "APP_LABEL_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APP_LABEL_ID_GENERATOR")
	@Column(name = "ID", unique = true, nullable = false, precision = 38)
	private long id;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "LOCALE_ID")
	private LocaleMaster localeMaster;

	@Column(name = "MESSAGE_CODE", unique = false, nullable = false)
	private String messageCode;

	@Column(name = "MESSAGE", unique = false, nullable = true)
	private String message;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LocaleMaster getLocaleMaster() {
		return localeMaster;
	}

	public void setLocaleMaster(LocaleMaster localeMaster) {
		this.localeMaster = localeMaster;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}
}
