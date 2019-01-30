package com.avnet.emasia.webquote.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the APP_PROPERTY database table.
 * 
 */
@Entity
@Table(name="APP_PROPERTY")
public class AppProperty implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="PROP_KEY", unique=true, nullable=false, length=200)
	private String propKey;

	@Column(name="PROP_DESC", length=500)
	private String propDesc;

	@Column(name="PROP_RESERVED", nullable=false, length=1)
	private boolean propReserved;

	@Column(name="PROP_VALUE", nullable=false, length=1000)
	private String propValue;

	public AppProperty() {
	}

	public String getPropKey() {
		return this.propKey;
	}

	public void setPropKey(String propKey) {
		this.propKey = propKey;
	}

	public String getPropDesc() {
		return this.propDesc;
	}

	public void setPropDesc(String propDesc) {
		this.propDesc = propDesc;
	}

	public boolean getPropReserved() {
		return this.propReserved;
	}

	public void setPropReserved(boolean propReserved) {
		this.propReserved = propReserved;
	}

	public String getPropValue() {
		return this.propValue;
	}

	public void setPropValue(String propValue) {
		this.propValue = propValue;
	}

}